//Assignment2
//Elliott Barnes(201561735) & Zachary Smith(201825478)

// Bug in Kuwahara Filter. Doesn;t calculate end mean correctly, causes brightness to be very low. Appears to cause blur but too dark to really tell 
// Mean filter function calculates the mean of a pixels neighbours with a 5x5 kernel, and applies the mean to the center pixel
// Median filter function calculates the median of a pixels neighbours with a 5x5 kernel, and applies the median value to the center pixel
// Kuwahara filter function creates 4 subregions, and calculates the variance and mean, applying the mean from the region with the lowest variance to the center pixel
// Gaussian filter function takes sigma as input to increase/decrease the strength of the gaussian blur on an image with a 5x5 kernel
//Update source excludes kuwahara because it darkens the image too much. Can be readded to that call chain by removing the comments on line 122


import java.util.*;
import java.util.Arrays; 
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class SmoothingFilter extends Frame implements ActionListener {
	BufferedImage input;
	ImageCanvas source, target;
	TextField texSigma;
	double sigma;
	int width, height;

	// Constructor
	public SmoothingFilter(String name) {
		super("Smoothing Filters");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Add noise");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 mean");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Sigma:"));
		texSigma = new TextField("1", 1);
		controls.add(texSigma);
		button = new Button("5x5 Gaussian");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 median");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("5x5 Kuwahara");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Update Source");
		button.addActionListener(this);
		// add two panels
		add("Center", main);
		add("South", controls);
		add("North",button);
		addWindowListener(new ExitListener());
		setSize(width*2+100, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- add random noise
		if ( ((Button)e.getSource()).getLabel().equals("Add noise") ) {
			Random rand = new Random();
			int dev = 64;
			for ( int y=0, i=0 ; y<height ; y++ )
				for ( int x=0 ; x<width ; x++, i++ ) {
					Color clr = new Color(source.image.getRGB(x, y));
					int red = clr.getRed() + (int)(rand.nextGaussian() * dev);
					int green = clr.getGreen() + (int)(rand.nextGaussian() * dev);
					int blue = clr.getBlue() + (int)(rand.nextGaussian() * dev);
					red = red < 0 ? 0 : red > 255 ? 255 : red;
					green = green < 0 ? 0 : green > 255 ? 255 : green;
					blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;
					source.image.setRGB(x, y, (new Color(red, green, blue)).getRGB());
				}
			source.repaint();
		}

		// 5x5 mean replaces each pixel value with the mean of it's neighbours (need to optimize)
		if(((Button)e.getSource()).getLabel().equals("5x5 mean")) {
			target.resetImage(mean(target.image));
		}

		// 5x5 gaussian replaces each pixel value with the weighted values of it's neighbours (need to optimize)
		if(((Button)e.getSource()).getLabel().equals("5x5 Gaussian")) {
			target.resetImage(gaussian(target.image));
		}
		if(((Button)e.getSource()).getLabel().equals("5x5 median")){
			target.resetImage(median(target.image));
		}

		if(((Button)e.getSource()).getLabel().equals("5x5 Kuwahara")){
			target.resetImage(kuwahara(target.image));
		}

		if(((Button)e.getSource()).getLabel().equals("Update Source"))
		{
			BufferedImage buffer = mean(source.image);
			buffer = gaussian(buffer);
			buffer = median(buffer);
			//buffer = kuwahara(buffer);
			source.resetImage(buffer);
		}
	}

	BufferedImage mean(BufferedImage image)
	{
					//Create a duplicate of the input image
			
					BufferedImage sourceDuplicate = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics duplicateG = sourceDuplicate.getGraphics();
					duplicateG.drawImage(image, 0, 0, null);
					duplicateG.dispose();
		
					int[][][] hStorage = new int[this.width][this.height][3];
					int[][][] vStorage = new int[this.width][this.height][3];
					
					int w = 2;
					//Vertical Filter
					for(int x = 0; x < this.width; x++)
					{
						int redSum = 0, greenSum = 0, blueSum = 0;
						//Firstly, read in the initial values
						//Since we are assuming pixels outside the image have values equal to the closest one inside the image, read in some duplicate values
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
		
						//Beginning the creeping calculation down the columns of the image
						//Start y at 1, because y of 0 has just been accounted for
						for(int y = 1; y < this.height; y++)
						{
							//Firstly, like above, if the value is out of bounds simply set it to be the closest in-bounds pixel
							int yCoord1 = (y + w >= this.height) ? this.height - 1 : y + w;
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
		
					//Now to do the horizontal filter
					for(int y = 0; y < this.height; y++)
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
		
						//Beginning the horizontal crawl
						//Include 0 here, as the image editing occurs here. However skip the main process on the first iteration
						for(int x = 0; x < this.width; x++)
						{
							if(x != 0)
							{
								int xCoord1 = (x + w >= this.height) ? this.height - 1 : x + w;
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
							int finalRedSum = (hStorage[x][y][0] + vStorage[x][y][0]) / 2;
							int finalGreenSum = (hStorage[x][y][1] + vStorage[x][y][1]) / 2;
							int finalBlueSum = (hStorage[x][y][2] + vStorage[x][y][2]) / 2;
							int newRGB = finalRedSum;
							newRGB = (newRGB << 8) + finalGreenSum;
							newRGB = (newRGB << 8) + finalBlueSum;
							sourceDuplicate.setRGB(x,y,newRGB);
						}
					}
					return sourceDuplicate;
	}

	BufferedImage gaussian(BufferedImage image)
	{

		BufferedImage sourceDuplicate = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics duplicateG = sourceDuplicate.getGraphics();
		duplicateG.drawImage(image, 0, 0, null);
		duplicateG.dispose();
		
		float[][][] hStorage = new float[this.width][this.height][3];
		float[][][] vStorage = new float[this.width][this.height][3];

		sigma = Double.parseDouble(texSigma.getText());
		//Use the "true" values so its separable
		//Separable, therefore w = 2
		int w = 2;
		Color pixelColor;
		float[] kernel = GaussianKernel(sigma, w);
		//Horizontal filter rewrite
		for(int y = 0; y < this.height; y++)
		{
			float redSum = 0, greenSum = 0, blueSum = 0;
			ArrayList<Float> redValues = new ArrayList<Float>();
			ArrayList<Float> greenValues = new ArrayList<Float>();
			ArrayList<Float> blueValues = new ArrayList<Float>();
			//Preload the lists for the first set of values, except for 
			//<= here because the first value is going to be the same as the OOB values
			for(int i = -w; i <= w; i++)
			{
				int xCoord = (i < 0) ? 0 : i;
				pixelColor = new Color(image.getRGB(xCoord,y));
				redValues.add((float) pixelColor.getRed());
				greenValues.add((float) pixelColor.getGreen());
				blueValues.add((float) pixelColor.getBlue());
			}
			for(int x = 0; x < this.width; x++)
			{
				redSum = 0;
				greenSum = 0;
				blueSum = 0;
				//For x = 0, the loop will have already been preloaded, so perform calculation right away
				for(int i = 0; i < 2 * w + 1; i++)
				{
					redSum += redValues.get(i) * kernel[i];
					greenSum += greenValues.get(i) * kernel[i];
					blueSum += blueValues.get(i) * kernel[i];
				}
				hStorage[x][y][0] = redSum;
				hStorage[x][y][1] = greenSum;
				hStorage[x][y][2] = blueSum;
				//Now realign the kernel
				int xCoord = (x + w + 1 >= this.width) ? this.width - 1 : x + w + 1;
				pixelColor = new Color(image.getRGB(xCoord,y));
				redValues.remove(0);
				redValues.add((float) pixelColor.getRed());
				greenValues.remove(0);
				greenValues.add((float) pixelColor.getGreen());
				blueValues.remove(0);
				blueValues.add((float) pixelColor.getBlue());
			}
		}
		//Vertical rewrite
		for(int x = 0; x < this.width; x++)
		{
			float redSum = 0, greenSum = 0, blueSum = 0;
			ArrayList<Float> redValues = new ArrayList<Float>();
			ArrayList<Float> greenValues = new ArrayList<Float>();
			ArrayList<Float> blueValues = new ArrayList<Float>();
			//Preload the lists for the first set of values, except for 
			
			//<= here because the first value is going to be the same as the OOB values
			for(int i = -w; i <= w; i++)
			{
				int yCoord = (i < 0) ? 0 : i;
				pixelColor = new Color(image.getRGB(x,yCoord));
				redValues.add((float) pixelColor.getRed());
				greenValues.add((float) pixelColor.getGreen());
				blueValues.add((float) pixelColor.getBlue());
			}
			for(int y = 0; y < this.width; y++)
			{
				redSum = 0;
				greenSum = 0;
				blueSum = 0;
				//For x = 0, the loop will have already been preloaded, so perform calculation right away
				for(int i = 0; i < 2 * w + 1; i++)
				{
					redSum += redValues.get(i) * kernel[i];
					greenSum += greenValues.get(i) * kernel[i];
					blueSum += blueValues.get(i) * kernel[i];
				}
				vStorage[x][y][0] = redSum;
				vStorage[x][y][1] = greenSum;
				vStorage[x][y][2] = blueSum;
				//Now realign the kernel
				int yCoord = (y + w + 1 >= this.height) ? this.height - 1 : y + w + 1;
				pixelColor = new Color(image.getRGB(x,yCoord));
				redValues.remove(0);
				redValues.add((float) pixelColor.getRed());
				greenValues.remove(0);
				greenValues.add((float) pixelColor.getGreen());
				blueValues.remove(0);
				blueValues.add((float) pixelColor.getBlue());

				//Edit the image
				int finalRed = (int) ((vStorage[x][y][0] + hStorage[x][y][0]) / 2);
				int finalGreen = (int) ((vStorage[x][y][1] + hStorage[x][y][1]) / 2);
				int finalBlue = (int) ((vStorage[x][y][2] + hStorage[x][y][2]) / 2);
				int newRGB = finalRed;
				newRGB = (newRGB << 8) + finalGreen;
				newRGB = (newRGB << 8) + finalBlue;
				sourceDuplicate.setRGB(x,y,newRGB);
			}
		}
		// return target image
		return sourceDuplicate;
	}

	BufferedImage median(BufferedImage image)
	{			
		BufferedImage sourceDuplicate = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics duplicateG = sourceDuplicate.getGraphics();
		duplicateG.drawImage(input, 0, 0, null);
		duplicateG.dispose();

		int w = 2;
		ArrayList<Integer> red = new ArrayList<Integer>();
		ArrayList<Integer> blue = new ArrayList<Integer>();
		ArrayList<Integer> green = new ArrayList<Integer>();

		// iterate through image and compute median
		// of neighbouring pixel values at each pixel 
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				// scan over box filter
				for (int v = y-w; v <= y + w; v++) {
					for (int u = x-w; u <= x + w; u++) {

						int xCoord = v, yCoord = u;
						if(v < 0)
						{
							xCoord = 0;
						}
						if(v >= this.width)
						{
							xCoord = this.width - 1;
						}
						if(u < 0)
						{
							yCoord = 0;
						}
						if(u >= this.height)
						{
							yCoord = this.height - 1;
						}
						// get pixel color
						Color pixelColor = new Color(image.getRGB(xCoord, yCoord));
						red.add(pixelColor.getRed());
						green.add(pixelColor.getGreen());
						blue.add(pixelColor.getBlue());
					}
				}

				// sort the array of pixels in ascending order
				
				Collections.sort(red);
				Collections.sort(blue);
				Collections.sort(green);
				
				// assign the median pixel value to the image
				sourceDuplicate.setRGB(y, x, (new Color(red.get(red.size() / 2), green.get(green.size() / 2), blue.get(blue.size() / 2))).getRGB());
				red.clear();
				green.clear();
				blue.clear();
			}
		}
		return sourceDuplicate;
	}

	float[] GaussianKernel(double sigma, int w)
	{
		float sum = 0;
		float[] kernel = new float[2 * w + 1];
		for(int i = -w, j = 0; i <= w; i++, j++)
		{
			kernel[j] = (float) (Math.exp(-(i * i) / (2 * sigma * sigma)) / (Math.sqrt(2 * Math.PI) * sigma));
			sum += kernel[j];
		}
		for(int i = 0; i < 2 * w + 1; i++)
		{
			kernel[i] /= sum;
		}
		return kernel;
	}

	BufferedImage kuwahara(BufferedImage image) {
		BufferedImage sourceDuplicate = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics duplicateG = sourceDuplicate.getGraphics();
		duplicateG.drawImage(image, 0, 0, null);
		duplicateG.dispose();

		//Initializing each of the subregions
		ArrayList<Color> topleft = new ArrayList<Color>();
		ArrayList<Color> topright = new ArrayList<Color>();
		ArrayList<Color> bottomleft = new ArrayList<Color>();
		ArrayList<Color> bottomright = new ArrayList<Color>();

		//Intializing the w value
		int w = 2;

		for(int y = 0; y < this.height; y++)
		{
			for(int x = 0; x < this.width; x++)
			{
				//Clearing each of the lists
				topleft.clear();
				topright.clear();
				bottomleft.clear();
				bottomright.clear();
				for(int u = y-w, y2 = -w; u <= w + y; u++, y2++)
				{
					for(int v = x-w, x2 = -w; v <= x + w; v++, x2++)
					{
						//If they're in bounds, they set themselves here
						int xCoord = v, yCoord = u;
						//Account for OOB
						if(xCoord < 0)
						{
							xCoord = 0;
						}
						if(xCoord >= this.width)
						{
							xCoord = this.width - 1;
						}
						if(yCoord < 0)
						{
							yCoord = 0;
						}
						if(yCoord >= this.height)
						{
							yCoord = this.height - 1;
						}
						//Topleft
						if(y2 <= 0 && x2 <= 0)
						{
							topleft.add(new Color(image.getRGB(xCoord,yCoord)));
						}
						//Topright
						if(y2 <= 0 && x2 >= 0)
						{
							topright.add(new Color(image.getRGB(xCoord,yCoord)));
						}
						//Botleft
						if(y2 >= 0 && x2 <= 0)
						{
							bottomleft.add(new Color(image.getRGB(xCoord,yCoord)));
						}
						//Botright
						if(y2 >= 0 && x2 >= 0)
						{
							bottomright.add(new Color(image.getRGB(xCoord,yCoord)));
						}
					}
				}
				//Now iterate over each list and get the mean and variance for each region
				int[] topleftMean = new int[3], toprightMean = new int[3], bottomleftMean = new int[3], bottomrightMean = new int[3],
				topleftVar = new int[3], toprightVar = new int[3], bottomleftVar = new int[3], bottomrightVar = new int[3];
				for(int i = 0; i < topleft.size(); i++)
				{
					Color topleftColor = topleft.get(i);
					Color toprightColor = topright.get(i);
					Color bottomleftColor = bottomleft.get(i);
					Color bottomrightColor = bottomright.get(i);
					//To calc the mean
					topleftMean[0] += topleftColor.getRed();
					topleftMean[1] += topleftColor.getGreen();
					topleftMean[2] += topleftColor.getBlue();

					toprightMean[0] += toprightColor.getRed();
					toprightMean[1] += toprightColor.getGreen();
					toprightMean[2] += toprightColor.getBlue();

					bottomleftMean[0] += bottomleftColor.getRed();
					bottomleftMean[1] += bottomleftColor.getGreen();
					bottomleftMean[2] += bottomleftColor.getBlue();

					bottomrightMean[0] += bottomrightColor.getRed();
					bottomrightMean[1] += bottomrightColor.getGreen();
					bottomrightMean[2] += bottomrightColor.getBlue();
				}
				topleftMean[0] /= 2 * w + 1;
				topleftMean[1] /= 2 * w + 1;
				topleftMean[2] /= 2 * w + 1;

				toprightMean[0] /= 2 * w + 1;
				toprightMean[1] /= 2 * w + 1;
				toprightMean[2] /= 2 * w + 1;

				bottomleftMean[0] /= 2 * w + 1;
				bottomleftMean[1] /= 2 * w + 1;
				bottomleftMean[2] /= 2 * w + 1;

				bottomrightMean[0] /= 2 * w + 1;
				bottomrightMean[1] /= 2 * w + 1;
				bottomrightMean[2] /= 2 * w + 1;
				//Calculate variance
				for(int i = 0; i < topleft.size(); i++)
				{
					Color topleftColor = topleft.get(i);
					Color toprightColor = topright.get(i);
					Color bottomleftColor = bottomleft.get(i);
					Color bottomrightColor = bottomright.get(i);
					toprightVar[0] += Math.pow(toprightColor.getRed() - toprightMean[0], 2);
					toprightVar[1] += Math.pow(toprightColor.getGreen() - toprightMean[1], 2);
					toprightVar[2] += Math.pow(toprightColor.getBlue() - topleftMean[2], 2);

					topleftVar[0] += Math.pow(topleftColor.getRed() - topleftMean[0], 2);
					topleftVar[1] += Math.pow(topleftColor.getGreen() - topleftMean[1], 2);
					topleftVar[2] += Math.pow(topleftColor.getBlue() - topleftMean[2], 2);

					bottomrightVar[0] += Math.pow(bottomrightColor.getRed() - bottomrightMean[0], 2);
					bottomrightVar[1] += Math.pow(bottomrightColor.getGreen() - bottomrightMean[1], 2);
					bottomrightVar[2] += Math.pow(bottomrightColor.getBlue() - bottomrightMean[2], 2);

					bottomleftVar[0] += Math.pow(bottomleftColor.getRed() - bottomleftMean[0], 2);
					bottomleftVar[1] += Math.pow(bottomleftColor.getGreen() - bottomleftMean[1], 2);
					bottomleftVar[2] += Math.pow(bottomleftColor.getBlue() - bottomleftMean[2], 2);
				}
				int[] chosenRegionMean = new int[3];
				for(int i = 0; i < 3; i++)
				{
					toprightVar[i] /= (2 * w);
					topleftVar[i] /= (2 * w);
					bottomleftVar[i] /= (2 * w);
					bottomrightVar[i] /= (2 * w);
					ArrayList<Integer> sort = new ArrayList<Integer>(Arrays.asList(toprightVar[i], topleftVar[i], bottomleftVar[i], bottomrightVar[i]));
					Collections.sort(sort);
					int chosenInt = sort.get(0);
					if(toprightVar[i] == chosenInt)
					{
						chosenRegionMean[i] = toprightMean[i];
					}
					else if(topleftVar[i] == chosenInt)
					{
						chosenRegionMean[i] = topleftMean[i];
					}
					else if(bottomleftVar[i] == chosenInt)
					{
						chosenRegionMean[i] = bottomleftMean[i];
					}
					else if(bottomrightVar[i] == chosenInt)
					{
						chosenRegionMean[i] = bottomrightMean[i];
					}
				}
				for(int i = 0; i < 3; i++)
				{
					if(chosenRegionMean[i] == 0)
					{
						chosenRegionMean[i] = 1;
					}
				}
				int redSum = 0, blueSum = 0, greenSum = 0;
				for(int u = y - w; u <= y + w; u++)
				{
					for(int v = x - w; v <= x + w; v++)
					{
						//If they're in bounds, they set themselves here
						int xCoord = v, yCoord = u;
						//Account for OOB
						if(xCoord < 0)
						{
							xCoord = 0;
						}
						if(xCoord >= this.width)
						{
							xCoord = this.width - 1;
						}
						if(yCoord < 0)
						{
							yCoord = 0;
						}
						if(yCoord >= this.height)
						{
							yCoord = this.height - 1;
						}
						Color pixelColor = new Color(image.getRGB(xCoord,yCoord));
						redSum += pixelColor.getRed();
						greenSum += pixelColor.getGreen();
						blueSum += pixelColor.getBlue();
					}
				}
				redSum /= chosenRegionMean[0];
				greenSum /= chosenRegionMean[1];
				blueSum /= chosenRegionMean[2];
				int newRGB = redSum;
				newRGB = (newRGB << 8) + greenSum;
				newRGB = (newRGB << 8) + blueSum;
				sourceDuplicate.setRGB(x,y,newRGB);
			}
		}

		return sourceDuplicate;
	}
	
	public static void main(String[] args) {
		new SmoothingFilter(args.length==1 ? args[0] : "baboon.png");
	}

}

