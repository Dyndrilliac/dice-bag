/*
	Title:  Creature
	Author: Matthew Boyette
	Date:   2/19/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It was originally nested within the CombatTracker
	class but I have since separated it out to avoid duplicating code when using similar objects in other modules. This class is an abstract
	class that other Creature classes are meant to inherit and extend.
*/

package org.dicebag.objects;

import org.dicebag.modules.CombatTracker;
import org.dicebag.modules.DiceBag;

import java.awt.Component;
import java.io.Serializable;

public abstract class Creature implements Comparable<Creature>, Serializable
{
	private	final static long	serialVersionUID	= 1L;
	private		DiceBag			diceRoller			= null;
	private		CombatTracker	combatTracker		= null;
	protected	StatBlock		statBlock			= null;
	protected	Constants		constants			= null;
	
	public Creature(final DiceBag diceBag, final CombatTracker combatTracker, final StatBlock statBlock, final Constants constants)
	{
		this.setDiceRoller(diceBag);
		this.setCombatTracker(combatTracker);
		this.setStatBlock(statBlock);
		this.setConstants(constants);
	}
	
	@Override
	public abstract int		compareTo(final Creature creature);
	public abstract void	damage(final int amount);
	
	public final CombatTracker getCombatTracker()
	{
		return this.combatTracker;
	}
	
	public abstract Constants getConstants();
	
	public final DiceBag getDiceRoller()
	{
		return this.diceRoller;
	}
	
	public abstract StatBlock	getStatBlock();
	public abstract void		heal(final int amount);
	public abstract void		openOrSaveFile(final Component parent, final boolean isOpen, final boolean isDebugging);
	public abstract void		rollInitiative(final boolean isTieBreaker);
	
	protected final void setCombatTracker(final CombatTracker combatTracker)
	{
		this.combatTracker = combatTracker;
	}
	
	protected abstract void setConstants(final Constants constants);
	
	protected final void setDiceRoller(final DiceBag diceRoller)
	{
		this.diceRoller = diceRoller;
	}
	
	protected abstract void setStatBlock(final StatBlock statBlock);
	@Override
	public abstract String	toString();
	public abstract void	updateStatus();
}