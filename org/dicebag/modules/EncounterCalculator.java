/*
	Title:  Encounter Calculator
	Author: Matthew Boyette
	Date:   4/10/2014
	
	This simple module calculates encounter levels, standard XP rewards, and standard treasure rewards based on the number of enemies
	and the challenge ratings.
*/

package org.dicebag.modules;

import java.io.Serializable;

import api.util.Mathematics;
import api.util.Support;

public class EncounterCalculator implements Serializable
{
	private final static long	serialVersionUID	= 1L;
	
	protected final static int[] TREASURE = {300, 600, 900, 1200, 1600, 2000, 2600, 3400, 4500, 5800, 7500, 9800, 13000, 17000, 22000, 28000, 36000,
		47000, 61000, 80000, 87000, 96000, 106000, 116000, 128000, 141000, 155000, 170000, 187000, 206000, 227000, 249000, 274000, 302000, 332000, 365000,
		401000, 442000, 486000, 534000};
	
	protected static double filterInputLevels(final String s)
	{
		if (s.equals("1/2"))
		{
			return (1.0 / 2.0);
		}
		if (s.equals("1/3"))
		{
			return (1.0 / 3.0);
		}
		if (s.equals("1/4"))
		{
			return (1.0 / 4.0);
		}
		if (s.equals("1/6"))
		{
			return (1.0 / 6.0);
		}
		if (s.equals("1/8"))
		{
			return (1.0 / 8.0);
		}
		if (s.equals("1/10"))
		{
			return (1.0 / 10.0);
		}
		if (Support.isStringParsedAsDouble(s))
		{
			return Double.parseDouble(s);
		}
		
		return 0.0;
	}
	
	protected static double CRtoPL(final double x)
	{
		double retVal = 0.0;
		
		if (x < 2.0)
		{
			retVal = x;
		}
		else
		{
			retVal = Math.pow(2.0, (x / 2.0));
		}
		
		return retVal;
	}

	protected static double PLtoCR(final double x)
	{
		double retVal = 0.0;
		
		if (x < 2.0)
		{
			retVal = x;
		}
		else
		{
			retVal = (2.0 * Mathematics.logarithm(x, 2.0));
		}
		
		return retVal;
	}
	
	protected static double difference(final double x, final double y)
	{
		return (2.0 * (Mathematics.logarithm(x, 2.0) - Mathematics.logarithm(y, 2.0)));
	}
	
	protected static String difficulty(final double x)
	{
		String retVal = "Unknown";
		
		if (x < -9.0)
		{
			retVal = "Trivial";
		}
		else if (x < -4.0)
		{
			retVal = "Very Easy";
		}
		else if (x <  0.0)
		{
			retVal = "Easy";
		}
		else if (x <= 0.0)
		{
			retVal = "Challenging";
		}
		else if (x <= 4.0)
		{
			retVal = "Very Difficult";
		}
		else if (x <= 7.0)
		{
			retVal = "Overpowering";
		}
		else
		{
			retVal = "Unbeatable";
		}
		
		return retVal;	
	}
	
	protected static String percentEnc(final double x)
	{
		String retVal = "Unknown";
		
		if (x < -4.0)
		{
			retVal = "0%";
		}
		else if (x <  0.0)
		{
			retVal = "10%";
		}
		else if (x <= 0.0)
		{
			retVal = "50%";
		}
		else if (x <= 4.0)
		{
			retVal = "15%";
		}
		else if (x <= 7.0)
		{
			retVal = "5%";
		}
		else
		{
			retVal = "0%";
		}
		
		return retVal;	
	}
	
	protected static String percentEncs(final double x)
	{
		double p = 0.0;
		
		if (x < 0.0)
		{
			p = (50.0 + (x * 20.0));
		}
		else if (x > 5.0)
		{
			p = (15.0 - ((x - 5.0) * 5.0));
		}
		else
		{
			p = (50.0 - (x * 7.0));
		}

		if ((x <= 8.0) && (x > 5.0) && (p <= 2.0))
		{
			p = 2.0;
		}
		
		if (p < 0.0)
		{
			p = 0.0;
		}
		
		p = Math.round(p);
		
		return (p + "%");
	}
	
	protected static double even(final double x)
	{
		double retVal = (2.0 * ((int)(x / 2.0)));
		
		if (x < retVal)
		{
			retVal += -2.0;
		}
		else if (x > retVal)
		{
			retVal += 2.0;
		}
		
		return retVal;
	}
	
