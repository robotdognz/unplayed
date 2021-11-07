package handlers;

import java.io.File;

import objects.Event;
import processing.core.*;

public class EventHandler extends Handler implements Comparable<EventHandler>{
//	PApplet p;
//	TextureCache texture;
//	File datapath;
//	PImage LOD256;
//	PImage LOD128 = null;
//	PImage LOD64 = null;
//	PImage LOD32 = null;
//	PImage LOD16 = null;
//	private int pWidth;
//	private int pHeight;

	public EventHandler(PApplet p, TextureCache texture, File file) {
		super(p, texture, file, 1, 1);
		
//		this.p = p;
//		this.texture = texture;
//		datapath = file;
		
//		this.pWidth = 100; // these are turned from grid amount to draw units for the level
//		this.pHeight = 100;
//		String path = file.toString();
//
//		try {
//			LOD256 = p.loadImage(path); // requestImage
//			LOD256.resize(256 * pWidth / 100, 256 * pHeight / 100);
////			LOD256.resize((int) (p.width / 5.625), (int) (p.width / 5.625));
//		} catch (Exception e) {
//			// set sprite to file not found image
//		}
	}

	public Event makeEvent(int x, int y) {
		// this method gets overridden whenever an event handler object is created
		return null;
	}

//	@Override
//	public int getWidth() {
//		return pWidth;
//	}
//
//	@Override
//	public int getHeight() {
//		return pHeight;
//	}

//	@Override
//	public PImage getSprite(float scale) {
//		if (scale > texture.LOD32) {
//			if (LOD16 == null) {
//				LOD16 = LOD256.get(); // 16
//				LOD16.resize(16 * pWidth / 100, 16 * pHeight / 100);
////				LOD16.resize((int) (p.width / 90), (int) (p.width / 90));
//			}
//			return LOD16;
//		} else if (scale > texture.LOD64) {
//			if (LOD32 == null) {
//				LOD32 = LOD256.get(); // 32
//				LOD32.resize(32 * pWidth / 100, 32 * pHeight / 100);
////				LOD32.resize((int) (p.width / 45), (int) (p.width / 45));
//			}
//			return LOD32;
//		} else if (scale > texture.LOD128) {
//			if (LOD64 == null) {
//				LOD64 = LOD256.get(); // 64
//				LOD64.resize(64 * pWidth / 100, 64 * pHeight / 100);
////				LOD64.resize((int) (p.width / 22.5), (int) (p.width / 22.5));
//			}
//			return LOD64;
//		} else if (scale > texture.LOD256) {
//			if (LOD128 == null) {
//				LOD128 = LOD256.get(); // 16
//				LOD128.resize(128 * pWidth / 100, 128 * pHeight / 100);
////				LOD128.resize((int) (p.width / 11.25), (int) (p.width / 11.25));
//			}
//			return LOD128;
//		} else {
//			return LOD256;
//		}
//	}

//	@Override
//	public File getFile() {
//		return datapath;
//	}

//	@Override
//	public void draw(float pX, float pY, float size) {
//		// calculate how to scale the image so it appears in the scroll bar correctly
//		float scaleFactor;
//		if (getWidth() >= getHeight()) {
//			scaleFactor = size / getWidth();
//		} else {
//			scaleFactor = size / getHeight();
//		}
//		// draw the scaled image
//		p.image(getSprite(6), pX, pY, pWidth * scaleFactor, pHeight * scaleFactor);
//		// if (LOD128 != null) {
//		// float scaleFactor;
//		// if (getWidth() >= getHeight()) {
//		// scaleFactor = size/getWidth();
//		// } else {
//		// scaleFactor = size/getHeight();
//		// }
//
//		// //draw the scaled image
//		// image(getSprite(6), pX, pY, pWidth*scaleFactor, pHeight*scaleFactor);
//		// } else {
//		// showToast("Failed to load: " + datapath);
//		// }
//	}

	@Override
	public int compareTo(EventHandler otherEventHandler) {
		String otherName = otherEventHandler.getFile().toString();
		String name = datapath.toString();
		return otherName.compareTo(name);
	}
}
