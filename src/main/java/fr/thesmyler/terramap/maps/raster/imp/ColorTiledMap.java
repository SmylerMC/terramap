package fr.thesmyler.terramap.maps.raster.imp;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.SmyLibGuiContext;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.raster.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import fr.thesmyler.terramap.util.ImageUtil;
import fr.thesmyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class ColorTiledMap extends CachingRasterTiledMap<ColorTile> {

    private final Color color;
    private final String name;
    private final ResourceLocation textureLocation;

    public ColorTiledMap(Color color, String name) {
        this.color = color;
        this.name = name;
        if (SmyLibGui.getContext() != SmyLibGuiContext.JUNIT) {
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            DynamicTexture texture = new DynamicTexture(ImageUtil.imageFromColor(256, 256, this.color.asRGBInt()));
            this.textureLocation = textureManager.getDynamicTextureLocation(
                    TerramapMod.MODID + ":color_tile_" + this.color.asHexString(), texture);
        } else {
            this.textureLocation = null;
        }
    }

    @Override
    protected ColorTile createNewTile(TilePosImmutable pos) {
        return new ColorTile(pos, this.textureLocation);
    }

    @Override
    public int getMinZoom() {
        return 0;
    }

    @Override
    public int getMaxZoom() {
        return 25;
    }

    @Override
    public String getId() {
        return "color_" + this.name;
    }

    @Override
    public String getLocalizedName(String localeKey) {
        return this.name;
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public TiledMapProvider getProvider() {
        return TiledMapProvider.INTERNAL;
    }

    @Override
    public long getProviderVersion() {
        return 0;
    }

    @Override
    public int getDisplayPriority() {
        return 0;
    }

    @Override
    public boolean isAllowedOnMinimap() {
        return true;
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    public Color getColor() {
        return this.color;
    }

}
