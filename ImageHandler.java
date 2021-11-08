package handlers;

import java.io.File;

import processing.core.*;

public class ImageHandler extends Handler implements Comparable<ImageHandler> {
//	private float widthRenderScale;
//	private float heightRenderScale;

	public ImageHandler(PApplet p, TextureCache texture, File file, int width, int height) {
		super(p, texture, file, width, height);

//		if (width > height) {
//			heightRenderScale = 1/width;
//			widthRenderScale = 1;
//		} else if (width < height) {
//			heightRenderScale = 1;
//			widthRenderScale = 1/height;
//		} else {
//			widthRenderScale = width;
//			heightRenderScale = height;
//		}
	}

//	public void draw(float pX, float pY, float size) {
//		// calculate how to scale the image so it appears in the scroll bar correctly
//		// and draw the scaled image
//		p.image(getSprite(6), pX, pY, width * size, height * size);
//	}

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
