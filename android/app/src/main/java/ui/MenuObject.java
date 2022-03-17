package ui;

import handlers.Handler;
import processing.core.PApplet;

public class MenuObject {
	protected float width;
	protected float height;

	protected Handler image;

	public MenuObject(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public MenuObject(float width, float height, Handler image) {
		this.width = width;
		this.height = height;
		this.image = image;
	}

	public void draw(PApplet p, float y) {
	}

	public void drawOnPage(PApplet p, float x, float y) {
		image.draw(p.g, x, y, width, height, 3);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
