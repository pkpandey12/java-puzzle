import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * JavaPuzzle class which which allows the user to select an image as a puzzle and the user can then play with the selected images.
 * @author Praneet Kumar Pandey
 *
 */
public class JavaPuzzle {
	private ArrayList<Point> solutions = new ArrayList<Point>();
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	private JFrame jf;
	private JButton[][] grid;
	private JPanel gridPanel;
	private JButton prevButton, currButton;
	private ImageIcon display_img;
	private Image img1;
	private JTextArea text;
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		JavaPuzzle ob = new JavaPuzzle();
		ob.begin();
	}
	/**
	 * This function initializes the JFrame and other components of the GUI,
	 * and also initializes the files needed to make this game work
	 */
	public void begin() {
		//Declaring GUI objects
		this.openDialog();
		jf=new JFrame ("JavaPuzzle");
		gridPanel = new JPanel();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel buttonBar= new JPanel();
		JPanel textArea = new JPanel();
		JPanel wrapper = new JPanel();

		//Button area handling
		JButton load = new JButton("Load Another Image");
		load.addActionListener(new LoadListener());
		JButton showImage = new JButton("Show Orginal Image");
		showImage.addActionListener(new showImage());
		JButton exit=new JButton("Exit");
		exit.addActionListener(new exitClass());
		buttonBar.add(load);
		buttonBar.add(showImage);
		buttonBar.add(exit);

		//text area handling
		text = new JTextArea();

		text.setLineWrap(true);
		JScrollPane scroller = new JScrollPane(text);
		scroller.setPreferredSize(new Dimension(750, 50));
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		text.setText("Game started!");
		textArea.add(scroller);

		wrapper.add(textArea);
		wrapper.add(buttonBar);
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

		//Image/Play Area handling


		Image temp1= display_img.getImage();
		ImageIcon img=new ImageIcon(temp1.getScaledInstance(800, 800, Image.SCALE_SMOOTH));
		img1 = img.getImage();

		gridPanel.setLayout(new GridLayout (10,10));
		grid = new JButton[10][10];
		for(int y=0;y<10;y++) {
			for(int x=0; x<10; x++) {
				Image image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img1.getSource(), new CropImageFilter(x * 800 / 10, y * 800 / 10, 100, 100)));
				ImageIcon icon = new ImageIcon(image);
				JButton temp = new JButton(icon);
				temp.putClientProperty("position", new Point(y,x));
				solutions.add(new Point(y,x));
				temp.putClientProperty("isLocked", false);
				temp.addMouseListener(new DragMouseAdapter());
				grid[x][y]=temp;
				//gridPanel.add(grid[x][y]);
				buttons.add(temp);
			}
		}
		Collections.shuffle(buttons);
		for(JButton j: buttons) {
			gridPanel.add(j);
		}

		jf.getContentPane().add(BorderLayout.SOUTH, wrapper);


		jf.add(gridPanel);
		jf.pack();
		jf.setSize(800, 1200);
		jf.setVisible(true);
	}
	/**
	 * This function checks the image locations against the solution
	 */
	public void checkSolution() {
		ArrayList<Point> current = new ArrayList<>();

        for (JComponent btn : buttons) {
            current.add((Point) btn.getClientProperty("position"));
        }

        if (compareList(solutions, current)) {
        	JOptionPane.showMessageDialog(jf,"You win!!!.");
        	System.exit(0);
        }
	}
	/**
	 * Comparator to enable comparisons in checkSolution()
	 * @param solutions2 an ArrayList
	 * @param current another ArrayList
	 * @return if both their contents are the same
	 */
	private boolean compareList(ArrayList<Point> solutions2, ArrayList<Point> current) {
		return solutions2.toString().contentEquals(current.toString());
	}
	/**
	 * This function opens the filechooser dialog box and sets the image which can be played with
	 */
	public void openDialog() {
		String filename = this.getFileName();
		if (filename.equals(null) || filename.equals("")  ) {
			text.append("\nPlease select valid image");
			openDialog();
		}
		display_img = this.loadImage(filename);

	}
	/**
	 * This functions rebuilds all buttons based on cropping the chosen image and arranging the new elements
	 */
	public void buildButtons() {
		Image temp1= display_img.getImage();
		ImageIcon img=new ImageIcon(temp1.getScaledInstance(800, 800, Image.SCALE_SMOOTH));
		img1 = img.getImage();
		for(int y=0;y<10;y++) {
			for(int x=0; x<10; x++) {
				Image image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img1.getSource(), new CropImageFilter(x * 800 / 10, y * 800 / 10, 100, 100)));
				ImageIcon icon = new ImageIcon(image);
				JButton temp = new JButton(icon);
				temp.putClientProperty("position", new Point(y,x));
				solutions.add(new Point(y,x));
				temp.putClientProperty("isLocked", false);
				temp.addMouseListener(new DragMouseAdapter());
				grid[x][y]=temp;

				buttons.add(temp);
			}
		}
	}
