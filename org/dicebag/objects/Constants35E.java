/*
 * Title: Constants35E
 * Author: Matthew Boyette
 * Date: 4/9/2014
 *
 * This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature35E class.
 * It represents constants useful to a standard D&D 3.5E creature.
 */

package org.dicebag.objects;

public final class Constants35E extends Constants
{
	public static enum Abilities
	{
		CHARISMA, CONSTITUTION, DEXTERITY, INTELLIGENCE, STRENGTH, WISDOM
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
		BLOODIED, DEAD, DISABLED, DYING, HEALTHY, UNCONCIOUS
	}

	private final static long	serialVersionUID	= 1L;

	@Override
	public int DEAD_HP()
	{
		return -10;
	}

	@Override
	public int DISABLED_HP()
	{
		return 0;
	}

	@Override
	public int DYING_HP()
	{
		return -1;
	}

	@Override
	public String STRING_FORMAT()
	{
		return "{Name: [%-25s] HP: [%03d/%03d] Initiative: [%02d] Position: [%-5s] Status: [%-10s]}";
	}
}