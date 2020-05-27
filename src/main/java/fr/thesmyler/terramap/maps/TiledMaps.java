package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.maps.tiles.OSMFranceTile;
import fr.thesmyler.terramap.maps.tiles.OSMHumanitarianTile;
import fr.thesmyler.terramap.maps.tiles.OSMTile;
import fr.thesmyler.terramap.maps.tiles.OpenTopoMapTile;
import fr.thesmyler.terramap.maps.tiles.TerrainTile;
import fr.thesmyler.terramap.maps.tiles.WatercolorTile;
import fr.thesmyler.terramap.maps.tiles.WikimediaTile;
import net.minecraft.client.resources.I18n;

public abstract class TiledMaps {

	public static final TiledMap<WikimediaTile> WIKIMEDIA = new TiledMap<WikimediaTile>(
			TileFactory.WIKIMEDIA_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.wikimedia"), I18n.format("terramap.maps.copyright.wikimedia"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMTile> OSM = new TiledMap<OSMTile>(
			TileFactory.OSM_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.osm"), I18n.format("terramap.maps.copyright.osm"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMHumanitarianTile> OSM_HUMANITARIAN = new TiledMap<OSMHumanitarianTile>(
			TileFactory.OSM_HUMANITARIAN_TILE_FACTORY,
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.humanitarian"), I18n.format("terramap.maps.copyright.humanitarian"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OSMFranceTile> OSM_FRANCE = new TiledMap<OSMFranceTile>(
			TileFactory.OSM_FRANCE_TILE_FACTORY,
			0, 20,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.osmfr"), I18n.format("terramap.maps.copyright.osmfr"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<OpenTopoMapTile> OPEN_TOPO = new TiledMap<OpenTopoMapTile>(
			TileFactory.OPEN_TOPO_TILE_FACTORY,
			0, 17,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.opentopo"), I18n.format("terramap.maps.copyright.opentopo"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<WatercolorTile> WATERCOLOR = new TiledMap<WatercolorTile>(
			TileFactory.WATERCOLOR_TILE_FACTORY,
			0, 14,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.watercolor"), I18n.format("terramap.maps.copyright.watercolor"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final TiledMap<TerrainTile> TERRAIN = new TiledMap<TerrainTile>(
			TileFactory.TERRAIN_TILE_FACTORY,
			0, 18,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.terrain"), I18n.format("terramap.maps.copyright.terrain"),
			GeoServices.OSM_CR_LINK
	);
	
}
