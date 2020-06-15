package fr.thesmyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.thesmyler.terramap.TerramapMod;

public class GenericURLRasterWebTile extends RasterWebTile {
	
	private String urlPattern;
	
	public GenericURLRasterWebTile(String url, int zoom, long x, long y) {
		super(256, zoom, x, y);
		this.urlPattern = url;
	}

	@Override
	public URL getURL() {
		try {
			return new URL(
					this.urlPattern
						.replace("{x}", ""+this.getX())
						.replace("{y}", ""+this.getY())
						.replace("{z}", ""+this.getZoom()));
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Failed to craft url for a generic url tile. URL pattern is " + this.urlPattern);
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getFileName(){
		URL u = this.getURL();
		u.getPath();
		return u.getHost() + u.getPath().replace('/', '.'); //TODO Make sure this is always a valid file name
	}
	
}
