package fr.smyler.terramap.caching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.common.io.Files;

import fr.smyler.terramap.TerramapMod;

//FIXME If we loose Internet access, the thread just gets stuck...


/**
 * @author Smyler
 * 
 * This class is responsible for handling all resources which needs to be downloaded at some point
 * It has a worker thread running to allow for asynchronous caching.
 * The worker is started by the mod during preinit
 *
 * FIXME Some tiles are read to soon on disk are invalid (not fully written) avoid that!
 */
public class CacheManager implements Runnable {

	private Thread worker;
	private volatile boolean workerRunning;
	private LinkedList<Cachable> toCacheAsync = new LinkedList<Cachable>();
	private Map<URL, Integer> faultyUrls = new HashMap<URL, Integer>();
	private int maxCacheTries = 3;

	private File cachingDirectory;

	private Cachable currentlyCachedByWorker = null;

	public CacheManager(String path) throws IOException {
		this.setCachingDirectory(new File(path));
		this.createWorker();
	}


	public CacheManager() {
		this.cachingDirectory = Files.createTempDir();
		this.createWorker();
	}


	private void createWorker() {
		this.worker = new Thread(this);
		this.worker.setName("Terramap Caching Thread");
		this.worker.setDaemon(true);
	}

	/**
	 * Called by the worker when it is launched.
	 * If called by an other thread, it calls CacheManager::startWorker and returns. It should be avoided
	 */
	@Override
	public void run() {
		if(! this.isCallingFromWorker()) {
			this.startWorker();
			return;
		}
		this.workerRunning = true;
		synchronized(TerramapMod.logger) {
			TerramapMod.logger.info("Started Terramap cache manager");
		}
		while(this.workerRunning) {
			Cachable toCache;
			int sleep = 10;
			synchronized(this.toCacheAsync) {
				toCache = this.toCacheAsync.poll();
			}
			if(toCache == null) {
				sleep = 20;
			}else {
				sleep = 0;
				synchronized(toCache) {  //Crashes the thread when null
					this.currentlyCachedByWorker = toCache;
					while(this.shouldCache(toCache)) {
						try {
							this.cache(toCache);
							break;
						} catch(IOException e) {
							TerramapMod.logger.error("Failed to cache a file, you may not be connected to the internet, logging exception.");
							TerramapMod.logger.catching(e);
							this.reportError(toCache);
						}
					}
					this.currentlyCachedByWorker = null;
				}
				try {Thread.sleep(sleep);} catch (InterruptedException e) {TerramapMod.logger.catching(e);}
			}
		}
		synchronized(TerramapMod.logger) {
			TerramapMod.logger.info("Stopping IRLW cache manager.");
		}
	}

	/**
	 * Starts the worker if it is not running
	 * Does nothing if it is
	 */
	public void startWorker() {
		if(!this.worker.isAlive()) this.worker.start();
	}

	/**
	 * Stops the worker if it is running
	 * Does nothing if it isn't
	 */
	public void stopWorker() {
		if(this.worker.isAlive()) this.workerRunning = false;
	}

	/**
	 * 
	 * @return True if the worker is running
	 */
	public boolean isWorkerAlive() {
		return this.worker.isAlive();
	}

	/**
	 * 
	 * @return true when called from the worker, false otherwise.
	 */
	public boolean isCallingFromWorker() {
		return Thread.currentThread().equals(this.worker);
	}

	/**
	 * @return The directory where cached files are saved as a File object
	 */
	public File getCachingDirectory() {
		return cachingDirectory;
	}

	/**
	 * @return The path to the directory where cached files are saved as a File string
	 */
	public String getCachingPath() {
		return cachingDirectory.getAbsolutePath();
	}


	/**
	 * Sets directory where cached files are saved
	 * 
	 * @param path to a directory as a string
	 * @throws IOException if the path is not an actual directory
	 */
	public void setCachingDirectory(String path) throws IOException {
		this.setCachingDirectory(new File(path));
	}

	/**
	 * Sets directory where cached files are saved
	 * 
	 * @param a File object representing the directory
	 * @throws IOException if the file is not an actual directory
	 */
	public void setCachingDirectory(File cachingDirectory) throws IOException {
		if(!cachingDirectory.exists() && cachingDirectory.isDirectory()) {
			throw new IOException("No such file or directory: " + cachingDirectory.getPath());
		}
		this.cachingDirectory = cachingDirectory;
	}



