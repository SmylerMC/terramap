package fr.thesmyler.terramap.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * @author SmylerMC
 *
 */
public final class ImageUtil {
    
    private ImageUtil() {}

    /**
     * Creates an image and sets it background to the specified color
     * 
     * @param width - width of the image
     * @param height - height of the image
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
