//Assignment3
//Elliott Barnes(201561735) & Zachary Smith(201825478)
import java.awt.image.*;
import java.awt.*;

public class HistogramCalc 
{
    
    public static float[][] getHistograms(BufferedImage image, boolean normalize)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int totalPixels = width * height;

        float[][] gsHistogram = new float[256][1];

        boolean greyscale = true;
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                if(pixelColor.getRed() != pixelColor.getGreen() || pixelColor.getGreen() != pixelColor.getBlue())
                {
                    greyscale = false;
                    break;
                }
                else
                {
                    gsHistogram[pixelColor.getRed()][0]++;
                }
            }
        }
        if(greyscale)
        {
            return (normalize) ? normalizeAndScale(gsHistogram, totalPixels) : gsHistogram;
            
        }
        float[][] histograms = new float[256][3];

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                histograms[pixelColor.getRed()][0] += 1;
                histograms[pixelColor.getGreen()][1] += 1;
                histograms[pixelColor.getBlue()][2] += 1;
            }
        }
        //Normalizing
        return (normalize) ? normalizeAndScale(histograms, totalPixels) : histograms;
        
    }
    
    private static float[][] normalizeAndScale(float[][] histo, int totalPixels)
    {
        for(int i = 0; i < 256; i++)
        {
            for(int j = 0; j < histo[0].length; j++)
            {
                histo[i][j] /= (totalPixels);
            }
        }
        float max[] = new float[histo[0].length];
        int maxHeight = 200;
        for(int i = 0; i < 256; i++)
        {
            for(int j = 0; j < histo[0].length; j++)
            {
                if(max[j] < histo[i][j])
                {
                    max[j] = histo[i][j];
                }
            }
        }
        float scaleFactor[] = new float[histo[0].length];
        for(int i = 0; i < scaleFactor.length; i++)
        {
            scaleFactor[i] = max[i] / maxHeight;
        }
        for(int i = 0; i < 256; i++)
        {
            for(int j = 0; j < histo[0].length; j++)
            {
                histo[i][j] /= scaleFactor[j];
            }
        }
        return histo;
    }
    
}