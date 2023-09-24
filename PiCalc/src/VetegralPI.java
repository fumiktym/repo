/*
Vetegral PI calculates circle areas, perimeters, and PI itself without the use of sin or any idea of what PI is. 


    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********
Ideally, Vetegral calculates a quarter area or perimeter of a circle using Triangle Integrals(Vetegrals) along a path.
It is possible with Vetegration to design curved shapes, or get information about a curved shape knowing without any 
initial information about it, except for its line path. 

This is flawed from the beginning because it should be adjusted to calculate what is in between each triangle, 
which this does not.

Change it as you wish for your purposes. 

Version 0.1
*/
import java.lang.Math;
public class VetegralPI
{

	private long limit = Long.MAX_VALUE;
	private long limit2 = Long.MAX_VALUE;
	private double multi = 1;

	public VetegralPI(long lim)
	{
		if (lim > 0 && lim <= limit)
		{
			limit = lim;
			limit2 = lim;
		}
	}
/*
	Sets the precision limit
	limit2 is an alternateLimit, that is used to make quicker calculations. 
*/
	public VetegralPI(long limit, long limit2, double multi)
	{
		this.limit = limit;
		if (limit2 <= limit && limit2 >= 0)
			this.limit2 = limit2;
		if(multi > 0)
			this.multi = multi;
			
	}
	// Finds PI from the Area of the circle
	public double PIar(long start)
	{
		return((vareaOfSector(start)*4)/(Math.pow((double)limit,2)));
	}
	// Finds PI from the Area of the Perimeter
	public double PIper(long start)
	{
		return((verimeterOfSector(start)*4)/(((double)limit)*2));
	}
	// The quarter of the circle can be divided into much smaller pieces, and set by limit2.
	// This creates better precision, but to fill the quarter it must be multiplied back to be correct.
	public void setMulti(double multi)
	{
		this.multi = multi;
	}
	public double vareaOfSector(long start)
	{
		double vArea = 0;
		double s = 0;
		double d = 0;
		for(long i = start; i < limit2; i++)
		{
			s = serimeter(i);
			d = distance(i);
			vArea = vArea + (
				Math.sqrt(s
					*(s-((double)limit))
					*(s-((double)limit))
					*(s-d))*multi);
		}
		return (vArea);
	}
	public double verimeterOfSector(long start)
	{
		double verim = 0;
		for(long i = start; i < limit2; i++)				
			verim = verim + (distance(i)*multi);		

		return (verim);
	}
	public double distance(long i)
	{
		return (Math.sqrt(
				(Math.pow((double)((limit-(limit-i)) - ((limit-(limit-i))-1)), 2))
				+ (Math.pow((double)((limit-i) - ((limit-i)+1)), 2))));	
	}
	public double serimeter(long i)
	{		
		return (((((double)(limit))*2) +distance(i))/2);
	}
	public void showCoordinates(long i)
	{
		System.out.println("("+ (limit-(limit-i)) + "," + (limit-i) + ")");
	}
	
}
