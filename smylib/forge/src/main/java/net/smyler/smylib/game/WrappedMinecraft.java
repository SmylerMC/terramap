package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smyler.smylib.gui.*;

import java.nio.file.Path;

public class WrappedMinecraft implements GameClient {

    private float width = 1f;
    private float height = 1f;
    private int nativeWidth = 1;
    private int nativeHeight = 1;
    private int scale = 1;

    private final Mouse mouse = new Lwjgl2Mouse();
    private final Keyboard keyboard = new Lwjgl2Keyboard();
    private final Clipboard clipboard = new AwtClipboard();
    private final SoundSystem soundSystem = new MinecraftSoundSystem();
    private final Translator translator = new I18nTranslator();
    private final Font font = new ReflectedFontRenderer(1f, 0.5f);
    private final DrawContext drawContext = new Lwjgl2DrawContext();
    private final SpriteLibrary sprites = new LegacyVanillaSprites();

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
        return Minecraft.getMinecraft().gameDir.toPath();
    }

    @Override
    public MinecraftServerInfo currentServerInfo() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
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

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft mc = Minecraft.getMinecraft();
        this.scale = res.getScaleFactor();
        this.width = (float) res.getScaledWidth_double();
        this.height = (float) res.getScaledHeight_double();
        this.nativeWidth = mc.displayWidth;
        this.nativeHeight = mc.displayHeight;
    }

}
