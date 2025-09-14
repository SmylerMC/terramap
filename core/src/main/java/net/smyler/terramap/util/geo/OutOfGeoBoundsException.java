package net.smyler.terramap.util.geo;

/**
 * Thrown when attempting an operation that cannot be completed because
 * it takes place outside a supported geographic area.
 *
 * @author Smyler
 */
public class OutOfGeoBoundsException extends GeoException {

    public OutOfGeoBoundsException() {
        super();
    }

    public OutOfGeoBoundsException(String message) {
        super(message);
    }

    public  OutOfGeoBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

}
