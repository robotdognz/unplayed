package handlers;

import java.io.File;

import processing.core.*;

public class ImageHandler implements Comparable<ImageHandler>, Handler {
	PApplet p;
	TextureCache texture;
	File datapath;
	PImage LOD256;
	PImage LOD128 = null;
	PImage LOD64 = null;
	PImage LOD32 = null;
	PImage LOD16 = null;
	int pWidth;
	int pHeight;

	public ImageHandler(PApplet p, TextureCache texture, File file, int pWidth, int pHeight) {
		this.p = p;
		this.texture = texture;
		datapath = file;
		this.pWidth = pWidth * 100; // these are turned from grid amound to draw units for the level
		this.pHeight = pHeight * 100;
		//String path = file.getAbsolutePath();
		String path = file.toString();

		try {
			LOD256 = p.requestImage(path);
			LOD256.resize(256 * pWidth, 256 * pHeight);
		} catch (Exception e) {
			// set sprite to file not found image
		}
	}

	@Override
	public int getWidth() {
		return pWidth;
	}

	@Override
	public int getHeight() {
		return pHeight;
	}

	@Override
	public PImage getSprite(float scale) {
		if (scale > texture.LOD32) {
			if (LOD16 == null) {
				LOD16 = LOD256.get(); // 16
				LOD16.resize(16 * pWidth / 100, 16 * pHeight / 100);
			}
			return LOD16;
		} else if (scale > texture.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
				LOD32.resize(32 * pWidth / 100, 32 * pHeight / 100);
			}
			return LOD32;
		} else if (scale > texture.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
				LOD64.resize(64 * pWidth / 100, 64 * pHeight / 100);
			}
			return LOD64;
		} else if (scale > texture.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
				LOD128.resize(128 * pWidth / 100, 128 * pHeight / 100);
			}
			return LOD128;
		} else {
			return LOD256;
		}
	}

	@Override
	public File getFile() {
		return datapath;
	}

	@Override
	public void draw(float pX, float pY, float size) {
		// calculate how to scale the image so it appears in the scroll bar correctly
		float scaleFactor;
		if (getWidth() >= getHeight()) {
			scaleFactor = size / getWidth();
		} else {
			scaleFactor = size / getHeight();
		}
		// draw the scaled image
		p.image(getSprite(6), pX, pY, pWidth * scaleFactor, pHeight * scaleFactor);
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
