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

    public PVector getRectangleCenter() {
        return new PVector(topLeft.x + width / 2, topLeft.y + height / 2);
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

    public void setTopLeft(float x, float y) {
        this.topLeft.x = x;
        this.topLeft.y = y;
        this.bottomRight.x = topLeft.x + width;
        this.bottomRight.y = topLeft.y + height;
    }

    public void adjustTopLeft(PVector topLeft) {
        this.topLeft.x = topLeft.x;
        this.topLeft.y = topLeft.y;
        this.width = this.bottomRight.x - this.topLeft.x;
        this.height = this.bottomRight.y - this.topLeft.y;
    }

    public void setBottomRight(PVector bottomRight) {
        this.bottomRight.x = bottomRight.x;
        this.bottomRight.y = bottomRight.y;
        this.width = this.bottomRight.x - this.topLeft.x;
        this.height = this.bottomRight.y - this.topLeft.y;
    }

    public void setBottomRight(float x, float y) {
        this.bottomRight.x = x;
        this.bottomRight.y = y;
        this.width = this.bottomRight.x - this.topLeft.x;
        this.height = this.bottomRight.y - this.topLeft.y;
    }

    public void setCorners(PVector topLeft, PVector bottomRight) {
        this.topLeft.x = topLeft.x;
        this.topLeft.y = topLeft.y;
        this.bottomRight.x = bottomRight.x;
        this.bottomRight.y = bottomRight.y;
        this.width = this.bottomRight.x - this.topLeft.x;
        this.height = this.bottomRight.y - this.topLeft.y;
    }

    public void setCorners(float topLeftX, float topLeftY, float bottomRightX, float bottomRightY) {
        this.topLeft.x = topLeftX;
        this.topLeft.y = topLeftY;
        this.bottomRight.x = bottomRightX;
        this.bottomRight.y = bottomRightY;
        this.width = this.bottomRight.x - this.topLeft.x;
        this.height = this.bottomRight.y - this.topLeft.y;
    }

    public void drawSelected(PGraphics g, float scale) {
        g.noFill();
        g.stroke(255, 0, 0); // selection color, red
        g.strokeWeight(getSelectionStrokeWeight(scale));
        g.rectMode(CORNER);
        g.rect(getX(), getY(), getWidth(), getHeight());
    }

    public int getSelectionStrokeWeight(float scale) {
        return (int) (scale * 0.5f);
    }

    public String getName() {
        return "Rectangle";
    }

    @Override
    public String toString() {
        return this.getName() + " " + (int) getX() + " " + (int) getY() + " " + (int) getWidth() + " "
                + (int) getHeight();
    }

    public Rectangle copy() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public boolean sameDimensions(Rectangle other) {
        if (getX() != other.getX()) {
            return false;
        }
        if (getY() != other.getY()) {
            return false;
        }
        if (getWidth() != other.getWidth()) {
            return false;
        }
        if (getHeight() != other.getHeight()) {
            return false;
        }
        return true;
    }

    /**
     * Expands this rectangle to encompass the area of a provided rectangle
     *
     * @param other the provided rectangle
     */
    public void expandToFit(Rectangle other) {
		float newTopLeftX = Math.min(getTopLeft().x, other.getTopLeft().x);
		float newTopLeftY = Math.min(getTopLeft().y, other.getTopLeft().y);
		float newBottomRightX = Math.max(getBottomRight().x, other.getBottomRight().x);
		float newBottomRightY = Math.max(getBottomRight().x, other.getBottomRight().x);
        setCorners(newTopLeftX, newTopLeftY, newBottomRightX, newBottomRightY);
    }

//	public void setPosition(PVector newPosition) {
//		setX(newPosition.x);
//		setY(newPosition.y);
//	}
}
