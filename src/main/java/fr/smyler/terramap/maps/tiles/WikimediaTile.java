package fr.smyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.smyler.terramap.TerramapMod;

/**
 * @author SmylerMC
 *
 *
 */
public class WikimediaTile extends RasterWebTile {

	/**
	 * @param size
	 * @param x
	 * @param y
	 * @param zoom
	 * @param defaultPixel
	 */
	public WikimediaTile(int zoom, long x, long y) {
		super(256, zoom, x, y);
	}

	@Override
	public URL getURL() {
		try {
			//TODO Randomize servers
			return new URL("https://maps.wikimedia.org/osm-intl/" +this.getZoom() + "/" + this.getX() + "/" + this.getY() + ".png");
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Generated an invalid URL for a map tile. This should not happend and is likely to crash the game. Please tell the dev at " + TerramapMod.AUTHOR_EMAIL);
			TerramapMod.logger.catching(e);
			return null;
		}
	}

}
