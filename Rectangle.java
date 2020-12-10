package objects;

import processing.core.PGraphics;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class Rectangle {
	private float rWidth, rHeight;
	private PVector topLeft, bottomRight;

	public Rectangle(float x, float y, float rWidth, float rHeight) {
		this.rWidth = rWidth;
		this.rHeight = rHeight;
		this.topLeft = new PVector(x, y);
		this.bottomRight = new PVector(x + rWidth, y + rHeight);
	}
	
	public void drawSelected(PGraphics g) {
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.rectMode(CORNER);
		g.rect(getX(), getY(), getWidth(), getHeight());
	}

	// getters
	public float getX() {
		return topLeft.x;
	}

	public float getY() {
		return topLeft.y;
	}

	public float getWidth() {
		return rWidth;
	}

	public float getHeight() {
		return rHeight;
	}

	public PVector getTopLeft() {
		return topLeft;
	}

	public PVector getBottomRight() {
		return bottomRight;
	}

	public String toString() {
		return this.getName() + " " + (int) getX() + " " + (int) getY() + " " + (int) getWidth() + " "
				+ (int) getHeight();
	}

	public String getName() {
		return "Rectangle";
	}

	// setters
	public void setX(float x) {
		this.topLeft.x = x;
		this.bottomRight.x = x + rWidth;
	}

	public void setY(float y) {
		this.topLeft.y = y;
		this.bottomRight.y = y + rHeight;
	}

	public void setWidth(float rWidth) {
		this.rWidth = rWidth;
		this.bottomRight.x = this.topLeft.x + rWidth;
	}

	public void setHeight(float rHeight) {
		this.rHeight = rHeight;
		this.bottomRight.y = this.topLeft.y + rHeight;
	}

	public void setPosition(PVector newPosition) {
		setX(newPosition.x);
		setY(newPosition.y);
	}
}