package fr.thesmyler.terramap.caching;

import java.net.URL;
import java.util.function.Consumer;

import fr.thesmyler.terramap.caching.requests.CachedRequest;

/**
 * @author SmylerMC
 * 
 * TODO javadoc for cache manager
 *
 */
public abstract interface CacheManager {

	public abstract void setup();

	/**
	 * Gets the given url in a new thread, either by downloading it or by loading it from the cache,
	 * then calls the given consumer from that thread.
	 * 
	 * @param url
	 * @param callback
	 * 
	 * @return
	 */
	public QueuedCacheTask getAsync(URL url, Consumer<CachedRequest> callback);

	public CachedRequest get(URL url);

	public abstract int getQueueSize();

}
