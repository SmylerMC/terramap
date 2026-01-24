package net.smyler.terramap.tilesets.raster;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.ImageUtil;
import net.smyler.terramap.geo.TilePosImmutable;

import java.awt.image.BufferedImage;

import static net.smyler.smylib.SmyLib.getGameClient;

public class ColorTileSet extends CachingRasterTileSet {

    private final Color color;
    private final String name;
    private final Identifier textureLocation;

    public ColorTileSet(Color color, String name) {
        this.color = color;
        this.name = name;
        if (getGameClient().isGlAvailabale()) {
            BufferedImage image = ImageUtil.imageFromColor(256, 256, this.color.asRGBInt());
            this.textureLocation = getGameClient().guiDrawContext().loadDynamicTexture(image);
        } else {
            this.textureLocation = null;
        }
    }

    @Override
    protected RasterTile createNewTile(TilePosImmutable pos) {
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
    public RasterTileSetProvider getProvider() {
        return RasterTileSetProvider.INTERNAL;
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
    public Identifier getDefaultTileTexture() {
        return this.textureLocation;
    }

    public Color getColor() {
        return this.color;
    }

    private static class ColorTile implements RasterTile {

        private final TilePosImmutable position;
        private final Identifier texture;

        public ColorTile(TilePosImmutable position, Identifier texture) {
            this.position = position;
            this.texture = texture;
        }

        @Override
        public boolean isTextureAvailable() {
            return this.texture != null;
        }

        @Override
        public Identifier getTexture() {
            return this.texture;
        }

        @Override
        public void cancelTextureLoading() {
        }

        @Override
        public void unloadTexture() {
            // Nop, we don't want to do that !
        }

        @Override
        public TilePosImmutable getPosition() {
            return this.position;
        }

    }
}
