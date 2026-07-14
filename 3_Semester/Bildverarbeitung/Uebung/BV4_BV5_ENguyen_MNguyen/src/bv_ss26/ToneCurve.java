// BV Ue4 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ToneCurve {
 		   	  	 		 
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    
    private int[] grayTable = new int[grayLevels];
 		   	  	 		 
	public int[] getGrayTable() {
		return grayTable;
	}

	public ToneCurve(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void updateTable(int brightness, double contrast, double gamma) {
		
		// TODO: Fill the grayTable[] array to map gay input values to gray output values.
		// It will be used as follows: grayOut = grayTable[grayIn].
		//
		// Use brightness, contrast, and gamma settings.
		//
		// See "Gammakorrektur" at slide no. 20 of 
		// http://home.htw-berlin.de/~barthel/veranstaltungen/GLDM/vorlesungen/04_GLDM_Bildmanipulation1_Bildpunktoperatoren.pdf
		//
		// First apply the brightness change, afterwards the contrast modification and finally the gamma correction.
		double grayOut; 

		for(int grayIn = 0; grayIn < grayLevels; grayIn++){
		 	grayOut = contrast * (grayIn + brightness - 128) + 128;
			if (grayOut < 0) grayOut = 0;
			if (grayOut > 255) grayOut = 255;
			grayOut = 255 * Math.pow((grayOut/255), (1/gamma));
			grayTable[grayIn] = (int) grayOut;
		}
	}
	
	public void applyTo(RasterImage image) {
		
		// TODO: apply the gray value mapping to the given image
		for(int x = 0; x < image.width; x++){
			for(int y = 0; y < image.height; y++){
				int pos = y * image.width + x;
				int pixel = image.argb[pos];
				int r = (pixel >> 16) & 0xff;
				int g = (pixel >> 8) & 0xff;
				int b = pixel & 0xff;
				int grayIn = (r + g + b) / 3;
				
				grayIn = grayTable[grayIn];

				image.argb[pos] = 0xFF000000 | (grayIn << 16) | (grayIn << 8) | grayIn;
			}
		}
	}
	
	public void draw(Color lineColor) {
		if(gc == null) return;
		gc.clearRect(0, 0, grayLevels, grayLevels);
		gc.setStroke(lineColor);
		gc.setLineWidth(3);
		
		// TODO: draw the tone curve into the gc graphic context
		// Note that we need to add 0.5 to all coordinates to align points to pixel centers 
		
		double shift = 0.5;

		// Remark: This is some dummy code to give you an idea for graphics drawing using paths	
		//For schleife über x und y koordinaten und dann die koordinaten mit dem array füllen	
		/*gc.beginPath();
		gc.moveTo(64 + shift, 128 + shift);
		gc.lineTo(128 + shift, 192 + shift);
		gc.lineTo(192 + shift, 64 + shift);
		gc.stroke();*/

		gc.beginPath();
		gc.moveTo(0 + shift, 255 - grayTable[0] + shift);
		for(int i = 1; i < grayLevels; i++){
			gc.lineTo(i + shift, 255 - grayTable[i] + shift);
		}
		gc.stroke();
	}

 		   	  	 		 
}
 		   	  	 		 




