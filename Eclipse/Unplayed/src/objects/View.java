package objects;

import processing.core.PApplet;
import processing.core.PGraphics;

public class View extends Rectangle {
	private PApplet p;
	private int color; // color of border to be drawn in game
	private boolean removed; // if this is true, then if you're a page using this as your
	// selection area you should be removed

	public View(PApplet p, int x, int y, int rWidth, int rHeight) {
		super(x, y, rWidth, rHeight);
		this.p = p;
		removed = false;
		color = p.color(255, 0, 0); // TODO: implement random color
	}

	public void draw(PGraphics graphics) {
		graphics.noFill();
		graphics.stroke(color);
		graphics.strokeWeight(4);
		graphics.rect(getX(), getY(), getWidth(), getHeight());
		graphics.noStroke();
	}

	public void drawToolbar(float pX, float pY, float size) {
		// calculate how to scale the image so it appears in the scroll bar correctly
		float scaleFactor;
		if (getWidth() >= getHeight()) {
			scaleFactor = size / getWidth();
		} else {
			scaleFactor = size / getHeight();
		}
		// draw the scaled view
		p.noStroke();
		p.fill(color);
		//p.strokeWeight(4);
		p.rect(pX, pY, getWidth() * scaleFactor, getHeight() * scaleFactor);
		//p.noStroke();
	}

	public int getColor() {
		return color;
	}

	public void remove() {
		removed = true;
	}

	public boolean isRemoved() {
		return removed;
	}

}
