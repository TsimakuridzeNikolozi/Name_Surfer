/*
 * File: NameSurfer.java
 * ---------------------
 * When it is finished, this program will implements the viewer for
 * the baby-name database described in the assignment handout.
 */

import acm.program.*;
import acmx.export.javax.swing.JTextPane;

import java.awt.event.*;
import javax.swing.*;

public class NameSurferExtension extends Program implements NameSurferConstants {
	
	// instance variables
	
	private JButton graphButton , clearButton , deleteButton , tableButton , upButton , downButton;
	private JTextField nameField ;
	private JLabel nameLabel;
	
	private NameSurferDataBase dataBase = new NameSurferDataBase(NAMES_DATA_FILE);
	
	private NameSurferEntry currentEntry;
	
	private NameSurferGraphExtension graph = new NameSurferGraphExtension(); 


/* Method: init() */
/**
 * This method has the responsibility for reading in the data base
 * and initializing the interactors at the bottom of the window.
 */
	public void init() {
		add(graph);
		
		nameLabel = new JLabel("Name");
		add(nameLabel , SOUTH);
		
		nameField = new JTextField(20);
		add(nameField  , SOUTH);
		nameField .addActionListener(this);
		
		graphButton = new JButton("Graph");
		add(graphButton , SOUTH);
		graphButton.addActionListener(this);
		
		tableButton = new JButton("Table");
		add(tableButton , SOUTH);
		tableButton.addActionListener(this);
		
		clearButton = new JButton("Clear");
		add(clearButton , SOUTH);
		clearButton.addActionListener(this);
		
		deleteButton = new JButton("Delete");
		add(deleteButton , SOUTH);
		deleteButton.addActionListener(this);
		
		upButton = new JButton("Up");
		add(upButton , SOUTH);
		upButton.setVisible(false);
		upButton.addActionListener(this);
		
		downButton = new JButton("Down");
		add(downButton , SOUTH);
		downButton.setVisible(false);
		downButton.addActionListener(this);
		
		
	}

/* Method: actionPerformed(e) */
/**
 * This class is responsible for detecting when the buttons are
 * clicked, so you will have to define a method to respond to
 * button actions.
 */
	public void actionPerformed(ActionEvent e) {
		// You fill this in //
		if(e.getActionCommand().equals("Graph")) {
			addEntry();
			graph.view = "Graph";
			upButton.setVisible(false);
			downButton.setVisible(false);
			graph.update();
			
		} else if (e.getActionCommand().equals("Table")) {
			addEntry();
			graph.view = "Table";
			upButton.setVisible(true);
			downButton.setVisible(true);
			graph.drawTable();
			
		} else if(e.getSource() == nameField) {
			addEntry();
			graph.update();
			
		} else if (e.getActionCommand().equals("Clear")) {
			graph.clear();
			
		} else if (e.getActionCommand().equals("Delete")) {
			graph.delete();
		} else if (e.getActionCommand().equals("Up")) {
			graph.scroll(1);
		} else if (e.getActionCommand().equals("Down")) {
			graph.scroll(-1);
		}
	}
	
	private void addEntry() {
		if (!nameField .getText().equals("")) {
			currentEntry = dataBase.findEntry(nameField .getText());
			if (currentEntry != null) graph.addEntry(currentEntry);
			
			nameField .setText("");
		} 
	}
}
