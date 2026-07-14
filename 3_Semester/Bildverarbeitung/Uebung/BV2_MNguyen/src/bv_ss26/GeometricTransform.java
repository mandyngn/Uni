// BV Ue2 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;


public class GeometricTransform {
 		   	  	 		 
	public enum InterpolationType { 
		NEAREST("Nearest Neighbour"), 
		BILINEAR("Bilinear");
		
		private final String name;       
	    private InterpolationType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch(interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;	
		}
		
	}
 		   	  	 		 
	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
 		   	  	 		 
		// TODO: implement the geometric transformation using nearest neighbour image rendering
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant

		for(int xd = 0; xd < dst.width; xd++){
			for(int yd = 0; yd < dst.height; yd++){
				int dstPos = yd * dst.width + xd;

				//Grad in Bogenmaß
				double radiant = angle * (Math.PI/180);

				//Ursprung muss Mitte des Bildes sein
				int newXd = xd - (dst.width/2);
				int newYd = yd - (dst.height/2);

				double newYs = newYd / (Math.cos(radiant) - newYd * perspectiveDistortion * Math.sin(radiant));
				double newXs = newXd * (perspectiveDistortion * Math.sin(radiant) * newYs +1);

				newYs = Math.round(newYs);
				newXs = Math.round(newXs);

				//Ursprung vom Source Bild oben links
				int Xs = (int) newXs + (src.width/2);
				int Ys = (int) newYs + (src.height/2);

				int pos = Ys * src.width + Xs;

				if(Xs < 0 || Xs >= src.width || Ys < 0 || Ys >= src.height){
					dst.argb[dstPos] = 0xFF000000 | 0xFFFFFFFF | 0xFFFFFFFF | 0xFFFFFFFF;
				} else {
					dst.argb[dstPos] = src.argb[pos];
				}
			}
		}
		
	}


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
 		   	  	 		 
		// TODO: implement the geometric transformation using bilinear interpolation
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radiant
		//ARGB Wert für alle Pixel rausholen und dann mischen

		for(int xd = 0; xd < dst.width; xd++){
			for(int yd = 0; yd < dst.height; yd++){
				int dstPos = yd * dst.width + xd;

				//Grad in Bogenmaß
				double radiant = angle * (Math.PI/180);

				//Ursprung muss Mitte des Bildes sein
				int newXd = xd - (dst.width/2);
				int newYd = yd - (dst.height/2);

				double newYs = newYd / (Math.cos(radiant) - newYd * perspectiveDistortion * Math.sin(radiant));
				double newXs = newXd * (perspectiveDistortion * Math.sin(radiant) * newYs +1);

				//Ursprung vom Source Bild oben links
				double Xs = newXs + (src.width/2);
				double Ys = newYs + (src.height/2);

				int posAX = (int) Math.floor(Xs);
				int posAY = (int) Math.floor(Ys);
				int posBDX = posAX + 1;
				int posCDY = posAY + 1;

				double h = Xs - posAX;
				double v = Ys - posAY;

				double newH = 1 - h;
				double newV = 1 - v;

				int A = posAY * src.width + posAX;
				int B = posAY * src.width + (posAX + 1);
				int C = (posAY + 1) * src.width + posAX;
				int D = (posAY + 1) * src.width + (posAX + 1);

				int rA;
				int gA;
				int bA;
				int rB;
				int gB;
				int bB;
				int rC;
				int bC;
				int gC;
				int rD;
				int bD;
				int gD;
				
				//Um den Rand smooth zu machen muss gecheckt werden, welche Pixel über den Rand gehen und die jeweiligen RGB-Werte auf weiß setzen, damit das Weiß dann mit den anderen gemischt wird
				if(posAX < 0 || posAX >= src.width || posAY < 0 || posAY < 0 || posAY >= src.height){
					rA = 255;
					gA = 255;
					bA = 255;
				} else {
					rA = (src.argb[A] >> 16) & 0xFF;
					gA = (src.argb[A] >> 8) & 0xFF;
					bA = src.argb[A] & 0xFF;
				}

				if(posBDX < 0 || posBDX >= src.width || posAY < 0 || posAY >= src.height){
					rB = 255;
					gB = 255;
					bB = 255;
				} else {
					rB = (src.argb[B] >> 16) & 0xFF;
					gB = (src.argb[B] >> 8) & 0xFF;
					bB = src.argb[B] & 0xFF;
				}
					
				if(posAX < 0 || posAX >= src.width || posCDY < 0 || posCDY >= src.height){
					rC = 255;
					gC = 255;
					bC = 255;
				} else {
					rC = (src.argb[C] >> 16) & 0xFF;
					gC = (src.argb[C] >> 8) & 0xFF;
					bC = src.argb[C] & 0xFF;
				}

				if(posBDX < 0 || posBDX >= src.width || posCDY < 0 || posCDY >= src.height){
					rD = 255;
					gD = 255;
					bD = 255;
				} else {
					rD = (src.argb[D] >> 16) & 0xFF;
					gD = (src.argb[D] >> 8) & 0xFF;
					bD = src.argb[D] & 0xFF;
				}

				int newR = (int) ((rA * newH * newV) + (rB * h * newV) + (rC * newH * v) + (rD * h * v));
				int newG = (int) ((gA * newH * newV) + (gB * h * newV) + (gC * newH * v) + (gD * h * v));
				int newB = (int) ((bA * newH * newV) + (bB * h * newV) + (bC * newH * v) + (bD * h * v));

				dst.argb[dstPos] = 0xFF000000 | (newR << 16) | (newG << 8) | newB;
				}
			}
		}
 	}	   	  	 		 
 		   	  	 		 



