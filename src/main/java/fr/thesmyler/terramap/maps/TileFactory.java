package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.maps.tiles.GenericURLRasterWebTile;
import fr.thesmyler.terramap.maps.tiles.RasterWebTile;

/**
 * @author SmylerMC
 *
 */
public interface TileFactory <T extends RasterWebTile>{

	public T getInstance(int zoom, long x, long y);
	
	public static TileFactory<GenericURLRasterWebTile> getGenericURLTileFactory(String urlPattern) {
		return new TileFactory<GenericURLRasterWebTile>() {
			@Override
			public GenericURLRasterWebTile getInstance(int zoom, long x, long y) {
				return new GenericURLRasterWebTile(urlPattern, zoom, x, y);
			}
		};
	}

}
