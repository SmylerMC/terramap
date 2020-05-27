package fr.thesmyler.terramap.maps.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author SmylerMC
 *
 */
//FIXME It conflicts with java  built ins
public class ImageUtils {

	/**
	 * Converts an integer representing a pixel value to an array
	 * Assumes the channels are one byte each
	 * 
	 * @param rgba the value to decode
	 * 
	 * @return the decoded value as an array {red, green, blue, alpha}
	 */
	public static int[] decodeRGBA2Array(int rgba) {
	    int red = (rgba >> 16) & 0xff;
	    int green = (rgba >> 8) & 0xff;
	    int blue = (rgba) & 0xff;
	    int alpha = (rgba >> 24) & 0xff;
	    int[] pixel = {red, green, blue, alpha};
	    return pixel;
	}
	
	/**
	 * Converts an array of the form {red, greeen, blue, alpha} to an integer
	 * alpha may be omitted
	 * 
	 * Values should be between 0 and 255
	 * 
	 * The length of the array should be 3 or 4
	 * 
	 * @param pixel
	 * @return The encoded value
	 */
	public static int encodeArray2RGBA(int[] pixel) {
		for (int val: pixel)
			if(val < 0 || val > 255)
				throw new IllegalArgumentException("encodeArray2RGBA excpects an array of 3 or 4 values between 0 and 255");
		if(pixel.length == 3 || pixel.length == 4) {
			int rgba = (pixel[0] << 16) + (pixel[0] << 8) + pixel[0];
			if(pixel.length == 4) rgba += (pixel[3] << 24);
			return rgba;
		}else {
			throw new ArrayIndexOutOfBoundsException("encodeArray2RGBA excpects an array of 3 or 4 values");
		}
	}
	
	
	/**
	 * Creates an image and sets it background to the specified color
	 * 
	 * @param width
	 * @param height
	 * @param color
	 * @return the new image
	 */
	public static BufferedImage imageFromColor(int width, int height, int[] color) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);		
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(new Color(color[0], color[1], color[2]));
		graphics.fillRect(0, 0, width, height);
		return img;
	}
	
}
