package net.smyler.smylib.text;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * A text content that is simply plain text.
 */
public final class PlainTextContent implements TextContent {

    private final String content;

    /**
     * Creates a plain text content from a string.
     *
     * @param content the content
     * @throws NullPointerException if content is <code>null</code>
     */
    public PlainTextContent(@NotNull String content) {
        this.content = requireNonNull(content);
    }

    @Override
    public String toString() {
        return this.content;
    }

    @Override
    public boolean isResolved() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlainTextContent that = (PlainTextContent) o;

        return this.content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return this.content.hashCode();
    }

}
