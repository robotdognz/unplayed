package handlers;

import java.io.File;

import processing.core.*;
//import static processing.core.PConstants.*;

public class TileHandler implements Comparable<TileHandler>, Handler {
	PApplet p;
	TextureCache texture;
	File datapath;
	PImage LOD256;
	PImage LOD128 = null;
	PImage LOD64 = null;
	PImage LOD32 = null;
	PImage LOD16 = null;

	public TileHandler(PApplet p, TextureCache texture, File file) {
		this.p = p;
		this.texture = texture;
		datapath = file;
		String path = file.toString();

		try {
			LOD256 = p.loadImage(path); // requestImage
//			LOD256.resize(256, 256);
			LOD256.resize((int) (p.width / 5.625), (int) (p.width / 5.625));

		} catch (Exception e) {
			// set sprite to file not found image
		}
	}

	@Override
	public int compareTo(TileHandler otherTileHandler) {
		String otherName = otherTileHandler.getFile().toString();
		String name = datapath.toString();
		return name.compareTo(otherName);
	}

	@Override
	public PImage getSprite(float scale) {
		if (scale > texture.LOD32) {
			if (LOD16 == null) {
				LOD16 = LOD256.get(); // 16
//				LOD16.resize(16, 16);
				LOD16.resize((int) (p.width / 90), (int) (p.width / 90));
			}
			return LOD16;
		} else if (scale > texture.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
//				LOD32.resize(32, 32);
				LOD32.resize((int) (p.width / 45), (int) (p.width / 45));
			}
			return LOD32;
		} else if (scale > texture.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
//				LOD64.resize(64, 64);
				LOD64.resize((int) (p.width / 22.5), (int) (p.width / 22.5));
			}
			return LOD64;
		} else if (scale > texture.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
//				LOD128.resize(128, 128);
				LOD128.resize((int) (p.width / 11.25), (int) (p.width / 11.25));
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
