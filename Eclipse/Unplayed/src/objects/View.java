package objects;

import processing.core.PApplet;
import processing.core.PGraphics;

public class View extends Rectangle {
	private PApplet p;
	private int color; // color of border to be drawn in game

	public View(PApplet p, int x, int y, int rWidth, int rHeight) {
		super(x, y, rWidth, rHeight);
		this.p = p;
		color = p.color(p.random(255), p.random(255), p.random(255), 100);
	}

	public void draw(PGraphics graphics) {
		//graphics.noFill();
		//graphics.stroke(color);
		//graphics.strokeWeight(4);
		graphics.fill(color);
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
		p.rect(pX, pY, getWidth() * scaleFactor, getHeight() * scaleFactor);
		//TODO: draw width and height on top
	}

	public int getColor() {
		return color;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

}
