package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.util.geo.GeoPointImmutable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class RightClickMarker extends AbstractFixedMarker {

    public RightClickMarker(MarkerController<?> controller) {
        super(controller, 15, 23, GeoPointImmutable.ORIGIN);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGuiTextures.WIDGET_TEXTURES);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.color(1f, 1f, 1f);
        RenderUtil.drawTexturedModalRect(x, y, 0, hovered? 126:94, 15, 27);
    }

    @Override
    public float getDeltaX() {
        return -8;
    }

    @Override
    public float getDeltaY() {
        return -23;
    }

    @Override
    public void update(MapWidget map) {
        this.setLocation(map.getMouseLocation());
    }

    @Override
    public boolean canBeTracked() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString("Right click marker");
    }

    @Override
    public String getIdentifier() {
        return this.getControllerId() + ":0";
    }

}
