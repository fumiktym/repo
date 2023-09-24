/*
GoneCircles uses the circle formula to find the Area of a circle, without using "rectangles".

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********


Version 0.1
*/

public class GoneCircles
{
	public static void main(String[] args)
	{
		//Calculate Quarter Circle and Multiply by 4. 
		
		double r = 1.0;
		double area = 0;
		for(double x = 0.0; x < r; x+=0.000010d)
		{
			for(double y = r; y > 0; y-=0.000010d)
			{
				if( Math.sqrt(Math.pow(x,2) + Math.pow(y, 2)) == r)
					area = area + 1;
			}
		}
		System.out.println("Area of Circle and PI = " + (area*4));

		
	}

}
