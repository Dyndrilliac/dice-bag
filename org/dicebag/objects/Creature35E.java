/*
	Title:  Creature35E
	Author: Matthew Boyette
	Date:   4/7/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. Creature35E is the default Creature type in DiceBag.
	It represents a standard D&D 3.5E creature.
*/

package org.dicebag.objects;

import org.dicebag.modules.CombatTracker;
import org.dicebag.modules.DiceBag;

import api.gui.RichTextPane;
import api.util.Support;

import java.awt.Color;
import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Creature35E extends Creature
{
	private final static long	serialVersionUID	= 1L;
	
	public Creature35E(final DiceBag diceBag, final CombatTracker combatTracker, final StatBlock35E statBlock)
	{
		super(diceBag, combatTracker, statBlock, new Constants35E());
	}
	
	// Implements initiative as the natural ordering mechanism for the Creature class.
	// See d20 SRD Initiative rules: http://www.d20srd.org/srd/combat/initiative.htm
	@Override
	public final int compareTo(final Creature creature)
	{
		if (creature.getStatBlock().getTotalInit() != this.getStatBlock().getTotalInit())
		{
			// By default, the creature with the highest total initiative acts first. Turns then proceed in descending order of initiative.
			return (creature.getStatBlock().getTotalInit() - this.getStatBlock().getTotalInit());
		}
		else
		{
			// If two creatures have the same total initiative, the one with the higher initiative bonus acts first.
			if (creature.getStatBlock().getInitBonus() != this.getStatBlock().getInitBonus())
			{
				return (creature.getStatBlock().getInitBonus() - this.getStatBlock().getInitBonus());
			}
			else
			{
				// If two creatures have the same total initiative and initiative bonus, then they both roll d20 tie-breakers until there is a winner.
				do
				{
					if ((creature.getDiceRoller() != null) && (this.getDiceRoller() != null))
					{
						creature.rollInitiative(true);
						this.rollInitiative(true);
					}
				}
				while (creature.getStatBlock().getTieBreaker() == this.getStatBlock().getTieBreaker());
				
				return (creature.getStatBlock().getTieBreaker() - this.getStatBlock().getTieBreaker());
			}
		}
	}
	
	public final void damage(final int amount)
	{	
		this.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.RED, Color.WHITE, "Damaging " + this.getStatBlock().getName() + " for " + amount + " HP.\n\n");
		
		if ((this.getStatBlock().getCurHealth() - amount) <= this.getConstants().DEAD_HP())
		{
			this.getStatBlock().setCurHealth(this.getConstants().DEAD_HP());
		}
		else
		{
			this.getStatBlock().setCurHealth(this.getStatBlock().getCurHealth() - amount);
		}
		
		this.updateStatus();
	}
	
	@Override
	public final Constants35E getConstants()
	{
		return ((Constants35E)this.constants);
	}
	
	@Override
	public final StatBlock35E getStatBlock()
	{
		return ((StatBlock35E)this.statBlock);
	}
	
	public final void heal(final int amount)
	{
		this.getDiceRoller().getOutput().append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
			Color.GREEN, Color.WHITE, "Healing " + this.getStatBlock().getName() + " for " + amount + " HP.\n\n");
		
		if ((this.getStatBlock().getCurHealth() + amount) > this.getStatBlock().getMaxHealth())
		{
			this.getStatBlock().setCurHealth(this.getStatBlock().getMaxHealth());
		}
		else
		{
			this.getStatBlock().setCurHealth(this.getStatBlock().getCurHealth() + amount);
		}
		
		this.updateStatus();
	}
	
	public final void openOrSaveFile(final Component parent, final boolean isOpen, final boolean isDebugging)
	{
		Object	stream		= null;
		String	filePath	= Support.getFilePath(parent, isOpen, isDebugging);
		
		if ((filePath == null) || filePath.isEmpty())
		{
			return;
		}
		
		try
		{
			if (isOpen)
			{
				// Use binary file manipulation to import a file containing a StatBlock35E object.
				stream = new ObjectInputStream(new FileInputStream(filePath));
				this.setStatBlock((StatBlock35E)((ObjectInputStream)stream).readObject());
			}
			else
			{
				// Use binary file manipulation to export a file containing a StatBlock35E object.
				stream = new ObjectOutputStream(new FileOutputStream(filePath));
				((ObjectOutputStream)stream).writeObject(this.getStatBlock());
			}
			
		}
		catch (final Exception exception)
		{
			Support.displayException(parent, exception, false);
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
					Support.displayException(parent, exception, false);
				}
			}
		}
	}
	
	public final void rollInitiative(final boolean isTieBreaker)
	{
		if ((this.getDiceRoller() != null) && (this.getDiceRoller().getOutput() != null))
		{
			final RichTextPane output = this.getDiceRoller().getOutput();
			
			if (isTieBreaker)
			{
				output.append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling tie-breaker for " + this.getStatBlock().getName() + "...\n");
				this.getStatBlock().setTieBreaker(this.getDiceRoller().processInput("1d20"));
			}
			else
			{
				output.append(Color.BLACK, Color.WHITE, "\t\t\t   Rolling initiative for " + this.getStatBlock().getName() + "...\n");
				this.getStatBlock().setInitBase(this.getDiceRoller().processInput("1d20"));
			}
		}
	}
	
	@Override
	protected final void setConstants(final Constants constants)
	{
		this.constants = constants;
	}
	
	@Override
	protected final void setStatBlock(final StatBlock statBlock)
	{
		this.statBlock = statBlock;
	}
	
	@Override
	public final String toString()
	{
		String status;
		
		switch (this.getStatBlock().getStatus())
		{
			case HEALTHY:
				
				status = "Healthy";
				break;
				
			case BLOODIED:
				
				status = "Bloodied";
				break;
				
			case DISABLED:
				
				status = "Disabled";
				break;
				
			case DYING:
				
				status = "Dying";
				break;
				
			case UNCONCIOUS:
				
				status = "Unconcious";
				break;
				
			case DEAD:
				
				status = "Dead";
				break;
				
			default:
				
				status = "Unknown";
				break;
		}
		
		return String.format(this.getConstants().STRING_FORMAT(), this.getStatBlock().getName(), this.getStatBlock().getCurHealth(),
			this.getStatBlock().getMaxHealth(), this.getStatBlock().getTotalInit(), this.getStatBlock().getPosition(), status);
	}
	
	public void updateStatus()
	{
		final RichTextPane output = this.getDiceRoller().getOutput();
		
		if ((this.getStatBlock().getCurHealth() <= (this.getStatBlock().getMaxHealth() / 2)) && (this.getStatBlock().getCurHealth() > this.getConstants().DISABLED_HP()))
		{
			if (this.getStatBlock().getStatus() != Constants35E.Status.BLOODIED)
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getStatBlock().getName() + " has been reduced to half or less of its HP and is bloodied!\n\n");
				this.getStatBlock().setStatus(Constants35E.Status.BLOODIED);
			}
		}
		else if (this.getStatBlock().getCurHealth() == this.getConstants().DISABLED_HP())
		{
			if (this.getStatBlock().getStatus() != Constants35E.Status.DISABLED)
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getStatBlock().getName() + " has been reduced to " + this.getConstants().DISABLED_HP() + " HP and is disabled!\n\n",
					Color.GRAY, Color.WHITE, "Note: Disabled creatures can only take one move action or one standard action per turn, " +
					"and take 1 point of damage after completing that action.\n\n");
				this.getStatBlock().setStatus(Constants35E.Status.DISABLED);
			}
		}
		else if ((this.getStatBlock().getCurHealth() <= this.getConstants().DYING_HP()) && (this.getStatBlock().getCurHealth() > this.getConstants().DEAD_HP()))
		{
			if ((this.getStatBlock().getStatus() != Constants35E.Status.DYING) && (this.getStatBlock().getStatus() != Constants35E.Status.UNCONCIOUS))
			{
				output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
					Color.RED, Color.WHITE, this.getStatBlock().getName() + " has been reduced to " + this.getConstants().DYING_HP() + " or less HP and is dying!\n\n",
					Color.GRAY, Color.WHITE, "Note: Dying creatures are also unconcious. Each round, a dying creature has a 10% chance to become " +
						"stable. If the creature fails, it loses 1 HP. If the creature succeeds, it is still unconcious." +
						"Every hour the creature has a 10% chance to regain conciousness. If it fails, it loses 1 HP.\n\n");
				this.getStatBlock().setStatus(Constants35E.Status.DYING);
			}
		}
		else if (this.getStatBlock().getCurHealth() <= this.getConstants().DEAD_HP())
		{
			output.append(Color.BLACK, Color.WHITE, "[" + Support.getDateTimeStamp() + "]: ",
				Color.RED, Color.WHITE, this.getStatBlock().getName() + " has been reduced to " + this.getConstants().DEAD_HP() + " or less HP and is dead!\n\n");
			this.getStatBlock().setStatus(Constants35E.Status.DEAD);
		}
		else
		{
			this.getStatBlock().setStatus(Constants35E.Status.HEALTHY);
		}
	}
}