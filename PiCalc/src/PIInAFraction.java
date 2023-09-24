/*
PIInAFraction finds the final fraction and the amount of steps required to reach Java's PI

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********

Version 0.1
*/

public class PIInAFraction
{
	public static void main(String[] args)
	{
		long num = 1;
		long denom = 4;
		int count = 0;
		double pi = 0;

		for(int n = 0,m=1,q=-4; pi != Math.PI; n++, m+=2)
		{		
			q = q * -1;
			count++;
			pi = pi+(double)((double)q/(double)m);
			if(n >= 1)
			{
				num = ((num * m) + (denom * q));
				denom = (denom * m);
			}
		}
		System.out.println("PI in a fraction = " + num + "/" + denom);
		System.out.println("Number of steps to reach PI = " + count); 
	}
}
