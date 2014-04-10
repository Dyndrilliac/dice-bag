/*
	Title:  StatBlock35E  
	Author: Matthew Boyette
	Date:   4/9/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature35E class.
	It represents the stat block for a standard D&D 3.5E creature.
*/

package org.dicebag.objects;

import java.util.LinkedList;

import org.dicebag.objects.Constants35E.Abilities;
import org.dicebag.objects.Constants35E.Effects;
import org.dicebag.objects.Constants35E.Feats;
import org.dicebag.objects.Constants35E.Skills;
import org.dicebag.objects.Constants35E.Status;

public final class StatBlock35E extends StatBlock
{
	private final static long		serialVersionUID	= 1L;
	private LinkedList<Abilities>	abilitiesList		= new LinkedList<Abilities>();
	private LinkedList<Effects> 	effectsList			= new LinkedList<Effects>();
	private LinkedList<Feats>		featsList			= new LinkedList<Feats>();
	private LinkedList<Skills>		skillsList			= new LinkedList<Skills>();
	private Status					status				= null;
	
	public StatBlock35E()
	{
		super();
	}
	
	public StatBlock35E(int initBonus, int maxHealth, String name, String position)
	{
		super(initBonus, maxHealth, name, position);
	}
	
	public StatBlock35E(int curHealth, int initBonus, int maxHealth, String name, String position)
	{
		super(curHealth, initBonus, maxHealth, name, position);
	}
	
	public final LinkedList<Abilities> getAbilitiesList()
	{
		return this.abilitiesList;
	}
	
	public final LinkedList<Effects> getEffectsList()
	{
		return this.effectsList;
	}
	
	public final LinkedList<Feats> getFeatsList()
	{
		return this.featsList;
	}
	
	public final LinkedList<Skills> getSkillsList()
	{
		return this.skillsList;
	}
	
	public final Status getStatus()
	{
		return this.status;
	}
	
	protected final void setAbilitiesList(final LinkedList<Abilities> abilitiesList)
	{
		this.abilitiesList = abilitiesList;
	}
	
	protected final void setEffectsList(final LinkedList<Effects> effectsList)
	{
		this.effectsList = effectsList;
	}
	
	protected final void setFeatsList(final LinkedList<Feats> featsList)
	{
		this.featsList = featsList;
	}
	
	protected final void setSkillsList(final LinkedList<Skills> skillsList)
	{
		this.skillsList = skillsList;
	}
	
	protected final void setStatus(final Status status)
	{
		this.status = status;
	}
}
