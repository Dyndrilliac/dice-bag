/*
	Title:  CombatTracker
	Author: Matthew Boyette
	Date:   2/19/2014

	This class is an add-on module for DiceBag which allows a DM to track combat information like health, initiative, and the current round.
	It interfaces with the DiceBag class so that initiative die rolls are recorded in the log automatically. Currently only the v3.5 d20 rules
	are implemented but in a future version users will be able to seamlessly switch configurations.
*/

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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
					case "Save":
						
						if (target != null)
						{
							target.openOrSaveFile(parent.getWindow(), false, parent.isDebugging());
						}
						break;
						
					case "Damage":
						
						if (target != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Damage " + target.getName());
							
							if (amount != 0)
							{
								target.damage(amount);
								
								if (target.getStatus() == Creature.Status.DEAD)
								{
									parent.getCreatureList().remove(target);
									
									if (target.getTotalInit() > current.getTotalInit())
									{
										parent.setHigherInitKillFlag(true);
									}
									else if ((target.getTotalInit() == current.getTotalInit()) && (target.getInitBonus() > current.getInitBonus()))
									{
										parent.setHigherInitKillFlag(true);
									}
									else if ((target.getTotalInit() == current.getTotalInit()) && 
											(target.getInitBonus() == current.getInitBonus()) && 
											(target.getTieBreaker() > current.getTieBreaker()))
									{
										parent.setHigherInitKillFlag(true);
									}
								}
								
								parent.getWindow().reDrawGUI();
							}
						}
						break;
					
					case "Heal":
						
						if (target != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Heal " + target.getName());
							
							if (amount != 0)
							{
								target.heal(amount);
								window.reDrawGUI();
							}
						}
						break;
					
					case "Move":
						
						if (target != null)
						{
							String prevPosition = target.getPosition();
							String nextPosition = parent.getCoordinateInputString("Where to? Prompt expects X:YY coordinates." +
								"\nEnter " + prevPosition + " to cancel.",
								"Move " + target.getName());
							if (!nextPosition.equals(prevPosition))
							{
								target.setPosition(nextPosition);
								output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
									Color.BLUE, Color.WHITE, "Moving " + target.getName() + " from " + prevPosition + " to " + nextPosition + ".\n\n");
								window.reDrawGUI();
							}
						}
						break;
					
					case "Next":
						
						Creature next = parent.nextCombatant();
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Next Combatant:\n",
							Color.GRAY, Color.WHITE, "\t\t\t   " + next.toString() + "\n\n");
						break;
					
					case "Reset All":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Resetting All Parameters...\n\n");
						parent.reset(1);
						window.reDrawGUI();
						break;
					
					case "Reset Characters":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
							Color.BLACK, Color.WHITE, "Resetting Character Parameters...\n\n");
						parent.reset(2);
						window.reDrawGUI();
						break;
					
					case "Reset Monsters":
						
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
				
				ApplicationWindow	window			= (ApplicationWindow)arguments[0];
				JButton				btnSave			= new JButton("Save");
				JButton				btnDamage		= new JButton("Damage");
				JButton				btnHeal			= new JButton("Heal");
				JButton				btnMove			= new JButton("Move");
				JButton				btnNext			= new JButton("Next");
				JButton				btnResetAll		= new JButton("Reset All");
				JButton				btnResetChars	= new JButton("Reset Characters");
				JButton				btnResetMons	= new JButton("Reset Monsters");
				JPanel				buttonPanel		= new JPanel();
				JPanel				cboPanel		= new JPanel();
				JLabel				cboLabel		= new JLabel("Target: ");
				CombatTracker		combatTracker	= (CombatTracker)this.parent;
				Container			contentPane		= window.getContentPane();
				JPanel				curPanel		= new JPanel();
				
				btnDamage.setFont(CombatTracker.TEXT_FONT);
				btnDamage.addActionListener(window);
				btnHeal.setFont(CombatTracker.TEXT_FONT);
				btnHeal.addActionListener(window);
				btnMove.setFont(CombatTracker.TEXT_FONT);
				btnMove.addActionListener(window);
				btnSave.setFont(CombatTracker.TEXT_FONT);
				btnSave.addActionListener(window);
				btnNext.setFont(CombatTracker.TEXT_FONT);
				btnNext.addActionListener(window);
				btnResetAll.setFont(CombatTracker.TEXT_FONT);
				btnResetAll.addActionListener(window);
				btnResetChars.setFont(CombatTracker.TEXT_FONT);
				btnResetChars.addActionListener(window);
				btnResetMons.setFont(CombatTracker.TEXT_FONT);
				btnResetMons.addActionListener(window);
				buttonPanel.setLayout(new FlowLayout());
				buttonPanel.add(btnSave);
				buttonPanel.add(btnDamage);
				buttonPanel.add(btnHeal);
				buttonPanel.add(btnMove);
				buttonPanel.add(Box.createHorizontalStrut(15));
				buttonPanel.add(btnNext);
				buttonPanel.add(Box.createHorizontalStrut(15));
				buttonPanel.add(btnResetAll);
				buttonPanel.add(btnResetChars);
				buttonPanel.add(btnResetMons);
				combatTracker.setCboCreatureList(new JComboBox<Creature>());
				combatTracker.getCboCreatureList().setFont(CombatTracker.TEXT_FONT);
				combatTracker.getCboCreatureList().setEditable(false);
				combatTracker.setLblCurrentCreature(new JLabel("Current: " + combatTracker.getCurrentCreature().toString()));
				combatTracker.getLblCurrentCreature().setFont(CombatTracker.TEXT_FONT);
				cboLabel.setFont(CombatTracker.TEXT_FONT);
				cboPanel.setLayout(new FlowLayout());
				cboPanel.add(cboLabel);
				cboPanel.add(combatTracker.getCboCreatureList());
				curPanel.setLayout(new FlowLayout());
				curPanel.add(combatTracker.getLblCurrentCreature());
				contentPane.setLayout(new BorderLayout());
				contentPane.add(curPanel, BorderLayout.NORTH);
				contentPane.add(cboPanel, BorderLayout.CENTER);
				contentPane.add(buttonPanel, BorderLayout.SOUTH);
				
				for (int i = 0; i < combatTracker.getCreatureList().size(); i++)
				{
					combatTracker.getCboCreatureList().addItem(combatTracker.getCreatureList().get(i));
				}
				
				combatTracker.getCboCreatureList().setSelectedIndex(combatTracker.getCreatureList().indexOf(combatTracker.getCurrentCreature()));
				window.setTitle(CombatTracker.WINDOW_TITLE + combatTracker.getNumRounds());
			}
		};
		
		this.setWindow(new ApplicationWindow(this.getParent().getWindow(), CombatTracker.WINDOW_TITLE + this.getNumRounds(), new Dimension(800, 114), 
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
			boolean		loadFile	= this.getChoiceInput("Would you like to load a previously saved character?", "Load Previously Saved Character?");
			
			if (loadFile)
			{
				character = new CreatureV35(this.getParent(), this, 0);
				
				if (this.getChoiceInput("Would you like to change this character's current HP?", "Change HP?"))
				{
					int curHealth = this.getIntegerInputString("What is character " + i + "'s current health?", "Characters Setup");
					character.setCurHealth(curHealth);
				}
				
				if (this.getChoiceInput("Would you like to change this character's current position?", "Change Position?"))
				{
					String position = this.getCoordinateInputString("What is character " + i + "'s battle grid position?" +
						"\nPrompt expects X:YY coordinates.", "Characters Setup");
					character.setPosition(position);
				}
				
				if (this.getChoiceInput("Would you like to change this character's current initiative?", "Change Initiative?"))
				{
					this.parent.getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + character.getName() + "... ()\n");
					character.setInitBase(this.parent.processInput("1d20"));
					character.setTotalInit(character.getInitBase() + character.getInitBonus());
				}
			}
			else
			{
				String name = this.getInputString("What is character " + i + "'s name?", "Characters Setup");
				String position = this.getCoordinateInputString("What is character " + i + "'s battle grid position?" +
					"\nPrompt expects X:YY coordinates.", "Characters Setup");
				
				this.parent.getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + name + "...\n");
				
				int initBase = this.parent.processInput("1d20");
				int curHealth = this.getIntegerInputString("What is character " + i + "'s current HP?", "Characters Setup");
				int maxHealth = this.getIntegerInputString("What is character " + i + "'s maximum HP?", "Characters Setup");
				int initBonus = this.getIntegerInputString("What is character " + i + "'s initiative modifier?", "Characters Setup");
				
				character = new CreatureV35(curHealth, this.getParent(), this, initBase, initBonus, maxHealth, name, position);
			}
			
			characterList.add(character);
		}
		
		this.setCharacterList(characterList);
	}
	
	public void resetMonsters() // TODO: Enable loading of saved monsters.
	{
		LinkedList<Creature> monsterList = new LinkedList<Creature>();
		this.setNumMonsterTypes(this.getIntegerInputString("How many monster types?", "Monsters Setup"));
		
		for (int i = 1; i <= this.getNumMonsterTypes(); i++)
		{
			String name = this.getInputString("What is monster type " + i + "'s name?", "Monsters Setup");
			int numMonsters = this.getIntegerInputString("How many monsters are there of type " + i + "?", "Monsters Setup");
			int maxHealth = this.getIntegerInputString("What is monster type " + i + "'s maximum HP?", "Monsters Setup");
			int initBonus = this.getIntegerInputString("What is monster type " + i + "'s initiative modifier?", "Monsters Setup");
			
			for (int j = 1; j <= numMonsters; j++)
			{
				String position = this.getCoordinateInputString("What is " + name + " " + j + "'s battle grid position?" +
					"\nPrompt expects X:YY coordinates.", "Monsters Setup");
				
				this.parent.getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + name + " " + j + "...\n");
				
				int initBase = this.parent.processInput("1d20");
				Creature monster = new CreatureV35(this.getParent(), this, initBase, initBonus, maxHealth, name, position);
				
				monsterList.add(monster);
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