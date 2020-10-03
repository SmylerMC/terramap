package fr.thesmyler.terramap.maps;

/**
 * @author SmylerMC
 *
 */
public interface TileFactory <T extends WebTile>{

	public T getInstance(int zoom, long x, long y);

}
