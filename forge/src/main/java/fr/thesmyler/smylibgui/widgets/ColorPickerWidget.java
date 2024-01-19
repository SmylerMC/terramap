package fr.thesmyler.smylibgui.widgets;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import net.minecraft.client.renderer.GlStateManager;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.Font;

import java.util.Optional;
import java.util.function.Consumer;

import static net.smyler.smylib.Color.*;

/**
 * A simple widgets that allows the user to input a color.
 * The current version is nothing more than a text field with a color preview.
 *
 * @author Smyler
 */
public class ColorPickerWidget extends TextFieldWidget {

    private Color color;
    private boolean hasValidColor = true;
    private Consumer<Optional<Color>> onColorChange = c -> {};

    /**
     * Constructs a new {@link ColorPickerWidget color picker widget}.
     *
     * @param x             the x position of this widget on the parent container
     * @param y             the y position of this widget on the parent container
     * @param z             the z index of this widget on the parent container
     * @param defaultColor  the color this {@link ColorPickerWidget color picker} initially shows
     * @param font          the font to render the text of the widget
     */
    public ColorPickerWidget(float x, float y, int z, Color defaultColor, Font font) {
        super(x, y, z, font.height() * 0.5f * 16 + font.height(), defaultColor.asHtmlHexString(), s -> {}, s -> false, s -> true, 9, font);
        this.setOnChangeCallback(s -> {});
        this.color = defaultColor;
    }

    private void updateColor(String text) {
        if (isValidHexColorCode(text)) {
            this.color = fromHtmlHexString(text);
            this.setEnabledTextColor(MEDIUM_GRAY);
            this.setFocusedTextColor(LIGHT_GRAY);
            this.setDisabledTextColor(DARK_GRAY);
            this.hasValidColor = true;
            this.onColorChange.accept(Optional.of(this.color));
        } else {
            this.setFocusedTextColor(RED);
            this.setEnabledTextColor(RED);
            this.setDisabledTextColor(RED);
            if (this.hasValidColor) {
                this.onColorChange.accept(Optional.empty());
            }
            this.hasValidColor = false;
        }
    }

    /**
     * Provides access to this {@link ColorPickerWidget color picker}'s last valid color.
     *
     * @return the current user choice of {@link Color}
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Sets the current chosen color of this {@link ColorPickerWidget color picker}.
     *
     * @param color the new color for this {@link ColorPickerWidget color picker}
     */
    public void setColor(Color color) {
        super.setText(color.asHtmlHexString());
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        super.draw(context, x, y, mouseX, mouseY, hovered, focused, parent);
        context.glState().enableAlpha();
        GlStateManager.enableBlend();
        float width = this.getWidth();
        float height = this.getHeight();
        context.drawRectangle(x + width - height, y, x + width, y + height, this.color);
        Color borderColor = hovered && this.isEnabled() ? this.getBorderColorHovered(): this.getBorderColorNormal();
        float colorSeparatorX = x + width - height;
        context.drawStrokeLine(borderColor, 1f,
                colorSeparatorX, y,
                colorSeparatorX, y + this.getHeight());
    }

    /**
     * Indicates whether this {@link ColorPickerWidget color picker}'s text is a valid color hex code.
     * If this returns false, calls to {@link #getColor()} will return the last valid color.
     *
     * @return whether the current text input is a valid HTML color hex code
     */
    public boolean hasValidColor() {
        return this.hasValidColor;
    }

    @Override
    public TextFieldWidget setOnChangeCallback(Consumer<String> onChangeCallback) {
        return super.setOnChangeCallback(t -> {
            this.updateColor(t);
            if (onChangeCallback != null) {
                onChangeCallback.accept(t);
            }
        });
    }

    /**
     * Provide access to this {@link ColorPickerWidget color picker}'s color change callback.
     *
     * @see #setOnColorChange(Consumer) for details on the excpected callback
     *
     * @return a callback to be called when the color changes
     */
    public Consumer<Optional<Color>> getOnColorChange() {
        return this.onColorChange;
    }

    /**
     * Sets a function that will be called when the color input of this {@link ColorPickerWidget color picker} changes.
     * As the user may enter an invalid color code, this color may be an empty {@link Optional}.
     *
     * @see #getColor() to get the last valid color
     *
     */
    public void setOnColorChange(Consumer<Optional<Color>> onColorChange) {
        this.onColorChange = onColorChange;
    }

}