	/**
	 * Caches a Cachable and returns when done
	 * 
	 * @param toCache
	 * @throws IOException
	 * @throws InvalidMapboxSessionException
	 */
	public void cache(Cachable toCache) throws IOException {

		if(this.isCached(toCache)) return;

		if(!this.shouldCache(toCache)) {
			TerramapMod.logger.error("Will not attempt to cache " + toCache.getURL() + ", too many failed attempts");
			return;
		}
		if(!this.isCallingFromWorker()) 
			TerramapMod.logger.warn("Caching from an other thread!!");

		File f = this.getCachableFile(toCache);
		this.downloadUrlToFile(toCache.getURL(), f);
		if(!f.exists() || !f.isFile()) {
			throw new IOException("A file should have been cached but doesn't exit: " + f.getAbsolutePath());
		}
		toCache.cached(f);
	}

	/**
	 * Adds the Cachable to a queue so that it gets cached by the worker in a near future
	 * 
	 * @param toCache
	 */
	public void cacheAsync(Cachable toCache) {
		synchronized(this.toCacheAsync) {
			this.toCacheAsync.add(toCache);
		}
	}


	private File getCachableFile(Cachable c) {
		return new File(this.cachingDirectory.getAbsoluteFile() + "/" + c.getFileName());
	}


	/**
	 * @param c The resource to check
	 * 
	 * @return true if the given resource has been cached already
	 */
	public boolean isCached(Cachable c) {
		File f = this.getCachableFile(c);
		return f.exists() && f.isFile() && !c.equals(this.currentlyCachedByWorker);
	}


	/**
	 * Return the file from the cachable.
	 * If the file does not exist but no error has been thrown, such as with empty tiles, returns null
	 * Downloads the resource if needed, or read it from disk.
	 * 
	 * @param c
	 * @throws InvalidMapboxSessionException 
	 * @throws IOException 
	 */
	public File getFile(Cachable c) throws IOException {
		File f = this.getCachableFile(c);
		if(f.exists() && f.isFile()) return f;
		this.cache(c);
		if(f.exists() && f.isFile()) return f;
		return null;
	}

	/**
	 * Empties the queue of resources waiting to be cached by the worker
	 */
	public void clearQueue() {
		synchronized(this.toCacheAsync) {
			this.toCacheAsync = new LinkedList<Cachable>();
		}
	}

	/**
	 * 
	 * @return The number of resources waiting to be cached by the worker
	 */
	public int getQueueSize() {
		synchronized(this.toCacheAsync) {
			return this.toCacheAsync.size();
		}
	}


	public void createDirectory() {
		this.cachingDirectory.mkdirs();
	}

	public boolean isBeingCached(Cachable c) {
		if(c.equals(this.currentlyCachedByWorker))
			return true;
		synchronized(this.toCacheAsync) {
			for(Cachable ca: this.toCacheAsync) {
				if(ca.getURL().equals(c.getURL())) return true;
			}
		}
		return false;		
	}


	public int downloadUrlToFile(URL url, File file) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();

		connection.setAllowUserInteraction(false);
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", TerramapMod.HTTP_USER_AGENT);

		connection.connect();

		switch(connection.getResponseCode()) {

		case HttpURLConnection.HTTP_OK:  //TODO Make sure we don't need to support 304 response (could they happend in this context?)
			InputStream inStream = connection.getInputStream();
			OutputStream outStream = new FileOutputStream(file);
			int lastByte = -1;
			byte[] buffer = new byte[32];
			while ((lastByte = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, lastByte);
			}
			outStream.close();
			inStream.close();
			break;
		case HttpURLConnection.HTTP_NOT_FOUND:
			throw new FileNotFoundException();
		}
		connection.disconnect();
		return connection.getResponseCode();
	}

	public void reportError(Cachable c) {
		TerramapMod.logger.error("Failed to cache " + c.getURL() + " to " + c.getFileName());
		URL url = c.getURL();
		if(this.faultyUrls.containsKey(url)) {
			this.faultyUrls.put(url, this.faultyUrls.get(url) + 1);
		} else {
			this.faultyUrls.put(url, 1);
		}
	}

	public int getMaxCacheTries() {
		return this.maxCacheTries;
	}

	public void setMaxCacheTries(int i) {
		this.maxCacheTries = i;
	}

	/**
	 * Check if a Cachable should be cached or has failed to cache to many times
	 * 
	 * @param c The Cachable
	 * @return a boolean indicating if the CacheManager will cache or discard the Cachable
	 */
	public boolean shouldCache(Cachable c) {
		URL url = c.getURL();
		return !this.faultyUrls.containsKey(url)|| this.faultyUrls.get(url) < this.maxCacheTries;
	}

}
