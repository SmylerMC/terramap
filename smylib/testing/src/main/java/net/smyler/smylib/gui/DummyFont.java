package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;

/**
 * A font that does nothing, which can be used in unit tests when no rendering context is available.
 *
 * @author SmylerMC
 */
public class DummyFont extends BaseFont {

    public static final float CHAR_WIDTH = 9f;
    private final float size;

    public DummyFont(float size, float interline) {
        super(interline);
        this.size = size;
    }

    @Override
    public float scale() {
        return this.size;
    }

    @Override
    public Font withScale(float scale) {
        return new DummyFont(scale, this.interlineFactor);
    }

    @Override
    public Font scaled(float scaleFactor) {
        return new DummyFont(this.size * scaleFactor, this.interlineFactor);
    }

    @Override
    public float height() {
        return CHAR_WIDTH * this.size;
    }

    @Override
    public Font withInterlineRatio(float interline) {
        return new DummyFont(this.size, interline);
    }

    @Override
    public float draw(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow) {
        return 0f;
    }

    @Override
    public float getCharWidth(char character) {
        return CHAR_WIDTH * this.size;
    }

    @Override
    public boolean isUnicode() {
        return false;
    }

    String wrapFormattedStringToWidth(String str, float width) {
        StringBuilder builder = new StringBuilder(str);
        final int maxLineLength = (int) floor(width / CHAR_WIDTH / this.size);
        for (int lineStart = 0; lineStart < builder.length() - maxLineLength;) {

            final int limit = min(lineStart + maxLineLength, str.length() - 1);
            boolean hasSplit = false;

            // Look for a suitable place to split, going backward
            for (int i = limit; i > lineStart; i--) {
                char chr = builder.charAt(i);
                // Split, we aren't taking into account format chars,
                // but it's not that big of a problem as this is just a test thing which doesn't have to be perfect
                if (chr == ' ' || chr == '\n') {
                    builder.setCharAt(i, '\n');
                    hasSplit = true;
                    lineStart = i + 1;
                    break;
                }
            }

            if (!hasSplit && limit < str.length() - 1) {
                builder.insert(limit, '\n');
            }
        }
        return builder.toString();
    }

}
