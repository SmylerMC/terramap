package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.*;
import net.smyler.smylib.gui.popups.Popup;
import net.smyler.smylib.gui.screen.PopupScreenImplementation;
import net.smyler.smylib.gui.screen.*;
import net.smyler.smylib.gui.screen.test.TestScreen;
import net.smyler.smylib.gui.sprites.SpriteLibrary;
import net.smyler.smylib.resources.*;
import net.smyler.smylib.resources.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue;
import static net.smyler.smylib.Preconditions.checkState;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.SmyLib.getLogger;

public class WrappedMinecraft implements GameClient {

    private final Minecraft minecraft;
    private final MetadataSerializer metadataSerializer;
    private static final String SRG_metadataSerializer = "field_110452_an";
    private float width = 1f;
    private float height = 1f;
    private int nativeWidth = 1;
    private int nativeHeight = 1;
    private int scale = 1;
    private boolean showTestScreen = true;

    private final Mouse mouse = new Lwjgl2Mouse();
    private final Keyboard keyboard = new Lwjgl2Keyboard();
    private final Clipboard clipboard = new AwtClipboard();
    private final SoundSystem soundSystem = new MinecraftSoundSystem();
    private final Translator translator = new I18nTranslator();
    private final Font font = new ReflectedFontRenderer(1f, 0.5f);
    private final UiDrawContext uiDrawContext = new Lwjgl2UiDrawContext();
    private final SpriteLibrary sprites = new LegacyVanillaSprites();

    private CursorManager<?> cursorManager;

    private WrappedVanillaScreen lastAccessedVanillaScreen = null;

