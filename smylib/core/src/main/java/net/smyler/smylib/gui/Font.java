package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import net.smyler.smylib.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * A font used to render text onto the screen.
 * Implementation is handled by the underlying Game,
 * with {@link BaseFont} providing a first unified layer for common methods.
 *
 * @author Smyler
 */
public interface Font {

    /**
     * @return the scale factor of this {@link Font font} (1.0 is the default scale)
     */
    float scale();

    /**
     * Creates a copy of this {@link Font font} but with a different scale factor
     *
     * @param scale a new scale for the copy (1 is the default scale)
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
     * @return the interline to use when rendering multiple lines at once, in pixels
     */
    float interline();

    /**
     * Creates a copy of this {@link Font font} but with a different interline ratio.
     *
     * @param interline a new interline, as a fraction of the font's height
     * @return a copy of this font with a different interline
     */
    Font withInterlineRatio(float interline);

    /**
     * Draws the specified {@link String string} onto the screen,
     * to the right of the specified coordinates.
     *
     * @param x         x coordinate to draw the string at on the screen
     * @param y         y coordinate to draw the string at on the screen
     * @param text      the string to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @param color     the default color to draw the string with in the absence of formatting codes
     * @param shadow    whether to render the string's shadow
     * @return          the width of the rendered string, in pixels
     */
    float draw(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow);

    /**
     * Draws the specified {@link Text text} onto the screen,
     * to the right of the specified coordinates.
     * The {@link Text text}'s content {@link Text#isContentResolved() is resolved} before being drawn.
     *
     * @param x         x coordinate to draw the text at on the screen
     * @param y         y coordinate to draw the text at on the screen
     * @param text      the {@link Text text} to draw
     * @param color     a default color to draw the text with in places where it does specify a color
     * @param shadow    whether to draw a shadow
     * @return          the width of the rendered text, in pixels
     */
    float draw(float x, float y, @NotNull Text text, @NotNull Color color, boolean shadow);

    /**
     * Draws the specified {@link String string} onto the screen,
     * centering it horizontally around the given coordinates.
     *
     * @param x         x coordinate to draw the string around on the screen
     * @param y         y coordinate to draw the string at on the screen
     * @param text      the string to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @param color     the default color to draw the string with in the absence of formatting codes
     * @param shadow    whether to render the string's shadow
     * @return          the width of the rendered string, in pixels
     */
    float drawCentered(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow);

    /**
     * Draws the specified {@link Text text} onto the screen,
     * centering it horizontally around the given coordinates.
     * The {@link Text text}'s content {@link Text#isContentResolved() is resolved} before being drawn.
     *
     * @param x         x coordinate to draw the {@link Text text} around on the screen
     * @param y         y coordinate to draw the {@link Text text} at on the screen
     * @param text      the {@link Text text} to draw
     * @param color     a default color to draw the text with in places where it does specify a color
     * @param shadow    whether to render the {@link Text text}'s shadow
     * @return          the width of the rendered {@link Text text}, in pixels
     */
    float drawCentered(float x, float y, @NotNull Text text, @NotNull Color color, boolean shadow);

    /**
     * Draws specified strings onto the screen,
     * one per line,
     * to the right of the specified coordinates.
     *
     * @param x         x coordinate to draw the strings at on the screen
     * @param y         y coordinate to draw the first string at on the screen
     * @param color     the default color to draw the strings with in the absence of formatting codes
     * @param shadow    whether to render the strings' shadow
     * @param lines     the lines to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @return          the largest width of the rendered strings
     *
     * @see Font#wrapToWidth(String, float)
     */
    float drawLines(float x, float y, @NotNull Color color, boolean shadow, @NotNull String... lines);

    /**
     * Draws specified {@link Text texts} onto the screen,
     * one per line,
     * to the right of the specified coordinates.
     * The {@link Text texts}' content {@link Text#isContentResolved() are resolved} before being drawn.
     *
     * @param x         x coordinate to draw the {@link Text texts} at on the screen
     * @param y         y coordinate to draw the first {@link Text text} at on the screen
     * @param color     a default color to draw the texts with in places where they do specify a color
     * @param shadow    whether to render the string's shadow
     * @param lines     the {@link Text texts} to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @return          the largest width of the rendered texts
     *
     * @see Font#wrapToWidth(Text, float)
     */
    float drawLines(float x, float y, @NotNull Color color, boolean shadow, @NotNull Text... lines);

    /**
     * Draws specified strings onto the screen,
     * one per line,
     * centering them horizontally around the given coordinates.
     *
     * @param x         x coordinate to draw the strings at on the screen
     * @param y         y coordinate to draw the first string at on the screen
     * @param color     the default color to draw the strings with in the absence of formatting codes
     * @param shadow    whether to render the strings' shadow
     * @param lines     the lines to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @return          the largest width of the rendered strings
     *
     * @see Font#wrapToWidth(String, float)
     */
    float drawCenteredLines(float x, float y, @NotNull Color color, boolean shadow, @NotNull String... lines);

