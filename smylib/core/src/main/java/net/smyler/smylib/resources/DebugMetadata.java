package net.smyler.smylib.resources;


/**
 * Custom metadata meant to test the metadata system.
 * Not present unless SmyLib runs in debug mode and the underlying backend supports it.
 *
 * @author Smyler
 */
public class DebugMetadata {

    private final String value;

    public DebugMetadata(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

}
