package net.smyler.smylib.text;

import net.smyler.smylib.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Objects.requireNonNullElse;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.text.BooleanTextStyle.*;
import static net.smyler.smylib.text.Formatting.*;

/**
 * A style to apply to a {@link Text}.
 * Styles are inherited from a root text to its siblings.
 * Styles can manage both RGB colors and classical minecraft formatting color codes,
 * and convert between them when necessary.
 *
 * @author Smyler
 */
public final class TextStyle {

    /**
     * A special color singleton to be used to specify that a color should be inherited from the parent style.
     */
    public static final Color INHERIT_COLOR = new Color(255, 255, 255, 0);
    private static boolean gameSupportsRgb = false;

    @Nullable private final Color color;
    @NotNull private final BooleanTextStyle bold;
    @NotNull private final BooleanTextStyle italic;
    @NotNull private final BooleanTextStyle strikethrough;
    @NotNull private final BooleanTextStyle underlined;
    @NotNull private final BooleanTextStyle obfuscated;

    /**
     * Constructs a new style by explicitly providing values.
     *
     * @param color         a color. If a reference to {@link TextStyle#INHERIT_COLOR} is given this style will inherit colors from its parents.
     *                      <code>null</code> is interpreted as no color, not as inherited.
     * @param bold          whether this style should be bold
     * @param italic        whether this style should be italic
     * @param strikethrough whether this style should be strikethrough
     * @param underlined    whether this style should be underlined
     * @param obfuscated    whether this style should be obfuscated
     */
    public TextStyle(@Nullable Color color, BooleanTextStyle bold, BooleanTextStyle italic, BooleanTextStyle strikethrough, BooleanTextStyle underlined, BooleanTextStyle obfuscated) {
        this.color = color;
        this.bold = requireNonNullElse(bold, INHERIT);
        this.italic = requireNonNullElse(italic, INHERIT);
        this.strikethrough = requireNonNullElse(strikethrough, INHERIT);
        this.underlined = requireNonNullElse(underlined, INHERIT);
        this.obfuscated = requireNonNullElse(obfuscated, INHERIT);
    }

    /**
     * Creates a new style by providing the desired properties.
     *
     * @param color     any color,
     *                  or null for no color,
     *                  or a reference to {@link TextStyle#INHERIT_COLOR} to make the style inherit colors from its parents.
     * @param formats   all provided formatting codes applied in the provided order.
     *                  {@link Formatting#RESET} sets all boolean properties to <code>false</code>.
     */
    public TextStyle(@Nullable Color color, Formatting... formats) {
        this.color = color;
        BooleanTextStyle bold = INHERIT;
        BooleanTextStyle italic = INHERIT;
        BooleanTextStyle strikethrough = INHERIT;
        BooleanTextStyle underline = INHERIT;
        BooleanTextStyle obfuscated = INHERIT;
        for (Formatting format: formats) {
            if (format == null) {
                continue;
            }
            checkArgument(!format.isColor(), "Color formatting codes are not allowed in TextStyle. Use the color argument instead.");
            switch (format) {
                case OBFUSCATED:
                    obfuscated = TRUE;
                    break;
                case BOLD:
                    bold = TRUE;
                case STRIKETHROUGH:
                    strikethrough = TRUE;
                    break;
                case UNDERLINE:
                    underline = TRUE;
                    break;
                case ITALIC:
                    italic = TRUE;
                    break;
                case RESET:
                    bold = italic = strikethrough = underline = obfuscated = FALSE;
                    break;
            }
        }
        this.bold = bold;
        this.italic = italic;
        this.strikethrough = strikethrough;
        this.underlined = underline;
        this.obfuscated = obfuscated;
    }

    public Color color() {
        return this.color;
    }

    public TextStyle withColor(@Nullable Color color) {
        if (Objects.equals(this.color, color) && this.color != INHERIT_COLOR && color != INHERIT_COLOR) {
            return this;
        }
        return new TextStyle(color, this.bold, this.italic, this.strikethrough, this.underlined, this.obfuscated);
    }

    @NotNull
    public BooleanTextStyle isBold() {
        return this.bold;
    }

