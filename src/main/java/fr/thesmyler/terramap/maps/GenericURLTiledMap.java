package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.maps.tiles.GenericURLRasterWebTile;

public class GenericURLTiledMap extends TiledMap<GenericURLRasterWebTile> {

	public GenericURLTiledMap(String urlPattern, int minZoom, int maxZoom, int maxLoaded, String name, String copyright, String copyrightURL) {
		super(TileFactory.getGenericURLTileFactory(urlPattern), minZoom, maxZoom, maxLoaded, name, copyright, copyrightURL);
	}
	
	public GenericURLTiledMap(String urlPattern) {
		this(urlPattern, 0, 19, 120, "", "", "");
	}

}
