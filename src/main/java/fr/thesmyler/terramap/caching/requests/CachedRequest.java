package fr.thesmyler.terramap.caching.requests;

import java.net.URL;

public abstract class CachedRequest {
	
	private final URL url;
	
	public CachedRequest(URL url) {
		this.url = url;
	}

	public abstract byte[] getData();
	
	public boolean wasSuccessful() {
		return this.getError() == null;
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	public abstract Object getError();
	
	public abstract boolean wasNetwork();
	
}
