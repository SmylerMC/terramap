package fr.smyler.terramap.maps;

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
		public OpenSMCyclingTile getInstance(long x, long y, int zoom) {
			return new OpenSMCyclingTile(x, y, zoom);
		}
	};
	
	public static final TileFactory<WikimediaTile> 
	WIKIMEDIA_TILE_FACTORY = new TileFactory<WikimediaTile>() {
		@Override
		public WikimediaTile getInstance(long x, long y, int zoom) {
			return new WikimediaTile(x, y, zoom);
		}
	};


	public T getInstance(long x, long y, int zoom);

}
