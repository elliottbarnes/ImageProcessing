// Skeletal program for the "Image Histogram" assignment
// Written by:  Minglun Gong

/**
 * COMP 3301 Assignment 1, Group 19: Zachary Smith (201825478) & Elliot Barnes (201561735)
 * 
 * This program performs the following operations:
 * 1) Reads in an image file
 * 2) Upon selection of one of the 4 buttons, will perform one of the following
 * i) Display histogram:
 * Generating the Histogram for the image. If the image is greyscale, it will only display 1 (red) line, otherwise it displays 3 lines, 1 for each color channel
 * KNOWN ISSUE: 
 * 		The scaling of the histograms can be strange, but will always be correct in respect to the other lines. As well, when a pixel's color
 * 		is > 0, or < 255, it is set to one of the respective values. This leads to there being a lot of accumulation of pixels at either end of the histogram
 * 		when performing the other operations.
 * 
 * ii) Histogram Stretch:
 * Performs a Histogram stretch on the image file, before displaying the newly changed image on the right, and displaying the new histogram for that image in the center,
 * this newly generated histogram will follow the same rules as with Display Histogram, including the scaling issue
 * 
 * iii) Aggressive Stretch:
 * Will read in the cutoff value from the text box in the bottom center, and use it to perform an aggressive stretch on the image. The stretched image and its
 * histogram are then displayed.
 * 
 * iv) Equalization:
 * Performs an equalization of the image. For a color image, operates only on the brightness value gotten by HSB/HSV (As directed in the assignment handout).
 * For a greyscale image, uses the greyscale histogram as the PDF to assign to the CDF.
 * 
 * 
 * In specific, each extra file does the following:
 * Transformations.java: Is a class full of static methods that implement the required algorithms for each transformation.
 * HistogramCalc.java: Is a class full of static methods that implement the required algorithms to generate histograms for RGB and Greyscale images.
 * 
 * All assigned tasks have been completed, and known bugs are written with their associated functions up above.
 */

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class ImageHistogram extends Frame implements ActionListener {
	BufferedImage input;
	int width, height, min, max, max_gray_level;
	float cutoff;
	TextField texRad, texThres;
	ImageCanvas source, target;
	PlotCanvas plot;
	String name;


	// Constructor
	public ImageHistogram(String name) {
		super("Image Histogram");
		// load image
		this.name = name;
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		max_gray_level = 255;
		// prepare the panel for image canvas.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		plot = new PlotCanvas();
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(plot);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Display Histogram");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Stretch");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Cutoff fraction:"));
		texThres = new TextField("10", 2);
		controls.add(texThres);
		button = new Button("Aggressive Stretch");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Histogram Equalization");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+400, height+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- compute the average color for the image
		if ( ((Button)e.getSource()).getLabel().equals("Display Histogram") ) {
			if(HistogramCalc.isGreyscale(input))
			{
				target.resetImage(input);
				plot.buildHistogram(new float[][]{HistogramCalc.getGreyscaleHistogram(input)}, name);
			}
			else
			{
				//Send off the 3 normalized color histograms to be displayed
				target.resetImage(input);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(input), name);
			}
		}
		if ( ((Button)e.getSource()).getLabel().equals("Histogram Stretch") )
		{
			if(HistogramCalc.isGreyscale(input))
			{
				BufferedImage stretchedImage = Transformations.greyscaleConservativeStretch(HistogramCalc.getGreyscaleHistogram(input), input);
				plot.buildHistogram(new float[][]{HistogramCalc.getGreyscaleHistogram(stretchedImage)}, name);
				target.resetImage(stretchedImage);
			}
			else
			{
				BufferedImage stretchedImage = Transformations.conservativeStretch(HistogramCalc.getNormalizedHistograms(input), input);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(stretchedImage), name);
				target.resetImage(stretchedImage);

				/*
				float[] gsHisto = HistogramCalc.getGreyscaleHistogram(Transformations.grayScaleTransformation(input));
				BufferedImage stretchedImage = Transformations.conversionConservativeStretch(gsHisto, input);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(stretchedImage), name);
				target.resetImage(stretchedImage);
				*/
			}
		}

		if(((Button)e.getSource()).getLabel().equals("Aggressive Stretch"))
		{
			if(HistogramCalc.isGreyscale(input))
			{
				float cutoff = Integer.parseInt(texThres.getText());
				BufferedImage aggStretchedImg = Transformations.greyscaleAggStretch(HistogramCalc.getGreyscaleHistogram(input), input, cutoff);
				plot.buildHistogram(new float[][]{HistogramCalc.getGreyscaleHistogram(aggStretchedImg)}, name);
				target.resetImage(aggStretchedImg);
			}
			else
			{

				float cutoff = Integer.parseInt(texThres.getText());
				BufferedImage aggStretchedImg = Transformations.aggStretch(HistogramCalc.getNormalizedHistograms(input), input, cutoff);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(aggStretchedImg), name);
				target.resetImage(aggStretchedImg);

				/*
				float cutoff = Integer.parseInt(texThres.getText());
				BufferedImage aggStretchedImg = Transformations.conversionAggStretch(HistogramCalc.getGreyscaleHistogram(input), input, cutoff);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(aggStretchedImg), name);
				target.resetImage(aggStretchedImg);
				*/

			}
		}

		if(((Button)e.getSource()).getLabel().equals("Histogram Equalization"))
		{
			/*
			BufferedImage equalizedImg = Transformations.equalization(HistogramCalc.getGreyscaleHistogram(input), HistogramCalc.getGreyscaleHistogram(input),input);
			plot.buildHistogram(HistogramCalc.getNormalizedHistograms(equalizedImg), name);
			target.resetImage(equalizedImg);
			*/
			if(HistogramCalc.isGreyscale(input))
			{
				BufferedImage equalizedImage = Transformations.greyscaleEqualize(HistogramCalc.getGreyscaleHistogram(input), input);
				plot.buildHistogram(new float[][]{HistogramCalc.getGreyscaleHistogram(equalizedImage)}, name);
				target.resetImage(equalizedImage);
			}
			else
			{
				BufferedImage equalizedImage = Transformations.equalize(input);
				plot.buildHistogram(HistogramCalc.getNormalizedHistograms(equalizedImage), name);
				target.resetImage(equalizedImage);
			}
		}

		
		
	}
	public static void main(String[] args) {
		new ImageHistogram(args.length==1 ? args[0] : "signal_hill.png");
	}
}

