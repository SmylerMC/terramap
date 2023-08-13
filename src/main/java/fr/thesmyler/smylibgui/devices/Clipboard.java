package fr.thesmyler.smylibgui.devices;

/**
 * Provides access to the device's clipboard.
 * Only string content is supported.
 *
 * @author Smyler
 */
public interface Clipboard {

    /**
     * Gets the content of the clipboard.
     * If the clipboard is empty, returns the empty string.
     *
     * @return the clipboard's content
     */
    String getContent();

    /**
     * Sets the content of the clipboard.
     *
     * @param content   the new clipboard content
     */
    void setContent(String content);

}
