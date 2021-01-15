//Assignment3
//Elliott Barnes(201561735) & Zachary Smith(201825478)
import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class ThresholdCalc
{
    // functions to calculate otsu threshold value
    public static int[] calculateOtsu(BufferedImage image, float histogram[][])
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int totalPix = width*height;
        int threshold[] = new int[3];
        float sum[] = new float[3];
        // need to find total sum of histogram vals
        for(int j = 0; j < histogram[0].length; j++)
        {
            for(int i = 0; i<256; i++)
            {
                sum[j] += i*histogram[i][j];
            }
        }

        float backgroundSum[] = new float[3];
        float backgroundMean[] = new float[3];
        float backgroundWeight[] = new float[3];
        float foregroundWeight[] = new float[3];
        float foregroundMean[] = new float[3];
        float maxVariance[] = new float[3];

        for(int j = 0; j < histogram[0].length; j++)
        {
            for(int i = 0; i<256; i++)
            {
                    backgroundWeight[j] += histogram[i][j];
                    if(backgroundWeight[j] == 0)
                    {
                        continue;
                    }
                    foregroundWeight[j] = totalPix - backgroundWeight[j];
                    if(foregroundWeight[j] == 0)
                    {
                        break;
                    }
                    backgroundSum[j] += i*histogram[i][j];
                    backgroundMean[j] = (backgroundSum[j]/backgroundWeight[j]);
                    
                    foregroundMean[j] = ((sum[j] - backgroundSum[j])/foregroundWeight[j]);
        
                    float interVariance = (float) Math.pow(backgroundMean[j]-foregroundMean[j], 2) * backgroundWeight[j] * foregroundWeight[j];
                    if(interVariance > maxVariance[j])
                    {
                        maxVariance[j] = interVariance;
                        threshold[j] = i;
                    }
                }
        }
        if(threshold[1] == 0 && threshold[2] == 0)
        {
            threshold[1] = threshold[0];
            threshold[2] = threshold[0];
        }
        return threshold;
    }

    public static int[] autoCalc(BufferedImage image, float[][] histogram)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int size = width * height;
        int threshold[] = new int[3];
        for(int i = 0; i < histogram.length; i++)
        {
            for(int j = 0; j < histogram[0].length; j++)
            {
                threshold[j] += i * histogram[i][j];
            }
        }
        for(int i = 0; i < histogram[0].length; i++)
        {
            threshold[i] /= size;
        }
        int fgSum[] = new int[histogram[0].length];
        int bgSum[] = new int[histogram[0].length];
        for(int i = 0; i < fgSum.length; i++)
        {
            while(fgSum[i] - bgSum[i] != 0)
            {
                for(int y = 0; y < height; y++)
                {
                    for(int x = 0; x < width; x++)
                    {
                        Color pixelColor = new Color(image.getRGB(x,y));
                        int colors[] = new int[]{pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
                        if(colors[i] <= threshold[i])
                        {
                            bgSum[i] += colors[i];
                        }
                        else
                        {
                            fgSum[i] += colors[i];
                        }
                    }
                }
                fgSum[i] /= size;
                bgSum[i] /= size;
                threshold[i] = (fgSum[i] + bgSum[i]) / 2;
            }
        }
        if(threshold[1] == 0 && threshold[2] == 0)
        {
            threshold[1] = threshold[0];
            threshold[2] = threshold[0];
        }
        return threshold;
    }
}
