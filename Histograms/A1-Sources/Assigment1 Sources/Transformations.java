import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;


public class Transformations 
{
    public static float[][] negative(float[][] histograms)
    {
        float[][] negativeHistograms = new float[255][255];
        for(int i = 254; i >= 0; i--)
        {
            negativeHistograms[0][i] = histograms[0][254 - i];
            negativeHistograms[1][i] = histograms[1][254 - i];
            negativeHistograms[2][i] = histograms[2][254 - i];
        }
        return negativeHistograms;
    }

    // create greyscale image and paint the original image 

    public static BufferedImage grayScaleTransformation(BufferedImage img)
    {

        BufferedImage grayScaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics grayGraphics = grayScaleImg.getGraphics();

        // may have to set img.getWidth and img.getHeight parameters to 0 instead

        grayGraphics.drawImage(img, 0, 0, null);
        grayGraphics.dispose();
        for(int y = 0; y < grayScaleImg.getHeight(); y++)
        {
            for(int x = 0; x < grayScaleImg.getWidth(); x++)
            {
                Color pixelColor = new Color(grayScaleImg.getRGB(x,y));
                int red = pixelColor.getRed();
                int blue = pixelColor.getBlue();
                int green = pixelColor.getGreen();
                int average = (red + blue + green) / 3;
                int newRGB = average;
                newRGB = (newRGB << 8) + average;
                newRGB = (newRGB << 8) + average;
                grayScaleImg.setRGB(x,y,newRGB);
            }
        }
        return grayScaleImg;
    }

