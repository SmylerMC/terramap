package net.smyler.smylib.text;

/**
 * The content of a text.
 * Implementing classes are expected to have the following properties:
 * <ul>
 *     <li>be immutable</li>
 *     <li>implement equals</li>
 *     <li>implement hashCode</li>
 * </ul>
 */
public interface TextContent {

    /**
     * Resolves this text content as a String.
     *
     * @return this text content as a string
     */
    String toString();

}
