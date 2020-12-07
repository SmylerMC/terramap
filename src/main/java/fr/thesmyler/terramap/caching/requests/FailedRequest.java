package fr.thesmyler.terramap.caching.requests;

import java.net.URL;

public class FailedRequest extends CachedRequest {

	private final boolean wasNetwork;
	private final Exception exception;
	
	public FailedRequest(URL url, Exception exception, boolean wasNetwork) {
		super(url);
		this.wasNetwork = wasNetwork;
		this.exception = exception;
	}

	@Override
	public byte[] getData() {
		return new byte[0];
	}

	@Override
	public Object getError() {
		return this.exception;
	}

	@Override
	public boolean wasNetwork() {
		return this.wasNetwork;
	}

}