    /**
     * Draws specified {@link Text texts} onto the screen,
     * one per line,
     * centering them horizontally around the given coordinates.
     * The {@link Text texts}' content {@link Text#isContentResolved() are resolved} before being drawn.
     *
     * @param x         x coordinate to draw the {@link Text texts} at on the screen
     * @param y         y coordinate to draw the first {@link Text text} at on the screen
     * @param color     a default color to draw the texts with in places where they do specify a color
     * @param shadow    whether to render the string's shadow
     * @param lines     the {@link Text texts} to draw (may contain {@link net.smyler.smylib.text.Formatting formatting codes})
     * @return          the largest width of the rendered texts
     *
     * @see Font#wrapToWidth(Text, float)
     */
    float drawCenteredLines(float x, float y, @NotNull Color color, boolean shadow, @NotNull Text... lines);

    /**
     * Splits a string into multiple lines,
     * where each line fits within a given maximum render width.
     * <br>
     * The algorithm breaks lines on new line characters,
     * as well as between words when necessary (' ' and '\t' are used as world delimiters)
     * <br>
     * In the event that a word is too long to be placed on a single line,
     * it will be broken into multiple parts.
     * <br>
     * If the given width is smaller than the width of a character,
     * said character will be on its own line.
     *
     * @param text      the string to split
     * @param maxWidth  the maximum line width
     * @return          an array where each entry is a line
     */
    String[] wrapToWidth(@NotNull String text, float maxWidth);

    /**
     * Splits a {@link Text} into multiple lines,
     * where each line fits within a given maximum render width.
     * The resulting texts have their content resolved to plain text.
     * The hierarchical structure of {@link Text texts} might not be kept.
     * <br>
     * The algorithm breaks lines on new line characters,
     * as well as between words when necessary (' ' and '\t' are used as word delimiters).
     * <br>
     * In the event that a word is too long to be placed on a single line,
     * it will be broken into multiple parts.
     * <br>
     * If the given width is smaller than the width of a character,
     * said character will be on its own line.
     *
     * @param text      the text to split
     * @param maxWidth  the maximum line width
     * @return          an array where each entry is a line
     */
    Text[] wrapToWidth(@NotNull Text text, float maxWidth);

    /**
     * Calculates the rendered width of string, in pixels.
     * Multiple strings may be supplied, in which case the result is the largest one.
     *
     * @param texts the strings to calculate the width of
     * @return the rendered width of the string, in pixels
     */
    float computeWidth(@NotNull String... texts);

    /**
     * Calculates the rendered width of {@link Text texts}, in pixels.
     * The text will be resolved first.
     * Multiple texts may be supplied, in which case the result is the largest one.
     *
     * @param texts the {@link Text text} to calculate the width of
     * @return the rendered width of the {@link Text text}, in pixels
     */
    float computeWidth(@NotNull Text... texts);

    /**
     * Calculates the rendered height of a set of strings,
     * considering each one is a single line.
     *
     * @param lines the lines
     * @return the rendered height of the given lines
     */
    float computeHeight(@NotNull String... lines);

    /**
     * Calculates the rendered height of a set of {@link Text texts},
     * considering each one is a single line.
     *
     * @param lines the lines
     * @return the rendered height of the given lines
     */
    float computeHeight(@NotNull Text... lines);

    /**
     * Trims the right part of a string, so it fits within a given width.
     *
     * @param text  the string to trim
     * @param width the maximum width of the string, in pixels
     * @return trimmed string
     */
    String trimRight(@NotNull String text, float width);

    /**
     * Trims the right part of a {@link Text text} so it fits within a given width.
     * The operation resolves the {@link Text text} and does not preserve its hierarchical structure.
     *
     * @param text      the text to trim
     * @param maxWidth  the maximum width of the string, in pixels
     * @return the trimmed text
     */
    Text trimRight(@NotNull Text text, float maxWidth);

    /**
     * Trims the left part of a string, so it fits within a given width.
     *
     * @param text  the string to trim
     * @param width the maximum width of the string, in pixels
     * @return trimmed string
     */
    String trimLeft(@NotNull String text, float width);

    /**
     * Trims the left part of a {@link Text text} so it fits within a given width.
     * The operation resolves the {@link Text text} and does not preserve its hierarchical structure.
     *
     * @param text      the text to trim
     * @param maxWidth  the maximum width of the string, in pixels
     * @return the trimmed text
     */
    Text trimLeft(@NotNull Text text, float maxWidth);

    /**
     * @return whether this is a Unicode font
     */
    boolean isUnicode();

}