    public TextStyle withBold(@NotNull BooleanTextStyle bold) {
        requireNonNull(bold);
        if (this.bold == bold) {
            return this;
        }
        return new TextStyle(this.color, bold, this.italic, this.strikethrough, this.underlined, this.obfuscated);
    }

    @NotNull
    public BooleanTextStyle isItalic() {
        return this.italic;
    }

    public TextStyle withItalic(@NotNull BooleanTextStyle italic) {
        requireNonNull(italic);
        if (this.italic == italic) {
            return this;
        }
        return new TextStyle(this.color, this.bold, italic, this.strikethrough, this.underlined, this.obfuscated);
    }

    @NotNull
    public BooleanTextStyle isStrikethrough() {
        return this.strikethrough;
    }

    public TextStyle withStrikethrough(@NotNull BooleanTextStyle strikethrough) {
        requireNonNull(strikethrough);
        if (this.strikethrough == strikethrough) {
            return this;
        }
        return new TextStyle(this.color, this.bold, this.italic, strikethrough, this.underlined, this.obfuscated);
    }

    @NotNull
    public BooleanTextStyle isUnderlined() {
        return this.underlined;
    }

    public TextStyle withUnderlined(@NotNull BooleanTextStyle underlined) {
        requireNonNull(underlined);
        if (this.underlined == underlined) {
            return this;
        }
        return new TextStyle(this.color, this.bold, this.italic, this.strikethrough, underlined, this.obfuscated);
    }

    @NotNull
    public BooleanTextStyle isObfuscated() {
        return this.obfuscated;
    }

    public TextStyle withObfuscated(@NotNull BooleanTextStyle obfuscated) {
        requireNonNull(obfuscated);
        if (this.obfuscated == obfuscated) {
            return this;
        }
        return new TextStyle(this.color, this.bold, this.italic, this.strikethrough, this.underlined, obfuscated);
    }

    public TextStyle withParentStyle(TextStyle parent) {
        return new TextStyle(
                this.color == INHERIT_COLOR ? parent.color : this.color,
                this.bold.applyParent(parent.bold),
                this.italic.applyParent(parent.italic),
                this.strikethrough.applyParent(parent.strikethrough),
                this.underlined.applyParent(parent.underlined),
                this.obfuscated.applyParent(parent.obfuscated)
        );
    }

    @Nullable
    public Formatting nearestFormattingColor() {
        if (this.color == null) {
            return null;
        }
        //TODO check the perf impact of this and maybe cache it
        return stream(Formatting.values())
                .filter(Formatting::isColor)
                .min(comparing(c -> {
                    float red = c.color.redf() - this.color.redf();
                    float green = c.color.greenf() - this.color.greenf();
                    float blue = c.color.bluef() - this.color.bluef();
                    return (red * red) + (green * green) + (blue * blue);
                })).orElseThrow(IllegalStateException::new);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(RESET);
        if (this.color != null) {
            if (TextStyle.gameSupportsRgb) {
                builder.append(this.color.asHtmlHexString());
            } else {
                builder.append(this.nearestFormattingColor());
            }
        }
        if (this.bold == TRUE) {
            builder.append(BOLD);
        }
        if (this.italic == TRUE) {
            builder.append(ITALIC);
        }
        if (this.strikethrough == TRUE) {
            builder.append(STRIKETHROUGH);
        }
        if (this.underlined == TRUE) {
            builder.append(UNDERLINE);
        }
        if (this.obfuscated == TRUE) {
            builder.append(OBFUSCATED);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextStyle textStyle = (TextStyle) o;

        if (!Objects.equals(color, textStyle.color)) return false;
        if (bold != textStyle.bold) return false;
        if (italic != textStyle.italic) return false;
        if (strikethrough != textStyle.strikethrough) return false;
        if (underlined != textStyle.underlined) return false;
        return obfuscated == textStyle.obfuscated;
    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + bold.hashCode();
        result = 31 * result + italic.hashCode();
        result = 31 * result + strikethrough.hashCode();
        result = 31 * result + underlined.hashCode();
        result = 31 * result + obfuscated.hashCode();
        return result;
    }

    public static void enableRgbSupport() {
        TextStyle.gameSupportsRgb = true;
    }

    public static void disableRgbSupport() {
        TextStyle.gameSupportsRgb = false;
    }

}