    public static BufferedImage conversionConservativeStretch(float[] histogram, BufferedImage image)
    {
        int max = 255;
        int min = 0;
        boolean minfound;
        minfound = false;
        for(int i = 0; i < 256; i++)
        {
            if(histogram[i] > 0)
            {
                if(!minfound)
                {
                    min = i;
                    minfound = true;
                }
                max = i;
            }
        }
        BufferedImage stretchedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = stretchedImage.getGraphics();
        colorGraphics.drawImage(image, 0, 0, null);
        colorGraphics.dispose();

        //Iterate over it and apply the stretch to the image
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                int red = (pixelColor.getRed() - min) * (255 / (max - min));
                if(red < 0)
                {
                    red = 0;
                }
                if(red > 255)
                {
                    red = 255;
                }
                int green = (pixelColor.getGreen() - min) * (255 / (max - min));
                if(green < 0)
                {
                    green = 0;
                }
                if(green > 255)
                {
                    green = 255;
                }
                int blue = (pixelColor.getBlue() - min) * (255 / (max - min));
                if(blue < 0)
                {
                    blue = 0;
                }
                if(blue > 255)
                {
                    blue = 255;
                }
                int newRGB = red;
                newRGB = (newRGB << 8) + green;
                newRGB = (newRGB << 8) + blue;
                stretchedImage.setRGB(x,y, newRGB);
            }
        }
        return stretchedImage;
    }
    
    public static BufferedImage greyscaleConservativeStretch(float[] histogram, BufferedImage image)
    {
        int max = 255;
        int min = 0;
        boolean minfound;
        minfound = false;
        for(int i = 0; i < 256; i++)
        {
            if(histogram[i] > 0)
            {
                if(!minfound)
                {
                    min = i;
                    minfound = true;
                }
                max = i;
            }
        }
        BufferedImage stretchedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = stretchedImage.getGraphics();
        colorGraphics.drawImage(image, 0, 0, null);
        colorGraphics.dispose();

        //Iterate over it and apply the stretch to the image
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                int average = (pixelColor.getRed() - min) * (255 / (max - min));
                int newRGB = average;
                newRGB = (newRGB << 8) + average;
                newRGB = (newRGB << 8) + average;
                stretchedImage.setRGB(x,y, newRGB);
            }
        }
        return stretchedImage;
    }

    public static BufferedImage conservativeStretch(float[][] histogram, BufferedImage image)
    {
        int[] max = new int[]{255, 255, 255};
        int[] min = new int[]{0, 0, 0};
        boolean minfound;
        for(int j = 0; j < 3; j++)
        {
            minfound = false;
            for(int i = 0; i < 256; i++)
            {
                if(histogram[j][i] > 0)
                {
                    if(!minfound)
                    {
                        min[j] = i;
                        minfound = true;
                    }
                    max[j] = i;
                }
            }
        }
        BufferedImage stretchedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = stretchedImage.getGraphics();
        colorGraphics.drawImage(image, 0, 0, null);
        colorGraphics.dispose();

        //Iterate over it and apply the stretch to the image
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                int red = (pixelColor.getRed() - min[0]) * (255 / (max[0] - min[0]));
                int green = (pixelColor.getGreen() - min[1]) * (255 / (max[1] - min[1]));
                int blue = (pixelColor.getBlue() - min[2]) * (255 / (max[2] - min[2]));
                int newRGB = red;
                newRGB = (newRGB << 8) + green;
                newRGB = (newRGB << 8) + blue;
                stretchedImage.setRGB(x,y, newRGB);
            }
        }
        return stretchedImage;
    }

    public static BufferedImage conversionAggStretch(float[] histogram, BufferedImage img, float cutoff)
    {
        int max = 255;
        int min = 0;
        float minPixSumPercent = 0;
        float maxPixSumPercent = 0;

        // define cutoff percentage
        
        float cutoffPercent = cutoff;

        // ignore ⍶% of pixels  (modified for matrix)
        for(int i = 0; i < 256; i++)                                
        {
            if(minPixSumPercent < cutoffPercent)                  // if pixelSum is less than cutoff
            {
                if(minPixSumPercent + histogram[i] >= cutoffPercent)
                {
                        //If the amount of pixels in this bucket would go over, then this is still the min
                    min = i;
                }
                minPixSumPercent += histogram[i];               // add to pixelSum
            }
            if(maxPixSumPercent < cutoffPercent)
            {
                if(maxPixSumPercent + histogram[i] >= cutoffPercent)
                {
                    max = 255 - i;
                }
                maxPixSumPercent += histogram[i];
            }
            }

        BufferedImage aggStretchedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = aggStretchedImg.getGraphics();
        colorGraphics.drawImage(img, 0, 0, null);
        colorGraphics.dispose();


        // iterate over and apply the aggressive stretch 

        for(int y=0; y<img.getHeight(); y++)
        {

            for(int x=0; x<img.getWidth(); x++){

                Color pixelColor = new Color(aggStretchedImg.getRGB(x,y));
                int red = (pixelColor.getRed() - min) * (255 / (max - min));
                if(red < 0)
                {
                    red = 0;
                }
                if(red > 255)
                {
                    red = 255;
                }
                int green = (pixelColor.getGreen() - min) * (255 / (max - min));
                if(green < 0)
                {
                    green = 0;
                }
                if(green > 255)
                {
                    green = 255;
                }
                int blue = (pixelColor.getBlue() - min) * (255 / (max - min));
                if(blue < 0)
                {
                    blue = 0;
                }
                if(blue > 255)
                {
                    blue = 255;
                }
                int newRGB = red;
                newRGB = (newRGB << 8) + green;
                newRGB = (newRGB << 8) + blue;
                aggStretchedImg.setRGB(x,y, newRGB);
            }
        }
        return aggStretchedImg;
    }

    // math/algorithm may be incorrect (need to ignore x% of pixels when calculating min&max intensities)

    public static BufferedImage aggStretch(float[][] histogram, BufferedImage img, float cutoff)
    {
        int[] max = {255,255,255};
        int[] min = {0,0,0};

        // define cutoff percentage
        
        float cutoffPercent = cutoff;

        // ignore ⍶% of pixels  (modified for matrix)

        for(int j = 0; j < 3; j++)
        {
            float minPixSumPercent = 0;
            float maxPixSumPercent = 0;
            for(int i = 0; i < 256; i++)                                
            {
                if(minPixSumPercent < cutoffPercent)                  // if pixelSum is less than cutoff
                {
                    if(minPixSumPercent + histogram[j][i] >= cutoffPercent)
                    {
                        //If the amount of pixels in this bucket would go over, then this is still the min
                        min[j] = i;
                    }
                    minPixSumPercent += histogram[j][i];               // add to pixelSum
                }
                if(maxPixSumPercent < cutoffPercent)
                {
                    if(maxPixSumPercent + histogram[j][i] >= cutoffPercent)
                    {
                        max[j] = 255 - i;
                    }
                    maxPixSumPercent += histogram[j][i];
                }
            }
        }

        BufferedImage aggStretchedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = aggStretchedImg.getGraphics();
        colorGraphics.drawImage(img, 0, 0, null);
        colorGraphics.dispose();


        // iterate over and apply the aggressive stretch 

        for(int y=0; y<img.getHeight(); y++)
        {

            for(int x=0; x<img.getWidth(); x++){

                Color pixelColor = new Color(aggStretchedImg.getRGB(x,y));
                int red = (pixelColor.getRed() - min[0]) * (255 / (max[0] - min[0]));
                if(red < 0)
                {
                    red = 0;
                }
                if(red > 255)
                {
                    red = 255;
                }
                int green = (pixelColor.getGreen() - min[1]) * (255 / (max[1] - min[1]));
                if(green < 0)
                {
                    green = 0;
                }
                if(green > 255)
                {
                    green = 255;
                }
                int blue = (pixelColor.getBlue() - min[1]) * (255 / (max[1] - min[1]));
                if(blue < 0)
                {
                    blue = 0;
                }
                if(blue > 255)
                {
                    blue = 255;
                }
                int newRGB = red;
                newRGB = (newRGB << 8) + green;
                newRGB = (newRGB << 8) + blue;
                aggStretchedImg.setRGB(x,y, newRGB);
            }
        }
        return aggStretchedImg;
    }

    public static BufferedImage greyscaleAggStretch(float[] histogram, BufferedImage img, float cutoff)
    {
        int max = 255;
        int min = 0;
        float minPixSumPercent = 0;
        float maxPixSumPercent = 0;

        // define cutoff percentage
        
        float cutoffPercent = cutoff;

        // ignore ⍶% of pixels  (modified for matrix)
        for(int i = 0; i < 256; i++)                                
        {
            if(minPixSumPercent < cutoffPercent)                  // if pixelSum is less than cutoff
            {
                if(minPixSumPercent + histogram[i] >= cutoffPercent)
                {
                        //If the amount of pixels in this bucket would go over, then this is still the min
                    min = i;
                }
                minPixSumPercent += histogram[i];               // add to pixelSum
            }
            if(maxPixSumPercent < cutoffPercent)
            {
                if(maxPixSumPercent + histogram[i] >= cutoffPercent)
                {
                    max = 255 - i;
                }
                maxPixSumPercent += histogram[i];
            }
            }

        BufferedImage aggStretchedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = aggStretchedImg.getGraphics();
        colorGraphics.drawImage(img, 0, 0, null);
        colorGraphics.dispose();


        // iterate over and apply the aggressive stretch 

        for(int y=0; y<img.getHeight(); y++)
        {
            for(int x=0; x<img.getWidth(); x++){

                Color pixelColor = new Color(aggStretchedImg.getRGB(x,y));
                int average = (pixelColor.getRed() - min) * (255 / (max - min));
                if(average < 0)
                {
                    average = 0;
                }
                if(average > 255)
                {
                    average = 255;
                }
                int newRGB = average;
                newRGB = (newRGB << 8) + average;
                newRGB = (newRGB << 8) + average;
                aggStretchedImg.setRGB(x,y, newRGB);
            }
        }
        return aggStretchedImg;
    }

    public static BufferedImage equalize(BufferedImage image)
    {
        BufferedImage equalizedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = equalizedImage.getGraphics();
        colorGraphics.drawImage(image, 0, 0, null);
        colorGraphics.dispose();

        HashMap<Float, Float> brightnessHistogram = new HashMap<Float, Float>();
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                float brightness = Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), null)[2];
                if(brightnessHistogram.containsKey(brightness))
                {
                    brightnessHistogram.replace(brightness, brightnessHistogram.get(brightness) + 1);
                }
                else
                {
                    brightnessHistogram.put(brightness, (float) 1);
                }
            }
        }
        //Normalize it
        for(float key : brightnessHistogram.keySet())
        {
            brightnessHistogram.replace(key, brightnessHistogram.get(key) / (image.getWidth() * image.getHeight()));
        }
        ArrayList<Float> sortedKeyList = new ArrayList<Float>();
        sortedKeyList.addAll(brightnessHistogram.keySet());
        //Make it greatest to lowest
        Collections.sort(sortedKeyList);

        HashMap<Float, Float> cdfHistogram = new HashMap<Float, Float>();
        cdfHistogram.put(sortedKeyList.get(0), brightnessHistogram.get(sortedKeyList.get(0)));
        for(int i = 1; i < sortedKeyList.size(); i++)
        {
            cdfHistogram.put(sortedKeyList.get(i), cdfHistogram.get(sortedKeyList.get(i - 1)) + brightnessHistogram.get(sortedKeyList.get(i)));
        }
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                float[] HSB = new float[3];
                Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), HSB);
                HSB[2] = cdfHistogram.get(HSB[2]);
                int newRGB = Color.HSBtoRGB(HSB[0], HSB[1], HSB[2]);
                equalizedImage.setRGB(x,y,newRGB);
            }
        }
        return equalizedImage;
    }

    public static BufferedImage greyscaleEqualize(float[] pdfHistogram, BufferedImage image)
    {
        BufferedImage equalizedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics colorGraphics = equalizedImage.getGraphics();
        colorGraphics.drawImage(image, 0, 0, null);
        colorGraphics.dispose();

        //Firstly use the PDF to create the CDF
        float[] cdfHistogram = new float[256];
        cdfHistogram[0] = pdfHistogram[0] / 100;//Initializing the first value
        for(int i = 1; i < 256; i ++)
        {
            cdfHistogram[i] = cdfHistogram[i - 1] + (pdfHistogram[i] / 100);
        }
        //The CDF is now complete, so:
        for(int y = 0; y < image.getHeight(); y++)
        {
            for(int x = 0; x < image.getWidth(); x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                float newBrightness = cdfHistogram[pixelColor.getBlue()];
                float[] HSB = new float[3];
                Color.RGBtoHSB(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), HSB);
                HSB[2] = newBrightness;
                int newRGB = Color.HSBtoRGB(HSB[0], HSB[1], HSB[2]);
                equalizedImage.setRGB(x,y,newRGB);
            }
        }
        return equalizedImage;
    }
}
