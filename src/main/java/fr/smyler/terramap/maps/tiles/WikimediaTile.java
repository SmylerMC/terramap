/* IRL World Minecraft Mod
    Copyright (C) 2017  Smyler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
	along with this program. If not, see <http://www.gnu.org/licenses/>.

	The author can be contacted at smyler@mail.com

	File created on 13 avr. 2018 
*/
package fr.smyler.terramap.maps.tiles;

import java.net.MalformedURLException;
import java.net.URL;

import fr.smyler.terramap.TerramapMod;

/**
 * @author SmylerMC
 *
 * TODO Type comment
 *
 */
public class WikimediaTile extends RasterWebTile {

	/**
	 * @param size
	 * @param x
	 * @param y
	 * @param zoom
	 * @param defaultPixel
	 */
	public WikimediaTile(long x, long y, int zoom) {
		super(255, x, y, zoom);
	}

	@Override
	public URL getURL() {
		// TODO Auto-generated method stub
		try {
			return new URL("https://maps.wikimedia.org/osm-intl/" +this.getZoom() + "/" + this.getX() + "/" + this.getY() + ".png");
		} catch (MalformedURLException e) {
			TerramapMod.logger.error("Generated an invalid URL for a map tile. This should not happend and is likely to crash the game. Please tell the dev at " + TerramapMod.AUTHOR_EMAIL);
			TerramapMod.logger.catching(e);
			return null;
		}
	}

}
