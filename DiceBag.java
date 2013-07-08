/*
	Title:  DiceBag
	Author: Matthew Boyette
	Date:   3/31/2012
	
	This application simulates dice rolls for table top games like Dungeons & Dragons. It accepts input in the form of a string.
	The string should be formatted such that it contains two integers separated by the character 'd'.
	
	Examples: 3d6, 2d8, 1d20, etc.
*/
import api.gui.*;
import api.util.*;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.swing.*;

public final class DiceBag
{	
	public static final void main(final String[] args)
	{
		ApplicationWindow mainWindow = null;
		int choice = Support.promptDebugMode(mainWindow);
		
		// Define a self-contained ActionListener event handler.
		EventHandler myActionPerformed = new EventHandler()
		{
			@SuppressWarnings("unchecked")
			public final void run(final Object... arguments) throws IllegalArgumentException
			{
				if ((arguments.length <= 1) || (arguments.length > 2))
				{
					throw new IllegalArgumentException("myActionPerformed Error : incorrect number of arguments.");
				}
				else if (!(arguments[0] instanceof ActionEvent))
				{
					throw new IllegalArgumentException("myActionPerformed Error : argument[0] is of incorrect type.");
				}
				else if (!(arguments[1] instanceof ApplicationWindow))
				{
					throw new IllegalArgumentException("myActionPerformed Error : argument[1] is of incorrect type.");
				}
				
				ActionEvent       event  = (ActionEvent)arguments[0];
				ApplicationWindow window = (ApplicationWindow)arguments[1];
				RichTextPane      output = null;
				JComboBox<String> input  = null;
				
				for (int i = 0; i < window.getElements().size(); i++)
				{
					if (window.getElements().get(i) instanceof RichTextPane)
					{
						output = (RichTextPane)window.getElements().get(i);
					}
				}
				
				for (int i = 0; i < window.getElements().size(); i++)
				{
					if (window.getElements().get(i) instanceof JComboBox<?>)
					{
						input = (JComboBox<String>)window.getElements().get(i);
					}
				}
				
				if ((output != null) && (input != null))
				{
					/*
						JDK 7 allows string objects as the expression in a switch statement.
						This generally produces more efficient byte code compared to a chain of if statements.
						http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
				 	*/
					switch (event.getActionCommand())
					{
						case "Clear":
							
							output.clear();
							input.grabFocus();
							break;
							
						case "Open":
							
							output.openFile();
							input.grabFocus();
							break;
							
						case "Save":
							
							output.saveFile();
							input.grabFocus();
							break;
							
						case "Throw":
							
							processInput(window, output, input);
							break;
							
						default:
							
							break;
					}
				}
			}
		};
		
		// Define a self-contained interface construction event handler.
		EventHandler myDrawGUI = new EventHandler()
		{
			public final void run(final Object... arguments) throws IllegalArgumentException
			{
				if (arguments.length <= 0)
				{
					throw new IllegalArgumentException("myDrawGUI Error : incorrect number of arguments.");
				}
				else if (!(arguments[0] instanceof ApplicationWindow))
				{
					throw new IllegalArgumentException("myDrawGUI Error : argument[0] is of incorrect type.");
				}
				
				ApplicationWindow window      = (ApplicationWindow)arguments[0];
				Container         contentPane = window.getContentPane();
				JMenuBar          menuBar     = new JMenuBar();
				JMenu             fileMenu    = new JMenu("File");
				JMenuItem         clearOption = new JMenuItem("Clear");
				JMenuItem         openOption  = new JMenuItem("Open");
				JMenuItem         saveOption  = new JMenuItem("Save");
				Font              textFont    = new Font("Lucida Console", Font.PLAIN, 14);
				RichTextPane      outputBox   = new RichTextPane((Component)window, true, window.isDebugging(), textFont);
				JComboBox<String> inputBox    = new JComboBox<String>();
				JButton           inputBtn    = new JButton("Throw");
				
				contentPane.setLayout(new BorderLayout());
				clearOption.addActionListener(window);
				fileMenu.add(clearOption);
				openOption.addActionListener(window);
				fileMenu.add(openOption);
				saveOption.addActionListener(window);
				fileMenu.add(saveOption);
				menuBar.add(fileMenu);
				window.setJMenuBar(menuBar);
				
				JScrollPane outputPanel = new JScrollPane(outputBox);
				JPanel      inputPanel  = new JPanel();
				
				inputBox.setFont(textFont);
				inputBox.setEditable(true);
				inputBox.setPreferredSize(new Dimension(600, 25));
				inputBtn.setPreferredSize(new Dimension(100, 25));
				inputBtn.addActionListener(window);
				inputPanel.setLayout(new FlowLayout());
				inputPanel.add(inputBox);
				inputPanel.add(inputBtn);
				contentPane.add(outputPanel, BorderLayout.CENTER);
				contentPane.add(inputPanel, BorderLayout.SOUTH);
				window.getElements().add(outputBox);
				window.getElements().add(inputBox);
				window.getElements().add(inputBtn);
			}
		};
		
		if (choice == JOptionPane.YES_OPTION)
		{
			mainWindow = new ApplicationWindow(null, "DiceBag", new Dimension(800, 600), true, false, 
				myActionPerformed, myDrawGUI);
		}
		else if (choice == JOptionPane.NO_OPTION)
		{
			mainWindow = new ApplicationWindow(null, "DiceBag", new Dimension(800, 600), false, false, 
				myActionPerformed, myDrawGUI);
		}
		else
		{
			return;
		}
		
		mainWindow.setIconImageByResourceName("icon.png");
	}
	
	private static final void processInput(ApplicationWindow window, RichTextPane output, JComboBox<String> input)
	{
		String inputString = (String)input.getSelectedItem();
		
		if ((inputString != null) && (inputString.isEmpty() == false))
		{
			if (inputString.matches("[0-9]+d[0-9]+"))
			{
				if (window.isDebugging())
				{
					Support.displayDebugMessage(window, "Input: " + inputString + "\n");
				}
				
				String[] paramArray   = inputString.split("d");
				int[]    resultsArray = Games.throwDice(Integer.parseInt(paramArray[0]), Integer.parseInt(paramArray[1]));
				int      upperBound   = (resultsArray.length - 1);
				
				StringBuilder outputStringBuilder = new StringBuilder();
				
				for (int i = 0; i < upperBound; i++)
				{
					outputStringBuilder.append(resultsArray[i] + " ");
				}
				
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ", 
					Color.BLUE, Color.WHITE, "Input ", 
					Color.BLACK, Color.WHITE, inputString + "\n");
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ", 
					Color.RED, Color.WHITE, "Results ", 
					Color.BLACK, Color.WHITE, outputStringBuilder.toString() + "\n");
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ", 
					Color.MAGENTA, Color.WHITE, "Sum ", 
					Color.BLACK, Color.WHITE, resultsArray[upperBound] + "\n\n");
				
				if (input.getSelectedIndex() == -1)
				{
					input.addItem((String)input.getSelectedItem());
				}
			}
			else
			{
				Support.displayException(window, new Exception("Incorrect input format! Provide two integers separated the character 'd'. Ex: 3d6, 2d8, 1d20, etc."), false);
			}
		}
		
		input.setSelectedIndex(-1);
		input.grabFocus();
	}
}