	protected static double experience(final double a, final double y)
	{
		double retVal = 0.0, x = a;
		
		if (a < 3.0)
		{
			x = 3.0;
		}
		
		if ((x <= 6.0) && (y <= 1.0))
		{
			retVal = (300.0 * y);
		}
		else if (y < 1.0)
		{
			retVal = 0.0;
		}
		else
		{
			retVal = (6.25 * x * (Math.pow(2.0, (EncounterCalculator.even(7.0 - (x - y)) / 2.0)) * (11.0 - (x - y) - EncounterCalculator.even(7.0 - (x - y)))));
		}

		if ((y == 4.0) || (y == 6.0) || (y == 8.0) || (y == 10.0) || (y == 12.0) || (y == 14.0) ||
			(y == 16.0) || (y == 18.0) || (y == 20.0))
		{
			if (x <= 3.0)
			{
				retVal = (1350.0 * Math.pow(2.0, (y - 4.0) / 2.0));
			}
			else if ((x == 5.0) && (y >= 6.0))
			{
				retVal = (2250.0 * Math.pow(2.0, (y - 6.0) / 2.0));
			}
			else if ((x == 7.0) && (y >= 8.0))
			{
				retVal = (3150.0 * Math.pow(2.0, (y - 8.0) / 2.0));
			}
			else if ((x == 9.0) && (y >= 10.0))
			{
				retVal = (4050.0 * Math.pow(2.0, (y - 10.0) / 2.0));
			}
			else if ((x == 11.0) && (y >= 12.0))
			{
				retVal = (4950.0 * Math.pow(2.0, (y - 12.0) / 2.0));
			}
			else if ((x == 13.0) && (y >= 14.0))
			{
				retVal = (5850.0 * Math.pow(2.0, (y - 14.0) / 2.0));
			}
			else if ((x == 15.0) && (y >= 16.0))
			{
				retVal = (6750.0 * Math.pow(2.0, (y - 16.0) / 2.0));
			}
			else if ((x == 17.0) && (y >= 18.0))
			{
				retVal = (7650.0 * Math.pow(2.0, (y - 18.0) / 2.0));
			}
			else if ((x == 19.0) && (y >= 20.0))
			{
				retVal = (8550.0 * Math.pow(2.0, (y - 20.0) / 2.0));
			}
		}
		
		if ((y == 7.0) || (y == 9.0) || (y == 11.0) || (y == 13.0) || (y == 15.0) || (y == 17.0) ||(y == 19.0))
		{
			if (x == 6.0)
			{
				retVal = (2700.0 * Math.pow(2.0, (y - 7.0) / 2.0));
			}
			if ((x == 8.0) && (y >= 9.0))
			{
				retVal = (3600.0 * Math.pow(2.0, (y - 9.0) / 2.0));
			}
			if ((x == 10.0) && (y >= 11.0))
			{
				retVal = (4500.0 * Math.pow(2.0, (y - 11.0) / 2.0));
			}
			if ((x == 12.0) && (y >= 13.0))
			{
				retVal = (5400.0 * Math.pow(2.0, (y - 13.0) / 2.0));
			}
			if ((x == 14.0) && (y >= 15.0))
			{
				retVal = (6300.0 * Math.pow(2.0, (y - 15.0) / 2.0));
			}
			if ((x == 16.0) && (y >= 17.0))
			{
				retVal = (7200.0 * Math.pow(2.0, (y - 17.0) / 2.0));
			}
			if ((x == 18.0) && (y >= 19.0))
			{
				retVal = (8100.0 * Math.pow(2.0, (y - 19.0) / 2.0));
			}
		}
		
		if (y > 20.0)
		{
			retVal = (2.0 * EncounterCalculator.experience(x, (y - 2.0)));
		}
		
		if ((x - y) > 7.0)
		{
			retVal = 0.0;
		}
		else if ((y - x) > 7.0)
		{
			retVal = 0.0;
		}

		return retVal;
	}

	protected static int treasure(final double a)
	{
		double x = a;
		
		if (a > 40.0)
		{
			x = 40.0;
		}
		
		int x2 = ((int)(x)), retVal;
		
		if (x < 1.0)
		{
			retVal = ((int)(x * EncounterCalculator.TREASURE[0]));
		}
		else if (x > x2)
		{
			retVal = ((int)(TREASURE[x2-1] + (x - x2) * (EncounterCalculator.TREASURE[x2] - EncounterCalculator.TREASURE[x2-1])));
		}
		else
		{
			retVal = EncounterCalculator.TREASURE[x2-1];
		}
		
		return retVal;
	}
	
	protected static double xDy(final double x, final double y)
	{
		return xDyPz(x, y, 0.0);
	}
	
	protected static long xDyPz(final double x, final double y, final double z)
	{
		long temp = ((long)z);
		
		for (int j = ((int)x); j > 0; j--)
		{
			temp += Math.round(Math.random() * y);
		}
		
		return temp;
	}
	
	protected static String findSpotDistance(final String terrainType)
	{
		String	spotDist	= null;
		boolean	isTxt		= false;

		switch (terrainType)
		{
			case "dpForest":
			case "rHills":
				
				spotDist = ((xDy(2, 6) * 10.0) + " (2d6x10)");
				break;
				
			case "spForest":
				
				spotDist = ((xDy(3, 6) * 10.0) + " (3d6x10)");
				break;
				
			case "Moor":
			case "DesertDunes":
				
				spotDist = ((xDy(6, 6) * 10.0) + " (6d6x10)");
				break;
				
			case "Desert":
				
				spotDist = ((xDy(6, 6) * 20.0) + " (6d6x20)");
				break;
				
			case "Plains":
				
				spotDist = ((xDy(6, 6) * 40.0) + " (6d6x40)");
				break;
				
			case "mWater":
				
				spotDist = ((xDy(1, 8) * 10.0) + " (1d8x10)");
				break;
				
			case "mpForest":
			case "Swamp":
				
				spotDist = ((xDy(2, 8) * 10.0) + " (2d8x10)");
				break;
				
			case "cWater":
				
				spotDist = ((xDy(4, 8) * 10.0) + " (4d8x10)");
				break;
				
			case "gHills":
				
				spotDist = ((xDy(2, 10) * 10.0) + " (2d10x10)");
				break;
				
			case "Mount":
				
				spotDist = ((xDy(4, 10) * 10.0) + " (4d10x10)");
				break;
				
			case "Dungeon":
				
				spotDist = "Line of sight and lighting.";
				isTxt = true;
				break;
				
			case "Urban":
				
				spotDist = "When event triggered.";
				isTxt = true;
				break;
				
			default:
				
				spotDist = "ERROR";
				isTxt = true;
				break;
		}
		
		if (isTxt == true)
		{
			return spotDist;
		}
		else
		{
			return spotDist + " feet away";
		}
	}
	
	public EncounterCalculator(final DiceBag parent, final boolean isDebugging)
	{
		// TODO: Encounter Level Calculator.
	}
}