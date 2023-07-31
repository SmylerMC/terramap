package fr.thesmyler.smylibgui.devices.lwjgl2;

import fr.thesmyler.smylibgui.devices.GameContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.nio.file.Path;

public class MinecraftGameContext implements GameContext {

    private float width = 1f;
    private float height = 1f;
    private int nativeWidth = 1;
    private int nativeHeight = 1;
    private int scale = 1;

    @Override
    public float getWindowWidth() {
        return this.width;
    }

    @Override
    public float getWindowHeight() {
        return this.height;
    }

    @Override
    public int getNativeWindowWidth() {
        return this.nativeWidth;
    }

    @Override
    public int getNativeWindowHeight() {
        return this.nativeHeight;
    }

    @Override
    public int getScaleFactor() {
        return this.scale;
    }

    @Override
    public String getLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }

    @Override
    public boolean isMac() {
        return Minecraft.IS_RUNNING_ON_MAC;
    }

    @Override
    public Path getGameDirectory() {
        return Minecraft.getMinecraft().gameDir.toPath();
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft mc = Minecraft.getMinecraft();
        this.scale = res.getScaleFactor();
        this.width = (float) res.getScaledWidth_double();
        this.height = (float) res.getScaledHeight_double();
        this.nativeWidth = mc.displayWidth;
        this.nativeHeight = mc.displayHeight;
    }

}
