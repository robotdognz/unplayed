package handlers;

import java.io.File;

import processing.core.*;
import static processing.core.PConstants.*;

public class TileHandler implements Comparable<TileHandler>, Handler {
	PApplet p;
	TextureCache texture;
	File datapath;
	PImage LOD256;
	float scale256;
	PImage LOD128 = null;
	float scale128;
	PImage LOD64 = null;
	float scale64;
	PImage LOD32 = null;
	float scale32;
	PImage LOD16 = null;
	float scale16;
	
	PShape tile;

	public TileHandler(PApplet p, TextureCache texture, File file) {
		this.p = p;
		this.texture = texture;
		datapath = file;
		String path = file.getAbsolutePath();

		try {
			LOD256 = p.loadImage(path); //requestImage
			LOD256.resize(256, 256);
			scale256 = 100 / LOD256.width;
		} catch (Exception e) {
			// set sprite to file not found image
		}
		
		//tile = p.createShape(RECT,-50,-50,100,100);
		tile = p.createShape();
		tile.beginShape(QUAD);
//		tile.setStroke(false);
		tile.texture(LOD256);
		tile.vertex(-50, -50); //top left
	    tile.vertex(50, -10); //top right
	    tile.vertex(50, 50); //bottom right
	    tile.vertex(-50, 50); //bottom left
		tile.endShape();
	}

	@Override
	public int compareTo(TileHandler otherTileHandler) {
		String otherName = otherTileHandler.getFile().toString();
		String name = datapath.toString();
		return otherName.compareTo(name);
	}

	@Override
	public PImage getSprite(float scale) {
		if (scale > texture.LOD32) {
			if (LOD16 == null) {
				LOD16 = LOD256.get(); // 16
				LOD16.resize(16, 16);
				scale16 = 100 / LOD16.width;
			}
			return LOD16;
		} else if (scale > texture.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
				LOD32.resize(32, 32);
				scale32 = 100 / LOD32.width;
			}
			return LOD32;
		} else if (scale > texture.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
				LOD64.resize(64, 64);
				scale64 = 100 / LOD64.width;
			}
			return LOD64;
		} else if (scale > texture.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
				LOD128.resize(128, 128);
				scale128 = 100 / LOD128.width;
			}
			return LOD128;
		} else {
			return LOD256;
		}
	}

	public void drawSprite(PGraphics g, float scale) {
//		if (scale > texture.LOD32) {
//			g.scale(scale16);
//		} else if (scale > texture.LOD64) {
//			g.scale(scale32);
//		} else if (scale > texture.LOD128) {
//			g.scale(scale64);
//		} else if (scale > texture.LOD256) {
//			g.scale(scale128);
//		} else {
//			g.scale(scale256);
//		}
		//g.scale(100/getSprite(scale).width);
//		g.image(getSprite(scale), 0, 0);
		g.shape(tile);
	}

	@Override
	public File getFile() {
		return datapath;
	}

	@Override
	public void draw(float pX, float pY, float size) {
		// draw the scaled image
		p.image(getSprite(6), pX, pY, size, size);
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int getHeight() {
		return 100;
	}
}
