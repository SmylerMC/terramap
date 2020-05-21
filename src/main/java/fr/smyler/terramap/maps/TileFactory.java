package fr.smyler.terramap.maps;

import fr.smyler.terramap.maps.tiles.OSMTile;
import fr.smyler.terramap.maps.tiles.OpenSMCyclingTile;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.tiles.WikimediaTile;

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


	public T getInstance(int zoom, long x, long y);

}
