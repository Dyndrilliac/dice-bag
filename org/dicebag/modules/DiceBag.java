/*
	Title:  DiceBag
	Author: Matthew Boyette
	Date:   3/31/2012
	
	This application simulates dice rolls for table top games like Dungeons & Dragons. It accepts input in the form of a string.
	The string should be formatted such that it contains two positive integers separated by the character 'd'. The 'd' is not case
	sensitive. Additionally, it has a new combat tracking system built into it that allows it to keep track of initiative, HP, the
	number of rounds, etc.
	
	Examples: 3d6, 2d8, 1d20, 15D6, 10D10, 4D4, etc.
*/

package org.dicebag.modules;

import api.gui.ApplicationWindow;
import api.gui.RichTextPane;
import api.util.EventHandler;
import api.util.Games;
import api.util.Support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

public class DiceBag implements Serializable
{
	public final static class UniqueComboBoxModel extends DefaultComboBoxModel<String>
	{
		private final static long	serialVersionUID	= 1L;
		
		@Override
		public final void addElement(final String s)
		{
			if (this.getIndexOf(s) == -1)
			{
				super.addElement(s);
			}
		}
	}
	
	private final static long	serialVersionUID		= 1L;
	public final static Font	TEXT_FONT				= new Font("Lucida Console", Font.PLAIN, 14);
	public final static String	INPUT_EXCEPTION_STRING	= "Incorrect input format! Provide two non-negative integers separated by the character 'd'." +
															"\nThe 'd' is not case sensitve." +
															"\nExamples: 3d6, 2d8, 1d20, 15D6, 10D10, 4D4, etc.";
	
	public final static void main(final String[] args)
	{
		new DiceBag(true);
	}
	
	private boolean				debugMode	= false;
	private JComboBox<String>	input		= null;
	private RichTextPane		output		= null;
	private ApplicationWindow	window		= null;
	
	public DiceBag(final boolean showWindow)
	{
		this.setDebugging(Support.promptDebugMode(this.getWindow()));
		
		// Define a self-contained ActionListener event handler.
		EventHandler myActionPerformed = new EventHandler(this)
		{
			private final static long	serialVersionUID	= 1L;

			@Override
			public final void run(final Object... arguments) throws IllegalArgumentException, RuntimeException
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
				
				ActionEvent			event	= (ActionEvent)arguments[0];
				//ApplicationWindow	window	= (ApplicationWindow)arguments[1];
				DiceBag				parent	= ((DiceBag)this.parent);
				
				if ((parent.getOutput() != null) && (parent.getInput() != null))
				{
					/*
						JDK 7 allows string objects as the expression in a switch statement.
						This generally produces more efficient byte code compared to a chain of if statements.
						http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
					*/
					switch (event.getActionCommand())
					{
						case "Clear":
							
							parent.clearLog();
							break;
						
						case "Combat Tracker":
							
							new CombatTracker(parent, parent.isDebugging());
							break;
						
						case "Open":
							
							parent.openLog();
							break;
							
						case "Point Buy Calculator":
							
							new PointBuyCalculator();
							break;
							
						case "Challenge Rating Calculator":
							
							new ChallengeRatingCalculator();
							break;
							
						case "Encounter Level Calculator":
							
							new EncounterLevelCalculator();
							break;
						
						case "Save":
							
							parent.saveLog();
							break;
						
						case "Throw":
							
							Object obj = parent.getInput().getSelectedItem();
							
							if ((obj != null) && (((String)obj).isEmpty() == false))
							{
								parent.processInput(((String)parent.getInput().getSelectedItem()).toLowerCase());
							}
							break;
						
						default:
							
							break;
					}
				}
			}
		};
		