    public WrappedMinecraft(Minecraft minecraft) {
        this.minecraft = minecraft;

        MetadataSerializer metadataSerializer;
        try {
            metadataSerializer = getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), SRG_metadataSerializer);
        } catch (Exception e) {
            SmyLib.getLogger().error("Encountered an error when accessing vanilla metadata serializer. Features that depend on custom resource metadata might get disabled");
            SmyLib.getLogger().catching(e);
            metadataSerializer = null;
        }
        this.metadataSerializer = metadataSerializer;

        if (this.isMac()) {
            SmyLib.getLogger().warn("Not enabling cursor support as we are running on macOS");
        } else if (!this.isGlAvailabale()) {
            SmyLib.getLogger().warn("Not enabling cursor support as GL is not available");
        } else {
            this.cursorManager = new Lwjgl2CursorManager();
        }
        if (this.cursorManager == null) {
            this.cursorManager = new DummyCursorManager();
        }
        IResourceManager resourceManager = minecraft.getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager) resourceManager;
            reloadableResourceManager.registerReloadListener(r -> {
                try {
                    this.cursorManager.reload();
                } catch (Exception e) {
                    SmyLib.getLogger().error("Failed to reload cursor manager");
                    SmyLib.getLogger().catching(e);
                }
            });
        } else {
            SmyLib.getLogger().warn("Vanilla resource manager does not support reloading, will not reload cursors on resource reload");
        }
    }

    public void init() {
        if (SmyLib.isDebug()) {
            getLogger().info("Registering debug resource metadata");
            this.metadataSerializer.registerMetadataSectionType(new DebugMetadataSerializer(), DebugMetadataSection.class);
        }
        this.metadataSerializer.registerMetadataSectionType(new GuiMetadataSerializer(), GuiMetadataSection.class);
        this.metadataSerializer.registerMetadataSectionType(new VillagerMetadataSerializer(), VillagerMetadataSection.class);
        this.metadataSerializer.registerMetadataSectionType(new CursorMetadataSectionSerializer(), CursorMetadataSection.class);
    }

    @Override
    public String gameVersion() {
        return this.minecraft.getVersion();
    }

    @Override
    public String modLoader() {
        return "Forge";
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
    public Optional<Cursor> cursor() {
        try {
            return Optional.ofNullable(this.cursorManager.getCurrent());
        } catch (Exception e) {
            SmyLib.getLogger().warn("Encountered an exception when getting current mouse cursor. Disabling support.");
            SmyLib.getLogger().catching(e);
            this.cursorManager = new DummyCursorManager();
            return Optional.empty();
        }
    }

    @Override
    public void setCursor(@Nullable Identifier identifier) {
        try {
            this.cursorManager.set(identifier);
        } catch (Exception e) {
            SmyLib.getLogger().warn("Encountered an exception when setting mouse cursor. Disabling support.");
            SmyLib.getLogger().catching(e);
            this.cursorManager = new DummyCursorManager();
        }
    }

    @Override
    public Optional<Cursor> getCursor(Identifier identifier) {
        try {
            return Optional.ofNullable(this.cursorManager.get(identifier));
        } catch (Exception e) {
            SmyLib.getLogger().warn("Encountered an exception when getting a mouse cursor: '{}'. Disabling support.", identifier);
            SmyLib.getLogger().catching(e);
            this.cursorManager = new DummyCursorManager();
            return Optional.empty();
        }
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
    public UiDrawContext guiDrawContext() {
        return this.uiDrawContext;
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
        if (screen == null) {
            vanillaScreen = null;
        } else if (screen instanceof WrappedVanillaScreen) {
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
        if (currentGuiScreen == null) {
            return null;
        }
        if (currentGuiScreen instanceof GuiScreenProxy) {
            return ((GuiScreenProxy)currentGuiScreen).getScreen();
        }
        // If it is not a SmyLib screen avoid creating a new wrapper each time
        if (this.lastAccessedVanillaScreen != null && currentGuiScreen == this.lastAccessedVanillaScreen.getWrapped()) {
            return this.lastAccessedVanillaScreen;
        }
        this.lastAccessedVanillaScreen = new WrappedVanillaScreen(currentGuiScreen);
        return this.lastAccessedVanillaScreen;
    }

    @Override
    public void displayPopup(@NotNull Popup popup) {
        final PopupScreen screen = new PopupScreenImplementation(this.minecraft.currentScreen, popup);
        Object o = new Object() {
            @SubscribeEvent
            public void onPostGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
                WrappedMinecraft.this.displayScreen(screen);
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        };
        MinecraftForge.EVENT_BUS.register(o);
    }

    @Override
    public Popup getTopPopup() {
        Screen currentScreen = getGameClient().getCurrentScreen();
        if (!(currentScreen instanceof PopupScreen)) {
            return null;
        }
        PopupScreen popupScreen = (PopupScreen) currentScreen;
        return popupScreen.getPopup();
    }

    @Override
    public Popup closeTopPopup() {
        Screen currentScreen = this.getCurrentScreen();
        if (!(currentScreen instanceof PopupScreen)) {
            return null;
        }
        checkState(
            currentScreen instanceof PopupScreenImplementation,
            "Illegal PopupScreen implementation: " + currentScreen.getClass().getCanonicalName()
        );
        PopupScreenImplementation popupScreen = (PopupScreenImplementation) currentScreen;
        this.minecraft.displayGuiScreen(popupScreen.getBackgroundScreen());
        return popupScreen.getPopup();
    }

    @Override
    public int closeAllPopups() {
        int i = 0;
        while(this.getCurrentScreen() instanceof PopupScreen) {
            this.closeTopPopup();
            i++;
        }
        return i;
    }

    @Override
    public int currentFPS() {
        return Minecraft.getDebugFPS();
    }

    @Override
    public Optional<Resource> getResource(Identifier resource) {
        try {
            IResource vanilla = this.minecraft.getResourceManager().getResource(new ResourceLocation(resource.namespace, resource.path));
            return Optional.of(new WrappedResource(resource, vanilla));
        } catch (IOException ignored) {}
        return Optional.empty();
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
        if (!SmyLib.isDebug()) {
            return;
        }
        if(!(event.getGui() instanceof GuiScreenProxy) && this.showTestScreen) {
            this.displayScreen(new TestScreen(new WrappedVanillaScreen(event.getGui())));
            this.showTestScreen = false;
        }
    }

}
