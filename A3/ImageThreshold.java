// Skeletal program for the "Image Threshold" assignment
// Written by:  Minglun Gong

//Assignment3
//Elliott Barnes(201561735) & Zachary Smith(201825478)
//Thresholding class contains methods for applying global and adaptive thresholding
//	globalThresholdImage takes an image and threshold as parameters, and applies manual or automatic thresholding to the image
//	adaptiveThresholdImage takes an image and offset as parameters, and applies adaptive mean-c thresholding to the image
//ThresholdCalc class contains methods for calculating automatic and otsu thresholds
//	calculateOtsu takes an image and histogram as parameters, and calculates the otsu thresholding value
// 	autoCalc takes an image and histogram as parameters and calculates the automatic thresholding value
//HistogramCalc class contains methods for creating and normalizing the histograms


import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class ImageThreshold extends Frame implements ActionListener {
	BufferedImage input, img;
	int width, height;
	TextField texThres, texOffset;
	ImageCanvas source, target;
	PlotCanvas2 plot;
	float normalizedHistogram[][] = new float[256][3];
	float histogram[][] = new float[256][3];
	Color[] colors = new Color[]{Color.RED, Color.GREEN, Color.BLUE};;
	// Constructor
	public ImageThreshold(String name) {
		super("Image Histogram");
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
		plot = new PlotCanvas2(256, 200);
		target = new ImageCanvas(width, height);
		//target.copyImage(input);
                target.resetImage(input);
		main.setLayout(new GridLayout(1, 3, 10, 10));
		main.add(source);
		main.add(plot);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		controls.add(new Label("Threshold:"));
		texThres = new TextField("128", 2);
		controls.add(texThres);
		Button button = new Button("Manual Selection");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Automatic Selection");
		button.addActionListener(this);
		controls.add(button);
		button = new Button("Otsu's Method");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Offset:"));
		texOffset = new TextField("10", 2);
		controls.add(texOffset);
		button = new Button("Adaptive Mean-C");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(width*2+400, height+100);

		onLoad();

		setVisible(true);
	}

	private void onLoad()
	{
		//Compute the histograms for the image
		normalizedHistogram = HistogramCalc.getHistograms(input, true);
		histogram = HistogramCalc.getHistograms(input, false);
		//Display the histograms
		for(int i = 1; i < normalizedHistogram.length; i++)
		{
			for(int j = 0; j < normalizedHistogram[0].length; j++)
			{
				plot.addObject(new LineSegment(colors[j], i-1, (int)normalizedHistogram[i-1][j], i, (int)normalizedHistogram[i][j]));
			}
		}
	}

	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener for button click events
	public void actionPerformed(ActionEvent e) {
		// example -- compute the average color for the image
		if ( ((Button)e.getSource()).getLabel().equals("Manual Selection") ) {
			int threshold[] = new int[3];
			try {
				threshold[0] = Integer.parseInt(texThres.getText());
				threshold[1] = threshold[0];
				threshold[2] = threshold[0];
			} catch (Exception ex) {
				texThres.setText("128");
				threshold[0] = 128;
				threshold[1] = 128;
				threshold[2] = 128;
			}
			plot.clearObjects();
			for(int i = 0; i < threshold.length; i++)
			{
				plot.addObject(new VerticalBar(Color.BLACK, threshold[i], threshold[i]));
			}
			//Call for the image thresholding
			BufferedImage thresholdedImage = Thresholding.globalThresholdImage(threshold, input);
			target.resetImage(thresholdedImage);
		}

		if ( ((Button)e.getSource()).getLabel().equals("Otsu's Method") ) {
			int threshold[] = new int[3];
			try {
				threshold = ThresholdCalc.calculateOtsu(input, histogram);
			} catch (Exception ex) {
				texThres.setText("128");
				threshold[0] = 128;
				threshold[1] = 128;
				threshold[2] = 128;
				System.out.println("Error");
			}
			plot.clearObjects();
			for(int i = 0; i < threshold.length; i++)
			{
				plot.addObject(new VerticalBar(colors[i], threshold[i], threshold[i]));
			}
			//Call for the image thresholding
			BufferedImage otsuThresholdedImage = Thresholding.globalThresholdImage(threshold, input);
			target.resetImage(otsuThresholdedImage);
			
		}
		if ( ((Button)e.getSource()).getLabel().equals("Automatic Selection") ) 
		{
			int threshold[] = new int[3];
			try {
				threshold = ThresholdCalc.autoCalc(input, histogram);
			} catch (Exception ex) {
				texThres.setText("128");
				threshold[0] = 128;
				threshold[1] = 128;
				threshold[2] = 128;
				System.out.println("Error");
			}
			plot.clearObjects();
			for(int i = 0; i < threshold.length; i++)
			{
				plot.addObject(new VerticalBar(colors[i], threshold[i], threshold[i]));
			}
			//Call for the image thresholding
			BufferedImage autoThresholdedImage = Thresholding.globalThresholdImage(threshold, input);
			target.resetImage(autoThresholdedImage);
		}

		if ( ((Button)e.getSource()).getLabel().equals("Adaptive Mean-C"))
		{
			BufferedImage thresholdedImage = Thresholding.adaptiveThresholdImage(Integer.parseInt(texOffset.getText()), input);
			plot.clearObjects();
			target.resetImage(thresholdedImage);
		}
	}

	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		new ImageThreshold(args.length==1 ? args[0] : "fingerprint.png");
	}

}
