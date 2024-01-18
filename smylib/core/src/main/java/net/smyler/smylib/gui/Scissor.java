package net.smyler.smylib.gui;

/**
 * Limits rendering to a specific part of the screen.
 *
 * @author Smyler
 */
public interface Scissor {

    /**
     * Enables or disable scissoring entirely.
     *
     * @param yesNo whether scissoring should be enabled
     */
    void setEnabled(boolean yesNo);

    /**
     * @return whether scissoring is enabled
     */
    boolean isEnabled();

    /**
     * Crop the entire screen to a given section.
     * Any rendering done after this call will only affect that region.
     *
     * @param x         upper left corner X coordinate of the zone in Minecraft screen space
     * @param y         upper left corner Y coordinate of the zone in Minecraft screen space
     * @param width     width of the zone in Minecraft screen space
     * @param height    height of the zone in Minecraft screen space
     */
    void cropScreen(float x, float y, float width, float height);

    /**
     * Crop the existing scissoring section.
     * The resulting scissoring section will be the intersection of the current scissoring section with the new one.
     *
     * @param x         upper left corner X coordinate of the zone in Minecraft screen space
     * @param y         upper left corner Y coordinate of the zone in Minecraft screen space
     * @param width     width of the zone in Minecraft screen space
     * @param height    height of the zone in Minecraft screen space
     */
    void cropSection(float x, float y, float width, float height);

    /**
     * Pushes the current scissoring state to an internal stack.
     */
    void push();

    /**
     * Reset scissoring state and zone to what they were last time {@link #push()} was called.
     * Removes the corresponding frame from the internal stack.
     */
    void pop();

}
