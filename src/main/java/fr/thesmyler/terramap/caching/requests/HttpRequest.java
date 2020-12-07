package fr.thesmyler.terramap.caching.requests;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest extends CachedRequest {
	
	private byte[] data;
	private int code;
	
	public HttpRequest(URL url, int code, byte[] data) {
		super(url);
		this.data = data;
		this.code = code;
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
		return true;
	}
	
	@Override
	public boolean wasSuccessful() {
		return this.code == HttpURLConnection.HTTP_OK || this.code == HttpURLConnection.HTTP_NOT_MODIFIED;
	}

	public int getStatusCode() {
		return this.code;
	}

}
