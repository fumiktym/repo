/*
PIAnalysis is a quick and fast analysis of PI by using a simple formula.

    Copyright (C) 2008 Alexander Pilon, blueapple2

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

*********

As a programmer and problem solver I like to see the individual steps reaching PI. 

Through a gut feeling and this analysis, I find that the reason PI is considered irrational is
because we are going in circles. The final point of PI is in a state of flux: it is an ending, the original beginning
and the start of a new circle. The point just before the final calculation would be PI but that would not be a full 
circle, but ending the circle triggers this flux dilemma. 


There are three possible PIs. If we think vertically and sequentially instead of thinking of PI as being one number, 
we get the following, a set of alternating numbers, for example:

3.1416026536897204, starting the new circle
3.14159265363972, part of the original famous PI
3.1415826535897198, the pre-full circle. Notice that part of the original PI starts here, specifically the 589.

The difference between the new and the pre is 0.00002 .
The difference between the original and the others is 0.00001 .
If we add the new and the pre and divide them by 0.00002, we get the original 3.14159265363972 .

Throughout the sequence, we see some repetition like in a rational number. 
If we play the algorithm long enough, the three fuse but continue the flux. 

Naturally, an average would be more useful than three different states. 

This average gives a rational fixed constant: 3.141592654
Nonethless, what makes PI fun is the irrational part. :-)

Use this for your hearts desire. 


Version 0.2
*/

public class PIAnalysis
{
	public static void main(String[] args)
	{
		double pi = 0;
		double piADD = 0;
		double piA = 0;
		double piB = 0;
		double piAADD = 0;		
		int piAcnt = 0;
		double piBADD = 0;
		int piBcnt = 0;		
		int limit = 300000;

		for(int n = 0,m=1,q=-4; pi != Math.PI; n++, m+=2)
		{		
			q = q * -1;
			
			pi = pi+(double)((double)q/(double)m);
			piADD = piADD+pi;			
			
			if(piA == 0)
			//if(pi >= 3.1415D && pi < 3.1416D)
			{
				piA = pi;
				piAcnt++;
				piAADD = piAADD+pi;
			}
			else if(piB == 0)
			//else if(pi >= 3.1416D)
			{
				piB = pi;
				piBcnt++;
				piBADD = piBADD+pi;						
			
				System.out.println("************PI-Analysis****************");
				System.out.println("RegularPI=" + pi);
				System.out.println("");
				System.out.println("piA="+piA);
				System.out.println("piB="+piB);
				System.out.println("Addition piA+piB=" + (piA+piB)); 
				System.out.println("Addition piB+piA=" + (piB+piA));
				System.out.println("Subtraction piA-piB=" + ((double)piA-(double)piB)); 
				System.out.println("Subtraction piB-piA=" + (piB-piA));
				System.out.println("Division piA/piB=" + (piA/piB)); 
				System.out.println("Division piB/piA=" + (piB/piA));
				System.out.println("Modulus piA%piB=" + (piA%piB)); 
				System.out.println("Modulus piB%piA=" + (piB%piA));		
				System.out.println("Modulus 1%piB=" + (((double)1)%piB)); 
				System.out.println("Modulus piB%1=" + (piB%((double)1)));
				System.out.println("Modulus piA%1=" + (piA%((double)1))); 
				System.out.println("Modulus 1%piA=" + (((double)1)%piA));
				if(pi == piA%piB)				
					System.out.println("Count="+n+" PI="+pi+" AND PIMOD="+ piA%piB);	
				if(pi == piB%piA)				
					System.out.println("Count="+n+" PI="+pi+" AND PIMOD="+ piB%piA);	
				piA = 0;
				piB = 0;				
			}
			
			System.out.println("");
		}
				System.out.println("************PI-Final-Analysis****************");				
				System.out.println("piAADD="+piAADD);
				System.out.println("piBADD="+piBADD);
				System.out.println("Addition piAADD-piBADD=" + (piAADD+piBADD)); 
				System.out.println("Addition piBADD-piAADD=" + (piBADD+piAADD));
				System.out.println("Subtraction piAADD-piBADD=" + (piAADD-piBADD)); 
				System.out.println("Subtraction piBADD-piAADD=" + (piBADD-piAADD));
				System.out.println("Division piAADD/piBADD=" + (piAADD/piBADD)); 
				System.out.println("Division piBADD/piAADD=" + (piBADD/piAADD));
				System.out.println("Modulus piAADD%piBADD=" + (piAADD%piBADD)); 
				System.out.println("Modulus piBADD%piAADD=" + (piBADD%piAADD));
				System.out.println("PI="+pi);
				System.out.println("PIAVG="+piADD/(double)limit);
				double piAAVG = piAADD/(double)piAcnt;
				double piBAVG = piBADD/(double)piBcnt;
				System.out.println("piAADDAVG="+piAAVG);
				System.out.println("piBADDAVG="+piBAVG);
				System.out.println("************PI-Final-Analysis2****************");
				System.out.println("piAAVG="+piAAVG);
				System.out.println("piBADD="+piBADD);
				System.out.println("Addition piAAVG+piBAVG=" + (piAAVG+piBAVG)); 
				System.out.println("Addition piBAVG+piAAVG=" + (piBAVG+piAAVG));
				System.out.println("Subtraction piAAVG-piBAVG=" + (piAAVG-piBAVG)); 
				System.out.println("Subtraction piBAVG-piAAVG=" + (piBAVG-piAAVG));
				System.out.println("Division piAAVG/piBAVG=" + (piAAVG/piBAVG)); 
				System.out.println("Division piBAVG/piAAVG=" + (piBAVG/piAAVG));
				System.out.println("Modulus piAAVG%piBAVG=" + (piAAVG%piBAVG)); 
				System.out.println("Modulus piBAVG%piAAVG=" + (piBAVG%piAAVG));
	
	}
}
