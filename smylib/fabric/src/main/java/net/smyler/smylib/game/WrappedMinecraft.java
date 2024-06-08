package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.smyler.smylib.gui.*;
import net.smyler.smylib.gui.popups.Popup;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.screen.VanillaScreenProxy;
import net.smyler.smylib.gui.screen.WrappedVanillaScreen;
import net.smyler.smylib.gui.sprites.SpriteLibrary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;


public class WrappedMinecraft implements GameClient {

    private final Minecraft client;
    private final Mouse mouse;
    private WrappedFont font;
    private WrappedGuiGraphics uiDrawContext;
    private final SpriteLibrary spriteLibrary = new VanillaSprites();

    private WrappedVanillaScreen lastAccessedVanillaScreen = null;

    public WrappedMinecraft(Minecraft client) {
        this.client = client;
        this.mouse = new Lwjgl3Mouse(this.client);
    }

    @Override
    public float windowWidth() {
        return this.client.getWindow().getGuiScaledWidth();
    }

    @Override
    public float windowHeight() {
        return this.client.getWindow().getGuiScaledHeight();
    }

    @Override
    public int nativeWindowWidth() {
        return this.client.getWindow().getWidth();
    }

    @Override
    public int nativeWindowHeight() {
        return this.client.getWindow().getHeight();
    }

    @Override
    public int scaleFactor() {
        //TODO double check
        return (int)this.client.getWindow().getGuiScale();
    }

    @Override
    public boolean isMac() {
        return Minecraft.ON_OSX;
    }

    @Override
    public Path gameDirectory() {
        //TODO double-check
        return this.client.gameDirectory.toPath();
    }

    @Override
    public @Nullable MinecraftServerInfo currentServerInfo() {
        return null;  //TODO
    }

    @Override
    public Mouse mouse() {
        return this.mouse;
    }

    @Override
    public Keyboard keyboard() {
        return null;  //TODO
    }

    @Override
    public Clipboard clipboard() {
        return null;  //TODO
    }

    @Override
    public SoundSystem soundSystem() {
        return null; //TODO
    }

    @Override
    public Translator translator() {
        return null;  //TODO
    }

    @Override
    public Font defaultFont() {
        if (this.font == null) {
            this.font = new WrappedFont(1f, this.client.font);
        }
        return this.font;
    }

    @Override
    public WrappedGuiGraphics guiDrawContext() {
        return this.uiDrawContext;
    }

    public void setUidDrawContext(WrappedGuiGraphics uiDrawContext) {
        this.uiDrawContext = uiDrawContext;
        this.font.setVanillaGraphics(uiDrawContext.vanillaGraphics);
    }

    @Override
    public SpriteLibrary sprites() {
        return this.spriteLibrary;  //TODO register sprites
    }

    @Override
    public boolean isGlAvailabale() {
        return false;  //TODO
    }

    @Override
    public void displayScreen(Screen screen) {
        net.minecraft.client.gui.screens.Screen vanillaScreen;
        if (screen instanceof WrappedVanillaScreen wrappedScreen) {
            vanillaScreen = wrappedScreen.getWrapped();
            this.lastAccessedVanillaScreen = wrappedScreen;
        } else {
            vanillaScreen = new VanillaScreenProxy(screen);
        }
        this.client.setScreen(vanillaScreen);
    }

    @Override
    public Screen getCurrentScreen() {
        net.minecraft.client.gui.screens.Screen currentVanillaScreen = this.client.screen;
        if (currentVanillaScreen instanceof VanillaScreenProxy proxy) {
            return proxy.getScreen();
        }
        // If it is not a SmyLib screen avoid creating a new wrapper each time
        if (this.lastAccessedVanillaScreen != null && currentVanillaScreen == this.lastAccessedVanillaScreen.getWrapped()) {
            return this.lastAccessedVanillaScreen;
        }
        this.lastAccessedVanillaScreen = new WrappedVanillaScreen(currentVanillaScreen);
        return this.lastAccessedVanillaScreen;
    }

    @Override
    public void displayPopup(@NotNull Popup popup) {
        //TODO
    }

    @Override
    public @Nullable Popup getTopPopup() {
        return null;
    }

    @Override
    public @Nullable Popup closeTopPopup() {
        return null;  //TODO
    }

    @Override
    public int closeAllPopups() {
        return 0;  //TODO
    }

    @Override
    public int currentFPS() {
        return 0;  //TODO
    }
}
