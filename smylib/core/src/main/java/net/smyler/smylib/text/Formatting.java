package net.smyler.smylib.text;

import net.smyler.smylib.Color;
import org.jetbrains.annotations.NotNull;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static net.smyler.smylib.Color.fromHtmlHexString;

/**
 * Vanilla Minecraft formatting codes as an enum.
 *
 * @author Smyler
 */
public enum Formatting {

    // Colors
    BLACK('0', fromHtmlHexString("#000000")),
    DARK_BLUE('1', fromHtmlHexString("#0000aa")),
    DARK_GREEN('2', fromHtmlHexString("#00aa00")),
    DARK_AQUA('3', fromHtmlHexString("#00aaaa")),
    DARK_RED('4', fromHtmlHexString("#aa0000")),
    DARK_PURPLE('5', fromHtmlHexString("#aa00aa")),
    GOLD('6', fromHtmlHexString("#ffaa00")),
    GRAY('7', fromHtmlHexString("#aaaaaa")),
    DARK_GRAY('8', fromHtmlHexString("#555555")),
    BLUE('9', fromHtmlHexString("#5555ff")),
    GREEN('a', fromHtmlHexString("#55ff55")),
    AQUA('b', fromHtmlHexString("#55ffff")),
    RED('c', fromHtmlHexString("#ff5555")),
    LIGHT_PURPLE('d', fromHtmlHexString("#ff55ff")),
    YELLOW('e', fromHtmlHexString("#ffff55")),
    WHITE('f', fromHtmlHexString("#ffffff")),

    // Styles
    OBFUSCATED('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),

    // Reset
    RESET('r');

    public final char code;
    public final Color color;
    public static final char PREFIX = '\u00a7';

    public static final Formatting[] COLORS;

    static {
        COLORS = stream(Formatting.values()).filter(Formatting::isColor).toArray(Formatting[]::new);
    }

    Formatting(char code, Color color) {
        this.code = code;
        this.color = color;
    }

    Formatting(char code) {
        this.code = code;
        this.color = null;
    }

    public boolean isColor() {
        return this.color != null;
    }

    public char code() {
        return this.code;
    }

    public Color color() {
        return this.color;
    }

    public String toString() {
        return PREFIX + "" + this.code;
    }

    public static Formatting fromChar(char chr) {
        for (Formatting format: values()) {
            if (format.code == chr) {
                return format;
            }
        }
        return null;
    }

    /**
     * Finds the nearest color format code to an RGB color.
     * The color's alpha channel is ignored.
     * Euclidean distance is used.
     *
     * @param color the color to find the nearest formatting color from
     * @return the nearest formatting color to the given color
     */
    @NotNull
    public static Formatting nearestFormattingColor(@NotNull Color color) {
        return stream(COLORS)
                .min(comparing(c -> {
                    float red = c.color.redf() - color.redf();
                    float green = c.color.greenf() - color.greenf();
                    float blue = c.color.bluef() - color.bluef();
                    return (red * red) + (green * green) + (blue * blue);
                })).orElseThrow(IllegalStateException::new);
    }

}
