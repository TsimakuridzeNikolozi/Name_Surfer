/*
 * File: NameSurferGraph.java
 * ---------------------------
 * This class represents the canvas on which the graph of
 * names is drawn. This class is responsible for updating
 * (redrawing) the graphs whenever the list of entries changes or the window is resized.
 */

import acm.graphics.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

public class NameSurferGraphExtension extends GCanvas
	implements NameSurferConstants, ComponentListener {
	
	// instance variables
	
	private String FONT_NAME = "Arial";
	private Font myFont = new Font(FONT_NAME , Font.PLAIN , (int)((getWidth() + getHeight())/100));
	
	private ArrayList<NameSurferEntry> entries = new ArrayList<NameSurferEntry>();
	private Color[] colorArray = {Color.black, Color.red, Color.blue, Color.cyan, Color.green, Color.DARK_GRAY, Color.magenta};
	
	private GLabel selectedLabel;
	private GLabel lastSelectedLabel;
		
	private ArrayList<GLabel> nameLabels = new ArrayList<GLabel>();
	private ArrayList<GLabel> tableLabels = new ArrayList<GLabel>();
	
	public String view = "Graph";

	/**
	* Creates a new NameSurferGraph object that displays the data.
	*/
	public NameSurferGraphExtension() {
		addComponentListener(this);
		
		addMouseListener(new MouseAdapter() { // for deleting certain graphs or making them bolder
		        @Override
		        public void mouseClicked(MouseEvent e) {
		            makeChosenLabelsBold(e);
		        }
	    });
	}
	
	private void makeChosenLabelsBold(MouseEvent e) { // if the user clicks on a label , it's font size increases and the others are set to the default
		try {
			lastSelectedLabel = selectedLabel; 
			if (getElementAt(e.getX() , e.getY()).getColor() != Color.white) { // checking if the user clicked on a canvas
				selectedLabel = (GLabel) getElementAt(e.getX() , e.getY()); // getting the selected label
				
				StringTokenizer st = new StringTokenizer(selectedLabel.getLabel() , " ");
				String name = st.nextToken();
				
				selectedLabel.setFont(new Font(FONT_NAME , Font.BOLD ,selectedLabel.getFont().getSize()));
				
				for(int i = 0; i < nameLabels.size(); i++) { // makes labels bold
					StringTokenizer st1 = new StringTokenizer(nameLabels.get(i).getLabel() , " ");
					String name1 = st1.nextToken();
					if (name.equals(name1)) {
						nameLabels.get(i).setFont(new Font(FONT_NAME , Font.BOLD , nameLabels.get(i).getFont().getSize()));
					}
				}
				
				if(lastSelectedLabel != null) { // if there is a label which was selected before the current one, it's font is returned to plain
					makeLastChosenLabelsPlain();
				}
				
			} else { // if the user clicks on a canvas (which is white) selected labels return to plain font
				makeLastChosenLabelsPlain();
			}
        } catch(Exception ex) { // if user clicks on anything except label , everything is reset
        	selectedLabel = null;
        	makeLastChosenLabelsPlain();
        	lastSelectedLabel = null;
        }
	}
	
	private void makeLastChosenLabelsPlain() { // makes the font of the previously selected labels plain
		if (lastSelectedLabel != null) {
			lastSelectedLabel.setFont(new Font(FONT_NAME , Font.PLAIN , lastSelectedLabel.getFont().getSize()));
			StringTokenizer st = new StringTokenizer(lastSelectedLabel.getLabel() , " ");
			String name = st.nextToken();
			for(int i = 0; i < nameLabels.size(); i++) {
	    		StringTokenizer st1 = new StringTokenizer(nameLabels.get(i).getLabel() , " ");
				String name1 = st1.nextToken();
				if (name.equals(name1)) {
					nameLabels.get(i).setFont(new Font(FONT_NAME , Font.PLAIN , nameLabels.get(i).getFont().getSize()));
				}
			}
			if (compare(name)) { //in case user clicked on the same label two times in a row
				selectedLabel = null;
			}
		}
	}
	
	private boolean compare(String name) { // compares the lastSelectedLabel and SelectedLabels 
		if (selectedLabel != null) {
			StringTokenizer st = new StringTokenizer(selectedLabel.getLabel() , " ");
			if (st.nextToken().equals(name)) {
				return true;
			}
		}
		return false;
	}
		
	private void addVerticalLines() { // vertical lines
		for(int i = 0; i < NDECADES - 1; i++) {
			add(new GLine( (i + 1) * getWidth()/NDECADES , 0 ,  (i + 1) * getWidth()/NDECADES , getHeight()));
		}
	}
	
	private void addHorizontalLines() { // horizontal lines
		add(new GLine(0 , GRAPH_MARGIN_SIZE , getWidth() , GRAPH_MARGIN_SIZE));
		add(new GLine(0 , getHeight() - GRAPH_MARGIN_SIZE, getWidth() , getHeight() - GRAPH_MARGIN_SIZE));
	}
	
	private void addDecadeLabels() { // decades
		for(int i = 0; i < NDECADES; i++) {
			GLabel decadeLabel = new GLabel("" + (START_DECADE + 10 * i));
			decadeLabel.setFont(myFont);
			add(decadeLabel , 2 + i * getWidth()/NDECADES , getHeight() - GRAPH_MARGIN_SIZE + myFont.getSize() * 3/2);
		}
	}
	
	/**
	* Clears the list of name surfer entries stored inside this class.
	*/
	public void clear() {
		entries.clear();
		update();
	}
	
	/* Method: addEntry(entry) */
	/**
	* Adds a new NameSurferEntry to the list of entries on the display.
	* Note that this method does not actually draw the graph, but
	* simply stores the entry; the graph is drawn by calling update.
	*/
	public void addEntry(NameSurferEntry entry) {
		boolean check = true;
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i) == entry) {
				update();
				check = false;
			}
		}
		if (check) {
			entries.add(entry);
			update();
		}
	}
	
	
	
	/**
	* Updates the display image by deleting all the graphical objects
	* from the canvas and then reassembling the display according to
	* the list of entries. Your application must call update after
	* calling either clear or addEntry; update is also called whenever
	* the size of the canvas changes.
	*/
	public void update() {
		if (view == "Graph") {
			drawGraph();
		} else if (view == "Table") {
			drawTable();
		}
	}
	
	private void drawGraph() { // draws a regular graph for each entry
		view = "Graph";
		removeAll();
		updateFont();
		addVerticalLines();
		addHorizontalLines();
		addDecadeLabels();
		drawEntries();
	}

	private void drawEntries() { // adds labels and graph lines for the entries, calls addGraphsForRankZero and addGraphForTop1000 methods
		for (int i = 0; i < entries.size(); i++) {
			Color color = generateColor(i); // generates the colour which will be used for labels and lines
			for(int j = 0; j < NDECADES; j++) {
				if (entries.get(i) != null) {
					GLabel nameLabel = new GLabel(entries.get(i).getName());
					GLine line = null;
					
					nameLabel.setFont(myFont);
					nameLabel.setColor(color);
					
					double convert = (double)(getHeight() - 2 * GRAPH_MARGIN_SIZE)/MAX_RANK; // used for placing lines and labels at corresponding places
					
					if (entries.get(i).getRank(j) == 0) {
						addGraphsForRankZero(nameLabel , line , convert, i , j , color);
					} else {
						addGraphsForRankTop1000(nameLabel , line , convert , i , j , color);
					}
				}
			}
		}
	}
	
	// adds graph labels and lines for the 0 ranked decades for each entry
	private void addGraphsForRankZero(GLabel nameLabel , GLine line , double convert , int i , int j , Color color) {
		nameLabel.setLabel(nameLabel.getLabel() + " *");
		double yCor = getHeight() - GRAPH_MARGIN_SIZE - nameLabel.getDescent();
		
		addCheckPoints(yCor + new GLabel("").getDescent(), j , color);
		
		add(nameLabel ,  4 + j * getWidth()/NDECADES , nameLabelPlacement(yCor, i , j));
		nameLabels.add(nameLabel);
		
		drawGraphLine(line , yCor + new GLabel("").getDescent(), convert , i , j , color);
	}
	
	// adds graph labels and lines for the decades in which the entries didn't have a 0 rank
	private void addGraphsForRankTop1000(GLabel nameLabel , GLine line , double convert , int i , int j , Color color) {
		nameLabel.setLabel(nameLabel.getLabel() + " " + entries.get(i).getRank(j));
		double yCor = GRAPH_MARGIN_SIZE + (entries.get(i).getRank(j) * convert);
		
		addCheckPoints(yCor , j , color);
		
		add(nameLabel ,  4 + j * getWidth()/NDECADES , nameLabelPlacement(yCor , i , j));
		nameLabels.add(nameLabel);
		
		drawGraphLine(line , yCor , convert , i , j , color);
	}
	
	private void drawGraphLine(GLine line , double yCor , double convert , int i , int j , Color color) { // draws a single graph line
		if (j != NDECADES - 1) { // checking if the graph is at the last decade
			line = new GLine(j * getWidth()/NDECADES , yCor, (j + 1) * getWidth()/NDECADES ,
					entries.get(i).getRank(j + 1) > 0 ? GRAPH_MARGIN_SIZE + (entries.get(i).getRank(j + 1) * convert) : getHeight() - GRAPH_MARGIN_SIZE);
			line.setColor(color);
			add(line);
		}
	}
	
	private Color generateColor(int i) { // generates colour for each entry
		if (i > colorArray.length - 1) {
			while(i > colorArray.length - 1) {
				i -= colorArray.length;
			}
		}
		return colorArray[i];
	}
	
	
	private double nameLabelPlacement(double yCor , int i , int j) { // moves the nameLabel up or down if there is an object on an intended point
		int direction = checkDirection(i , j) , directionChanged = 0 , count = 0;
		double unchangedYCor = yCor;
		if (entries.get(i).getRank(j) != 0) {
			yCor += direction * myFont.getSize();
		}
		while(surroundingsAreNotClear(yCor , i , j)) {
			yCor += direction * myFont.getSize();
			count ++;
			if (count >= getHeight()/myFont.getSize() || directionChanged >= 2) {
				return unchangedYCor;
			}
			if (yCor <= GRAPH_MARGIN_SIZE || yCor >= getHeight() - GRAPH_MARGIN_SIZE) {
				direction *= -1;
				yCor += direction * myFont.getSize();
				directionChanged++;
			}
		}
		return yCor;
	}
	
	private int checkDirection(int i , int j) { // checks whether the label should be moved up or down
		if (j != NDECADES - 1 && entries.get(i) != null) {
			if (entries.get(i).getRank(j) == 0) {
				return -1;
			}
			if (entries.get(i).getRank(j) <= 5) {
				return 1;
			}
			if (entries.get(i).getRank(j) <= entries.get(i).getRank(j+1)) {
				return -1;
			} else {
				return 1;
			}
		} else if (j == NDECADES - 1){
			return -1;
		} else {
			return 0;
		}
	}
	
	private boolean surroundingsAreNotClear(double yCor , int i , int j) {
		for(int k = 0; k < myFont.getSize(); k++) {
			if (entries.get(i).getRank(j) == 0) {
				if (getElementAt(4 + j * getWidth()/NDECADES , yCor + k) != null) {
					return true;
				}
			} else if(entries.get(i).getRank(j) <= 5) {
				if (getElementAt(4 + j * getWidth()/NDECADES , yCor - k) != null) {
					return true;
				}
			} else {
				if (getElementAt(4 + j * getWidth()/NDECADES , yCor + k) != null || getElementAt(4 + j * getWidth()/NDECADES , yCor - i) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private void addCheckPoints(double yCor , double j , Color color) { // adds checkpoints for every decade by drawing circles
		GOval checkPoint = new GOval(j * getWidth()/NDECADES - 2 , yCor - 2 , 4 , 4);
		checkPoint.setFilled(true);
		checkPoint.setColor(color);
		add(checkPoint);
	}
	
	public void updateFont() { // increases or decreases the font size depending on the changes in window size
		int fontSize;
		fontSize = (getWidth() + getHeight())/100;
		myFont = new Font(FONT_NAME , Font.PLAIN , fontSize);
	}
	
	public void delete() { // deletes the selected graph
		if (selectedLabel != null) {
			StringTokenizer st = new StringTokenizer(selectedLabel.getLabel() , " ");
			String name = st.nextToken();
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i) != null) {
					if (entries.get(i).getName().equals(name)) {
						entries.remove(i);
						entries.add(i , null);
					}
				}
			}
		}
		update();
	}
	
	public void drawTable() { // draws the data table for entries
		view = "Table";
		Scrollbar scrollBar = new Scrollbar(Scrollbar.VERTICAL , getHeight() , 10 , 1 , getHeight());
		scrollBar.setVisible(true);
		add("East" , scrollBar);
		removeAll();
		updateFont();
		drawTableGrid();
		addDefaultLabels();
		addNamesAndRanks();
	}
	
	private void drawTableGrid() { // lines for the table grid
		for(int i = 0; i < NDECADES; i++) { // vertical lines for the grid
			add(new GLine( (i + 1) * getWidth()/(NDECADES + 1) , 0 ,  (i + 1) * getWidth()/(NDECADES + 1) , getHeight()));
		}
		double yCoordinate = getHeight()/20;
		add(new GLine(0 , yCoordinate , getWidth() , yCoordinate));
		yCoordinate += getHeight()/20;
		for (int i = 0; i < entries.size(); i++) { // horizontal lines for the grid
			if (entries.get(i) != null) {
				add(new GLine(0 , yCoordinate , getWidth() , yCoordinate));
				yCoordinate += getHeight()/20;
			}
		}
	}
	
	private void addDefaultLabels() { // adds Label with the text "Names" and decades on the top of the window
		GLabel namesLabel = new GLabel("Names");
		namesLabel.setFont(myFont);
		double xCoordinate = getWidth()/ (2 *(NDECADES + 1)) - namesLabel.getWidth()/2;
		add(namesLabel , xCoordinate , getHeight()/40 + namesLabel.getHeight()/2);
		tableLabels.add(namesLabel);
		for (int i = 0; i < NDECADES; i++) {
			xCoordinate += getWidth()/ (NDECADES + 1);
			GLabel rankLabel = new GLabel("" + (START_DECADE + 10 * i));
			rankLabel.setFont(myFont);
			add(rankLabel , xCoordinate - rankLabel.getWidth()/2 + namesLabel.getWidth()/2 , getHeight()/40 + rankLabel.getHeight()/2);
			tableLabels.add(rankLabel);
		}
	}
	
	private void addNamesAndRanks() { // adds the names and rankings of entries
		GLabel exampleLabel = new GLabel("");
		exampleLabel.setFont(myFont);
		double nameYCoordinate = getHeight()/40 + exampleLabel.getHeight()/2 + getHeight()/20;
		
		for(int i = 0; i < entries.size(); i++) {
			if (entries.get(i) != null) {
				GLabel nameLabel = new GLabel(entries.get(i).getName());
				Color color = generateColor(i);
				nameLabel.setColor(color);
				nameLabel.setFont(myFont);
				double nameXCoordinate = getWidth() / (2 *(NDECADES + 1)) - nameLabel.getWidth()/2 , rankXCoordinate = nameXCoordinate;
				add(nameLabel , nameXCoordinate , nameYCoordinate);
				tableLabels.add(nameLabel);
				
				for(int j = 0; j < NDECADES; j++) {
					GLabel rankLabel = new GLabel("" + entries.get(i).getRank(j));
					rankLabel.setColor(color);
					rankLabel.setFont(myFont);
					rankXCoordinate += getWidth()/(NDECADES + 1);
					add(rankLabel , rankXCoordinate + nameLabel.getWidth()/2 - rankLabel.getWidth()/2 , nameYCoordinate);
					tableLabels.add(rankLabel);
				}
				nameYCoordinate += getHeight()/20;
			}
		}
	}
	
	public void scroll(int direction) { // scrolling function
		for(GLabel label: tableLabels) {
			label.move(0, direction * (getHeight()/20));
		}
	}

	/* Implementation of the ComponentListener interface */
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e) { update(); }
	public void componentShown(ComponentEvent e) { }
}
