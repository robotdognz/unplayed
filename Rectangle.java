package objects;

import processing.core.PGraphics;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class Rectangle {
	private float width, height;
	private PVector topLeft, bottomRight;

	public Rectangle(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		this.topLeft = new PVector(x, y);
		this.bottomRight = new PVector(x + width, y + height);
	}
	
	// ------------getters
	public float getX() {
		return topLeft.x;
	}

	public float getY() {
		return topLeft.y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public PVector getTopLeft() {
		return topLeft;
	}

	public PVector getBottomRight() {
		return bottomRight;
	}

	// ------------setters
	public void setX(float x) {
		this.topLeft.x = x;
		this.bottomRight.x = x + width;
	}

	public void setY(float y) {
		this.topLeft.y = y;
		this.bottomRight.y = y + height;
	}
	
	public void setWidth(float rWidth) {
		this.width = rWidth;
		this.bottomRight.x = this.topLeft.x + rWidth;
	}

	public void setHeight(float rHeight) {
		this.height = rHeight;
		this.bottomRight.y = this.topLeft.y + rHeight;
	}

	public void setTopLeft(PVector topLeft) {
		this.topLeft.x = topLeft.x;
		this.topLeft.y = topLeft.y;
		this.bottomRight.x = topLeft.x + width;
		this.bottomRight.y = topLeft.y + height;
	}
	
	public void setBottomRight(PVector bottomRight) {
		this.bottomRight.x = bottomRight.x;
		this.bottomRight.y = bottomRight.y;
		this.width = this.bottomRight.x-this.topLeft.x;
		this.height = this.bottomRight.y-this.topLeft.y;
	}

	public void drawSelected(PGraphics g) {
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.rectMode(CORNER);
		g.rect(getX(), getY(), getWidth(), getHeight());
	}

	public String getName() {
		return "Rectangle";
	}

	public String toString() {
		return this.getName() + " " + (int) getX() + " " + (int) getY() + " " + (int) getWidth() + " "
				+ (int) getHeight();
	}

//	public void setPosition(PVector newPosition) {
//		setX(newPosition.x);
//		setY(newPosition.y);
//	}
}