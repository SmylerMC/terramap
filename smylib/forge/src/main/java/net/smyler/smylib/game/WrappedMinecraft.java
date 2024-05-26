package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smyler.smylib.gui.*;
import net.smyler.smylib.gui.screen.GuiScreenProxy;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.screen.TestScreen;
import net.smyler.smylib.gui.screen.WrappedVanillaScreen;
import net.smyler.smylib.gui.sprites.SpriteLibrary;

import java.nio.file.Path;

public class WrappedMinecraft implements GameClient {

    private final Minecraft minecraft;
    private float width = 1f;
    private float height = 1f;
    private int nativeWidth = 1;
    private int nativeHeight = 1;
    private int scale = 1;
    private boolean showTestScreen = false;

    private final Mouse mouse = new Lwjgl2Mouse();
    private final Keyboard keyboard = new Lwjgl2Keyboard();
    private final Clipboard clipboard = new AwtClipboard();
    private final SoundSystem soundSystem = new MinecraftSoundSystem();
    private final Translator translator = new I18nTranslator();
    private final Font font = new ReflectedFontRenderer(1f, 0.5f);
    private final DrawContext drawContext = new Lwjgl2DrawContext();
    private final SpriteLibrary sprites = new LegacyVanillaSprites();

    private WrappedVanillaScreen lastAccessedVanillaScreen = null;

    public WrappedMinecraft(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public float windowWidth() {
        return this.width;
    }

    @Override
    public float windowHeight() {
        return this.height;
    }

    @Override
    public int nativeWindowWidth() {
        return this.nativeWidth;
    }

    @Override
    public int nativeWindowHeight() {
        return this.nativeHeight;
    }

    @Override
    public int scaleFactor() {
        return this.scale;
    }

    @Override
    public boolean isMac() {
        return Minecraft.IS_RUNNING_ON_MAC;
    }

    @Override
    public Path gameDirectory() {
        return this.minecraft.gameDir.toPath();
    }

    @Override
    public MinecraftServerInfo currentServerInfo() {
        ServerData data = this.minecraft.getCurrentServerData();
        return data == null ? null: new MinecraftServerInfo(
                data.serverName,
                data.serverIP,
                data.serverMOTD,
                data.isOnLAN()
        );
    }

    @Override
    public Mouse mouse() {
        return this.mouse;
    }

    @Override
    public Keyboard keyboard() {
        return this.keyboard;
    }

    @Override
    public Clipboard clipboard() {
        return this.clipboard;
    }

    @Override
    public SoundSystem soundSystem() {
        return this.soundSystem;
    }

    @Override
    public Translator translator() {
        return this.translator;
    }

    @Override
    public Font defaultFont() {
        return this.font;
    }

    @Override
    public DrawContext guiDrawContext() {
        return this.drawContext;
    }

    @Override
    public SpriteLibrary sprites() {
        return this.sprites;
    }

    @Override
    public boolean isGlAvailabale() {
        return true;
    }

    @Override
    public void displayScreen(Screen screen) {
        GuiScreen vanillaScreen;
        if (screen instanceof WrappedVanillaScreen) {
            WrappedVanillaScreen wrapped = (WrappedVanillaScreen) screen;
            vanillaScreen = wrapped.getWrapped();
            this.lastAccessedVanillaScreen = wrapped;
        } else {
            vanillaScreen = new GuiScreenProxy(screen);
        }
        this.minecraft.displayGuiScreen(vanillaScreen);
    }

    @Override
    public Screen getCurrentScreen() {
        GuiScreen currentGuiScreen = this.minecraft.currentScreen;
        if (this.lastAccessedVanillaScreen != null && currentGuiScreen == this.lastAccessedVanillaScreen.getWrapped()) {
            return this.lastAccessedVanillaScreen;
        }
        this.lastAccessedVanillaScreen = new WrappedVanillaScreen(currentGuiScreen);
        return this.lastAccessedVanillaScreen;
    }

    @Override
    public int currentFPS() {
        return Minecraft.getDebugFPS();
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
        ScaledResolution res = new ScaledResolution(this.minecraft);
        this.scale = res.getScaleFactor();
        this.width = (float) res.getScaledWidth_double();
        this.height = (float) res.getScaledHeight_double();
        this.nativeWidth = this.minecraft.displayWidth;
        this.nativeHeight = this.minecraft.displayHeight;
    }

    @SubscribeEvent
    public void onGuiScreenInit(GuiScreenEvent.InitGuiEvent event) {
        if (!this.showTestScreen) {
            return;
        }
        if(!(event.getGui() instanceof GuiScreenProxy)) {
            this.displayScreen(new TestScreen(new WrappedVanillaScreen(event.getGui())));
            this.showTestScreen = false;
        }
    }

    public void showTestScreen() {
        this.showTestScreen = true;
    }

}
