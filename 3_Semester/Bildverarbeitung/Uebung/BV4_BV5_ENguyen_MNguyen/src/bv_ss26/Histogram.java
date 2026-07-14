// BV Ue4 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Histogram {
 		   	  	 		 
	private static final int grayLevels = 256;
	
    private GraphicsContext gc;
    private int maxHeight;
    
    private int[] histogram = new int[grayLevels];
 		   	  	 		 
    public Histogram() {
	}
    
	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}
	
	public int[] getValues() {
		return histogram;
	}

	public void setImageRegion(RasterImage image, int regionStartX, int regionStartY, int regionWidth, int regionHeight) {
		
		// TODO: calculate histogram[] out of the gray values found the given image region
		//durch das bild iterieren und graustufe pixel zählen und in array speichern
		int xEnd = regionStartX + regionWidth;
		int yEnd = regionStartY + regionHeight;

		for(int i = 0; i < grayLevels; i ++){
			histogram[i] = 0;
		}

		for(int x = regionStartX; x <= xEnd; x++){
			for(int y = regionStartY; y <= yEnd; y++){
				if(x >= 0 && x < image.width && y >= 0 && y < image.height){
					int pos = y * image.width + x;
					int pixel = image.argb[pos];
					int r = (pixel >> 16) & 0xff;
					int g = (pixel >> 8) & 0xff;
					int b = pixel & 0xff;
					int grayIn = (r + g + b) / 3;

					histogram[grayIn] += 1;
				}
			}
		}
	}

	
	public Integer getMinimum() {
		// Will be used in Exercise 5.
		int min = 0;
		for(int i = 0; i < grayLevels; i++){
			if(histogram[i] > 0){
				min = i;
				break;
			}
		}
		return min;
	}
 		   	  	 		 
	public Integer getMaximum() {
		// Will be used in Exercise 5.
		int max = 0;
		for(int i = 255; i >= 0; i--){
			if(histogram[i] > 0){
				max = i;
				break;
			}
		}
		return max;
	}
 		   	  	 		 
	public Double getMean() {
		// Will be used in Exercise 5.
		double mean = 0;
		double pJ = 0;
		double pixels = 0;

		for(int i = 0; i < grayLevels; i++){
			pixels += histogram[i];
		}

		for(int i = 0; i < grayLevels; i++){
			pJ = histogram[i] / pixels;
			mean += i * pJ;
		}
		return mean;
	}
 		   	  	 		 
	public Integer getMedian() {
		// Will be used in Exercise 5.
		double pixels = 0;
		int median = 0;
		double sum = 0;

		for(int i = 0; i < grayLevels; i++){
			pixels += histogram[i];
		}

		for(int i = 0; i < grayLevels; i++){
			sum += histogram[i];
			if(sum >= pixels/2){
				median = i;
				break;
			}
		}
		return median;
	}
 		   	  	 		 
	public Double getVariance() {
		// Will be used in Exercise 5.
		double pJ = 0;
		double pixels = 0;
		double mean = getMean();
		double meanDifference = 0;
		double variance = 0;

		for(int i = 0; i < grayLevels; i++){
			pixels += histogram[i];
		}

		for(int i = 0; i < grayLevels; i++){
			pJ = histogram[i]/pixels;
			meanDifference = Math.pow(i - mean, 2);
			variance += meanDifference * pJ;
		}
		return variance;
	}
	

	//KLAUSURRELEVANT!! -> log2(x) = log10(x) / log10(2)
	public Double getEntropy() {
		// Will be used in Exercise 5.
		double pJ = 0;
		double pixels = 0;
		double entropy = 0;
		double logTwo = 0;

		
		for(int i = 0; i < grayLevels; i++){
			pixels += histogram[i];
		}
		
		for(int i = 0; i < grayLevels; i++){
			pJ = histogram[i]/pixels;
			if(pJ == 0){
				continue;
			} else {
				logTwo = Math.log10(pJ) / Math.log10(2);
				entropy += - pJ * logTwo;
			}
		}
		return entropy;
	}
 		   	  	 		 
	public void draw(Color lineColor) {
		if(gc == null) return;
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setStroke(lineColor);
		gc.setLineWidth(1);
 		   	  	 		 
		// TODO: draw histogram[] into the gc graphic context
		// Note that we need to add 0.5 to all coordinates to align points to pixel centers 
		
		double shift = 0.5;
		
		// Remark: This is some dummy code to give you an idea for line drawing		
		/*gc.strokeLine(shift, shift, grayLevels-1 + shift, maxHeight-1 + shift);
		gc.strokeLine(grayLevels-1 + shift, shift, shift, maxHeight-1 + shift);*/
		double maxHisto = 0;

		for(int i = 0; i < grayLevels; i++){
			int value = histogram[i];
			if(value > maxHisto){
				maxHisto = value;
			}
		}

		for(int i = 0; i < grayLevels; i++){
			int value = histogram[i];

			double len = value * (maxHeight/maxHisto);
			gc.strokeLine(i + shift, maxHeight + shift, i + shift, maxHeight - len + shift);
		}
	} 	  	 		 
}
 		   	  	 		 






