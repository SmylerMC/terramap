package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.maps.tiles.OSMFranceTile;
import fr.thesmyler.terramap.maps.tiles.OSMHumanitarianTile;
import fr.thesmyler.terramap.maps.tiles.OSMTile;
import fr.thesmyler.terramap.maps.tiles.OpenSMCyclingTile;
import fr.thesmyler.terramap.maps.tiles.OpenTopoMapTile;
import fr.thesmyler.terramap.maps.tiles.RasterWebTile;
import fr.thesmyler.terramap.maps.tiles.TerrainTile;
import fr.thesmyler.terramap.maps.tiles.WatercolorTile;
import fr.thesmyler.terramap.maps.tiles.WikimediaTile;

/**
 * @author SmylerMC
 *
 */
public interface TileFactory <T extends RasterWebTile>{
	
	public static final TileFactory<OpenSMCyclingTile> 
	OPENSM_CYCLING_TILE_FACTORY = new TileFactory<OpenSMCyclingTile>() {
		@Override
		public OpenSMCyclingTile getInstance(int zoom, long x, long y) {
			return new OpenSMCyclingTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<WikimediaTile> 
	WIKIMEDIA_TILE_FACTORY = new TileFactory<WikimediaTile>() {
		@Override
		public WikimediaTile getInstance(int zoom, long x, long y) {
			return new WikimediaTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<OSMTile> 
	OSM_TILE_FACTORY = new TileFactory<OSMTile>() {
		@Override
		public OSMTile getInstance(int zoom, long x, long y) {
			return new OSMTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<OSMHumanitarianTile> 
	OSM_HUMANITARIAN_TILE_FACTORY = new TileFactory<OSMHumanitarianTile>() {
		@Override
		public OSMHumanitarianTile getInstance(int zoom, long x, long y) {
			return new OSMHumanitarianTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<OSMFranceTile> 
	OSM_FRANCE_TILE_FACTORY = new TileFactory<OSMFranceTile>() {
		@Override
		public OSMFranceTile getInstance(int zoom, long x, long y) {
			return new OSMFranceTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<OpenTopoMapTile> 
	OPEN_TOPO_TILE_FACTORY = new TileFactory<OpenTopoMapTile>() {
		@Override
		public OpenTopoMapTile getInstance(int zoom, long x, long y) {
			return new OpenTopoMapTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<WatercolorTile> 
	WATERCOLOR_TILE_FACTORY = new TileFactory<WatercolorTile>() {
		@Override
		public WatercolorTile getInstance(int zoom, long x, long y) {
			return new WatercolorTile(zoom, x, y);
		}
	};
	
	public static final TileFactory<TerrainTile> 
	TERRAIN_TILE_FACTORY = new TileFactory<TerrainTile>() {
		@Override
		public TerrainTile getInstance(int zoom, long x, long y) {
			return new TerrainTile(zoom, x, y);
		}
	};

	public T getInstance(int zoom, long x, long y);

}
