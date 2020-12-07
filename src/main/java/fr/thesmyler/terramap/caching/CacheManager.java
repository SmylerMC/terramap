package fr.thesmyler.terramap.caching;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.caching.requests.CachedRequest;
import fr.thesmyler.terramap.caching.requests.FailedRequest;
import fr.thesmyler.terramap.caching.requests.HttpRequest;
import fr.thesmyler.terramap.caching.requests.SuccessfulCachedRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author SmylerMC
 * 
 * TODO javadoc for cache manager
 *
 */
public class CacheManager {

	// How many threads can be downloading from the same server at the same time
	private static final int MAX_CONCURRENT_DOWNLOAD = 2;
	private final Map<String, AtomicInteger> threadCounts = new HashMap<>();

	// Used to name threads
	private static AtomicLong threadNumberingGet = new AtomicLong(0);
	private static AtomicLong threadNumberingCache = new AtomicLong(0);

	private final AtomicInteger waitingRequests = new AtomicInteger(0);

	/**
	 * Gets the given url in a new thread, either by downloading it or by loading it from the cache,
	 * then calls the given consumer from that thread.
	 * 
	 * @param url
	 * @param callback
	 * 
	 * @return
	 */
	public QueuedCacheTask getAsync(URL url, Consumer<CachedRequest> callback) {
		String host = url.getAuthority();
		Runnable action;
		QueuedCacheTask task = new QueuedCacheTask();
		if(!host.equals("")) {
			action = () -> {
				this.waitingRequests.getAndIncrement();
				AtomicInteger currentActions;
				synchronized(this.threadCounts) {
					currentActions = threadCounts.get(host);
					if(currentActions == null) {
						currentActions = new AtomicInteger(0);
					}
					threadCounts.put(host, currentActions);
				}
				boolean incremented = false;
				try {
					while(!task.isCanceled() && currentActions.get() >= MAX_CONCURRENT_DOWNLOAD) {
						synchronized(currentActions) {
							currentActions.wait();
						}
					}
					if(task.isCanceled()) return;
					currentActions.incrementAndGet();
					incremented = true;
					CachedRequest data = this.get(url);
					task.setResult(data);
					callback.accept(data);
				} catch(Exception e) {
					CachedRequest data= new FailedRequest(url, e, false);
					task.setResult(data);
					callback.accept(data);
				} finally {
					if(incremented) currentActions.decrementAndGet();
					synchronized(currentActions) {
						currentActions.notify();
					}
					this.waitingRequests.getAndDecrement();
				}
			};
		} else { // This is local, process immediately
			action = () -> {
				this.waitingRequests.getAndIncrement();
				try {
					CachedRequest data = this.get(url);
					task.setResult(data);
					callback.accept(data);
				} catch(Exception e) {
					CachedRequest data = new FailedRequest(url, e, false);
					task.setResult(data);
					callback.accept(data);
				} finally {
					this.waitingRequests.decrementAndGet();
				}
			};
		}
		Thread t = new Thread(action);
		t.setName("Terramap download " + threadNumberingGet.getAndIncrement());
		t.setDaemon(true);
		t.start();
		return task;
	}

	public CachedRequest get(URL url) {
		TerramapMod.logger.info(url);
		boolean isNetwork = false;
		try {
			//TODO Do not make a request if the cached version is recent enough
			URLConnection connection = url.openConnection();
			connection.setAllowUserInteraction(false);
			connection.setRequestProperty("User-Agent", TerramapMod.getUserAgent());
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.setInstanceFollowRedirects(true);
				isNetwork = true;
			}
			
			// TODO connection.setIfModifiedSince(ifmodifiedsince);

			ByteBuf buf = ByteBufAllocator.DEFAULT.ioBuffer();
			connection.connect();

			boolean errorStream = false;
			int code = -1;
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				code = httpConnection.getResponseCode();
				if(code >= 400 && code < 600) errorStream = true;
			}
			try(InputStream inStream = errorStream? ((HttpURLConnection)connection).getErrorStream(): connection.getInputStream()) {
				do {
					buf.ensureWritable(1024);
				} while (buf.writeBytes(inStream, 1024) > 0);
			}

			if (code > 0) {
				switch(code) {
				case HttpURLConnection.HTTP_NOT_MODIFIED:
					return this.readFromCache(url);
				default:
					byte[] read = new byte[buf.writerIndex()];
					buf.readBytes(read);
					CachedRequest req = new HttpRequest(url, code, read);
					this.cacheAsync(req);
					return req;
				}
			}

		} catch (Exception e) {
			return new FailedRequest(url, e, isNetwork);
		}
		return new FailedRequest(url, new Exception(), isNetwork);
	}

	public int getQueueSize() {
		return this.waitingRequests.get();
	}

	public CachedRequest readFromCache(URL url) {
		return new SuccessfulCachedRequest(url, new byte[0]); //TODO ReadFromCache
	}

	private void cache(CachedRequest data) {
		//TODO CacheManager::cache
	}

	private void cacheAsync(CachedRequest data) {
		Thread t = new Thread(() ->  {this.cache(data);});
		t.setName("Terramap caching " + threadNumberingCache.getAndIncrement());
		t.setDaemon(true);
		t.start();
	}

	public static void main(String args[]) {
		String[] urls = {
				"http://localhost/index.php?test01",
				"http://localhost/index.php?test02",
				"http://localhost/index.php?test03",
				"http://localhost/index.php?test04",
				"http://localhost/index.php?test05",
				"http://localhost/index.php?test06",
				"http://localhost/index.php?test07",
				"http://localhost/index.php?test08",
				"http://localhost/index.php?test09",
				"http://localhost/index.php?test10",
				"http://localhost:80/index.php?test11",
				"http://localhost/index.php?test12",
				"http://localhost/index.php?test13",
				"http://localhost/index.php?test14",
				"http://localhost/index.php?test15",
				"http://localhost/index.php?test16",
				"http://localhost/index.php?test17",
				"http://localhost/index.php?test18",
				"http://example.com/",
				"http://localhost:80/index.php?test19"
		};
		CacheManager m = new CacheManager();
		for(String s: urls) {
			URL url;
			try {
				url = new URL(s);
				m.getAsync(url, (d) -> {
					System.out.println((d.wasSuccessful()? "OK": "Nop") + " => " + d.getUrl());
					if(!d.wasSuccessful()) {
						Object e = d.getError();
						if(e instanceof Exception) ((Exception) e).printStackTrace();
						else if(d instanceof HttpRequest) System.out.println(((HttpRequest)d).getStatusCode());
					}
				});
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