// Canvas for plotting histogram
class PlotCanvas extends Canvas {
	// lines for plotting axes and mean color locations
	LineSegment x_axis, y_axis;
	LineSegment red, green, blue;
	boolean showMean = false;
	boolean showHistogram = false;
	ArrayList<LineSegment> lines = new ArrayList<LineSegment>();

	public PlotCanvas() {
		x_axis = new LineSegment(Color.BLACK, -10, 0, 256+10, 0);
		y_axis = new LineSegment(Color.BLACK, 0, -10, 0, 200+10);
	}
	// set mean image color for plot
	public void setMeanColor(Color clr) {
		red = new LineSegment(Color.RED, clr.getRed(), 0, clr.getRed(), 100);
		green = new LineSegment(Color.GREEN, clr.getGreen(), 0, clr.getGreen(), 100);
		blue = new LineSegment(Color.BLUE, clr.getBlue(), 0, clr.getBlue(), 100);
		showMean = true;
		repaint();
	}
	public void buildHistogram(float[][] histograms, String name)
	{
		//Receives 3 normalized color histograms
		//Histograms = {red, green, blue}
		lines.clear();
		int multiplier;
		if(name == "baboon.png")
		{
			multiplier = 200;
		}
		else
		{
			multiplier = 20;
		}
		for(int j = 0; j < histograms.length; j++)
		{
			for(int i = 0; i < 255; i++)
			{
				int point1 = (int) (histograms[j][i] * multiplier);
				int point2 = (int) (histograms[j][i + 1] * multiplier);
				Color color;
				if(j == 0)
				{
					color = Color.RED;
				}
				else if(j == 1)
				{
					color = Color.GREEN;
				}
				else
				{
					color = Color.BLUE;
				}
				LineSegment line = new LineSegment(color, i,point1, i + 1,point2);
				lines.add(line);
			}
		}
		showHistogram = true;
		repaint();
	}
	// redraw the canvas
	public void paint(Graphics g) {
		// draw axis
		int xoffset = (getWidth() - 256) / 2;
		int yoffset = (getHeight() - 200) / 2;
		x_axis.draw(g, xoffset, yoffset, getHeight());
		y_axis.draw(g, xoffset, yoffset, getHeight());
		if ( showMean ) {
			red.draw(g, xoffset, yoffset, getHeight());
			green.draw(g, xoffset, yoffset, getHeight());
			blue.draw(g, xoffset, yoffset, getHeight());
		}
		if(showHistogram)
		{
			for(LineSegment line : lines)
			{
				line.draw(g, xoffset, yoffset, getHeight());
			}
			lines.clear();
		}
	}
}

// LineSegment class defines line segments to be plotted
class LineSegment {
	// location and color of the line segment
	int x0, y0, x1, y1;
	Color color;
	// Constructor
	public LineSegment(Color clr, int x0, int y0, int x1, int y1) {
		color = clr;
		this.x0 = x0; this.x1 = x1;
		this.y0 = y0; this.y1 = y1;
	}
	public void draw(Graphics g, int xoffset, int yoffset, int height) {
		g.setColor(color);
		g.drawLine(x0+xoffset, height-y0-yoffset, x1+xoffset, height-y1-yoffset);
	}
}


// HistogramStretch applies stretch algorithm on input imag
