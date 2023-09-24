/*
PIlation contains the basic compuations that will be done to analyze PI.

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********

Version 0.1
*/

public class PIlations
{

	public static double piToPower(double power)
	{
		return Math.pow(Math.PI, power);
	}
	public static double baseToPowerPI(double base)
	{
		return Math.pow(base, Math.PI);
	}
	public static double piPrecision(double value)
	{
		if (value >= Math.PI)
			return (value - Math.PI);
		else 
			return (Math.PI - value);
	}
	public static double fractionPIPrecision(int num, int denom)
	{
		return piPrecision((double)num/(double)denom);
	}
	public static boolean isFractionCloseToPI(int num, int denom, double precision)
	{
		return valueCloseToPI(((double)num/(double)denom), precision);	
	}
	public static boolean valueCloseToPI(double value, double precision)
	{
		if (precision >= 0 && value >= Math.PI && value <= (Math.PI + precision))
			return true;
		else if (precision <= 0 && value >= (Math.PI + precision) && value <= Math.PI)
			return true;
		else
			return false;
	}
	public static double piXFraction(int num, int denom)
	{
		return (Math.PI * ((double)num/(double)denom));
	}
	public static double piModFraction(int num, int denom)
	{	
		return (Math.PI % ((double)num/(double)denom));	
	}
	public static double piPlusFraction(int num, int denom)
	{
		return (Math.PI + ((double)num/(double)denom));
	}
}