import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

// Elliott Barnes, Zachary Smith
// Hough Transform for line is calculated and displayed from within the Line Transform action listener
// Hough Transform for circle is calulated and displayed from within the Circle Transform action listener 
// Higlighting lines with edge detection not working
// Main class
public class HoughTransform extends Frame implements ActionListener {
	BufferedImage input;
	int width, height, diagonal;
	ImageCanvas source, target;
	TextField texRad, texThres;
	// Constructor
	public HoughTransform(String name) {
		super("Hough Transform");
		// load image
		try {
			input = ImageIO.read(new File(name));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		diagonal = (int)Math.sqrt(width * width + height * height);
		// prepare the panel for two images.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Line Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Radius:"));
		texRad = new TextField("10", 3);
		controls.add(texRad);
		button = new Button("Circle Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Threshold:"));
		texThres = new TextField("25", 3);
		controls.add(texThres);
		button = new Button("Search");
		button.addActionListener(this);
		controls.add(button);
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(diagonal*2+100, Math.max(height,360)+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener
	public void actionPerformed(ActionEvent e) {
		// perform one of the Hough transforms if the button is clicked.
		if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
			int[][] g = new int[360][diagonal];
            // insert your implementation for straight-line here.
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++)
				{
					//Implementation without tudent created edge detection
					Color pixelColor = new Color(input.getRGB(x,y));
					if(pixelColor.equals(Color.BLACK))
					{
						//In the test images, all black pixels are edges
						for(int theta = 0; theta < 180; theta++)
						{
                            int rho = (int) (((x - width/2) * Math.cos(Math.toRadians(theta))) + ((y - height /2) * Math.sin(Math.toRadians(theta))));
                            rho += 168;
                            g[2 * theta][rho]++;
						}
					}
				}
            }
            DisplayTransform(diagonal, 360, g);
            ArrayList<ArrayList<Integer>> lines = new ArrayList<ArrayList<Integer>>();
            for(int i = 0; i < 180; i++)
            {
                for(int j = 0; j < diagonal; j++)
                {
                    ArrayList<Integer> coordinates = new ArrayList<Integer>();
                    //Find the highest values
                    if(g[2 * i][j] >= Integer.parseInt(texThres.getText()))
                    {
                        coordinates.add(i);
                        coordinates.add(j);
                        lines.add(coordinates);
                    }
                }
            }
            int newRGB = 255;
            newRGB = newRGB << 8;
            newRGB = newRGB << 8;
            for(int x = 0; x < width; x++)
            {
                for(ArrayList<Integer> coordinates : lines)
                {
                    int m = coordinates.get(1);
                    int c = coordinates.get(0);
                    int y = m * x + c;
                    if(y < height)
                    {
                        source.image.setRGB(x,y, newRGB);
                    }
                }
            }
            source.repaint();
		}
		else if ( ((Button)e.getSource()).getLabel().equals("Circle Transform") ) {
			int[][] g = new int[height][width];
			int radius = Integer.parseInt(texRad.getText());
			// insert your implementation for circle here.
			// scan image
	
			for(int x = 0; x<width; x++)
			{
				for(int y = 0; y<height; y++)
				{
					Color pixelColor = new Color(input.getRGB(x, y));
					if(pixelColor.equals(Color.BLACK))
					{
						// iterate through values of theta
						for(int t = 0; t<=180; t++)
						{
							// search for a & b
							// a = x - rcos(theta)
							// b = y - rsin(theta)
							int a = (int)Math.floor(x-(radius*Math.cos(t*Math.PI/180)));
							int b = (int)Math.floor(y-(radius*Math.sin(t*Math.PI/180)));

							// fixes out of bounds error
							if(!((a>width-1 || a<0)||(b>height-1 || b<0)))
							{
								if(!(a==x && b==y))
								{
									g[a][b] +=1;
								}
							}
						}
					}
				}
			}
			DisplayTransform(width, height, g);
		}
	}

	// display the spectrum of the transform.
	public void DisplayTransform(int wid, int hgt, int[][] g) {
		target.resetBuffer(wid, hgt);
		for ( int y=0, i=0 ; y<hgt ; y++ )
			for ( int x=0 ; x<wid ; x++, i++ )
			{
                int value = g[y][x] > 255 ? 255 : g[y][x];
                value *= 8;
                if(value > 255)
                {
                    value = 255;
                }
				target.image.setRGB(x, y, new Color(value, value, value).getRGB());
			}
		target.repaint();
	}

	public static void main(String[] args) {
		new HoughTransform(args.length==1 ? args[0] : "HoughCircles.png");
	}

}
