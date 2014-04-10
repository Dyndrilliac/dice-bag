/*
	Title:  Constants35E
	Author: Matthew Boyette
	Date:   4/9/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature35E class.
	It represents constants useful to a standard D&D 3.5E creature.
*/

package org.dicebag.objects;

public final class Constants35E extends Constants
{
	private final static  long	serialVersionUID	= 1L;
	
	public static enum Abilities
	{
		STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
	}
	
	public static enum Effects
	{
		ABILITY_DAMAGED, ABILITY_DRAINED, AFFLICTED, BLINDED, BLOWN_AWAY, CHECKED, CONFUSED, COWERING, CURSED, DAZED, DAZZLED, DEAFENED, DISEASED,
		ENERGY_DRAINED, ENTANGLED, EXHAUSTED, FASCINATED, FATIGUED, FLAT_FOOTED, FRIGHTENED, GRAPPLING, HELPLESS, INCORPOREAL, INVISIBLE, KNOCKED_DOWN,
		NAUSEATED, PANICKED, PARALYZED, PETRIFIED, PINNED, POISONED, PRONE, SHAKEN, STAGGERED, STUNNED, TURNED
	}
	
	public static enum Feats
	{
		// TODO: Feat ID Constants.
	}
	
	public static enum Skills
	{
		// TODO: Skill ID Constants.
	}
	
	public static enum Status
	{
		HEALTHY, BLOODIED, DISABLED, DYING, UNCONCIOUS, DEAD
	}
	
	public String STRING_FORMAT()
	{
		return "{Name: [%-24s] HP: [%03d/%03d] Initiative: [%02d] Position: [%-4s] Status: [%-10s]}";
	}
	
	public int DISABLED_HP()
	{
		return 0;
	}
	
	public int DYING_HP()
	{
		return -1;
	}
	
	public int DEAD_HP()
	{
		return -10;
	}
}