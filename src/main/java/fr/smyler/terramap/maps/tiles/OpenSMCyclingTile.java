package fr.smyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.smyler.terramap.TerramapMod;

/**
 * @author SmylerMC
 *
 *
 */
public class OpenSMCyclingTile extends RasterWebTile {

	/**
	 * @param size
	 * @param x
	 * @param y
	 * @param zoom
	 */
	public OpenSMCyclingTile(int zoom, long x, long y) {
		super(256, zoom, x, y);
	}

	@Override
	public URL getURL() {
		//TODO API key...
		URL url = null;
		try {
			url = new URL("https://b.tile.thunderforest.com/cycle/" +
					this.zoom + "/" +
					this.x + "/" +
					this.y + ".png");
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Failed to craft a valid URL for a tile, please report to the mod author: " + TerramapMod.AUTHOR_EMAIL);
			TerramapMod.logger.catching(e);
		}
		return url;
	}

}
