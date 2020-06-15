package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.maps.utils.GenericURLTiledMap;
import net.minecraft.client.resources.I18n;

//FIXME Localization on class load is bad
public abstract class TiledMaps {

	public static final GenericURLTiledMap WIKIMEDIA = new GenericURLTiledMap(
			"https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png",
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.wikimedia"), I18n.format("terramap.maps.copyright.wikimedia"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap OSM = new GenericURLTiledMap(
			"https://tile.openstreetmap.org/{z}/{x}/{y}.png",
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.osm"), I18n.format("terramap.maps.copyright.osm"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap OSM_HUMANITARIAN = new GenericURLTiledMap(
			"https://tile.openstreetmap.fr/hot/{z}/{x}/{y}.png",
			0, 19,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.humanitarian"), I18n.format("terramap.maps.copyright.humanitarian"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap OSM_FRANCE = new GenericURLTiledMap(
			"https://tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png",
			0, 20,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.osmfr"), I18n.format("terramap.maps.copyright.osmfr"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap OPEN_TOPO = new GenericURLTiledMap(
			"https://tile.opentopomap.org/{z}/{x}/{y}.png",
			0, 17,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.opentopo"), I18n.format("terramap.maps.copyright.opentopo"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap WATERCOLOR = new GenericURLTiledMap(
			"http://tile.stamen.com/watercolor/{z}/{x}/{y}.png",
			0, 14,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.watercolor"), I18n.format("terramap.maps.copyright.watercolor"),
			GeoServices.OSM_CR_LINK
	);
	
	public static final GenericURLTiledMap TERRAIN = new GenericURLTiledMap(
			"http://tile.stamen.com/terrain/{z}/{x}/{y}.png",
			0, 18,
			TerramapConfiguration.maxTileLoad,
			I18n.format("terramap.maps.name.terrain"), I18n.format("terramap.maps.copyright.terrain"),
			GeoServices.OSM_CR_LINK
	);
	
}
