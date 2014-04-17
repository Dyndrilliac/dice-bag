/*
	Title:  CombatTracker
	Author: Matthew Boyette
	Date:   2/19/2014

	This class is an add-on module for DiceBag which allows a DM to track combat information like health, initiative, and the current round. It interfaces
	with the DiceBag class so that initiative die rolls are recorded in the log automatically. Currently only the v3.5 d20 rules are implemented but in a
	future version users will be able to seamlessly switch configurations. Saving and loading combatants is also an option, as is only resetting characters
	or monsters if desired. CombatTracker can also be used to track battlefield position.
*/

package org.dicebag.modules;

import org.dicebag.modules.CombatTracker;
import org.dicebag.objects.Constants35E;
import org.dicebag.objects.Creature;
import org.dicebag.objects.Creature35E;
import org.dicebag.objects.StatBlock35E;

import api.gui.ApplicationWindow;
import api.gui.RichTextPane;
import api.util.EventHandler;
import api.util.Support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class CombatTracker implements Serializable
{
	private final static long		serialVersionUID	= 1L;
	public final static Font		TEXT_FONT			= new Font("Lucida Console", Font.PLAIN, 14);
	public final static String		WINDOW_TITLE		= "Combat Tracker" + " - " + "Round: ";
	
	private JComboBox<Creature>		cboCreatureList		= null;
	private LinkedList<Creature>	characterList		= null;
	private LinkedList<Creature>	creatureList		= null;
	private LinkedList<Creature>	monsterList			= null;
	private Creature				curCreature			= null;
	private int						curCreatureIndex	= 0;
	private boolean					higherInitKillFlag	= false;
	private boolean					isDebugging			= false;
	private JLabel					lblCurrentCreature	= null;
	private int						numCharacters		= 0;
	private int						numMonsterTypes		= 0;
	private int						numRounds			= 0;
	private DiceBag					parent				= null;
	private ApplicationWindow		window				= null;
	
	public CombatTracker(final DiceBag parent, final boolean isDebugging)
	{
		this.setParent(parent);
		this.setDebugging(isDebugging);
		this.reset(1);
		
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
				ApplicationWindow	window	= (ApplicationWindow)arguments[1];
				CombatTracker		parent	= ((CombatTracker)this.parent);
				RichTextPane		output	= parent.getParent().getOutput();
				Creature			current	= null;
				Creature			target	= null;
				
				if (parent.getCboCreatureList() != null)
				{
					target = (Creature)parent.getCboCreatureList().getSelectedItem();
				}
				
				if (parent.getCurrentCreature() != null)
				{
					current = parent.getCurrentCreature();
				}
				
				/*
					JDK 7 allows string objects as the expression in a switch statement.
					This generally produces more efficient byte code compared to a chain of if statements.
					http://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
				*/
				switch (event.getActionCommand())
				{
					case "Save Target":
						
						if (target != null)
						{
							target.openOrSaveFile(parent.getWindow(), false, parent.isDebugging());
						}
						break;
						
					case "Damage Target":
						
						if (target != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Damage " + target.getStatBlock().getName());
							
							if (amount != 0)
							{
								target.damage(amount);
								
								if (((Creature35E)target).getStatBlock().getStatus() == Constants35E.Status.DEAD)
								{
									parent.getCreatureList().remove(target);
									
									if (target.getStatBlock().getTotalInit() > current.getStatBlock().getTotalInit())
									{
										parent.setHigherInitKillFlag(true);
									}
									else if ((target.getStatBlock().getTotalInit() == current.getStatBlock().getTotalInit()) 
										&& (target.getStatBlock().getInitBonus() > current.getStatBlock().getInitBonus()))
									{
										parent.setHigherInitKillFlag(true);
									}
									else if ((target.getStatBlock().getTotalInit() == current.getStatBlock().getTotalInit()) && 
											(target.getStatBlock().getInitBonus() == current.getStatBlock().getInitBonus()) && 
											(target.getStatBlock().getTieBreaker() > current.getStatBlock().getTieBreaker()))
									{
										parent.setHigherInitKillFlag(true);
									}
								}
								
								parent.getWindow().reDrawGUI();
							}
						}
						break;
					
					case "Heal Target":
						
						if (target != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Heal " + target.getStatBlock().getName());
							
							if (amount != 0)
							{
								target.heal(amount);
								window.reDrawGUI();
							}
						}
						break;
					
					case "Move Target":
						
						if (target != null)
						{
							String prevPosition = target.getStatBlock().getPosition();
							String nextPosition = parent.getCoordinateInputString("Where to? Prompt expects X:YY coordinates." +
								"\nEnter " + prevPosition + " to cancel.",
								"Move " + target.getStatBlock().getName());
							if (!nextPosition.equals(prevPosition))
							{
								target.getStatBlock().setPosition(nextPosition);
								output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
									Color.BLUE, Color.WHITE, "Moving " + target.getStatBlock().getName() + " from " + prevPosition + " to " + nextPosition + ".\n\n");
								window.reDrawGUI();
							}
						}
						break;
						
					case "Save Current":
						
						if (current != null)
						{
							current.openOrSaveFile(parent.getWindow(), false, parent.isDebugging());
						}
						break;
						
					case "Damage Current":
						
						if (current != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Damage " + current.getStatBlock().getName());
							
							if (amount != 0)
							{
								current.damage(amount);
								
								if (((Creature35E)current).getStatBlock().getStatus() == Constants35E.Status.DEAD)
								{
									parent.getCreatureList().remove(current);
								}
								
								parent.getWindow().reDrawGUI();
							}
						}
						break;
						
					case "Heal Current":
						
						if (current != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Heal " + current.getStatBlock().getName());
							
							if (amount != 0)
							{
								current.heal(amount);
								window.reDrawGUI();
							}
						}
						break;
						
					case "Move Current":
						
						if (current != null)
						{
							String prevPosition = current.getStatBlock().getPosition();
							String nextPosition = parent.getCoordinateInputString("Where to? Prompt expects X:YY coordinates." +
								"\nEnter " + prevPosition + " to cancel.",
								"Move " + current.getStatBlock().getName());
							if (!nextPosition.equals(prevPosition))
							{
								current.getStatBlock().setPosition(nextPosition);
								output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
									Color.BLUE, Color.WHITE, "Moving " + current.getStatBlock().getName() + " from " + prevPosition + " to " + nextPosition + ".\n\n");
								window.reDrawGUI();
							}
						}
						break;
						
					case "Next Combatant":
						
						try
						{
							Creature next = parent.nextCombatant();
							
							output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
								Color.BLACK, Color.WHITE, "Next Combatant:\n",
								Color.GRAY, Color.WHITE, "\t\t\t   " + next.toString() + "\n\n");
						}
						catch (final Exception e)
						{
							break;
						}
						break;
					
					case "Reset All Creatures":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Resetting All Parameters...\n\n");
						parent.reset(1);
						window.reDrawGUI();
						break;
					
					case "Reset Characters Only":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Resetting Character Parameters...\n\n");
						parent.reset(2);
						window.reDrawGUI();
						break;
					
					case "Reset Monsters Only":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Resetting Monster Parameters...\n\n");
						parent.reset(3);
						window.reDrawGUI();
						break;
					
					default:
						
						break;
				
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
				
				/*
					Declare & Initialize GUI Objects 
				*/
				
				ApplicationWindow	window			= (ApplicationWindow)arguments[0];
				Container			contentPane		= window.getContentPane();
				CombatTracker		parent			= (CombatTracker)this.parent;
				JMenuBar			menuBar			= new JMenuBar();
				JMenu				targetMenu		= new JMenu("Target Actions");
				JMenuItem			optSave			= new JMenuItem("Save Target");
				JMenuItem			optDamage		= new JMenuItem("Damage Target");
				JMenuItem			optHeal			= new JMenuItem("Heal Target");
				JMenuItem			optMove			= new JMenuItem("Move Target");
				JMenu				currentMenu		= new JMenu("Current Actions");
				JMenuItem			opcSave			= new JMenuItem("Save Current");
				JMenuItem			opcDamage		= new JMenuItem("Damage Current");
				JMenuItem			opcHeal			= new JMenuItem("Heal Current");
				JMenuItem			opcMove			= new JMenuItem("Move Current");
				JMenuItem			opcNext			= new JMenuItem("Next Combatant");
				JMenu				resetMenu		= new JMenu("Reset Combat");
				JMenuItem			oprAll			= new JMenuItem("Reset All Creatures");
				JMenuItem			oprChars		= new JMenuItem("Reset Characters Only");
				JMenuItem			oprMons			= new JMenuItem("Reset Monsters Only");
				JLabel				curLabel		= new JLabel("Current: " + parent.getCurrentCreature().toString());
				JPanel				curPanel		= new JPanel();
				JLabel				cboLabel		= new JLabel("Target: ");
				JPanel				cboPanel		= new JPanel();
				
				/*
					Configure Menu Bar 
				*/
				
				menuBar.setFont(CombatTracker.TEXT_FONT);
				targetMenu.setFont(CombatTracker.TEXT_FONT);
				optSave.setFont(CombatTracker.TEXT_FONT);
				optSave.addActionListener(window);
				optDamage.setFont(CombatTracker.TEXT_FONT);
				optDamage.addActionListener(window);
				optHeal.setFont(CombatTracker.TEXT_FONT);
				optHeal.addActionListener(window);
				optMove.setFont(CombatTracker.TEXT_FONT);
				optMove.addActionListener(window);
				
				currentMenu.setFont(CombatTracker.TEXT_FONT);
				opcSave.setFont(CombatTracker.TEXT_FONT);
				opcSave.addActionListener(window);
				opcDamage.setFont(CombatTracker.TEXT_FONT);
				opcDamage.addActionListener(window);
				opcHeal.setFont(CombatTracker.TEXT_FONT);
				opcHeal.addActionListener(window);
				opcMove.setFont(CombatTracker.TEXT_FONT);
				opcMove.addActionListener(window);
				opcNext.setFont(CombatTracker.TEXT_FONT);
				opcNext.addActionListener(window);
				
				resetMenu.setFont(CombatTracker.TEXT_FONT);
				oprAll.setFont(CombatTracker.TEXT_FONT);
				oprAll.addActionListener(window);
				oprChars.setFont(CombatTracker.TEXT_FONT);
				oprChars.addActionListener(window);
				oprMons.setFont(CombatTracker.TEXT_FONT);
				oprMons.addActionListener(window);
				
				targetMenu.setMnemonic('T');
				optSave.setMnemonic('S');
				optSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.ALT_MASK));
				optDamage.setMnemonic('D');
				optDamage.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.ALT_MASK));
				optHeal.setMnemonic('H');
				optHeal.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.Event.ALT_MASK));
				optMove.setMnemonic('M');
				optMove.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.Event.ALT_MASK));
				
				currentMenu.setMnemonic('C');
				opcSave.setMnemonic('S');
				opcSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
				opcDamage.setMnemonic('D');
				opcDamage.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
				opcHeal.setMnemonic('H');
				opcHeal.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.Event.CTRL_MASK));
				opcMove.setMnemonic('M');
				opcMove.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.Event.CTRL_MASK));
				opcNext.setMnemonic('N');
				opcNext.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
				
				resetMenu.setMnemonic('R');
				oprAll.setMnemonic('A');
				oprAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.SHIFT_MASK));
				oprChars.setMnemonic('C');
				oprChars.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.Event.SHIFT_MASK));
				oprMons.setMnemonic('M');
				oprMons.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.Event.SHIFT_MASK));
				
				targetMenu.add(optSave);
				targetMenu.add(optDamage);
				targetMenu.add(optHeal);
				targetMenu.add(optMove);
				currentMenu.add(opcSave);
				currentMenu.add(opcDamage);
				currentMenu.add(opcHeal);
				currentMenu.add(opcMove);
				currentMenu.addSeparator();
				currentMenu.add(opcNext);
				resetMenu.add(oprAll);
				resetMenu.add(oprChars);
				resetMenu.add(oprMons);
				menuBar.add(targetMenu);
				menuBar.add(currentMenu);
				menuBar.add(resetMenu);
				
				/*
					Configure Form Elements
				*/

				parent.setCboCreatureList(new JComboBox<Creature>());
				parent.getCboCreatureList().setFont(CombatTracker.TEXT_FONT);
				parent.getCboCreatureList().setEditable(false);
				
				cboLabel.setFont(CombatTracker.TEXT_FONT);
				cboPanel.setLayout(new FlowLayout());
				cboPanel.add(cboLabel);
				cboPanel.add(parent.getCboCreatureList());
				curLabel.setFont(CombatTracker.TEXT_FONT);
				curPanel.setLayout(new FlowLayout());
				curPanel.add(curLabel);
				contentPane.setLayout(new BorderLayout());
				contentPane.add(curPanel, BorderLayout.NORTH);
				contentPane.add(cboPanel, BorderLayout.CENTER);
				
				parent.setLblCurrentCreature(curLabel);
				
				for (int i = 0; i < parent.getCreatureList().size(); i++)
				{
					parent.getCboCreatureList().addItem(parent.getCreatureList().get(i));
				}
				
				parent.getCboCreatureList().setSelectedIndex(parent.getCreatureList().indexOf(parent.getCurrentCreature()));
				window.setJMenuBar(menuBar);
				window.setTitle(CombatTracker.WINDOW_TITLE + parent.getNumRounds());
				window.setFont(CombatTracker.TEXT_FONT);
			}
		};
		
		this.setWindow(new ApplicationWindow(this.getParent().getWindow(), CombatTracker.WINDOW_TITLE + this.getNumRounds(), new Dimension(1100, 125), 
			this.isDebugging(), false, myActionPerformed, myDrawGUI));
		this.getWindow().setIconImageByResourceName("icon.png");
		this.getWindow().pack();
	}
	
	public final JComboBox<Creature> getCboCreatureList()
	{
		return this.cboCreatureList;
	}
	
	public final LinkedList<Creature> getCharacterList()
	{
		return this.characterList;
	}
	
	public final boolean getChoiceInput(final String message, final String title)
	{
		return Support.getChoiceInput(this.getWindow(), message, title);
	}
	
	public String getCoordinateInputString(final String message, final String title)
	{
		String s;
		
		do
		{
			s = this.getInputString(message, title);
		}
		while (!s.matches("[1-9]:[0-9][0-9]"));
		
		return s;
	}
	
	public final LinkedList<Creature> getCreatureList()
	{
		return this.creatureList;
	}
	
	public final int getCurCreatureIndex()
	{
		return this.curCreatureIndex;
	}
	
	public final Creature getCurrentCreature()
	{
		return this.curCreature;
	}
	
	public final String getInputString(final String message, final String title)
	{
		return Support.getInputString(this.getWindow(), message, title);
	}
	
	public final int getIntegerInputString(final String message, final String title)
	{
		return Support.getIntegerInputString(this.getWindow(), message, title);
	}
	
	public final JLabel getLblCurrentCreature()
	{
		return this.lblCurrentCreature;
	}
	
	public final LinkedList<Creature> getMonsterList()
	{
		return this.monsterList;
	}
	
	public final int getNumCharacters()
	{
		return this.numCharacters;
	}
	
	public final int getNumMonsterTypes()
	{
		return this.numMonsterTypes;
	}
	
	public final int getNumRounds()
	{
		return this.numRounds;
	}
	
	public final DiceBag getParent()
	{
		return this.parent;
	}
	
	public final ApplicationWindow getWindow()
	{
		if (this.window != null)
		{
			return this.window;
		}
		else
		{
			return this.getParent().getWindow();
		}
	}
	
	public final boolean isDebugging()
	{
		return this.isDebugging;
	}
	
	public final boolean isHigherInitKillFlag()
	{
		return this.higherInitKillFlag;
	}
	
	public Creature nextCombatant()
	{
		boolean KilledCreatureHigherInit = false;
		int index = (this.getCurCreatureIndex() + 1);
		
		if (this.isHigherInitKillFlag())
		{
			KilledCreatureHigherInit = true;
			this.setHigherInitKillFlag(false);
		}
		
		if ((this.getCreatureList().indexOf(this.getCurrentCreature()) == -1) || (KilledCreatureHigherInit))
		{
			index = this.getCurCreatureIndex();
		}
		
		if (index >= this.getCreatureList().size())
		{
			this.setNumRounds(this.getNumRounds() + 1);
			index = 0;
			
			this.getParent().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
				Color.MAGENTA, Color.WHITE, "- Round " + this.getNumRounds() + " -\n\n");
		}
		
		this.setCurCreatureIndex(index);
		this.setCurrentCreature(this.getCreatureList().get(index));
		this.getWindow().reDrawGUI();
		
		return this.getCurrentCreature();
	}
	
	public void reset(final int numMode)
	{
		this.setNumRounds(1);
		
		switch (numMode)
		{
			case 1:
				
				this.resetCharacters();
				this.resetMonsters();
				break;
			
			case 2:
				
				this.resetCharacters();
				break;
			
			case 3:
				
				this.resetMonsters();
				break;
			
			default:
				
				this.resetCharacters();
				this.resetMonsters();
				break;
		}
		
		this.setCreatureList(new LinkedList<Creature>());
		this.getCreatureList().addAll(this.getCharacterList());
		this.getCreatureList().addAll(this.getMonsterList());
		
		Collections.sort(this.getCreatureList());
		this.setCurrentCreature(this.getCreatureList().getFirst());
		this.setCurCreatureIndex(this.getCreatureList().indexOf(this.getCurrentCreature()));
		
		this.getParent().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.BLACK, Color.WHITE, "Initial Combatants:\n");
		
		for (int i = 0; i < this.getCreatureList().size(); i++)
		{
			this.getParent().getOutput().append(Color.GRAY, Color.WHITE, "\t\t\t   " + this.getCreatureList().get(i).toString() + "\n");
		}
		
		this.getParent().getOutput().append(Color.BLACK, Color.WHITE, "\n");
		
		this.getParent().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.MAGENTA, Color.WHITE, "- Round " + this.getNumRounds() + " -\n\n");
		
		this.getParent().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.BLACK, Color.WHITE, "First Combatant:\n",
			Color.GRAY, Color.WHITE, "\t\t\t   " + this.getCurrentCreature().toString() + "\n\n");
	}
	
	public void resetCharacters()
	{
		LinkedList<Creature> characterList = new LinkedList<Creature>();
		this.setNumCharacters(this.getIntegerInputString("How many characters?", "Characters Setup"));
		
		for (int i = 1; i <= this.getNumCharacters(); i++)
		{
			Creature	character	= null;
			
			if (this.getChoiceInput("Would you like to load a previously saved character?", "Load Previously Saved Character?"))
			{
				character = new Creature35E(this.getParent(), this, new StatBlock35E());
				character.openOrSaveFile(this.getWindow(), true, this.isDebugging());
				
				if (this.getChoiceInput("Would you like to change this character's current HP?", "Change HP?"))
				{
					int curHealth = this.getIntegerInputString("What is the current HP of " + character.getStatBlock().getName() + "?", "Characters Setup");
					character.getStatBlock().setCurHealth(curHealth);
					character.updateStatus();
				}
				
				if (this.getChoiceInput("Would you like to change this character's current position?", "Change Position?"))
				{
					String position = this.getCoordinateInputString("What is the battle grid position of " + character.getStatBlock().getName() + "?" +
						"\nPrompt expects X:YY coordinates.", "Characters Setup");
					character.getStatBlock().setPosition(position);
				}
				
				if (this.getChoiceInput("Would you like to change this character's current initiative?", "Change Initiative?"))
				{
					character.rollInitiative(false);
				}
			}
			else
			{
				String name		= this.getInputString("What is the name of character " + i + "?", "Characters Setup");
				String position	= this.getCoordinateInputString("What is the battle grid position of " + name + "?" +
					"\nPrompt expects X:YY coordinates.", "Characters Setup");
				
				int curHealth = this.getIntegerInputString("What is the current HP of " + name + "?", "Characters Setup");
				int maxHealth = this.getIntegerInputString("What is the maximum HP of " + name + "?", "Characters Setup");
				int initBonus = this.getIntegerInputString("What is the initiative modifier of " + name + "?", "Characters Setup");
				
				character = new Creature35E(this.getParent(), this, new StatBlock35E(curHealth, initBonus, maxHealth, name, position));
				character.updateStatus();
				character.rollInitiative(false);
			}
			
			if (character != null)
			{
				characterList.add(character);
			}
		}
		
		this.setCharacterList(characterList);
	}
	
	public void resetMonsters()
	{
		LinkedList<Creature> monsterList = new LinkedList<Creature>();
		this.setNumMonsterTypes(this.getIntegerInputString("How many types of monsters?", "Monsters Setup"));
		
		for (int i = 1; i <= this.getNumMonsterTypes(); i++)
		{
			int	numMonsters	= this.getIntegerInputString("How many are there of monster type " + i + "?", "Monsters Setup");
			
			if (numMonsters > 0)
			{
				Creature monster = null;
				
				if (this.getChoiceInput("Would you like to load previously saved monsters of type " + i + "?", "Load Previously Saved Monsters?"))
				{
					boolean renameLoadedMonsters = this.getChoiceInput("Would you like to rename loaded monsters in numerical order?",
																			"Rename Loaded Monsters?");
					
					for (int j = 1; j <= numMonsters; j++)
					{
						monster = new Creature35E(this.getParent(), this, new StatBlock35E());
						monster.openOrSaveFile(this.getWindow(), true, this.isDebugging());
						
						if (renameLoadedMonsters)
						{
							int numberIndex = (monster.getStatBlock().getName().length() - 1);
							monster.getStatBlock().setName(monster.getStatBlock().getName().substring(0, numberIndex) + j);
						}
						
						if (this.getChoiceInput("Would you like to change this monster's current HP?", "Change HP?"))
						{
							int curHealth = this.getIntegerInputString("What is the current HP of " + monster.getStatBlock().getName() + "?", "Monsters Setup");
							monster.getStatBlock().setCurHealth(curHealth);
							monster.updateStatus();
						}
						
						if (this.getChoiceInput("Would you like to change this monster's current position?", "Change Position?"))
						{
							String position = this.getCoordinateInputString("What is the battle grid position of " + monster.getStatBlock().getName() + "?" +
								"\nPrompt expects X:YY coordinates.", "Monsters Setup");
							monster.getStatBlock().setPosition(position);
						}
						
						if (this.getChoiceInput("Would you like to change this monster's current initiative?", "Change Initiative?"))
						{
							monster.rollInitiative(false);
						}
						
						if (monster != null)
						{
							monsterList.add(monster);
						}
					}
				}
				else
				{
					String	name		= this.getInputString("What is the name of monster type " + i + "?", "Monsters Setup");
					int		maxHealth	= this.getIntegerInputString("What is the maximum HP of monster type " + i + "?", "Monsters Setup");
					int		initBonus	= this.getIntegerInputString("What is the initiative modifier of monster type " + i + "?", "Monsters Setup");
					
					for (int j = 1; j <= numMonsters; j++)
					{
						name = name + " " + j;
						String position = this.getCoordinateInputString("What is the battle grid position of " + name + "?" +
							"\nPrompt expects X:YY coordinates.", "Monsters Setup");
						
						monster = new Creature35E(this.getParent(), this, new StatBlock35E(initBonus, maxHealth, name, position));
						
						if (this.getChoiceInput("Would you like to change this monster's current HP?", "Change HP?"))
						{
							int curHealth = this.getIntegerInputString("What is the current HP of " + monster.getStatBlock().getName() + "?", "Monsters Setup");
							monster.getStatBlock().setCurHealth(curHealth);
						}
						
						monster.updateStatus();
						monster.rollInitiative(false);
						
						if (monster != null)
						{
							monsterList.add(monster);
						}
					}
				}
			}
		}
		
		this.setMonsterList(monsterList);
	}
	
	public final void setCboCreatureList(final JComboBox<Creature> cboCreatureList)
	{
		this.cboCreatureList = cboCreatureList;
	}
	
	public final void setCharacterList(final LinkedList<Creature> characterList)
	{
		this.characterList = characterList;
	}
	
	public final void setCreatureList(final LinkedList<Creature> creatureList)
	{
		this.creatureList = creatureList;
	}
	
	public final void setCurCreatureIndex(final int curCreatureIndex)
	{
		this.curCreatureIndex = curCreatureIndex;
	}
	
	public final void setCurrentCreature(final Creature creature)
	{
		this.curCreature = creature;
	}
	
	public final void setDebugging(final boolean isDebugging)
	{
		this.isDebugging = isDebugging;
	}
	
	public final void setHigherInitKillFlag(final boolean higherInitKillFlag)
	{
		this.higherInitKillFlag = higherInitKillFlag;
	}
	
	public final void setLblCurrentCreature(final JLabel lblCurrentCreature)
	{
		this.lblCurrentCreature = lblCurrentCreature;
	}
	
	public final void setMonsterList(final LinkedList<Creature> monsterList)
	{
		this.monsterList = monsterList;
	}
	
	public final void setNumCharacters(final int numCharacters)
	{
		this.numCharacters = numCharacters;
	}
	
	public final void setNumMonsterTypes(final int numMonsterTypes)
	{
		this.numMonsterTypes = numMonsterTypes;
	}
	
	public final void setNumRounds(final int numRounds)
	{
		this.numRounds = numRounds;
	}
	
	public final void setParent(final DiceBag parent)
	{
		this.parent = parent;
	}
	
	public final void setWindow(final ApplicationWindow window)
	{
		this.window = window;
	}
}