package fr.thesmyler.terramap.caching.requests;

import java.net.URL;

public class SuccessfulCachedRequest extends CachedRequest {
	
	private final byte[] data;

	public SuccessfulCachedRequest(URL url, byte[] data) {
		super(url);
		this.data = data;
	}

	@Override
	public byte[] getData() {
		return this.data;
	}

	@Override
	public Object getError() {
		return null;
	}

	@Override
	public boolean wasNetwork() {
		return false;
	}

}
