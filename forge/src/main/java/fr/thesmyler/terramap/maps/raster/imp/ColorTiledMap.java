package fr.thesmyler.terramap.maps.raster.imp;

import net.smyler.smylib.Color;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.raster.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import net.smyler.terramap.util.ImageUtil;
import net.smyler.terramap.util.geo.TilePosImmutable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import static net.smyler.smylib.SmyLib.getGameClient;

public class ColorTiledMap extends CachingRasterTiledMap<ColorTile> {

    private final Color color;
    private final String name;
    private final ResourceLocation textureLocation;

    public ColorTiledMap(Color color, String name) {
        this.color = color;
        this.name = name;
        if (getGameClient().isGlAvailabale()) {
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

    @Override
    public ResourceLocation getDefaultTileTexture() {
        return this.textureLocation;
    }

    public Color getColor() {
        return this.color;
    }

}
