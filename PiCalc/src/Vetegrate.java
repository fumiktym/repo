/*
Vetegral PI calculates circle areas, perimeters, and PI itself without the use of sin or any idea of what PI is. 


    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*********

This is flawed from the beginning because it should be adjusted to calculate what is in between each triangle, 
which this does not.

Version 0.1
*/

public class Vetegrate
{

	public static void main(String[] args)
	{
		
		long value = 10000000;

		VetegralPI vetegralPI = new VetegralPI(value);
		System.out.println("Area Circle= " + (vetegralPI.vareaOfSector(0)*4));		
		System.out.println("PI by Area= " + vetegralPI.PIar(0));
		System.out.println("Perimeter Circle= " + (vetegralPI.verimeterOfSector(0)*4));
		System.out.println("PI by Perimeter= " + vetegralPI.PIper(0));
	}	
}