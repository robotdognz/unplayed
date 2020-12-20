package handlers;

import java.io.File;

import processing.core.*;
import static processing.core.PConstants.*;

public class TileHandler implements Comparable<TileHandler>, Handler {
	PApplet p;
	TextureCache texture;
	File datapath;
	PImage LOD256;
	PImage LOD128 = null;
	PImage LOD64 = null;
	PImage LOD32 = null;
	PImage LOD16 = null;
	
	PShape tile256;
	PShape tile128;
	PShape tile64;
	PShape tile32;
	PShape tile16;

	public TileHandler(PApplet p, TextureCache texture, File file) {
		this.p = p;
		this.texture = texture;
		datapath = file;
		String path = file.getAbsolutePath();

		try {
			LOD256 = p.loadImage(path); //requestImage
			LOD256.resize(256, 256);
			
		} catch (Exception e) {
			// set sprite to file not found image
		}
		
		LOD16 = LOD256.get(); // 16
		LOD16.resize(16, 16);
		LOD32 = LOD256.get(); // 32
		LOD32.resize(32, 32);
		LOD64 = LOD256.get(); // 64
		LOD64.resize(64, 64);
		LOD128 = LOD256.get(); // 16
		LOD128.resize(128, 128);
		tile16 = p.createShape();
		tile32 = p.createShape();
		tile64 = p.createShape();
		tile128 = p.createShape();
		tile256 = p.createShape();
		createPShape(tile16, LOD16);
		createPShape(tile32, LOD32);
		createPShape(tile64, LOD64);
		createPShape(tile128, LOD128);
		createPShape(tile256, LOD256);
	}
	
	private void createPShape(PShape tile, PImage texture) {
		tile.beginShape(QUAD);
		tile.textureMode(NORMAL);
		tile.texture(texture);
		tile.vertex(-50, -50, 0, 0); //top left
	    tile.vertex(50, -50, 1, 0); //top right
	    tile.vertex(50, 50, 1, 1); //bottom right
	    tile.vertex(-50, 50, 0, 1); //bottom left
		tile.endShape();
		tile.setStroke(false);
		//tile.re
	}
	
	public PShape createPShape(float x, float y, float width, float height, float flipX, float flipY, float angle, float scale) {
		PShape tile;
		tile = p.createShape();
		tile.beginShape(QUAD);
		tile.textureMode(NORMAL);
		tile.texture(getSprite(scale));
		tile.vertex(-(width/2), -(height/2), 0, 0); //top left
	    tile.vertex((width/2), -(height/2), 1, 0); //top right
	    tile.vertex((width/2), (height/2), 1, 1); //bottom right
	    tile.vertex(-(width/2), (height/2), 0, 1); //bottom left
		tile.endShape();
		tile.setStroke(false);
		tile.scale(flipX, flipY);
		tile.translate(x+(width/2), y+(height/2));
		
		return tile;
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
			}
			return LOD16;
		} else if (scale > texture.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
				LOD32.resize(32, 32);
			}
			return LOD32;
		} else if (scale > texture.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
				LOD64.resize(64, 64);
			}
			return LOD64;
		} else if (scale > texture.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
				LOD128.resize(128, 128);
			}
			return LOD128;
		} else {
			return LOD256;
		}
	}

	public void drawSprite(PGraphics g, float scale) {
		if (scale > texture.LOD32) {
			g.shape(tile16);
		} else if (scale > texture.LOD64) {
			g.shape(tile32);
		} else if (scale > texture.LOD128) {
			g.shape(tile64);
		} else if (scale > texture.LOD256) {
			g.shape(tile128);
		} else {
			g.shape(tile256);
		}
		
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
