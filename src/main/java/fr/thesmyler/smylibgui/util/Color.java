package fr.thesmyler.smylibgui.util;

import org.lwjgl.opengl.GL11;

import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Locale;

import static java.lang.Integer.toHexString;
import static java.lang.Long.parseLong;

public class Color {

    private static final int MASK = 0xFF;

    private int color;

    public Color(int color) {
        this.color = color;
    }

    public Color(int red, int green, int blue, int alpha) {
        PValidation.checkArg(
                red >= 0 && red < 256 && 
                green >= 0 && green < 256 &&
                blue >= 0 && blue < 256 &&
                alpha >= 0 && alpha < 256,
                String.format("Invalid color operand, R=%s, G=%s, B=%s, A=%s", red, green, blue, alpha));
        this.color = blue;
        this.color += green << 8;
        this.color += red << 16;
        this.color += alpha << 24;
    }

    public Color(int red, int green, int blue) {
        this(red, green, blue, 255);
    }

    public Color(float red, float green, float blue, float alpha) {
        this(Math.round(red*255), Math.round(green*255), Math.round(blue*255), Math.round(alpha*255));
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public int encoded() {
        return this.color;
    }

    public int red() {
        return (this.color >> 16) & MASK;
    }

    public int green() {
        return (this.color >> 8) & MASK;
    }

    public int blue() {
        return this.color & MASK;
    }

    public int alpha() {
        return (this.color >> 24) & MASK;
    }

    public float redf() {
        return this.red() / 255f;
    }

    public float greenf() {
        return this.green() / 255f;
    }

    public float bluef( ) {
        return this.blue() / 255f;
    }

    public float alphaf() {
        return this.alpha() / 255f;
    }

    public Color withRed(int red) {
        return new Color(red, this.green(), this.blue(), this.alpha());
    }

    public Color withGreen(int green) {
        return new Color(this.red(), green, this.blue(), this.alpha());
    }

    public Color withBlue(int blue) {
        return new Color(this.red(), this.green(), blue, this.alpha());
    }

    public Color withAlpha(int alpha) {
        return new Color(this.red(), this.green(), this.blue(), alpha);
    }

    public Color withRed(float red) {
        return new Color(red, this.greenf(), this.bluef(), this.alphaf());
    }

    public Color withGreen(float green) {
        return new Color(this.redf(), green, this.bluef(), this.alphaf());
    }

    public Color withBlue(float blue) {
        return new Color(this.redf(), this.greenf(), blue, this.alphaf());
    }

    public Color withAlpha(float alpha) {
        return new Color(this.redf(), this.greenf(), this.bluef(), alpha);
    }
    
    public void applyGL() {
        GlStateManager.color(this.redf(), this.greenf(), this.bluef(), this.alphaf());
    }

    public int asInt() {
        return this.color;
    }

    public int[] asIntArray() {
        return new int[] {
                this.alpha(),
                this.red(),
                this.green(),
                this.blue()
        };
    }

    public int[] asRGBInt() {
        return new int[] {
                this.red(),
                this.green(),
                this.blue()
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Color color1 = (Color) o;
        return color == color1.color;
    }

    @Override
    public int hashCode() {
        return this.color;
    }

    public String asHexString() {
        return toHexString(this.asInt()).toUpperCase(Locale.ROOT);
    }

    public String asHtmlHexString() {
        StringBuilder builder = new StringBuilder(toHexString(this.color & 0xFFFFFF));
        for (int i = builder.length(); i < 6; i++) {
            builder.insert(0, '0');
        }
        builder.insert(0, '#');
        if (this.alpha() < 0xF) {
            builder.append("0");
        }
        if (this.alpha() < 0xFF) {
            builder.append(toHexString(this.alpha()));
        }
        return builder.toString().toUpperCase(Locale.ROOT);
    }

    /**
     * Parses an HTML color code.
     *
     * @param text  the text to parse
     * @return      the corresponding color
     * @throws      NumberFormatException if the hex code is invalid according to {@link #isValidHexColorCode(String)}
     */
    public static Color fromHtmlHexString(String text) {
        if (!isValidHexColorCode(text)) {
            throw new NumberFormatException("Invalid HTML color code: " + text);
        }
        long value = parseLong(text.substring(1), 16);
        if (text.length() == 9) {
            value = (value >>> 8) | (value << 24);
        } else {
            value |= 0xFF000000L;
        }
        return new Color((int)value);
    }

    public static boolean isValidHexColorCode(String text) {
        if (!text.startsWith("#")) {
            return false;
        }
        if (text.length() != 7 && text.length() != 9) {
            return false;
        }
        for (int i = 1; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean validHexChar =
                    ('0' <= c && c <= '9')
                            || ('A' <= c && c <= 'F')
                            || ('a' <= c && c <= 'f');
            if (!validHexChar) {
                return false;
            }
        }
        return true;
    }

    public static final Color RED = new Color(0xFFFF0000);
    public static final Color GREEN = new Color(0xFF00FF00);
    public static final Color BLUE = new Color(0xFF0000FF);
    public static final Color YELLOW = new Color(0xFFFFCC00);

    public static final Color WHITE = new Color(0xFFFFFFFF);
    public static final Color BLACK = new Color(0xFF000000);
    public static final Color LIGHT_GRAY = new Color(0xFFE0E0E0);
    public static final Color MEDIUM_GRAY = new Color(0xFFA0A0A0);
    public static final Color DARK_GRAY = new Color(0xFF707070);
    public static final Color DARKER_GRAY = new Color(0xFF404040);
    public static final Color SELECTION = new Color(0xFFFFFFA0);

    public static final Color TRANSPARENT = WHITE.withAlpha(0);
    public static final Color LIGHT_OVERLAY = BLACK.withAlpha(.25f);
    public static final Color DARK_OVERLAY = BLACK.withAlpha(.5f);
    public static final Color DARKER_OVERLAY = BLACK.withAlpha(.75f);
    public static final Color ERROR_OVERLAY = new Color(0xC0600000);

    public static Color fromHSL(float h, float s, float l){
        float r, g, b;

        if(s == 0){
            r = g = b = l; // Achromatic
        }else{
            float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hue2rgb(p, q, h + 1f/3f);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1f/3f);
        }

        return new Color(r, g, b);
    }
    
    public static Color currentGL() {
       return new Color(GL11.glGetInteger(GL11.GL_CURRENT_COLOR));
    }

    public static float hue2rgb(float p, float q, float r){
        float t = r;
        if(t < 0) t += 1;
        if(t > 1) t -= 1;
        if(t < 1f/6f) return p + (q - p) * 6f * t;
        if(t < 1f/2f) return q;
        if(t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }

}
