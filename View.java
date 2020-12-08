package objects;

import processing.core.PApplet;
import processing.core.PGraphics;

public class View extends Rectangle {
	private int color; // color of border to be drawn in game
	private boolean removed; // if this is true, then if you're a page using this as your
	// selection area you should be removed

	public View(PApplet p, int x, int y, int rWidth, int rHeight) {
		super(x, y, rWidth, rHeight);
		removed = false;
		color = p.color(255,0,0); // TODO: implement random color
	}

	public void draw(PGraphics graphics) {
		graphics.noFill();
		graphics.stroke(color);
		graphics.strokeWeight(4);
		graphics.rect(getX(), getY(), getWidth(), getHeight());
		graphics.noStroke();
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