/**
 * Clas DragMouseAdapter which facilitates mouse Drag and Drop operations
 * @author Praneet Kumar Pandey
 *
 */
class DragMouseAdapter implements MouseListener{

		public void mouseEntered(MouseEvent e) {
			currButton = (JButton) e.getSource();
		}
		public void mousePressed(MouseEvent e) {
			prevButton = (JButton)e.getSource();

		}
		public void mouseReleased(MouseEvent e) {
			if(!((boolean)prevButton.getClientProperty("isLocked")||(boolean)currButton.getClientProperty("isLocked"))) {
			int p = buttons.indexOf(prevButton);
			int c = buttons.indexOf(currButton);
			Collections.swap(buttons, p, c);
			gridPanel.removeAll();
			for(JButton j: buttons) {
				gridPanel.add(j);
			}
			gridPanel.validate();

			int index = buttons.indexOf(prevButton);

			if((new Point(index/10 , index%10)).equals((Point)prevButton.getClientProperty("position"))) {
				prevButton.putClientProperty("isLocked",true);
				text.append("\nImage block in correct position!");
			}

			checkSolution();
			}
			else {
				prevButton = null;
			}


			}

		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
	}

	/**
	 * This function creates the JFileChooser dialog-box and returns the filepath of the chosen file
	 * @return the filepath of the file chosen
	 */
	public String getFileName() {
		String filename="";
		JFileChooser file = new JFileChooser();
		file.setCurrentDirectory(new File(System.getProperty("user.home")));
		file.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "tif");
		file.addChoosableFileFilter(filter);
		file.setAcceptAllFileFilterUsed(true);

		int temp=file.showOpenDialog(jf);
		if(temp==JFileChooser.APPROVE_OPTION) {
			File selectedFile = file.getSelectedFile();
			if(selectedFile.length()==0) {
				JOptionPane.showMessageDialog(jf,
					    "ERROR: Image cannot be opened."
						+"\nPress OK to select another image",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return getFileName();
			}
			filename=selectedFile.getAbsolutePath();
		}
		else if(temp==JFileChooser.CANCEL_OPTION) {
			System.exit(0);
		}
		else if(temp==JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(jf,
				    "ERROR: Image cannot be opened."
					+"\nPress OK to select another image",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return getFileName();
		}

		return filename;
	}
	/**
	 * This function takes the selected image, scales it and returns the scaled image
	 * @param filename the filepath of the image
	 * @return the image, now scaled to 800x800
	 */
	private ImageIcon loadImage(String filename)  {
		ImageIcon MyImage = new ImageIcon (filename);
		Image img = MyImage.getImage();

		Image newImg = img.getScaledInstance(800,800, Image.SCALE_SMOOTH);
		ImageIcon image = new ImageIcon(newImg);
		return image;

    }
	/**
	 * LoadListener class which allows the user to load another image to the board and start playing
	 * @author Praneet Kumar Pandey
	 *
	 */
	public class LoadListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			openDialog();
			gridPanel.removeAll();
			buttons.clear();
			buildButtons();
			Collections.shuffle(buttons);
			for(JButton j: buttons) {
				gridPanel.add(j);
			}
			gridPanel.validate();
			//jf.repaint();
		}
	}
	/**
	 * This class enable the "Show Original Image" function in the game by loading the image into a Graphics object
	 * @author Praneet Kumar Pandey
	 *
	 */
	public class ImageDisplay extends JPanel {
		public void paintComponent(Graphics g) {
			Image img = display_img.getImage();
			g.drawImage(img,0,0,this);
		}
	}
	/**
	 * This class enables the "Show Original Image" function in the game by displaying the scaled original in a new JFrame
	 * @author Praneet Kumar Pandey
	 *
	 */
	class showImage implements ActionListener{
		public void actionPerformed (ActionEvent e) {
			JFrame show = new JFrame();
			show.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			ImageDisplay id = new ImageDisplay();
			show.getContentPane().add(id);

			show.setSize(800,800);
			show.setVisible(true);
		}
	}
	/**
	 * This class enable the user the exit the game by selecting the Exit option in the game,
	 * It's equivalent to pressing X on the window
	 * @author Praneet Kumar Pandey
	 *
	 */
	class exitClass implements ActionListener{
		public void actionPerformed (ActionEvent e) {
			jf.dispose();
		}
	}
}
