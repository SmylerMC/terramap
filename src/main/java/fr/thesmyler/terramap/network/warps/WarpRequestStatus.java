package fr.thesmyler.terramap.network.warps;

public enum WarpRequestStatus {
	
	OK(0x00, true),					// Everything went fine, the request has been handled
	MULTIPART(0x01, true),			// To many matching warps, other packets will follow
	TOO_MANY(0x02, false),			// To many matching warps, this request will not be handled
	NO_SUCH_WARP(0x03, false),		// The warp did not exist
	NOT_IMPLEMENTED(0x04, false),	// Warps are not supported
	INVALID_FILTER(0x05, false),	// The request contained an invalid filter
	INVALID_KEYS(0x06, false),		// One of the keys requested cannot be included in multi request
	FORBIDDEN(0x07, false),			// The user did not have the required permission to make that request
	UNKNOWN(Byte.MAX_VALUE, false);	// The status code was not known
	
	private final byte code;
	private final boolean success;
	
	private WarpRequestStatus(int networkCode, boolean success) {
		this.code = (byte)networkCode;
		this.success = success;
	}
	
	public byte getNetworkCode() {
		return this.code;
	}
	
	public boolean isSuccess() {
		return this.success;
	}
	
	public boolean isError() {
		return ! this.success;
	}
	
	public static WarpRequestStatus getFromNetworkCode(byte code) {
		for(WarpRequestStatus value: WarpRequestStatus.values()) {
			if(value.code == code) return value;
		}
		return UNKNOWN;
	}

}
