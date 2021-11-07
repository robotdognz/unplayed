package handlers;

import java.io.File;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class Handler {
	private PApplet p;
	protected TextureCache texture;
	protected File datapath;

	private PImage LOD256;
	private PImage LOD128 = null;
	private PImage LOD64 = null;
	private PImage LOD32 = null;
	private PImage LOD16 = null;

	private int pWidth;
	private int pHeight;

	public Handler(PApplet p, TextureCache texture, File file, int pWidth, int pHeight) {
		this.p = p;
		this.texture = texture;
		this.datapath = file;

		this.pWidth = pWidth;
		this.pHeight = pHeight;

		String path = file.toString();

		try {
			LOD256 = p.requestImage(path);
			LOD256.resize(256 * pWidth, 256 * pHeight);
		} catch (Exception e) {
			// set sprite to file not found image
		}
	}

	public PImage getSprite(float scale) {
		if (scale > texture.LOD32) {
			if (LOD16 == null) {
				LOD16 = LOD256.get(); // 16
				LOD16.resize(16 * pWidth, 16 * pHeight);
			}
			return LOD16;
		} else if (scale > texture.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
				LOD32.resize(32 * pWidth, 32 * pHeight);
			}
			return LOD32;
		} else if (scale > texture.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
				LOD64.resize(64 * pWidth, 64 * pHeight);
			}
			return LOD64;
		} else if (scale > texture.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
				LOD128.resize(128 * pWidth, 128 * pHeight);
			}
			return LOD128;
		} else {
			return LOD256;
		}
	}

	public File getFile() {
		return datapath;
	}

	public int getWidth() {
		return pWidth; // TODO: find an correct places where the *100 is needed and clean this up
	}

	public int getHeight() {
		return pHeight;
	}

	public void draw(float pX, float pY, float size) {

		// calculate how to scale the image so it appears in the scroll bar correctly
		// and draw the scaled image
		p.image(getSprite(6), pX, pY, getWidth() * size, getHeight() * size);

//		// calculate how to scale the image so it appears in the scroll bar correctly
//		float scaleFactor;
//		if (getWidth() >= getHeight()) {
//			scaleFactor = size / getWidth();
//		} else {
//			scaleFactor = size / getHeight();
//		}
//		// draw the scaled image
//		p.image(getSprite(6), pX, pY, getWidth() * scaleFactor, getHeight() * scaleFactor);

		// Legacy code from 2020 development, unknown use
		// if (LOD128 != null) {
		// float scaleFactor;
		// if (getWidth() >= getHeight()) {
		// scaleFactor = size / getWidth();
		// } else {
		// scaleFactor = size / getHeight();
		// }
		//
		// //draw the scaled image
		// image(getSprite(6), pX, pY, pWidth*scaleFactor, pHeight*scaleFactor);
		// } else {
		// showToast("Failed to load: " + datapath);
		// }
	}
}
