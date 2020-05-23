package fr.smyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.smyler.terramap.TerramapMod;

public class TerrainTile extends RasterWebTile {
	
	public TerrainTile(int zoom, long x, long y) {
		super(256, zoom, x, y);
	}
	
	@Override
	public URL getURL() {
		URL url = null;
		try {
			url = new URL("http://tile.stamen.com/terrain/"
					+ this.getZoom() + "/" + this.getX() + "/" + this.getY() + ".jpg");
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Failed to craft a valid URL for a tile, please report to the mod author: " + TerramapMod.AUTHOR_EMAIL);
			TerramapMod.logger.catching(e);
		}
		return url;
	}

}
