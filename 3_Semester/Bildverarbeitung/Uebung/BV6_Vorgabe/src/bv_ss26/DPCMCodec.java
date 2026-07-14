// BV Ue6 SS2026 Vorgabe
//
// Copyright (C) 2026 by Klaus Jung
// All rights reserved.
// Date: 2026-04-01
 		   	  	 		 

package bv_ss26;


public class DPCMCodec {
	 		   	  	 		 
	public enum PredictionType { 
		A("A (horizontal)"), 
		B("B (vertical)"), 
		C("C (diagonal)"),
		ABC("A+B-C"), 
		AB_MEAN("(A+B)/2"),
		ADAPTIVE("adaptive");
		
		private final String name;       
	    private PredictionType(String s) { name = s; }
	    public String toString() { return this.name; }
	};


	public void processDPCM(RasterImage originalImage, RasterImage errorImage, RasterImage reconstructedImage, double quantizationDelta, PredictionType type) {
		// TODO: Encode the originalImage with DPCM using the given prediction type, 
		// visualize the prediction error in errorImage, and
		// decode the prediction error into reconstructedImage.
		
		// Hint: You can implement encoding and decoding with a single iteration over the pixels of the given image.
		
		// Optional: Implement DPCM with quantization. The quantization step size is given in quantizationDelta.

		for (int y = 0; y < originalImage.height; y++) {
			for (int x = 0; x < originalImage.width; x++) {
				int pos = y * originalImage.width + x;
				int originalGray = originalImage.argb[pos] & 0xff;
				int a = x > 0 ? (reconstructedImage.argb[y * originalImage.width + (x - 1)] & 0xff) : 128;
				int b = y > 0 ? (reconstructedImage.argb[(y - 1) * originalImage.width + x] & 0xff) : 128;
				int c = (x > 0 && y > 0) ? (reconstructedImage.argb[(y - 1) * originalImage.width + (x - 1)] & 0xff) : 128;
				int predictedGray;

				switch (type) {
				case B:
					predictedGray = b;
					break;
				case C:
					predictedGray = c;
					break;
				case ABC:
					predictedGray = Math.max(0, Math.min(255, a + b - c));
					break;
				case AB_MEAN:
					predictedGray = (int)Math.round((a + b) / 2.0);
					break;
				case ADAPTIVE:
					predictedGray = Math.abs(a - c) < Math.abs(b - c) ? b : a;
					break;
				case A:
				default:
					predictedGray = a;
					break;
				}

				int error = originalGray - predictedGray;
				int codedError = error;

				if (quantizationDelta > 0) {
					int q = (int)Math.round(error / quantizationDelta);
					codedError = (int)Math.round(q * quantizationDelta);
				}

				int reconstructedGray = Math.max(0, Math.min(255, predictedGray + codedError));
				int visibleError = Math.max(0, Math.min(255, codedError + 128));

				errorImage.argb[pos] = 0xff000000 | (visibleError << 16) | (visibleError << 8) | visibleError;
				reconstructedImage.argb[pos] = 0xff000000 | (reconstructedGray << 16) | (reconstructedGray << 8) | reconstructedGray;
			}
		}
	}
	
	public double getMSE(RasterImage originalImage, RasterImage reconstructedImage) {
		// TODO: calculate and return the Mean Square Error between the given images
		if (originalImage == null || reconstructedImage == null) {
			return Double.NaN;
		}
		if (originalImage.width != reconstructedImage.width || originalImage.height != reconstructedImage.height) {
			return Double.NaN;
		}

		double sum = 0.0;
		int pixelCount = originalImage.width * originalImage.height;
		
		for (int i = 0; i < pixelCount; i++) {
			int originalGray = originalImage.argb[i] & 0xff;
			int reconstructedGray = reconstructedImage.argb[i] & 0xff;
			double diff = originalGray - reconstructedGray;
			sum += diff * diff;
		}
		return sum / pixelCount;
	}
	
 		   	  	 		 
}