package fr.thesmyler.terramap.caching;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.thesmyler.terramap.caching.requests.CachedRequest;

public class QueuedCacheTask {
	
	private AtomicBoolean canceled = new AtomicBoolean(false);
	private CachedRequest result;

	public void cancel() {
		this.canceled.set(true);
	}
	
	public boolean isCanceled() {
		return this.canceled.get();
	}
	
	public boolean isDone() {
		return this.isCanceled() || this.result != null;
	}
	
	protected void setResult(CachedRequest req) {
		this.result = req;
	}
}
