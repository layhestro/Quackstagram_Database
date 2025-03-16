package com.quackstagram.util;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for applying filters to images
 */
public class ImageFilterUtil {
    
    private static final Map<String, Function<BufferedImage, BufferedImage>> FILTERS = new HashMap<>();
    
    static {
        FILTERS.put("None", image -> image);
        FILTERS.put("Grayscale", ImageFilterUtil::applyGrayscale);
        FILTERS.put("Sepia", ImageFilterUtil::applySepia);
        FILTERS.put("Invert", ImageFilterUtil::applyInvert);
        FILTERS.put("Blur", ImageFilterUtil::applyBlur);
        FILTERS.put("Sharpen", ImageFilterUtil::applySharpen);
        FILTERS.put("Warm", ImageFilterUtil::applyWarm);
        FILTERS.put("Cool", ImageFilterUtil::applyCool);
    }
    
    /**
     * Gets all available filter names
     * 
     * @return array of filter names
     */
    public static String[] getFilterNames() {
        return FILTERS.keySet().toArray(new String[0]);
    }
    
    /**
     * Applies a named filter to an image
     * 
     * @param image the image to filter
     * @param filterName the name of the filter to apply
     * @return the filtered image, or the original if the filter name is not recognized
     */
    public static BufferedImage applyFilter(BufferedImage image, String filterName) {
        Function<BufferedImage, BufferedImage> filter = FILTERS.getOrDefault(filterName, img -> img);
        return filter.apply(image);
    }
    
    /**
     * Applies a grayscale filter
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applyGrayscale(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                
                int newRGB = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
    
    /**
     * Applies a sepia filter
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applySepia(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                
                int newR = (int)(0.393 * r + 0.769 * g + 0.189 * b);
                int newG = (int)(0.349 * r + 0.686 * g + 0.168 * b);
                int newB = (int)(0.272 * r + 0.534 * g + 0.131 * b);
                
                newR = Math.min(255, newR);
                newG = Math.min(255, newG);
                newB = Math.min(255, newB);
                
                int newRGB = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
    
    /**
     * Applies an invert filter
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applyInvert(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = 255 - ((rgb >> 16) & 0xff);
                int g = 255 - ((rgb >> 8) & 0xff);
                int b = 255 - (rgb & 0xff);
                
                int newRGB = (alpha << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
    
    /**
     * Applies a blur filter
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applyBlur(BufferedImage source) {
        float[] blurKernel = {
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f,
            1/9f, 1/9f, 1/9f
        };
        
        Kernel kernel = new Kernel(3, 3, blurKernel);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }
    
    /**
     * Applies a sharpen filter
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applySharpen(BufferedImage source) {
        float[] sharpenKernel = {
             0, -1,  0,
            -1,  5, -1,
             0, -1,  0
        };
        
        Kernel kernel = new Kernel(3, 3, sharpenKernel);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }
    
    /**
     * Applies a warm filter (increase red, decrease blue)
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applyWarm(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                
                r = Math.min(255, r + 30);
                b = Math.max(0, b - 20);
                
                int newRGB = (alpha << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
    
    /**
     * Applies a cool filter (increase blue, decrease red)
     * 
     * @param source the source image
     * @return the filtered image
     */
    private static BufferedImage applyCool(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                
                r = Math.max(0, r - 20);
                b = Math.min(255, b + 30);
                
                int newRGB = (alpha << 24) | (r << 16) | (g << 8) | b;
                result.setRGB(x, y, newRGB);
            }
        }
        
        return result;
    }
}