package fr.thesmyler.smylibgui.devices;

/**
 * A wrapper around whatever mouse is being used internally.
 *
 * @author SmylerMC
 */
public interface Mouse {

    /**
     * @return the X position of the mouse cursor on the game window, taking GUI scaling into account
     */
    float getX();

    /**
     * @return the X position of the mouse cursor on the game window, taking GUI scaling into account
     */
    float getY();

    /**
     * @return the number of physical buttons on the mouse
     */
    int getButtonCount();

    /**
     * @return whether this mouse has a wheel
     */
    boolean hasWheel();

    /**
     * Checks whether a given mouse button is pressed.
     *
     * @param button    the button to check
     *
     * @return whether the given button is being pressed
     *
     * @throws IllegalArgumentException if the given button does not exist
     */
    boolean isButtonPressed(int button) throws IllegalArgumentException;

    /**
     * Gets the human friendly name of a mouse button.
     *
     * @param button    the id button to get the name of
     *
     * @return the human friendly name of the given button, or null if it is unnamed
     *
     * @throws IllegalArgumentException if the given button does not exist
     */
    String getButtonName(int button) throws IllegalArgumentException;

    /**
     * Gets the numerical id of a button from its human friendly name.
     *
     * @param name  the human friendly name of the button
     *
     * @return the id of the given button
     *
     * @throws IllegalArgumentException if the given name does not exist
     * @throws NullPointerException if the given name is null
     */
    int getButtonByName(String name) throws IllegalArgumentException;

}
