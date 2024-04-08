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
     * The result should not contain formatting characters.
     *
     * @return this text content as a string
     */
    String toString();

    /**
     * Indicates whether getting this content as text using {@link #toString()} will have to resolve the actual content
     * from the game context, by example by translating it. Only {@link PlainTextContent} should be considered resolved.
     *
     * @return whether this content is already resolved.
     */
    boolean isResolved();

}