		// Define a self-contained interface construction event handler.
		EventHandler myDrawGUI = new EventHandler(this)
		{
			private final static long	serialVersionUID	= 1L;

			@Override
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
				
				ApplicationWindow	window		= (ApplicationWindow)arguments[0];
				Container			contentPane	= window.getContentPane();
				DiceBag				parent		= ((DiceBag)this.parent);
				JMenuBar			menuBar		= new JMenuBar();
				JMenu				toolsMenu	= new JMenu("Tools");
				JMenuItem			ctOption	= new JMenuItem("Combat Tracker");
				JMenuItem			pbOption	= new JMenuItem("Point Buy Calculator");
				JMenuItem			crOption	= new JMenuItem("Challenge Rating Calculator");
				JMenuItem			elOption	= new JMenuItem("Encounter Level Calculator");
				JMenu				fileMenu	= new JMenu("File");
				JMenuItem			clearOption	= new JMenuItem("Clear");
				JMenuItem			openOption	= new JMenuItem("Open");
				JMenuItem			saveOption	= new JMenuItem("Save");
				RichTextPane		outputBox	= new RichTextPane(window, true, window.isDebugging(), DiceBag.TEXT_FONT);
				JComboBox<String>	inputBox	= new JComboBox<String>();
				JButton				inputBtn	= new JButton("Throw");
				
				menuBar.setFont(DiceBag.TEXT_FONT);
				toolsMenu.setFont(DiceBag.TEXT_FONT);
				ctOption.setFont(DiceBag.TEXT_FONT);
				pbOption.setFont(DiceBag.TEXT_FONT);
				crOption.setFont(DiceBag.TEXT_FONT);
				elOption.setFont(DiceBag.TEXT_FONT);
				fileMenu.setFont(DiceBag.TEXT_FONT);
				clearOption.setFont(DiceBag.TEXT_FONT);
				openOption.setFont(DiceBag.TEXT_FONT);
				saveOption.setFont(DiceBag.TEXT_FONT);
				contentPane.setLayout(new BorderLayout());
				clearOption.addActionListener(window);
				fileMenu.add(clearOption);
				openOption.addActionListener(window);
				fileMenu.add(openOption);
				saveOption.addActionListener(window);
				fileMenu.add(saveOption);
				menuBar.add(fileMenu);
				ctOption.addActionListener(window);
				crOption.addActionListener(window);
				elOption.addActionListener(window);
				toolsMenu.add(ctOption);
				toolsMenu.add(crOption);
				toolsMenu.add(elOption);
				pbOption.addActionListener(window);
				toolsMenu.add(pbOption);
				menuBar.add(toolsMenu);
				window.setJMenuBar(menuBar);
				
				toolsMenu.setMnemonic('T');
				fileMenu.setMnemonic('F');
				openOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
				openOption.setMnemonic('O');
				saveOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
				saveOption.setMnemonic('S');
				clearOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
				clearOption.setMnemonic('C');
				ctOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.Event.ALT_MASK));
				ctOption.setMnemonic('M');
				pbOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.Event.ALT_MASK));
				pbOption.setMnemonic('B');
				crOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.ALT_MASK));
				crOption.setMnemonic('C');
				elOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.ALT_MASK));
				elOption.setMnemonic('E');
				
				JScrollPane	outputPanel	= new JScrollPane(outputBox);
				JPanel		inputPanel	= new JPanel();
				
				outputBox.setBackground(Color.WHITE);
				inputBox.setEditable(true);
				inputBox.setFont(DiceBag.TEXT_FONT);
				inputBox.setModel(new UniqueComboBoxModel());
				inputBtn.setFont(DiceBag.TEXT_FONT);
				inputBtn.addActionListener(window);
				inputPanel.setLayout(new FlowLayout());
				inputPanel.add(inputBox);
				inputPanel.add(inputBtn);
				contentPane.add(outputPanel, BorderLayout.CENTER);
				contentPane.add(inputPanel, BorderLayout.SOUTH);
				parent.setInput(inputBox);
				parent.setOutput(outputBox);
				window.getRootPane().setDefaultButton(inputBtn);
				window.setFont(DiceBag.TEXT_FONT);
			}
		};
		
		this.setWindow(new ApplicationWindow(null, "Dice Bag", new Dimension(1100, 600), this.isDebugging(), true, myActionPerformed, myDrawGUI));
		this.getWindow().setIconImageByResourceName("icon.png");
		
		if (!showWindow)
		{
			this.getWindow().toBack();
			this.getWindow().setVisible(false);
		}
	}
	
	public final void clearLog()
	{
		this.getOutput().clear();
		this.getInput().grabFocus();
	}
	
	public final JComboBox<String> getInput()
	{
		return this.input;
	}
	
	public final RichTextPane getOutput()
	{
		return this.output;
	}
	
	public final ApplicationWindow getWindow()
	{
		return this.window;
	}
	
	public final boolean isDebugging()
	{
		return this.debugMode;
	}
	
	public final void openLog()
	{
		this.getOutput().openOrSaveFile(true);
		this.getInput().grabFocus();
	}
	
	public int processInput(final String inputString)
	{
		boolean	isInputBad	= true;
		int		retVal		= -1;
		
		if ((inputString != null) && (inputString.isEmpty() == false))
		{
			if (inputString.matches("[0-9]+d[0-9]+"))
			{
				if (this.getWindow().isDebugging())
				{
					Support.displayDebugMessage(this.getWindow(), "Input: " + inputString + "\n");
				}
				
				String[]	paramArray		= inputString.split("d");
				int[]		resultsArray	= Games.throwDice(Integer.parseInt(paramArray[0]), Integer.parseInt(paramArray[1]));
				int			upperBound		= (resultsArray.length - 1);
				
				StringBuilder outputStringBuilder = new StringBuilder();
				
				for (int i = 0; i < upperBound; i++)
				{
					outputStringBuilder.append(resultsArray[i] + " ");
				}
				
				this.getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, "Input\t\t",
					Color.GRAY, Color.WHITE, inputString + "\n");
				
				this.getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.GREEN, Color.WHITE, "Sum\t\t",
					Color.GRAY, Color.WHITE, resultsArray[upperBound] + "\n");
				
				this.getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.BLUE, Color.WHITE, "Results\t",
					Color.GRAY, Color.WHITE, outputStringBuilder.toString() + "\n\n");
				
				isInputBad	= false;
				retVal		= resultsArray[upperBound];
				this.getInput().addItem(inputString);
			}
		}
		
		if (isInputBad)
		{
			Support.displayException(this.getWindow(), new IllegalArgumentException(DiceBag.INPUT_EXCEPTION_STRING), false);
		}
		
		this.getInput().setSelectedIndex(-1);
		this.getInput().grabFocus();
		return retVal;
	}
	
	public final void saveLog()
	{
		this.getOutput().openOrSaveFile(false);
		this.getInput().grabFocus();
	}
	
	protected final void setDebugging(final boolean debugMode)
	{
		this.debugMode = debugMode;
	}
	
	public final void setInput(final JComboBox<String> input)
	{
		this.input = input;
	}
	
	public final void setOutput(final RichTextPane output)
	{
		this.output = output;
	}
	
	protected final void setWindow(final ApplicationWindow window)
	{
		this.window = window;
	}
}