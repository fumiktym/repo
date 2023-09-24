/*
BestPI is part of PIAnalysis and finds the closest PI to Java's PI;

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********

Which fraction combination results in a PI that is close to Java's PI?
Which addition of two fractions results in a PI that is close to Java's PI?
Which modulus of two fractions results in a PI that is close to Java's PI?
Which multiplication of two fractions results in a PI that is close to Java's PI?
Which base power combination results in a PI that is close to Java's PI?
Which combination of all the above results in a PI that is close to Java's PI?

Use this to build your own analysis tools and libraries, and or have fun with it!
Aside from games, this will actually use your Xcore power computer. 

Pass the values to another program or to text as such:
java program > analysis.txt
****
java program | otherProgram
for example:
	java program | grep "3.14";
Version 0.1
*/

public class BestPI
{
	public static void main(String[] args)
	{
		int base = 1000;
		int power = base;

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
					bestPrecision = new String(
							precisionView(equalView(arithmaticView(p1, '/', b1),testPI),closeness));
								System.out.println(bestPrecision);
				}
				
			}

		}

		System.out.println("Best Result is " + bestPrecision);	

		System.out.println("//Find addition of two fractions closest to Java's PI using denominator and numerator 1 to "+ denom+ "//");		
	//		closeness = 1000;
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
					bestPrecision = new String(binaryView(p1, '/', b1, '+', p2, '/', b2, testPI, closeness));
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
//		closeness = 1000;
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
							= new String(binaryView(p1, '/', b1, 'x', p2, '/', b2, testPI, closeness));
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
//		closeness = 1000;
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
							= new String(binaryView(p1, '/', b1, '%', p2, '/', b2, testPI, closeness));
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
		System.out.println("//Find closest roots to Java's PI using bases and powers 1 to "+ base+ "//");
	//		closeness = 1000;
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
									= new String(
									precisionView(equalView(
									powerView(arithmaticView(b2, '/', b1),
										arithmaticView(p2, '/', p1)),testPI),closeness));
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
		System.out.println("//Find closest combination to Java's PI using numbers 1 to "+ base+ "//");
	//		closeness = 1000;
		char[] operators = {'+','x','%'};
		for(int b1 = 1; b1 < base; b1++)
		{
			for(int b2 = 1; b2 < base; b2++)
			{			
				for(int p1 = 1; p1 < power; p1++)
				{
					for(int p2 = 1; p2 < power; p2++)
					{
						double basePower= Math.pow(((double)b2/(double)b1),((double)p2/(double)p1));
		for(int d1 = 1; d1 < denom; d1++)
		{
			for(int d2 = 1; d2 < denom; d2++)
			{
			for(int n1 = 1; n1 < num; n1++)
			{
				for(int n2 = 1; n2 < num; n2++)
				{
					double[] testPI1 = {(((double)n1/(double)d1) + ((double)n2/(double)d2)),
								(((double)n1/(double)d1) * ((double)n2/(double)d2)),	
								(((double)n1/(double)d1) % ((double)n2/(double)d2))};

					for(int m = 0; m < operators.length; m++)
					{
						for(int q = 0; q < testPI1.length; q++)
						{
							if (m == 0)
								testPI = basePower + testPI1[q];
							else if (m == 1)
								testPI = basePower * testPI1[q];
							else if (m == 2)
								testPI = basePower % testPI1[q];
							if(PIlations.piPrecision(testPI) < closeness)
							{
								closeness = PIlations.piPrecision(testPI);
								bestPrecision 
									= new String(
									precisionView(
									equalView(
									arithmaticView(
									powerView(
									arithmaticView(b2, '/', b1),
									arithmaticView(p2,'/', p1)), 
									operators[m],
									arithmaticView(
									encloseValue(
									arithmaticView(n1, '/', d1)),
									operators[q],
									encloseValue(
									arithmaticView(n2, '/', d2)))),
									testPI),closeness));

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
			}
		}
		}}}}
		
		System.out.println("Best Result is " + bestPrecision);
		System.out.println("Absolute Best Result is " + absolutePrecision);
	}
	public static String arithmaticView(int num, char operator, int denom)
	{
		return ("" + num + "" + operator + "" + denom);
	}
	public static String arithmaticView(String num, char operator, String denom)
	{
		return ("" + num + "" + operator + "" + denom);
	}
	public static String powerView(String base, String power)
	{
		return ("" + base + " to the power of " + power);
	}
	public static String equalView(String represent, double result)
	{
		return ("" + represent + " = " + result);
	}
	public static String precisionView(String represent, double precision)
	{
		return ("" + represent + " Has precision of " + precision);
	}
	public static String encloseValue(String valueA)
	{
		return ("(" +valueA + ")");
	}
	public static String binaryView(int value1, char operator, int value2,  char oper2, int value3, char oper3, int value4, double ans, double precision)
	{
		return (precisionView(
						equalView(
						arithmaticView(
						encloseValue(						
						arithmaticView(value1, operator, value2)),oper2,
						encloseValue(						
						arithmaticView(value3, operator, value4))),ans),precision));
	}
}
