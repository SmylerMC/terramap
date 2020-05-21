package fr.smyler.terramap.maps;

import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.maps.tiles.OSMTile;
import fr.smyler.terramap.maps.tiles.WikimediaTile;

public abstract class TiledMaps {

	public static final TiledMap<WikimediaTile> WIKIMEDIA = new TiledMap<WikimediaTile>(
			TileFactory.WIKIMEDIA_TILE_FACTORY,
			TerramapConfiguration.maxTileLoad,
			0, 19,
			"Wikimedia maps", "Wikimedia Foundation, © OpenStreetMap contributors"
	);
	
	public static final TiledMap<OSMTile> OSM = new TiledMap<OSMTile>(
			TileFactory.OSM_TILE_FACTORY,
			TerramapConfiguration.maxTileLoad,
			0, 19,
			"OpenStreetMap", "© OpenStreetMap contributors"
	);
}
