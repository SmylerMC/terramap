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

	File created on 3 mars 2018 
*/
package fr.smyler.terramap.maps.utils;

/**
 * @author SmylerMC
 *
 * TODO Type comment
 *
 */
public class TerramapUtils {
	
	public static int modulus(int a, int b) {
		int mod = a%b;
		return mod<0?b+mod:mod ;
	}

	/**
	 * 
	 * @param x
	 * @return The integer just before x
	 */
	public static int roudSmaller(float x){
		return x >= 0? (int) x: (int) x - 1;
	}
	
	/**
	 * 
	 * @param x
	 * @return The integer just before x
	 */
	public static int roudSmaller(double x){
		return x >= 0? (int) x: (int) x - 1;
	}
	
}
