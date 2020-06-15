package fr.thesmyler.terramap.maps.utils;

import fr.thesmyler.terramap.maps.TileFactory;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.maps.tiles.GenericURLRasterWebTile;

public class GenericURLTiledMap extends TiledMap<GenericURLRasterWebTile> {

	public GenericURLTiledMap(String urlPattern, int minZoom, int maxZoom, int maxLoaded, String name, String copyright, String copyrightURL) {
		super(TileFactory.getGenericURLTileFactory(urlPattern), minZoom, maxZoom, maxLoaded, name, copyright, copyrightURL);
	}
	
	public GenericURLTiledMap(String urlPattern) {
		this(urlPattern, 0, 19, 120, "", "", "");
	}

}
