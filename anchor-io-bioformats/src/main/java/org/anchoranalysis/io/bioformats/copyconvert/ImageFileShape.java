package org.anchoranalysis.io.bioformats.copyconvert;

import org.anchoranalysis.image.extent.ImageDimensions;

import lombok.Value;

/** How to shape the image (across various dimensions) after the copying */
@Value
public class ImageFileShape {
	private final ImageDimensions imageDimensions;
	private final int numberChannels;
	private final int numberFrames;
	
	/** The total number of z-slices across all frames, channels */
	public int totalNumberSlices() {
		return numberFrames*imageDimensions.getZ()*numberChannels;
	}
	
	public int getNumberSlices() {
		return imageDimensions.getZ();
	}
}