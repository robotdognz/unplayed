package handlers;

import static processing.core.PConstants.CENTER;

import java.io.File;

import editor.DebugOutput;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public abstract class Handler {
	protected PApplet p;
	protected TextureCache texture;
	protected File datapath;

	private PImage LOD256;
	private PImage LOD128 = null;
	private PImage LOD64 = null;
	private PImage LOD32 = null;
	private PImage LOD16 = null;

	protected int width;
	protected int height;

	private float widthRenderRatio;
	private float heightRenderRatio;

	private boolean isRotatable; // can the editor default angle for this object be changed
	protected int editorRotation; // angle to add when creating new instance with level editor

	public Handler(PApplet p, TextureCache texture, File file, int width, int height) {
		this.p = p;
		this.texture = texture;
		this.datapath = file;

		this.width = width;
		this.height = height;

		String path = file.toString();

		try {
			LOD256 = p.requestImage(path);
			LOD256.resize(256 * width, 256 * height);
		} catch (Exception e) {
			// set sprite to file not found image
		}

		// setup rendering ratios for use in the editor bottom scroll bar
		if (width > height) {
			heightRenderRatio = (float) height / width;
			widthRenderRatio = 1;
		} else if (width < height) {
			heightRenderRatio = 1;
			widthRenderRatio = (float) width / height;
		} else {
			widthRenderRatio = 1;
			heightRenderRatio = 1;
		}

		this.isRotatable = true;
		this.editorRotation = 0;
	}

	public void setEditorAngle(float angle) {
		if (isRotatable) {
			editorRotation = (int) angle;
			DebugOutput.pushMessage("" + angle, 1);
		}
	}

	public int getEditorAngle() {
		return editorRotation;
	}

	public PImage getSprite(float scale) {
		if (scale > TextureCache.LOD32) {
			if (LOD16 == null) {
				LOD16 = LOD256.get(); // 16
				LOD16.resize(16 * width, 16 * height);
			}
			return LOD16;
		} else if (scale > TextureCache.LOD64) {
			if (LOD32 == null) {
				LOD32 = LOD256.get(); // 32
				LOD32.resize(32 * width, 32 * height);
			}
			return LOD32;
		} else if (scale > TextureCache.LOD128) {
			if (LOD64 == null) {
				LOD64 = LOD256.get(); // 64
				LOD64.resize(64 * width, 64 * height);
			}
			return LOD64;
		} else if (scale > TextureCache.LOD256) {
			if (LOD128 == null) {
				LOD128 = LOD256.get(); // 16
				LOD128.resize(128 * width, 128 * height);
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
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void drawEditor(float pX, float pY, float size) {
		// calculate how to scale the image so it appears in the editor bottom scroll
		// bar correctly and draw the scaled image
		if (editorRotation != 0) {
			p.pushMatrix();
			p.imageMode(CENTER);
			p.translate(pX, pY);
			p.rotate(PApplet.radians(editorRotation));
			p.image(getSprite(6), 0, 0, widthRenderRatio * size, heightRenderRatio * size);
			p.popMatrix();
		} else {
			p.image(getSprite(6), pX, pY, widthRenderRatio * size, heightRenderRatio * size);
		}
	}
	
	public void draw(PGraphics graphics, float x, float y, float scale) {
		graphics.image(getSprite(scale), x, y);
	}

	public void draw(PGraphics graphics, float x, float y, float width, float height, float scale) {
		graphics.image(getSprite(scale), x, y, width, height);
	}
	
	public void drawAll() {
		p.image(getSprite(TextureCache.LOD32), 0, 0, 100, 100);
		p.image(getSprite(TextureCache.LOD64), 0, 0, 100, 100);
		p.image(getSprite(TextureCache.LOD128), 0, 0, 100, 100);
		p.image(getSprite(TextureCache.LOD256), 0, 0, 100, 100);
	}
}