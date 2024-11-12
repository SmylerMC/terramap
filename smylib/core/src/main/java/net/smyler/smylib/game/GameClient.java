package net.smyler.smylib.game;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Cursor;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.Font;
import net.smyler.smylib.gui.popups.Popup;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.sprites.SpriteLibrary;
import net.smyler.smylib.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A wrapper around the main game object.
 *
 * @author Smyler
 */
public interface GameClient {

    /**
     * @return the version of the game
     */
    String gameVersion();

    /**
     * @return the current mod loader (e.g. Forge, Fabric...)
     */
    String modLoader();

    /**
     * @return the effective width of the window when scaling is taken into account
     */
    float windowWidth();

    /**
     * @return the effective height of the window when scaling is taken into account
     */
    float windowHeight();

    /**
     * @return the width of the native window
     */
    int nativeWindowWidth();

    /**
     * @return the height of the native window
     */
    int nativeWindowHeight();

    /**
     * @return the scale factor applied
     */
    int scaleFactor();

    /**
     * MacOS does weird things, we may want to account for them.
     *
     * @return whether Minecraft has detected that the underlying system is macOS
     */
    boolean isMac();

    /**
     * The game directory is where all game files are saved (e.g. ~/.minecraft on unix based OS).
     *
     * @return the game directory
     */
    Path gameDirectory();

    /**
     * Get information on the server which is currently being played on.
     *
     * @return the server info, or null if not currently playing on a server
     */
    @Nullable
    MinecraftServerInfo currentServerInfo();

    Mouse mouse();

    Keyboard keyboard();

    Clipboard clipboard();

    /**
     * Accessor for the current custom cursor.
     * {@link Cursor} objects returned by the method are short-lived:
     * they become irrelevant and unreliable when resources are reloaded.
     *
     * @return  the current cursor,
     *          or an empty {@link Optional} when using the default system cursor
     *          or if the cursor was set by means other than SmyLib
     */
    Optional<Cursor> cursor();

    /**
     * Sets a custom cursor using its resource identifier.
     *
     * @param identifier    a namespaced identifier for the cursor to set
     */
    void setCursor(@Nullable Identifier identifier);

    /**
     * Gets a cursor object from its {@link Identifier identifier}.
     * {@link Cursor} objects returned by the method are short-lived:
     * they become irrelevant and unreliable when resources are reloaded.
     *
     * @param identifier    the identifier of the cursor
     * @return              the cursor with the requested identifier
     */
    Optional<Cursor> getCursor(Identifier identifier);

    SoundSystem soundSystem();

    Translator translator();

    Font defaultFont();

    default Font smallestFont() {
        return this.defaultFont().withScale(1f / this.scaleFactor());
    }

    UiDrawContext guiDrawContext();

    /**
     * Get the sprites for this client.
     * This offers an abstraction over the vanilla texture and sprite systems,
     * which have changed from one version to another.
     *
     * @return the known vanilla sprites from this client
     */
    SpriteLibrary sprites();

    boolean isGlAvailabale();

    void displayScreen(Screen screen);

    Screen getCurrentScreen();

    /**
     * Display a popup on top of the current screen.
     * Multiple popup's may be shown at once, in which case they stack on top of each other.
     *
     * @param popup the popup to show
     */
    void displayPopup(@NotNull Popup popup);

    /**
     * @return the top popup, or null if no popup is displayed
     */
    @Nullable Popup getTopPopup();

    /**
     * Closes the top popup, if one is opened.
     * If no popup is being displayed, nothing is done.
     *
     * @return the popup that was closed, or null if no popup was opened
     */
    @Nullable Popup closeTopPopup();

    /**
     * Close all open popups.
     *
     * @return the number of popups that were closed
     */
    int closeAllPopups();

    int currentFPS();

    /**
     * Exposes resources loaded from resource packs.
     *
     * @param resource  the resource identifier
     * @return          the specified resource, if present
     */
    Optional<Resource> getResource(Identifier resource);

}
