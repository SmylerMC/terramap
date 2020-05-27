package fr.thesmyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.thesmyler.terramap.TerramapMod;

public class OSMTile extends RasterWebTile {

	public OSMTile(int zoom, long x, long y) {
		super(256, zoom, x, y);
	}

	@Override
	public URL getURL() {
		URL url = null;
		try {
			url = new URL("https://tile.openstreetmap.org/" +
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
