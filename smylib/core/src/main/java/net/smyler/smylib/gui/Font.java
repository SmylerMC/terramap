package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

import java.util.List;

public interface Font {

    /**
     * @return the scale factor of this font (1 is the default font)
     */
    float scale();

    /**
     * Creates a copy of this font but with a different scale factor
     *
     * @param scale a new scale for the copy (1 is the default font)
     * @return a copy of this font with a different scale
     */
    Font withScale(float scale);

    /**
     * Creates a copy of this font scaled to a given factor.
     * The new font's scale is the scale of this font times the scale factor.
     *
     * @param scaleFactor a factor to scale this font with
     * @return a copy of this font with a different scale factor
     */
    Font scaled(float scaleFactor);

    /**
     * @return the height of this font, in pixels
     */
    float height();

    /**
     * Draws the specified string on the screen.
     *
     * @param x         x coordinate to draw the string at on the screen
     * @param y         y coordinate to draw the string at on the screen
     * @param text      the text to draw (may have Minecraft formatting codes)
     * @param color     the default color to draw the text with in the absence of formatting codes
     * @param shadow    whether to render the string's shadow
     * @return          the width of the rendered string
     */
    float drawString(float x, float y, String text, Color color, boolean shadow);

    void drawCenteredString(float x, float y, String text, Color color, boolean shadow);

    void drawSplitString(float x, float y, String text, float wrapWidth, Color color, boolean shadow);

    //TODO List<TextComponent> splitText()

    float getStringWidth(String text);

    float getCharWidth(char character);

    String trimStringToWidth(String text, float width);

    String trimStringToWidth(String text, float width, boolean reverse);

    int getWordWrappedHeight(String str, float width);

    boolean isUnicode();

    List<String> listFormattedStringToWidth(String str, float wrapWidth);

}
