/*
	Title:  Constants
	Author: Matthew Boyette
	Date:   4/9/2014
	
	This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature class.
	It represents constants useful to a creature.
*/

package org.dicebag.objects;

import java.io.Serializable;

public abstract class Constants implements Serializable
{
	private final static long serialVersionUID = 1L;
	
	public abstract String	STRING_FORMAT();
	public abstract int		DISABLED_HP();
	public abstract int		DYING_HP();
	public abstract int		DEAD_HP();
}