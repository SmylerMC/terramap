package fr.smyler.terramap.maps;

import fr.smyler.terramap.GeoServices;
import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.maps.tiles.OSMFranceTile;
import fr.smyler.terramap.maps.tiles.OSMHumanitarianTile;
import fr.smyler.terramap.maps.tiles.OSMTile;
import fr.smyler.terramap.maps.tiles.WikimediaTile;

public abstract class TiledMaps {

	public static final TiledMap<WikimediaTile> WIKIMEDIA = new TiledMap<WikimediaTile>(
			TileFactory.WIKIMEDIA_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			"Wikimedia maps", "Wikimedia Foundation, © OpenStreetMap contributors",
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMTile> OSM = new TiledMap<OSMTile>(
			TileFactory.OSM_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			"OpenStreetMap", "© OpenStreetMap contributors",
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMHumanitarianTile> OSM_HUMANITARIAN = new TiledMap<OSMHumanitarianTile>(
			TileFactory.OSM_HUMANITARIAN_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			"OpenStreetMap Humanitarian", "© OpenStreetMap contributors, Humanitarian OpenStreetMap Team",
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMFranceTile> OSM_FRANCE = new TiledMap<OSMFranceTile>(
			TileFactory.OSM_FRANCE_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			"OpenStreetMap France", "© OpenStreetMap contributors, OSM France",
			GeoServices.OSM_CR_LINK
	);
	
}
