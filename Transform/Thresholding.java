//Assignment3
//Elliott Barnes(201561735) & Zachary Smith(201825478)
import java.awt.*;
import java.awt.image.*;

public class Thresholding 
{
    public static BufferedImage globalThresholdImage(int threshold[], BufferedImage image)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage sourceDuplicate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics duplicateG = sourceDuplicate.getGraphics();
        duplicateG.drawImage(image, 0, 0, null);
        duplicateG.dispose();

        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                Color pixelColor = new Color(image.getRGB(x,y));
                thresholdPixel(x, y, threshold, new int[]{pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()}, sourceDuplicate);
            }
        }

        return sourceDuplicate;
    }


    public static BufferedImage adaptiveThresholdImage(int offset, BufferedImage image)
    {
        int windowSize = 7;
        int threshold[] = new int[3];
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage sourceDuplicate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics duplicateG = sourceDuplicate.getGraphics();
        duplicateG.drawImage(image, 0, 0, null);
        duplicateG.dispose();

        int vStorage[][][] = new int[width][height][3];
        int hStorage[][][] = new int[width][height][3];
        int w = windowSize / 2;

        //Mean calculation code copied from A2

        //Vertical pass
        for(int x = 0; x < width; x++)
        {
            int redSum = 0, greenSum = 0, blueSum = 0;
            //Read in initial values accounting for border
            for(int u = -w; u <= w; u++)
            {
                Color pixelColor = (u < 0) ? new Color(image.getRGB(x,0)) : new Color(image.getRGB(x,u));
                redSum += pixelColor.getRed();
                greenSum += pixelColor.getGreen();
                blueSum += pixelColor.getBlue();
            }
            vStorage[x][0][0] = redSum / (2 * w + 1);
            vStorage[x][0][1] = greenSum / (2 * w + 1);
            vStorage[x][0][2] = blueSum / (2 * w + 1);

            //Starting the iterability
            //Begin at 1 because 0 is accounted for
            for(int y = 1; y < height; y++)
            {
                //If value is out of bounds, set it to closest in-bounds
                int yCoord1 = (y + w >= height) ? height - 1 : y + w;
                int yCoord2 = (y - w - 1 < 0) ? 0 : y - w - 1;
                Color pixelColor1 = new Color(image.getRGB(x,yCoord1));
                Color pixelColor2 = new Color(image.getRGB(x,yCoord2));
                redSum += pixelColor1.getRed() - pixelColor2.getRed();
                greenSum += pixelColor1.getGreen() - pixelColor2.getGreen();
                blueSum += pixelColor1.getBlue() - pixelColor2.getBlue();
                vStorage[x][y][0] = redSum / (2 * w + 1);
                vStorage[x][y][1] = greenSum / (2 * w + 1);
                vStorage[x][y][2] = blueSum / (2 * w + 1);
            }
        }
        //Now horizontal filter
        for(int y = 0; y < height; y++)
        {
            int redSum = 0, greenSum = 0, blueSum = 0;
            //Same as for the vertical filter, read in the initial values, accounting for boundaries
            for(int u = -w; u <= w; u++)
            {
                Color pixelColor = (u < 0) ? new Color(image.getRGB(0,y)) : new Color(image.getRGB(u,y));
                redSum += pixelColor.getRed();
                greenSum += pixelColor.getGreen();
                blueSum += pixelColor.getBlue();
            }
            hStorage[0][y][0] = redSum / (2 * w + 1);
            hStorage[0][y][1] = greenSum / (2 * w + 1);
            hStorage[0][y][2] = blueSum / (2 * w + 1);

            for(int x = 0; x < width; x++)
            {
                if(x != 0)
                {
                    int xCoord1 = (x + w >= width) ? width - 1 : x + w;
                    int xCoord2 = (x - w - 1 < 0) ? 0 : x - w - 1;
                    Color pixelColor1 = new Color(image.getRGB(xCoord1,y));
                    Color pixelColor2 = new Color(image.getRGB(xCoord2,y));
                    redSum += pixelColor1.getRed() - pixelColor2.getRed();
                    greenSum += pixelColor1.getGreen() - pixelColor2.getGreen();
                    blueSum += pixelColor1.getBlue() - pixelColor2.getBlue();
                    hStorage[x][y][0] = redSum / (2 * w + 1);
                    hStorage[x][y][1] = greenSum / (2 * w + 1);
                    hStorage[x][y][2] = blueSum / (2 * w + 1);
                }
                for(int i = 0; i < threshold.length; i++)
                {
                    threshold[i] = ((hStorage[x][y][i] + vStorage[x][y][i]) / 2) - offset;
                }
                Color pixelColor = new Color(image.getRGB(x,y));
                thresholdPixel(x, y, threshold, new int[]{pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()}, sourceDuplicate);
            }
        }

        return sourceDuplicate;
    }

    private static void thresholdPixel(int x, int y, int threshold[], int intensity[], BufferedImage targetImage)
    {
        int[] newRGB = new int[3];
        for(int i = 0; i < intensity.length; i++)
        {
            if(intensity[i] > threshold[i])
            {
                newRGB[i] = Color.WHITE.getRed();
            }
            else if(intensity[i] < threshold[i])
            {
                newRGB[i] = Color.BLACK.getRed();
            }
            else
            {
                newRGB[i] = intensity[i];
            }
        }
        int targetRGB = newRGB[0];
        targetRGB = (targetRGB << 8) + newRGB[1];
        targetRGB = (targetRGB << 8) + newRGB[2];
        targetImage.setRGB(x,y,targetRGB);
    }
}
