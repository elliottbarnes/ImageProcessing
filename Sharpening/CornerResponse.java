import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
public class CornerResponse {

    public static float[][] response(BufferedImage image, double k, float[][][] prodOfDerivatives)
    {
        int height = image.getHeight();
		int width = image.getWidth();
		BufferedImage sourceDuplicate = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics duplicateG = sourceDuplicate.getGraphics();
        duplicateG.drawImage(image, 0, 0, null);
        duplicateG.dispose();
        float[][] r = new float[width][height];
        float[][] a = new float[2][2];
        
        for(int y = 0; y<height;y++)
        {
            for(int x = 0; x<width;x++)
            {
                /**
				 * calculate sum of products of derivatives
				 * create structure tensor A from calculations
				 * A = |∑Ix^2 , ∑Ixy |
				 * 	   |∑Ixy  , ∑Iy^2|
				 */
				for(int t = 0; t<2;t++)
				{
					for(int d =0;d<2;d++)
					{
						if(d==0 && t==0)
						{
							a[t][d] += prodOfDerivatives[x][y][0];
						}
						else if(d==1 && t==1)
						{
							a[t][d] += prodOfDerivatives[x][y][1];
						}		
						else
						{
							a[t][d] += prodOfDerivatives[x][y][0] * prodOfDerivatives[x][y][1];
						}
					}
                }
                /**
                 * calculate R = det(A)-k*trace^2(A) at each pixel
                 */
                r[x][y] = (float)((a[0][0]*a[1][1]-a[0][1]*a[1][0]) - (k*Math.pow(a[0][0]+a[1][1],2)));
            }
        }
        return r;
    }

    public static BufferedImage displayCornerResponse(BufferedImage image, float[][] response)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        BufferedImage sourceDuplicate = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics duplicateG = sourceDuplicate.getGraphics();
        duplicateG.drawImage(image, 0, 0, null);
        duplicateG.dispose();

        for(int y = 0; y<height; y++)
        {
            for(int x = 0; x<width; x++)
            {
                sourceDuplicate.setRGB(x, y, (int)response[x][y]);
            }
        }

        return sourceDuplicate;
    }
}
