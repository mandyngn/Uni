// BV Ue1 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;

public class GaussFilter {
 		   	  	 		 
	private double[][] kernel;
 		   	  	 		 
	public double[][] getKernel() {
		return kernel;
	}

	public void apply(RasterImage src, RasterImage dst, int kernelSize, double sigma) {
 		   	  	 		 
		// TODO: Implement a Gauss filter of size "kernelSize" x "kernelSize" with given "sigma"
		
		// Step 1: Allocate appropriate memory for the field variable "kernel" representing a 2D array.
		kernel = new double[kernelSize][kernelSize];

		// Step 2: Fill in appropriate values into the "kernel" array.
		// Hint:
		// Use g(d) = e^(- d^2 / (2 * sigma^2)), where d is the distance of a coefficient's position to the hot spot.
		// Note that in this comment e^ denotes the exponential function and ^2 the square. In Java ^ is a different operator. 

		//Kernel geht von Minus zu Plus deswegen durch 2
		int kernelRange = kernelSize/2;
		double sum = 0;

		for (int dy = -kernelRange; dy <= kernelRange; dy++){
			for(int dx = -kernelRange; dx <= kernelRange; dx++){
				double d = Math.sqrt(dx*dx + dy*dy);
				double g = Math.exp((-d*d)/(2*sigma*sigma));
				kernel[dy + kernelRange][dx + kernelRange]= g;
				sum += g;
			}
		}

		// Step 3: Normalize the "kernel" such that the sum of all its values is one.
		for(int dy = -kernelRange; dy <= kernelRange; dy++){
			for(int dx = -kernelRange; dx <= kernelRange; dx++){
				double kernCo = kernel[dy + kernelRange][dx + kernelRange];
				kernel[dy + kernelRange][dx + kernelRange] = kernCo/sum;
			}
		}

		// Step 4: Apply the filter given by "kernel" to the source image "src". The result goes to image "dst".
		// Use "constant continuation" for boundary processing.
		for (int y = 0; y < src.height; y++){
			for (int x = 0; x < src.width; x++){
				int posHotspot = y * src.width + x;
				double newSum = 0;

				for(int dy = -kernelRange; dy <= kernelRange; dy++){
					for(int dx = -kernelRange; dx <= kernelRange; dx++){

						//Nachbarn vom Hotspot 
						int newX = x + dx;
						int newY = y + dy;

						if(newX < 0){
							newX = 0;
						} else if(newX >= src.width){
							newX = src.width - 1;
						}
						if(newY < 0){
							newY = 0;
						} else if(newY >= src.height){
							newY = src.height - 1;
						}

						//Postion des Nachbars im Bild
						int newPos = newY * src.width + newX;
						//nimmt Grauwert des Nachbarn
						int gray = src.argb[newPos] & 0xff;

						double kernCo = kernel[dy + kernelRange][dx + kernelRange];
						newSum += gray * kernCo; 
					}
				}
				int newGray = (int) newSum;
				dst.argb[posHotspot] = 0xFF000000 | (newGray << 16) | (newGray << 8) | newGray;
			}
		}		
	}
}