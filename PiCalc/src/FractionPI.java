/*
FractionPI is part of PIAnalysis and finds the closest PI to Java's PI;

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********

Which base power combination results in a PI that is close to Java's PI?
Which fraction combination results in a PI that is close to Java's PI?
Which addition of two fractions results in a PI that is close to Java's PI?
Which modulus of two fractions results in a PI that is close to Java's PI?
Which multiplication of two fractions results in a PI that is close to Java's PI?


Use this to build your own analysis tools and libraries, and or have fun with it!
Pass the values to another program or to text as such:
java program > analysis.txt
****
java program | otherProgram
for example:
	java program | grep "3.14";
Version 0.1
*/

public class FractionPI
{
	public static void main(String[] args)
	{
		int denom = 1000;
		int num = denom;
		double precision = 0.000001000d;
		double testPI = 0;
		String bestPrecision = "";
		double closeness = 1000; 
		double absoluteCloseness = 1000;
		String absolutePrecision = "";
		System.out.println("//Find closest fraction to Java's PI using denominator and numerator 1 to "+ denom+ "//");
		for(int b1 = 1; b1 < denom; b1++)
		{
			for(int p1 = 1; p1 < num; p1++)
			{
				testPI = (double)p1/(double)b1;
				if(PIlations.piPrecision(testPI) < closeness)
				{
					closeness = PIlations.piPrecision(testPI);
					bestPrecision 
							= new String("" 
							+ p1 + "/" + b1 +" = " + testPI + '\n'
									+ "Has precision of " 
									+ PIlations.piPrecision(testPI)); 
								System.out.println(bestPrecision);
				}
				
			}

		}
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("//Find addition of two fractions closest to Java's PI using denominator and numerator 1 to "+ denom+ "//");		
		closeness = 1000;

		for(int b1 = 1; b1 < denom; b1++)
		{
			for(int b2 = 1; b2 < denom; b2++)
			{
			for(int p1 = 1; p1 < num; p1++)
			{
				for(int p2 = 1; p2 < num; p2++)
				{
				testPI = (((double)p1/(double)b1) + ((double)p2/(double)b2));
				if(PIlations.piPrecision(testPI) < closeness)
				{
					closeness = PIlations.piPrecision(testPI);
					bestPrecision 
							= new String("(" 
							+ p1 + "/" + b1 +") + (" + p2 + "/" + b2 + ") = " + testPI + '\n'
									+ "Has precision of " 
									+ PIlations.piPrecision(testPI)); 
								System.out.println(bestPrecision);
					if(closeness < absoluteCloseness)
					{
						absoluteCloseness = closeness;
						absolutePrecision = bestPrecision;
					}
				}
				}
				
			}
			}

		}
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("Absolute Best Result is " + absolutePrecision);
		System.out.println("//Find multiplication of two fractions closest to Java's PI using denominator and numerator 1 to "+ denom+ "//");		
		closeness = 1000;

		for(int b1 = 1; b1 < denom; b1++)
		{
			for(int b2 = 1; b2 < denom; b2++)
			{
			for(int p1 = 1; p1 < num; p1++)
			{
				for(int p2 = 1; p2 < num; p2++)
				{
				testPI = (((double)p1/(double)b1) * ((double)p2/(double)b2));
				if(PIlations.piPrecision(testPI) < closeness)
				{
					closeness = PIlations.piPrecision(testPI);
					bestPrecision 
							= new String("(" 
							+ p1 + "/" + b1 +") x (" + p2 + "/" + b2 + ") = " + testPI + '\n'
									+ "Has precision of " 
									+ PIlations.piPrecision(testPI)); 
								System.out.println(bestPrecision);
					if(closeness < absoluteCloseness)
					{
						absoluteCloseness = closeness;
						absolutePrecision = bestPrecision;
					}
				}
				}
				
			}
			}

		}
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("Absolute Best Result is " + absolutePrecision);
		System.out.println("//Find modulus of two fractions closest to Java's PI using denominator and numerator 1 to "+ denom+ "//");		
		closeness = 1000;

		for(int b1 = 1; b1 < denom; b1++)
		{
			for(int b2 = 1; b2 < denom; b2++)
			{
			for(int p1 = 1; p1 < num; p1++)
			{
				for(int p2 = 1; p2 < num; p2++)
				{
				testPI = (((double)p1/(double)b1) % ((double)p2/(double)b2));
				if(PIlations.piPrecision(testPI) < closeness)
				{
					closeness = PIlations.piPrecision(testPI);
					bestPrecision 
							= new String("(" 
							+ p1 + "/" + b1 +") % (" + p2 + "/" + b2 + ") = " + testPI + '\n'
									+ "Has precision of " 
									+ PIlations.piPrecision(testPI)); 
								System.out.println(bestPrecision);
					if(closeness < absoluteCloseness)
					{
						absoluteCloseness = closeness;
						absolutePrecision = bestPrecision;
					}
				}
				}
				
			}
			}

		}
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("Absolute Best Result is " + absolutePrecision);
	}
}
