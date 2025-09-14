package net.smyler.terramap.util.geo;

/**
 * An exception thrown when errors occurs in geographic related operations.
 *
 * @author Smyler
 */
public class GeoException extends RuntimeException {

    public GeoException() {
        super();
    }

    public GeoException(String message) {
        super(message);
    }

    public GeoException(String message, Throwable cause) {
        super(message, cause);
    }

}
