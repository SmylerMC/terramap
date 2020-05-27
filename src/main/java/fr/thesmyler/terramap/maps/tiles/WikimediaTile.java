package fr.thesmyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.thesmyler.terramap.TerramapMod;


public class WikimediaTile extends RasterWebTile {

	public WikimediaTile(int zoom, long x, long y) {
		super(256, zoom, x, y);
	}

	@Override
	public URL getURL() {
		try {
			return new URL("https://maps.wikimedia.org/osm-intl/" +this.getZoom() + "/" + this.getX() + "/" + this.getY() + ".png");
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Generated an invalid URL for a map tile. This should not happend and is likely to crash the game. Please tell the dev at " + TerramapMod.AUTHOR_EMAIL);
			TerramapMod.logger.catching(e);
			return null;
		}
	}

}
