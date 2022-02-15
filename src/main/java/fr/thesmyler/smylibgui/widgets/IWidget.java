package fr.thesmyler.smylibgui.widgets;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.devices.Key;

//TODO make it possible to stop user inputs without processing them
public interface IWidget {

    /**
     * @return the x position of this widget in the parent container
     */
    float getX();

    /**
     * @return the y position of this widget in the parent container
     */
    float getY();

    /**
     * @return the z position of this widget in the parent container
     */
    int getZ();

    /**
     * @return the width of this widget
     */
    float getWidth();

    /**
     * @return the height of this widget
     */
    float getHeight();

    /**
     * This method should draw the widget on the screen.
     * It is the only method in this interface which takes as parameter the absolute position relative to the Minecraft widow.
     * 
     * @param x         the x position where to draw the widget on the screen, can be different than this.getX()
     * @param y         the y position where to draw the widget on the screen, can be different than this.getY()
     * @param mouseX    the mouse's x position on the screen
     * @param mouseY    the mouse's y position on the screen
     * @param focused   indicates whether this widget has its parent's focus (it will get keystrokes and so on)
     * @param parent    the parent Screen
     */
    void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, @Nullable WidgetContainer parent);

    /**
     * If this returns false, this widget will not be rendered and or notified of user actions
     * 
     * @param parent screen
     * 
     * @return true if the widget is visible
     */
    default boolean isVisible(WidgetContainer parent) {
        return true;
    }

    /**
     * If this returns false, this widget will not be notified of user actions.
     * 
     * @return true if this widget needs to process user inputs
     */
    default boolean takesInputs() {
        return true;
    }

    /**
     * Called when the user clicks this widget.
     * Can only be called if the widget is visible and takes inputs.
     * 
     * @param mouseX        mouse x position relative to the widget's origin
     * @param mouseY        mouse y position relative to the widget's origin
     * @param mouseButton   the button which is clicked
     * @param parent        the parent widget container
     * 
     * @see #isVisible(WidgetContainer)
     * @see #takesInputs()
     * 
     * @return a boolean indicating whether this event should propagate to widgets with lower priorities
     */
    default boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return true;
    }

    /**
     * Called when the user clicks anywhere else on the parent of this widget.
     * This will be called whether the widget takes inputs or is visible.
     * 
     * @param mouseX        mouse x position relative to the parent's origin
     * @param mouseY        mouse y position relative to the parent's origin
     * @param mouseButton   the button which was clicked
     * @param parent        the containing screen that was clicked. It may not be the direct parent
     *
     * @see #isVisible(WidgetContainer)
     * @see #takesInputs()
     *
     * @return a boolean indicating whether this event should propagate to widgets with lower priorities
     */
    default boolean onParentClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return true;
    }

    /**
     * Called when this widget is clicked but doesn't accept inputs.
     * In contrary to other methods, returning false to this one will not focus this widget.
     *
     * @param mouseX        mouse x position relative to the parent's origin
     * @param mouseY        mouse y position relative to the parent's origin
     * @param mouseButton   the button which was clicked
     * @param parent        the parent widget container
     *
     * @return whether to propagate the event
     */
    default boolean onClickedNotInput(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return true;
    }

    /**
     * Called when the user double clicks this widget.
     * Can only be called if the widget is visible and takes inputs.
     * 
     * @param mouseX        mouse x position relative to the widget's origin
     * @param mouseY        mouse y position relative to the widget's origin
     * @param mouseButton   the button which was clicked
     * @param parent        screen
     * 
     * @see #isVisible(WidgetContainer)
     * @see #takesInputs()
     * 
     * @return a boolean indicating whether this event should propagate to widgets with lower priorities
     */
    default boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    /**
     * Called when the user double clicks anywhere else on the parent of this widget.
     * This will be called whether the widget takes inputs or is visible.
     * 
     * @param mouseX        position relative to the parent's origin
     * @param mouseY        position relative to the parent's origin
     * @param mouseButton   the button which was clicked
     * @param parent        the widget container that was clicked. It may not be the direct parent
     *
     * @see #isVisible(WidgetContainer)
     * @see #takesInputs()
     * 
     * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
     */
    default boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return this.onParentClick(mouseX, mouseY, mouseButton, parent);
    }

    /**
     * Called when this widget is being dragged by the user.
     *
     * @param mouseX            the current X position of the mouse
     * @param mouseY            the current Y position of the mouse
     * @param dX                the X offset of the new position relative to the old one
     * @param dY                the Y offset of the new position relative to the old one
     * @param mouseButton       the button which is being pressed
     * @param parent            the widget container
     * @param timeSinceLastMove the time since this method was last called for the same press, in milliseconds
     */
    default void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long timeSinceLastMove) {}

    /**
     * Called when the mouse is released over this widget.
     * 
     * @param mouseX        position relative to the parent's origin
     * @param mouseY        position relative to the parent's origin
     * @param button        the button which was released
     * @param draggedWidget the widget the user was dragging and drop on this widget
     *
     */
    default void onMouseReleased(float mouseX, float mouseY, int button, @Nullable IWidget draggedWidget) {}

    /**
     * Called between the time the events are processed and the screen is drawn.
     *
     * @param mouseX    the X position of the mouse
     * @param mouseY    the Y position of the mouse
     * @param parent    the parent widget container
     */
    default void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {}

    /**
     * Called when a key is typed.
     * 
     * @param typedChar     the character represented by the key that was pressed on the current keyboard layout
     * @param key           the physical key that was pressed
     * @param parent        the parent widget container
     */
    default void onKeyTyped(char typedChar, Key key, @Nullable WidgetContainer parent) {}

    /**
     * Called when the mouse is over this widget and the wheel is turned
     * 
     * @param mouseX    position relative to the widget
     * @param mouseY    position relative to the widget
     * @param parent    the parent widget container
     * @param amount    how much was the wheel turned
     */
    default boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
        return true;
    }

    /**
     * @return the text to draw as a tooltip when this widget is hovered, or null if no tooltip should be displayed
     */
    @Nullable default String getTooltipText() {
        return null;
    }

    /**
     * 
     * @return the delay for which the mouse needs to stay over the widget without moving before the tooltip is displayed in milliseconds
     */
    default long getTooltipDelay() {
        return 750;
    }

    /**
     * Called when the screen is closed or this widget is removed from the screen
     */
    default void onRemoved() {}

}
