/*
	Title:  CombatTracker (DiceBag Add-on)
	Author: Matthew Boyette
	Date:   2/19/2014
	
	This class is an add-on which allows a DM to track combat information like health, initiative, and the current round.
	It also interfaces with the DiceBag class so that initiative die rolls are recorded in the log automatically.
*/

import api.gui.*;
import api.util.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CombatTracker
{
	public static class Creature implements Comparable<Creature>
	{
		public static final String STRING_FORMAT = "{Name: [%-25s] HP: [%03d/%03d] Init: [%02d] Pos: [%-4s]}";
		
		private int		curHealth	= 0;
		private DiceBag	diceRoller	= null;
		private int		initBase	= 0;
		private int		initBonus	= 0;
		private int		maxHealth	= 0;
		private String	name		= null;
		private String	position	= null;
		private int		tieBreaker	= 0;
		private int		totalInit	= 0;
		
		public Creature(final DiceBag diceBag, final String name, final int curHealth, final int initBase, final int initBonus, final int maxHealth, final String position)
		{
			this.setDiceRoller(diceBag);
			this.setCurHealth(curHealth);
			this.setInitBase(initBase);
			this.setInitBonus(initBonus);
			this.setMaxHealth(maxHealth);
			this.setName(name);
			this.setPosition(position);
			this.setTotalInit(this.getInitBase() + this.getInitBonus());
		}
		
		public Creature(final DiceBag diceBag, final String name, final int initBase, final int initBonus, final int maxHealth, final String position)
		{
			this.setDiceRoller(diceBag);
			this.setCurHealth(maxHealth);
			this.setInitBase(initBase);
			this.setInitBonus(initBonus);
			this.setMaxHealth(maxHealth);
			this.setName(name);
			this.setPosition(position);
			this.setTotalInit(this.getInitBase() + this.getInitBonus());
		}
		
		@Override
		// Implements initiative as the natural ordering mechanism for the Creature class.
		// See d20 SRD Initiative rules: http://www.d20srd.org/srd/combat/initiative.htm
		public int compareTo(final Creature creature)
		{
			if (creature.getTotalInit() == this.getTotalInit())
			{
				if (creature.getInitBonus() == this.getInitBonus())
				{
					do
					{
						creature.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling tie-breaker for " + creature.getName() + "...\n");
						creature.setTieBreaker(this.getDiceRoller().processInput("1d20"));
						this.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling tie-breaker for " + this.getName() + "...\n");
						this.setTieBreaker(this.getDiceRoller().processInput("1d20"));
					}
					while (creature.getTieBreaker() == this.getTieBreaker());
					
					return (creature.getTieBreaker() - this.getTieBreaker());
				}
				else
				{
					return (creature.getInitBonus() - this.getInitBonus());
				}
			}
			else
			{
				return (creature.getTotalInit() - this.getTotalInit());
			}
		}
		
		public void damage(final int amount)
		{
			this.setCurHealth(this.getCurHealth() - amount);
		}
		
		public final int getCurHealth()
		{
			return this.curHealth;
		}
		
		public final DiceBag getDiceRoller()
		{
			return this.diceRoller;
		}
		
		public final int getInitBase()
		{
			return this.initBase;
		}
		
		public final int getInitBonus()
		{
			return this.initBonus;
		}
		
		public final int getMaxHealth()
		{
			return this.maxHealth;
		}
		
		public final String getName()
		{
			return this.name;
		}
		
		public final String getPosition()
		{
			return this.position;
		}
		
		public final int getTieBreaker()
		{
			return this.tieBreaker;
		}
		
		public final int getTotalInit()
		{
			return this.totalInit;
		}
		
		public void heal(final int amount)
		{
			this.setCurHealth(this.getCurHealth() + amount);
		}
		
		public final void setCurHealth(final int curHealth)
		{
			this.curHealth = curHealth;
		}
		
		public final void setDiceRoller(final DiceBag diceRoller)
		{
			this.diceRoller = diceRoller;
		}
		
		public final void setInitBase(final int initBase)
		{
			this.initBase = initBase;
		}
		
		public final void setInitBonus(final int initBonus)
		{
			this.initBonus = initBonus;
		}
		
		public final void setMaxHealth(final int maxHealth)
		{
			this.maxHealth = maxHealth;
		}
		
		public final void setName(final String name)
		{
			this.name = name;
		}
		
		public final void setPosition(final String position)
		{
			this.position = position;
		}
		
		public final void setTieBreaker(final int tieBreaker)
		{
			this.tieBreaker = tieBreaker;
		}
		
		public final void setTotalInit(final int totalInit)
		{
			this.totalInit = totalInit;
		}
		
		@Override
		public String toString()
		{
			return String.format(Creature.STRING_FORMAT, this.getName(), this.getCurHealth(), this.getMaxHealth(), this.getTotalInit(), this.getPosition());
		}
	}
	
	public final static Font		TEXT_FONT			= new Font("Lucida Console", Font.PLAIN, 14);
	public final static String		WINDOW_TITLE			= "Combat Tracker" + " - " + "Round: ";
	
	private JComboBox<Creature>		cboCreatureList		= null;
	private LinkedList<Creature>	creatureList		= null;
	private Creature				curCreature			= null;
	private int						curCreatureIndex	= 0;
	private boolean					higherInitKillFlag  = false;
	private boolean					isDebugging			= false;
	private JLabel					lblCurrentCreature	= null;
	private int						numEnemyTypes		= 0;
	private int						numPlayers			= 0;
	private int						numRounds			= 0;
	private DiceBag					parent				= null;
	private ApplicationWindow		window				= null;
	
	public CombatTracker(final DiceBag parent, final boolean isDebugging)
	{
		this.setParent(parent);
		this.setDebugging(isDebugging);
		this.reset();
		
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
				Creature			current	= null;
				Creature			target	= null;
				CombatTracker		parent	= ((CombatTracker)this.parent);
				RichTextPane		output  = parent.getParent().getOutput();
				
				
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
					case "Damage":
						
						if (target != null)
						{
							int amount = parent.getIntegerInputString("How much?\nEnter zero to cancel.", "Damage " + target.getName());
							
							if (amount != 0)
							{
								target.setCurHealth(target.getCurHealth() - amount);
								output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
											  Color.RED, Color.WHITE, "Damaging " + target.getName() + " for " + amount + " HP.\n\n");
								
								if (target.getCurHealth() < 1)
								{
									parent.getCreatureList().remove(target);
									
									if (target.getTotalInit() > current.getTotalInit())
									{
										parent.setHigherInitKillFlag(true);
									}
									
									output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
												  Color.RED, Color.WHITE, target.getName() + " has been reduced to zero or less HP (KO'd).\n\n");
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
								target.setCurHealth(target.getCurHealth() + amount);
								output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
											  Color.GREEN, Color.WHITE, "Healing " + target.getName() + " for " + amount + " HP.\n\n");
								
								if (target.getCurHealth() > target.getMaxHealth())
								{
									target.setCurHealth(target.getMaxHealth());
								}
								
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
						
						parent.nextCombatant();
						current = parent.getCurrentCreature();
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
									  Color.BLACK, Color.WHITE, "Next Combatant:\n",
									  Color.GRAY, Color.WHITE, "\t\t\t   " + current.toString() + "\n\n");
						break;
						
					case "Reset":
						
						output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
									  Color.BLACK, Color.WHITE, "Resetting Combat Parameters...\n\n");
						parent.reset();
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
				JButton				btnDamage		= new JButton("Damage");
				JButton				btnHeal			= new JButton("Heal");
				JButton				btnMove			= new JButton("Move");
				JButton				btnNext			= new JButton("Next");
				JButton				btnReset		= new JButton("Reset");
				JPanel				buttonPanel		= new JPanel();
				JPanel				cboPanel		= new JPanel();
				CombatTracker		combatTracker	= (CombatTracker)this.parent;
				Container			contentPane		= window.getContentPane();
				JPanel				curPanel		= new JPanel();
				
				btnDamage.setFont(CombatTracker.TEXT_FONT);
				btnDamage.addActionListener(window);
				btnHeal.setFont(CombatTracker.TEXT_FONT);
				btnHeal.addActionListener(window);
				btnMove.setFont(CombatTracker.TEXT_FONT);
				btnMove.addActionListener(window);
				btnNext.setFont(CombatTracker.TEXT_FONT);
				btnNext.addActionListener(window);
				btnReset.setFont(CombatTracker.TEXT_FONT);
				btnReset.addActionListener(window);
				buttonPanel.setLayout(new FlowLayout());
				buttonPanel.add(btnDamage);
				buttonPanel.add(btnHeal);
				buttonPanel.add(btnMove);
				buttonPanel.add(Box.createHorizontalStrut(15));
				buttonPanel.add(btnNext);
				buttonPanel.add(btnReset);
				combatTracker.setCboCreatureList(new JComboBox<Creature>());
				combatTracker.getCboCreatureList().setFont(CombatTracker.TEXT_FONT);
				combatTracker.getCboCreatureList().setEditable(false);
				combatTracker.setLblCurrentCreature(new JLabel("Current: " + combatTracker.getCurrentCreature().toString()));
				combatTracker.getLblCurrentCreature().setFont(CombatTracker.TEXT_FONT);
				cboPanel.setLayout(new FlowLayout());
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
		
		this.setWindow(new ApplicationWindow(this.getParent().getWindow(), CombatTracker.WINDOW_TITLE + this.getNumRounds(),
			new Dimension(800, 114), this.isDebugging(), false, myActionPerformed, myDrawGUI));
		this.getWindow().setIconImageByResourceName("icon.png");
	}
	
	public final JComboBox<Creature> getCboCreatureList()
	{
		return this.cboCreatureList;
	}
	
	public final ApplicationWindow getWindow()
	{
		return this.window;
	}
	
	public final LinkedList<Creature> getCreatureList()
	{
		return this.creatureList;
	}
	
	public final Creature getCurrentCreature()
	{
		return this.curCreature;
	}
	
	public final int getCurCreatureIndex()
	{
		return this.curCreatureIndex;
	}
	
	public final String getCoordinateInputString(final String message, final String title)
	{
		String s;
		
		do
		{
			s = this.getInputString(message, title);
		}
		while (!s.matches("[1-9]:[0-9][0-9]"));
		
		return s;
	}
	
	public final String getInputString(final String message, final String title)
	{
		ApplicationWindow windowHandle = null;
		
		if (this.getWindow() != null)
		{
			windowHandle = this.getWindow();
		}
		else
		{
			windowHandle = this.getParent().getWindow();
		}
		
		return Support.getInputString(windowHandle, message, title);
	}
	
	public final int getIntegerInputString(final String message, final String title)
	{
		ApplicationWindow windowHandle = null;
		
		if (this.getWindow() != null)
		{
			windowHandle = this.getWindow();
		}
		else
		{
			windowHandle = this.getParent().getWindow();
		}
		
		return Support.getIntegerInputString(windowHandle, message, title);
	}
	
	public final JLabel getLblCurrentCreature()
	{
		return this.lblCurrentCreature;
	}
	
	public final int getNumEnemyTypes()
	{
		return this.numEnemyTypes;
	}
	
	public final int getNumPlayers()
	{
		return this.numPlayers;
	}
	
	public final int getNumRounds()
	{
		return this.numRounds;
	}
	
	public final DiceBag getParent()
	{
		return this.parent;
	}
	
	public final boolean isDebugging()
	{
		return this.isDebugging;
	}
	
	public final boolean isHigherInitKillFlag()
	{
		return this.higherInitKillFlag;
	}
	
	public void nextCombatant()
	{
		boolean	KilledCreatureHigherInit	= false;
		int		index						= (this.getCurCreatureIndex() + 1);
		
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
	}
	
	public void reset()
	{
		this.setNumRounds(1);
		this.setCreatureList(new LinkedList<Creature>());
		this.setNumPlayers(this.getIntegerInputString("How many players?", "Combat Setup"));
		this.setNumEnemyTypes(this.getIntegerInputString("How many enemy types?", "Combat Setup"));
		
		for (int i = 1; i <= this.getNumPlayers(); i++)
		{
			String	name		= this.getInputString("What is player " + i + "'s character name?", "Combat Setup");
			String	position	= this.getCoordinateInputString("What is player " + i + "'s battle grid position?" +
																"\nPrompt expects X:YY coordinates.", "Combat Setup");
			
			this.parent.getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + name + "...\n");
			
			int			initBase	= this.parent.processInput("1d20");
			int			curHealth	= this.getIntegerInputString("What is player " + i + "'s current health?", "Combat Setup");
			int			maxHealth	= this.getIntegerInputString("What is player " + i + "'s maximum health?", "Combat Setup");
			int			initBonus	= this.getIntegerInputString("What is player " + i + "'s initiative modifier?", "Combat Setup");
			Creature	player		= new Creature(this.getParent(), name, curHealth, initBase, initBonus, maxHealth, position);
			
			this.getCreatureList().add(player);
		}
		
		for (int i = 1; i <= this.getNumEnemyTypes(); i++)
		{
			String	name		= this.getInputString("What is enemy type " + i + "'s name?", "Combat Setup");
			int		numEnemies	= this.getIntegerInputString("How many enemies are there of type " + i + "?", "Combat Setup");
			int		maxHealth	= this.getIntegerInputString("What is enemy type " + i + "'s maximum health?", "Combat Setup");
			int		initBonus	= this.getIntegerInputString("What is enemy type " + i + "'s initiative modifier?", "Combat Setup");
			
			for (int j = 1; j <= numEnemies; j++)
			{
				String position = this.getCoordinateInputString("What is " + name + " " + j + "'s battle grid position?" +
																"\nPrompt expects X:YY coordinates.", "Combat Setup");
				
				this.parent.getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + name + " " + j + "...\n");
				
				int			initBase	= this.parent.processInput("1d20");
				Creature	enemy		= new Creature(this.getParent(), name + " " + j, initBase, initBonus, maxHealth, position);
				
				this.getCreatureList().add(enemy);
			}
		}
		
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
	
	public final void setCboCreatureList(final JComboBox<Creature> cboCreatureList)
	{
		this.cboCreatureList = cboCreatureList;
	}
	
	public final void setWindow(final ApplicationWindow window)
	{
		this.window = window;
	}
	
	public final void setCreatureList(final LinkedList<Creature> creatureList)
	{
		this.creatureList = creatureList;
	}
	
	public final void setCurrentCreature(final Creature creature)
	{
		this.curCreature = creature;
	}
	
	public final void setCurCreatureIndex(final int curCreatureIndex)
	{
		this.curCreatureIndex = curCreatureIndex;
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
	
	public final void setNumEnemyTypes(final int numEnemyTypes)
	{
		this.numEnemyTypes = numEnemyTypes;
	}
	
	public final void setNumPlayers(final int numPlayers)
	{
		this.numPlayers = numPlayers;
	}
	
	public final void setNumRounds(final int numRounds)
	{
		this.numRounds = numRounds;
	}
	
	public final void setParent(final DiceBag parent)
	{
		this.parent = parent;
	}
}