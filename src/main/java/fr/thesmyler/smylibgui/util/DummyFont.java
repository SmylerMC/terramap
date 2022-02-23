package fr.thesmyler.smylibgui.util;

import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static fr.thesmyler.terramap.util.math.Math.clamp;
import static java.lang.Math.*;

/**
 * A font that does nothing, which can be used in unit tests when no rendering context is available.
 *
 * @author SmylerMC
 */
public class DummyFont extends Font {

    private static final float CHAR_WIDTH = 9f;
    private final float size;

    public DummyFont(float size) {
        this.size = size;
    }

    @Override
    public float height() {
        return CHAR_WIDTH * this.size;
    }

    @Override
    public float drawString(float x, float y, String text, Color color, boolean shadow) {
        return 0f;
    }

    @Override
    public void drawCenteredString(float x, float y, String text, Color color, boolean shadow) {
    }

    @Override
    public void drawSplitString(float x, float y, String text, float wrapWidth, Color color, boolean shadow) {
    }

    @Override
    public float getStringWidth(String text) {
        return CHAR_WIDTH * this.size * text.length();
    }

    @Override
    public float getCharWidth(char character) {
        return CHAR_WIDTH * this.size;
    }

    @Override
    public String trimStringToWidth(String text, float width) {
        return text.substring(0, (int) clamp(width / CHAR_WIDTH / this.size, 0, text.length()));
    }

    @Override
    public String trimStringToWidth(String text, float width, boolean reverse) {
        if (reverse) return text.substring(text.length() - (int) clamp(width / CHAR_WIDTH / this.size, 0, text.length()));
        else return this.trimStringToWidth(text, width);
    }

    @Override
    public int getWordWrappedHeight(String str, float width) {
        return super.getWordWrappedHeight(str, width);
    }

    @Override
    public List<String> listFormattedStringToWidth(String str, float wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
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

    @Override
    public List<ITextComponent> splitText(ITextComponent textComponent, float maxTextLenght, boolean keepTrailingBlank, boolean forceTextColor) {
        return this.listFormattedStringToWidth(textComponent.getFormattedText(), maxTextLenght).stream().map(TextComponentString::new).collect(Collectors.toList());
    }

}
