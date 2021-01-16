import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public class DerivativeofGaussian 
{
    public static float[][][] XAndYDerivatives(BufferedImage image)
    {
		/**
		 * still need to visualize product of derivatives in another func
		 */
        int height = image.getHeight();
		int width = image.getWidth();
		float[][][] derivatives = new float[width][height][2];
		float[][][] prodOfDerivatives = new float[width][height][3];

		//Not separable, but it is iterable
		int w = 2;
		float[][][] kernel = DerivativeGaussianKernel(1, w);

		ArrayList<ArrayList<Integer>> pixelRGB = new ArrayList<ArrayList<Integer>>();

		for(int y = 0; y < height; y++)
		{
			//After iterating over the width, realign the column by returning to the beginning and iterating up a row
			pixelRGB.clear();
			for(int i = y - w; i <= y + w; i++)
			{
				ArrayList<Integer> row = new ArrayList<Integer>();
				for(int j = -w; j <= w; j++)
				{
					int xCoord = (j < 0) ? 0 : j;
					int yCoord = (i < 0) ? 0 : i;
					yCoord = (yCoord >= height) ? height - 1 : yCoord;
					Color color = new Color(image.getRGB(xCoord,yCoord));
					row.add(color.getRed());
				}
				pixelRGB.add(row);
			}
			for(int x = 0; x < width; x++)
			{
				float[] sums = performGaussian(w, pixelRGB, kernel);
				for(int i = 0; i < 2; i++)
				{
					derivatives[x][y][i] = sums[i];
				}
				prodOfDerivatives[x][y][0] = derivatives[x][y][0] * derivatives[x][y][0];
				prodOfDerivatives[x][y][2] = derivatives[x][y][0] * derivatives[x][y][1];
				prodOfDerivatives[x][y][1] = derivatives[x][y][1] * derivatives[x][y][1];
				//Realign the kernel by removing the first column, and adding a new one
				int xCoord = (x + w + 1 >= width) ? width - 1 : x + w + 1;
				for(int i = 0, j = y - w; j <= y + w; i++, j++)
				{
					int yCoord = (j < 0) ? 0 : j;
					yCoord = (yCoord >= height) ? height - 1 : yCoord;
					pixelRGB.get(i).remove(0);
					Color color = new Color(image.getRGB(xCoord,yCoord));
					pixelRGB.get(i).add(color.getRed());
				}
			}
		}
		return prodOfDerivatives;
	}

	private static BufferedImage sobel(int x, int y, BufferedImage image)
	{
		return null;
	}
	
	private static float[] performGaussian(int w, ArrayList<ArrayList<Integer>> pixelRGB, float[][][] kernel)
	{
		float sums[] = new float[2];
		for(int y = 0; y < 2 * w + 1; y++)
		{
			for(int x = 0; x < 2 * w + 1; x++)
			{
				sums[0] += pixelRGB.get(x).get(y) * kernel[x][y][0];
				sums[1] += pixelRGB.get(x).get(y) * kernel[x][y][1];
			}
		}
		return sums;
	}

    private static float[][][] DerivativeGaussianKernel(int sigma, int w)
    {
		float kernel[][][] = new float[2 * w + 1][2 * w + 1][2];
		float sum[] = {0,0};

		for(int x = -w, i = 0; x <= w; x++, i++)
		{
			for(int y = -w, j = 0; y <= w; y++, j++)
			{
				//With respect to x
				kernel[i][j][0] = (float) ((-x / (2 * Math.PI * Math.pow(sigma, 4))) * Math.exp((-Math.pow(x, 2) - Math.pow(y,2) / (2 * sigma * sigma))));
				sum[0] += Math.abs(kernel[i][j][0]);
				//With respect to y
				kernel[i][j][1] = (float) ((-y / (2 * Math.PI * Math.pow(sigma, 4))) * Math.exp((-Math.pow(x, 2) - Math.pow(y,2) / (2 * sigma * sigma))));
				sum[1] += Math.abs(kernel[i][j][1]);
			}
		}
		sum[0] /= 2;
		sum[1] /= 2;
		for(int x = 0; x < 2 * w + 1; x++)
		{
			for(int y = 0; y < 2 * w + 1; y++)
			{
				for(int i = 0; i < 2; i++)
				{
					kernel[x][y][i] /= sum[i];
				}
			}
		}
        return kernel;
	}

	// moved and modified XandYDerivatives to just display
	public static BufferedImage displayDerivatives(BufferedImage image)
    {
		/**
		 * still need to visualize product of derivatives in another func
		 */
        int height = image.getHeight();
		int width = image.getWidth();
		BufferedImage sourceDuplicate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics duplicateG = sourceDuplicate.getGraphics();
        duplicateG.drawImage(image, 0, 0, null);
        duplicateG.dispose();
		float[][][] prodOfDerivatives = XAndYDerivatives(image);
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				int newRGB = (int) prodOfDerivatives[x][y][0];
				newRGB = (newRGB << 8) + (int) prodOfDerivatives[x][y][1];
				newRGB = (newRGB << 8) + (int) prodOfDerivatives[x][y][2];
				sourceDuplicate.setRGB(x,y,newRGB);	
			}
		}
		
		return sourceDuplicate;
	}
}