/*
	Title:  StatBlock
	Author: Matthew Boyette
	Date:   4/9/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature class.
	It represents the stat block for a creature.
*/

package org.dicebag.objects;

import java.io.Serializable;

public abstract class StatBlock implements Serializable
{
	/*
		Fields
	*/
	
	private final static long	serialVersionUID	= 1L;
	private long				curHealth			= 0;
	private long				initBase			= 0;
	private long				initBonus			= 0;
	private long				maxHealth			= 0;
	private String				name				= "";
	private String				position			= "";
	private long				tieBreaker			= 0;
	
	/*
		Constructor Methods
	*/
	
	public StatBlock()
	{
		this(0, 0, 0, "", "");
	}
	
	public StatBlock(final int initBonus, final int maxHealth, final String name, final String position)
	{
		this(maxHealth, initBonus, maxHealth, name, position);
	}
	
	public StatBlock(final int curHealth, final int initBonus, final int maxHealth, final String name, final String position)
	{
		this.setCurHealth(curHealth);
		this.setInitBonus(initBonus);
		this.setMaxHealth(maxHealth);
		this.setName(name);
		this.setPosition(position);
	}
	
	/*
		Getter Methods
	*/
	
	public final long getCurHealth()
	{
		return this.curHealth;
	}
	
	public final long getInitBase()
	{
		return this.initBase;
	}
	
	public final long getInitBonus()
	{
		return this.initBonus;
	}
	
	public final long getMaxHealth()
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
	
	public final long getTieBreaker()
	{
		return this.tieBreaker;
	}
	
	public final long getTotalInit()
	{
		return (this.getInitBase() + this.getInitBonus());
	}
	
	/*
		Setter Methods
	*/
	
	public final void setCurHealth(final long curHealth)
	{
		this.curHealth = curHealth;
	}
	
	public final void setInitBase(final long initBase)
	{
		this.initBase = initBase;
	}
	
	public final void setInitBonus(final long initBonus)
	{
		this.initBonus = initBonus;
	}
	
	public final void setMaxHealth(final long maxHealth)
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
	
	public final void setTieBreaker(final long tieBreaker)
	{
		this.tieBreaker = tieBreaker;
	}
}