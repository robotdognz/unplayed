package handlers;

import java.io.File;

import processing.core.*;

public class ImageHandler extends Handler implements Comparable<ImageHandler> {

	public ImageHandler(PApplet p, TextureCache texture, File file, int width, int height) {
		super(p, texture, file, width, height);
	}

	@Override
	public int compareTo(ImageHandler otherImageHandler) {
		float otherArea = otherImageHandler.getWidth() * otherImageHandler.getHeight();
		float area = getWidth() * getHeight();
		if (otherArea > area) {
			return -1;
		} else if (otherArea < area) {
			return 1;
		} else {
			return 0;
		}
	}
}
