/*
	Title:  CreatureV35
	Author: Matthew Boyette
	Date:   4/07/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It was originally located nested within the
	CombatTracker class but I have separated it out to avoid duplicating code when using similar objects in other modules.
	
	CreatureV35 is the default Creature type in DiceBag, and is just a non-abstract version of Creature. It represents a D&D 3.5 creature.
*/

public final class CreatureV35 extends Creature
{
	private final static long	serialVersionUID	= 1L;
	
	public CreatureV35(DiceBag diceBag, CombatTracker combatTracker, int initBase)
	{
		super(diceBag, combatTracker, initBase);
	}
	
	public CreatureV35(DiceBag diceBag, CombatTracker combatTracker, int initBase, int initBonus, int maxHealth, String name, String position)
	{
		super(diceBag, combatTracker, initBase, initBonus, maxHealth, name, position);
	}
	
	public CreatureV35(int curHealth, DiceBag diceBag, CombatTracker combatTracker, int initBase, int initBonus, int maxHealth, String name, String position)
	{
		super(curHealth, diceBag, combatTracker, initBase, initBonus, maxHealth, name, position);
	}
}