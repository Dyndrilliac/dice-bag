/*
 * Title: DiceBag
 * Author: Matthew Boyette
 * Date: 3/31/2012
 *
 * This application simulates dice rolls for table top games like Dungeons & Dragons. It accepts input in the form of a string.
 * The string should be formatted such that it contains two positive integers separated by the character 'd'. The 'd' is not case
 * sensitive. Additionally, it has a new combat tracking system built into it that allows it to keep track of initiative, HP, the
 * number of rounds, etc.
 *
 * Examples: 3d6, 2d8, 1d20, 15D6, 10D10, 4D4, etc.
 */

package org.dicebag.modules;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
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

import api.gui.swing.ApplicationWindow;
import api.gui.swing.RichTextPane;
import api.util.EventHandler;
import api.util.Games;
import api.util.Support;

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

	public final static String	INPUT_EXCEPTION_STRING	= "Incorrect input format! Provide two non-negative integers separated by the character 'd'." +
		"\nThe 'd' is not case sensitve." +
		"\nExamples: 3d6, 2d8, 1d20, 15D6, 10D10, 4D4, etc.";

	private final static long	serialVersionUID		= 1L;

	public final static void main(final String[] args)
	{
		new DiceBag(true, args);
	}

	private JComboBox<String>	input		= null;
	private boolean				isDebugging	= false;
	private RichTextPane		output		= null;
	private ApplicationWindow	window		= null;

	public DiceBag(final boolean showWindow, final String[] args)
	{
		this.setDebugging(Support.promptDebugMode(this.getWindow()));

		// Define a self-contained ActionListener event handler.
		EventHandler<DiceBag> myActionPerformed = new EventHandler<DiceBag>(this)
			{
			private final static long	serialVersionUID	= 1L;

			@Override
			public final void run(final AWTEvent event)
			{
				ActionEvent actionEvent = (ActionEvent)event;
				DiceBag parent = this.getParent();

				if ((parent.getOutput() != null) && (parent.getInput() != null))
				{
					/*
					 * JDK 7 allows string objects as the expression in a switch statement.
					 * This generally produces more efficient byte code compared to a chain of if statements.
					 * http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
					 */
					switch (actionEvent.getActionCommand())
					{
						case "Clear":

							parent.getOutput().clear();
							parent.getInput().grabFocus();
							break;

						case "Combat Tracker":

							new CombatTracker(parent, parent.isDebugging());
							break;

						case "Open":

							parent.getOutput().openOrSaveFile(true);
							parent.getInput().grabFocus();
							break;

						case "Point Buy Calculator":

							new PointBuyCalculator(parent, parent.isDebugging());
							break;

						case "Encounter Level Calculator":

							new EncounterCalculator(parent, parent.isDebugging());
							break;

						case "Save":

							parent.getOutput().openOrSaveFile(false);
							parent.getInput().grabFocus();
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
			EventHandler<DiceBag> myDrawGUI = new EventHandler<DiceBag>(this)
				{
				private final static long	serialVersionUID	= 1L;

				@Override
				public final void run(final ApplicationWindow window)
				{
					DiceBag parent = this.getParent();
					Container contentPane = window.getContentPane();
					JMenuBar menuBar = new JMenuBar();
					JMenu fileMenu = new JMenu("File");
					JMenuItem clearOption = new JMenuItem("Clear");
					JMenuItem openOption = new JMenuItem("Open");
					JMenuItem saveOption = new JMenuItem("Save");
					JMenu toolsMenu = new JMenu("Tools");
					JMenuItem ctOption = new JMenuItem("Combat Tracker");
					JMenuItem elOption = new JMenuItem("Encounter Calculator");
					JMenuItem pbOption = new JMenuItem("Point Buy Calculator");
					RichTextPane outputBox = new RichTextPane(window, true, window.isDebugging());
					JScrollPane outputPanel = new JScrollPane(outputBox);
					JComboBox<String> inputBox = new JComboBox<String>();
					JButton inputBtn = new JButton("Throw");
					JPanel inputPanel = new JPanel();

					clearOption.setFont(Support.DEFAULT_TEXT_FONT);
					clearOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
					clearOption.setMnemonic('C');
					clearOption.addActionListener(window);
					openOption.setFont(Support.DEFAULT_TEXT_FONT);
					openOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
					openOption.setMnemonic('O');
					openOption.addActionListener(window);
					saveOption.setFont(Support.DEFAULT_TEXT_FONT);
					saveOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
					saveOption.setMnemonic('S');
					saveOption.addActionListener(window);
					fileMenu.setFont(Support.DEFAULT_TEXT_FONT);
					fileMenu.setMnemonic('F');
					fileMenu.add(clearOption);
					fileMenu.add(openOption);
					fileMenu.add(saveOption);
					ctOption.setFont(Support.DEFAULT_TEXT_FONT);
					ctOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.ALT_MASK));
					ctOption.setMnemonic('C');
					ctOption.addActionListener(window);
					elOption.setFont(Support.DEFAULT_TEXT_FONT);
					elOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.Event.ALT_MASK));
					elOption.setMnemonic('E');
					elOption.addActionListener(window);
					pbOption.setFont(Support.DEFAULT_TEXT_FONT);
					pbOption.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.ALT_MASK));
					pbOption.setMnemonic('P');
					pbOption.addActionListener(window);
					toolsMenu.setFont(Support.DEFAULT_TEXT_FONT);
					toolsMenu.setMnemonic('T');
					toolsMenu.add(ctOption);
					toolsMenu.add(elOption);
					toolsMenu.add(pbOption);
					menuBar.setFont(Support.DEFAULT_TEXT_FONT);
					menuBar.add(fileMenu);
					menuBar.add(toolsMenu);
					window.setJMenuBar(menuBar);
					inputBox.setEditable(true);
					inputBox.setFont(Support.DEFAULT_TEXT_FONT);
					inputBox.setModel(new UniqueComboBoxModel());
					inputBtn.setFont(Support.DEFAULT_TEXT_FONT);
					inputBtn.addActionListener(window);
					inputPanel.setLayout(new FlowLayout());
					inputPanel.add(inputBox);
					inputPanel.add(inputBtn);
					contentPane.add(outputPanel, BorderLayout.CENTER);
					contentPane.add(inputPanel, BorderLayout.SOUTH);
					parent.setInput(inputBox);
					parent.setOutput(outputBox);
					window.getRootPane().setDefaultButton(inputBtn);
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
		return this.isDebugging;
	}

	public long processInput(final String inputString)
	{
		boolean isInputBad = true;
		long retVal = -1;

		if ((inputString != null) && (inputString.isEmpty() == false))
		{
			if (inputString.matches("[0-9]+d[0-9]+"))
			{
				if (this.getWindow().isDebugging())
				{
					Support.displayDebugMessage(this.getWindow(), "Input: " + inputString + "\n");
				}

				String[] paramArray = inputString.split("d");
				int[] resultsArray = Games.throwDice(Integer.parseInt(paramArray[0]), Integer.parseInt(paramArray[1]));
				int upperBound = (resultsArray.length - 1);

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

				isInputBad = false;
				retVal = resultsArray[upperBound];
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

	protected final void setDebugging(final boolean debugMode)
	{
		this.isDebugging = debugMode;
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