package fr.thesmyler.smylibgui.widgets;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.screen.Screen;

public interface IWidget {

	/**
	 * @return the x position of this widget in the parent container
	 */
	public int getX();

	/**
	 * @return the y position of this widget in the parent container
	 */
	public int getY();

	/**
	 * @return the z position of this widget in the parent container
	 */
	public int getZ();
	
	/**
	 * @return the width of this widget
	 */
	public int getWidth();

	/**
	 * @return the height of this widget
	 */
	public int getHeight();
	
	/**
	 * This method should draw the widget on the screen
	 * It is the only method in this interface which takes as parameter the absolute position relative to the Minecraft widow
	 * 
	 * @param x the x position where to draw the widget on the screen, can be different than this.getX()
	 * @param y the y position where to draw the widget on the screen, can be different than this.getY()
	 * @param mouseX the mouse's x position on the screen
	 * @param mouseY the mouse's y position on the screen
	 * @param focused indicates whether or not this widget has its parent's focus (it will get keystrokes and so on)
	 * @param parent the parent Screen
	 */
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, @Nullable Screen parent);
	
	/**
	 * If this returns false, this widget will not be rendered and or notified of user actions
	 * 
	 * @param parent screen
	 * 
	 * @return true if the widget is visible
	 */
	public default boolean isVisible(Screen parent) {
		return true;
	}
	
	/**
	 * If this returns false, this widget will not be notified of user actions
	 * 
	 * @return true if this widget needs to process user inputs
	 */
	public default boolean takesInputs() {
		return true;
	}

	/**
	 * Called when the user clicks this widget
	 * Can only be called if the widget is visible and takes inputs
	 * 
	 * @param mouseX mouse x position relative to the widget's origin
	 * @param mouseY mouse y position relative to the widget's origin
	 * @param mouseButton
	 * @param parent screen
	 * 
	 * @see #isVisible(Screen)
	 * @see #takesInputs()
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return true;
	}
	
	/**
	 * Called when the user clicks anywhere else on the parent of this widget
	 * This will be called whether or not the widget takes inputs or is visible
	 * 
	 * @param mouseX mouse x position relative to the parent's origin
	 * @param mouseY mouse y position relative to the parent's origin
	 * @param mouseButton
	 * @param parent the containing screen that was clicked. It may not be the direct parent
	 * @param parent screen
	 * 
	 * @see #isVisible(Screen)
	 * @see #takesInputs()
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onParentClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return true;
	}
	
	/**
	 * Called when the user double clicks this widget
	 * Can only be called if the widget is visible and takes inputs
	 * 
	 * @param mouseX mouse x position relative to the widget's origin
	 * @param mouseY mouse y position relative to the widget's origin
	 * @param mouseButton
	 * @param parent screen
	 * 
	 * @see #isVisible(Screen)
	 * @see #takesInputs()
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}
	
	/**
	 * Called when the user double clicks anywhere else on the parent of this widget
	 * This will be called whether or not the widget takes inputs or is visible
	 * 
	 * @param mouseX position relative to the parent's origin
	 * @param mouseY position relative to the parent's origin
	 * @param mouseButton
	 * @param parent the containing screen that was clicked. It may not be the direct parent
	 * @param parent screen
	 * 
	 * @see #isVisible(Screen)
	 * @see #takesInputs()
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onParentDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return this.onParentClick(mouseX, mouseY, mouseButton, parent);
	}
	
	/**
	 * Called when this widget is being dragged by the user
	 * 
	 * @param dX
	 * @param dY
	 * @param mouseButton
	 * @param parent screen
	 */
	public default void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, @Nullable Screen parent) {}

	/**
	 * Called when the mouse is released over this widget
	 * 
	 * @param mouseX position relative to the parent's origin
	 * @param mouseY position relative to the parent's origin
	 * @param button
	 * @param draggedWidget the widget the user was dragging and drop on this widget
	 * @param parent screen
	 * 
	 */
	public default void onMouseReleased(int mouseX, int mouseY, int button, @Nullable IWidget draggedWidget) {}
	
	/**
	 * Called between the time the events are processed and the screen is drawn
	 * 
	 * @param parent screen
	 */
	public default void onUpdate(@Nullable Screen parent) {}
	
	/**
	 * Called when a key is typed
	 * 
	 * @param typedChar
	 * @param keyCode
	 * @param parent screen
	 */
	public default void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {}
	
	/**
	 * Called when the mouse is over this widget and the wheel is turned
	 * 
	 * @param mouseX position relative to the widget
	 * @param mouseY position relative to the widget
	 * @param parent screen
	 * @param amount
	 */
	public default boolean onMouseWheeled(int mouseX, int mouseY, int amount, @Nullable Screen parent) {
		return true;
	}
	
	/**
	 * @return the text to draw as a tooltip when this widget is hovered, or null if no tooltip should be displayed
	 */
	@Nullable public default String getTooltipText() {
		return null;
	}
	
	/**
	 * 
	 * @return the delay for which the mouse needs to stay over the widget without moving before the tooltip is displayed in milliseconds
	 */
	public default long getTooltipDelay() {
		return 750;
	}
	
	/**
	 * Called when the screen is closed or this widget is removed from the screen
	 */
	public default void onRemoved() {}
	
	
}
