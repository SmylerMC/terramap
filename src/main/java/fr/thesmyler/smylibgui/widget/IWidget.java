package fr.thesmyler.smylibgui.widget;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.Screen;

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
	 * @param parent the parent Screen
	 */
	public void draw(int x, int y, int mouseX, int mouseY, Screen parent);
	
	/**
	 * If this returns false, this widget will not be rendered and or notified of user actions
	 * 
	 * @return true if the widget is visible
	 */
	public default boolean isVisible() {
		return true;
	}
	
	/**
	 * If this returns false, this widget will not be notified of user actions
	 * 
	 * @return true if this widget is enabled
	 */
	public default boolean isEnabled() {
		return true;
	}

	/**
	 * Called when the user clicks this widget
	 * Can only be called if the widget is visible and enabled
	 * 
	 * @param mouseX mouse x position relative to the widget's origin
	 * @param mouseY mouse y position relative to the widget's origin
	 * @param mouseButton
	 */
	public default void onClick(int mouseX, int mouseY, int mouseButton) {}
	
	/**
	 * Called when the user clicks anywhere else on the parent of this widget
	 * This will be called whether or not the widget is enabled or visible
	 * 
	 * @param mouseX mouse x position relative to the parent's origin
	 * @param mouseY mouse y position relative to the parent's origin
	 * @param mouseButton
	 * @param parent the containing screen that was clicked. It may not be the direct parent
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onParentClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return true;
	}
	
	/**
	 * Called when the user double clicks this widget
	 * Can only be called if the widget is visible and enabled
	 * 
	 * @param mouseX mouse x position relative to the widget's origin
	 * @param mouseY mouse y position relative to the widget's origin
	 * @param mouseButton
	 */
	public default void onDoubleClick(int mouseX, int mouseY, int mouseButton) {}
	
	/**
	 * Called when the user double clicks anywhere else on the parent of this widget
	 * This will be called whether or not the widget is enabled or visible
	 * 
	 * @param mouseX position relative to the parent's origin
	 * @param mouseY position relative to the parent's origin
	 * @param mouseButton
	 * @param parent the containing screen that was clicked. It may not be the direct parent
	 * 
	 * @return a boolean indicating whether or not this event should propagate to widgets with lower priorities
	 */
	public default boolean onParentDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return true;
	}
	
	/**
	 * Called when this widget is being dragged by the user
	 * 
	 * @param dX
	 * @param dY
	 * @param mouseButton
	 */
	public default void onMouseDragged(int dX, int dY, int mouseButton) {}

	/**
	 * Called when the mouse is released over this widget
	 * 
	 * @param mouseX position relative to the parent's origin
	 * @param mouseY position relative to the parent's origin
	 * @param button
	 * @param draggedWidget the widget the user was dragging and drop on this widget
	 */
	public default void onMouseReleased(int mouseX, int mouseY, int button, @Nullable IWidget draggedWidget) {}
	
	/**
	 * Called before input events are processed and the screen is drawn
	 */
	public default void onUpdate() {} //TODO Call
	
	/**
	 * Called when a key is typed
	 * 
	 * @param typedChar
	 * @param keyCode
	 */
	public default void onKeyTyped(char typedChar, int keyCode) {} //TODO Call
	
	/**
	 * Called when the mouse is over this widget and the wheel is turned
	 * 
	 * @param mouseX position relative to the widget
	 * @param mouseY position relative to the widget
	 * 
	 * @param amount
	 */
	public default void onMouseWheeled(int mouseX, int mouseY, int amount) {} //TODO Call
	
	
}
