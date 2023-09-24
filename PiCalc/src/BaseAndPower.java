import java.util.Date;

/*
BaseAndPower is part of PIAnalysis and finds the closest PI to Java's PI;

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********
Which base power combination results in a PI that is close to Java's PI?

Use this to build your own analysis tools and libraries, and or have fun with it!
Pass the values to another program or to text as such:
java program > analysis.txt
****
java program | otherProgram
for example:
	java program | grep "3.14";

Version 0.2
*/

public class BaseAndPower
{
	public static void main(String[] args)
	{
		//int base = 1000;
		int base = 100;
		int power = base;
		double precision = 0.000001000d;
		System.out.println("//Find closest roots to Java's PI using bases 1 to "+ base+ "//");
		double testPI = 0;
		String bestPrecision = "";
		double closeness = 1000; 
		Date start = new Date();
		for(int b1 = 1; b1 < base; b1++)
		{
			for(int b2 = 1; b2 < base; b2++)
			{			
				for(int p1 = 1; p1 < power; p1++)
				{
					for(int p2 = 1; p2 < power; p2++)
					{
						testPI = Math.pow(((double)b2/(double)b1),((double)p2/(double)p1));
						if(PIlations.piPrecision(testPI) < closeness)
							{
								closeness = PIlations.piPrecision(testPI);
								bestPrecision 
									= new String("" 
									+ b2 + "/" + b1 +" to the power of "
									+ p2 + "/" + p1 + " = " + testPI + '\n'
									+ "Has precision of " 
									+ PIlations.piPrecision(testPI)); 
								System.out.println(bestPrecision);
							}						
					}
				}
			}
		}
		Date end = new Date();
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("Time = "+(end.getTime()-start.getTime())+" msec");
		
	}
}
