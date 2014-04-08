/*
	Title:  Creature
	Author: Matthew Boyette
	Date:   2/19/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It was originally located nested within the
	CombatTracker class but I have separated it out to avoid duplicating code when using similar objects in other modules.
	
	This class is an abstract class that other classes extend. This allows for alternative initiative logic, different HP thresholds,
	and other customized programming.
*/

import api.gui.RichTextPane;
import api.util.Support;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Creature implements Comparable<Creature>, Serializable
{
	public static enum Status
	{
		HEALTHY, BLOODIED, DISABLED, DYING, UNCONCIOUS, DEAD
	}
	
	public static enum Effects
	{
		// TODO
	}
	
	private	final static long	serialVersionUID	= 1L;
	
	public static String		STRING_FORMAT() { return "{Name: [%-25s] HP: [%03d/%03d] Init: [%02d] Pos: [%-4s] Status: [%-12]}";	}
	public static int			DISABLED_HP()	{ return 0;																			}
	public static int			DYING_HP()		{ return -1;																		}
	public static int			DEAD_HP()		{ return -10;																		}
	
	private int					curHealth			= 0;
	private DiceBag				diceRoller			= null;
	private CombatTracker		combatTracker		= null;
	private int					initBase			= 0;
	private int					initBonus			= 0;
	private int					maxHealth			= 0;
	private String				name				= null;
	private String				position			= null;
	private Status				status				= null;
	private int					tieBreaker			= 0;
	private int					totalInit			= 0;
	
	public Creature(final DiceBag diceBag, final CombatTracker combatTracker, final int initBase)
	{
		this.openOrSaveFile(combatTracker.getWindow(), true, combatTracker.isDebugging());
		this.setDiceRoller(diceBag);
		this.setCombatTracker(combatTracker);
		this.setInitBase(initBase);
		this.setTotalInit(this.getInitBase() + this.getInitBonus());
	}
	
	public Creature(final DiceBag diceBag, final CombatTracker combatTracker, final int initBase, final int initBonus, final int maxHealth, final String name,
		final String position)
	{
		this(maxHealth, diceBag, combatTracker, initBase, initBonus, maxHealth, name, position);
	}
	
	public Creature(final int curHealth, final DiceBag diceBag, final CombatTracker combatTracker, final int initBase, final int initBonus, final int maxHealth,
		final String name, final String position)
	{
		this.setDiceRoller(diceBag);
		this.setCombatTracker(combatTracker);
		this.setInitBase(initBase);
		this.setInitBonus(initBonus);
		this.setMaxHealth(maxHealth);
		this.setCurHealth(curHealth);
		this.setName(name);
		this.setPosition(position);
		this.setStatus(Status.HEALTHY);
		this.setTotalInit(this.getInitBase() + this.getInitBonus());
	}
	
	// Implements initiative as the natural ordering mechanism for the Creature class.
	// See d20 SRD Initiative rules: http://www.d20srd.org/srd/combat/initiative.htm
	@Override
	public int compareTo(final Creature creature)
	{
		if (creature.getTotalInit() == this.getTotalInit())
		{
			// If two creatures have the same total initiative, the one with the higher bonus acts first.
			if (creature.getInitBonus() == this.getInitBonus())
			{
				// If two creatures have the same total initiative and bonus, then they both roll d20 tie-breakers until there is a winner.
				do
				{
					if ((creature.getDiceRoller() != null) && (this.getDiceRoller() != null))
					{
						creature.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling tie-breaker for " + creature.getName() + "...\n");
						creature.setTieBreaker(this.getDiceRoller().processInput("1d20"));
						this.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling tie-breaker for " + this.getName() + "...\n");
						this.setTieBreaker(this.getDiceRoller().processInput("1d20"));
					}
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
			// By default, the creature with the highest total initiative acts first. Turns then proceed in descending order of initiative.
			return (creature.getTotalInit() - this.getTotalInit());
		}
	}
	
	public void damage(final int amount)
	{
		final RichTextPane output = this.getDiceRoller().getOutput();
		
		output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.RED, Color.WHITE, "Damaging " + this.getName() + " for " + amount + " HP.\n\n");
		
		if ((this.getCurHealth() - amount) <= Creature.DEAD_HP())
		{
			this.setCurHealth(Creature.DEAD_HP());
		}
		else
		{
			this.setCurHealth(this.getCurHealth() - amount);
		}
		
		this.updateStatus();
	}
	
	public final CombatTracker getCombatTracker()
	{
		return this.combatTracker;
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
	
	public final Status getStatus()
	{
		return this.status;
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
		final RichTextPane output = this.getDiceRoller().getOutput();
		
		output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.GREEN, Color.WHITE, "Healing " + this.getName() + " for " + amount + " HP.\n\n");
		
		if ((this.getCurHealth() + amount) > this.getMaxHealth())
		{
			this.setCurHealth(this.getMaxHealth());
		}
		else
		{
			this.setCurHealth(this.getCurHealth() + amount);
		}
		
		this.updateStatus();
	}
	
	public void openOrSaveFile(final Component parent, final boolean isOpen, final boolean isDebugging)
	{
		Object		stream		= null;
		Creature	creature	= null;
		String		filePath	= Support.getFilePath(parent, isOpen, isDebugging);
		
		if ((filePath == null) || filePath.isEmpty())
		{
			return;
		}
		
		try
		{
			if (isOpen)
			{
				// Use binary file manipulation to import a file containing a Creature object.
				stream = new ObjectInputStream(new FileInputStream(filePath));
				creature = ((Creature)((ObjectInputStream)stream).readObject());
				
				this.setCurHealth(creature.getCurHealth());
				this.setInitBase(creature.getInitBase());
				this.setInitBonus(creature.getInitBonus());
				this.setMaxHealth(creature.getMaxHealth());
				this.setName(new String(creature.getName().toCharArray()));
				this.setPosition(new String(creature.getPosition().toCharArray()));
			}
			else
			{
				// Use binary file manipulation to export a file containing a Creature object.
				stream = new ObjectOutputStream(new FileOutputStream(filePath));
				((ObjectOutputStream)stream).writeObject(this);
			}
			
		}
		catch (final Exception exception)
		{
			Support.displayException(null, exception, false);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					if (isOpen)
					{
						((ObjectInputStream)stream).close();
					}
					else
					{
						((ObjectOutputStream)stream).close();
					}
				}
				catch (final Exception exception)
				{
					Support.displayException(null, exception, false);
				}
			}
		}
	}
	
	public final void setCombatTracker(CombatTracker combatTracker)
	{
		this.combatTracker = combatTracker;
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
	
	public final void setStatus(final Status status)
	{
		this.status = status;
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
		return String.format(Creature.STRING_FORMAT(), this.getName(), this.getCurHealth(), this.getMaxHealth(), this.getTotalInit(), this.getPosition());
	}
	
	protected void updateStatus()
	{
		final RichTextPane output = this.getDiceRoller().getOutput();
		
		if ((this.getCurHealth() < (this.getMaxHealth() / 2)) && (this.getCurHealth() > Creature.DISABLED_HP()))
		{
			if (this.getStatus() != Status.BLOODIED)
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getName() + " has been reduced to half or less of its HP and is bloodied!\n\n");
				this.setStatus(Status.BLOODIED);
			}
		}
		else if (this.getCurHealth() == Creature.DISABLED_HP())
		{
			if (this.getStatus() != Status.DISABLED)
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getName() + " has been reduced to " + Creature.DISABLED_HP() + " HP and is disabled!\n\n",
					Color.GRAY, Color.WHITE, "Note: Disabled creatures can only take one move action or one standard action per turn, " +
					"and take 1 point of damage after completing that action.\n\n");
				this.setStatus(Status.DISABLED);
			}
		}
		else if ((this.getCurHealth() <= Creature.DYING_HP()) && (this.getCurHealth() > Creature.DEAD_HP()))
		{
			if ((this.getStatus() != Status.DYING) || (this.getStatus() != Status.UNCONCIOUS))
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getName() + " has been reduced to " + Creature.DYING_HP() + " or less HP and is dying!\n\n",
					Color.GRAY, Color.WHITE, "Note: Dying creatures are also unconcious. Each round, a dying creature has a 10% chance to become " +
					"stable. If the creature fails, it loses 1 HP. If the creature succeeds, it is still unconcious." +
					"Every hour the creature has a 10% chance to regain conciousness. If it fails, it loses 1 HP.\n\n");
				this.setStatus(Status.DYING);
			}
		}
		else if (this.getCurHealth() <= Creature.DEAD_HP())
		{
			output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
				Color.RED, Color.WHITE, this.getName() + " has been reduced to " + Creature.DEAD_HP() + " or less HP and is dead!\n\n");
			this.setStatus(Status.DEAD);
		}
		else
		{
			this.setStatus(Status.HEALTHY);
		}
	}
}