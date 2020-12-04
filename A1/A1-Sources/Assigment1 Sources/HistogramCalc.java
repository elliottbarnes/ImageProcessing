import javax.imageio.*;
import java.util.*;
import java.awt.color.*;
import java.awt.image.*;
import java.awt.*;


public class HistogramCalc 
{
    public static float[][] getNormalizedHistograms(BufferedImage image)
    {
        int[] redHistogram = new int[256];
        Arrays.fill(redHistogram, 0);
        int[] greenHistogram = new int[256];
        Arrays.fill(greenHistogram, 0);
        int[] blueHistogram = new int[256];
        Arrays.fill(blueHistogram, 0);
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                redHistogram[pixelColor.getRed()] += 1;
                greenHistogram[pixelColor.getGreen()] += 1;
                blueHistogram[pixelColor.getBlue()] += 1;
            }
        }
        float[] normalizedRedHistogram = new float[256];
        float[] normalizedGreenHistogram = new float[256];
        float[] normalizedBlueHistogram = new float[256];
        int totalPixels = image.getWidth() * image.getHeight();
        for(int i = 0; i < 256; i++)
        {
            normalizedRedHistogram[i] = (float) (redHistogram[i] * 100) / totalPixels;
            normalizedGreenHistogram[i] = (float) (greenHistogram[i] * 100) / totalPixels;
            normalizedBlueHistogram[i] = (float) (blueHistogram[i] * 100) / totalPixels;
        }
        return new float[][]
        {
            normalizedRedHistogram,
            normalizedGreenHistogram,
            normalizedBlueHistogram
        };
    }

    /**
     * Method to return whether or not 
     */
    public static boolean isGreyscale(BufferedImage image)
    {
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                int red = pixelColor.getRed();
                int blue = pixelColor.getBlue();
                int green = pixelColor.getGreen();
                if((red != blue) || (blue != green))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static float[] getGreyscaleHistogram(BufferedImage image)
    {
        float[] greyscaleHistogram = new float[256];
        Arrays.fill(greyscaleHistogram, 0);
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                greyscaleHistogram[pixelColor.getBlue()]++;
            }
        }
        int totalPixels = image.getWidth() * image.getHeight();
        for(int i = 0; i < 255; i++)
        {
            greyscaleHistogram[i] = (float) (greyscaleHistogram[i] * 100 / totalPixels);
        }
        return greyscaleHistogram;
    }
}
