// BV Ue3 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;

import java.util.Arrays;

public class MorphologicFilter {
 		   	  	 		 
	// filter implementations go here:
	
	public void copy(RasterImage src, RasterImage dst) {
		// TODO: just copy the image
		
		/* Das gleiche wie unten aber aufwändiger

		for(int pos = 0; pos < src.argb.length; pos ++){
			dst.argb[pos] = src.argb[pos];
		}
		*/
		System.arraycopy(src.argb, 0, dst.argb, 0, src.argb.length);
	}
	
	public void dilation(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// kernel's first dimension: y (row), second dimension: x (column)
		// TODO: dilate the image using the given kernel
		Arrays.fill(dst.argb, 0xFFFFFFFF);

		for(int y = 0; y < src.height; y++){
			for(int x = 0; x < src.width; x++){
				int pos = y * src.width + x;
				int pixel = src.argb[pos];
				int black = 0XFF000000;
				int kernelRange = 7/2;

				for (int dy = -kernelRange; dy <= kernelRange; dy++){
					for(int dx = -kernelRange; dx <= kernelRange; dx++){
						//Nachbarn vom Hotspot
						int newX = x + dx;
						int newY = y + dy;

						if (pixel == black) {
    						if (newX >= 0 && newX < src.width && newY >= 0 && newY < src.height) {
        						int kernelY = dy + kernelRange;
        						int kernelX = dx + kernelRange;

        						if (kernel[kernelY][kernelX]) {
            						int newPos = newY * src.width + newX;
            						dst.argb[newPos] = 0xFF000000;
								}
							}
						}
					}
				}
			}
		}
	}
 		   	  	 		 
	public void erosion(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// This is already implemented. Nothing to do.
		// It will function once you implemented dilation and RasterImage invert()
		src.invert();
		dilation(src, dst, kernel);
		dst.invert();
		src.invert();
	}
	
	public void opening(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// TODO: implement opening by using dilation() and erosion()
		RasterImage temp = new RasterImage(src.width, src.height);
		erosion(src, temp, kernel);
		dilation(temp, dst, kernel);
	}
	
	public void closing(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// TODO: implement closing by using dilation() and erosion()
		RasterImage temp = new RasterImage(src.width, src.height);
		dilation(src, temp, kernel);
		erosion(temp, dst, kernel);
	}
}