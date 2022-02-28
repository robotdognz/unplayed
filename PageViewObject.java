package objects;

import static processing.core.PConstants.CENTER;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class PageViewObject extends Editable {
	protected PApplet p;
	protected PVector position; // center of the background in page view

	// Page corners relative to center, used to check if the page is on screen
	PVector topLeft;
	PVector topRight;
	PVector bottomLeft;
	PVector bottomRight;

	public PageViewObject(PApplet p, PVector position, float width, float height) {
		super(position.x, position.y, width, height);
		this.p = p;

		setPosition(position);
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector pos) {
		if (position == null) {
			this.position = pos;
		} else {
			this.position.x = pos.x;
			this.position.y = pos.y;
		}
		updateCorners();
	}

	public void addPosition(float x, float y) {
		this.position.x += x;
		this.position.y += y;
		updateCorners();
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		updateCorners();
	}

	@Override
	public void addAngle(float angle) {
		super.addAngle(angle);
		updateCorners();
	}

	public void setSize(float size) {
		this.size = size;
		updateCorners();
	}

	public void addSize(float size) {
		if (this.size + size > 0.5) {
			this.size += size;
		} else {
			this.size = 0.5f;
		}
		updateCorners();
	}

	public float getSize() {
		return size;
	}

	@Override
	public String getName() {
		return "PageView Object";
	}

	public void draw(float scale) {

	}

	@Override
	public void drawSelected(PGraphics g) {
		g.pushMatrix();
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.translate(position.x, position.y);
		g.scale(size); // size the page will appear in the page view
		g.rotate(PApplet.radians(angle)); // angle of the page
		g.rectMode(CENTER);
		g.rect(0, 0, getWidth(), getHeight());
		g.popMatrix();
	}
	
	public void drawSelectedAsChild(PGraphics g) {
		g.pushMatrix();
		g.noFill();
		g.stroke(0, 255, 0); // selection color
		g.strokeWeight(2);
		g.translate(position.x, position.y);
		g.scale(size); // size the page will appear in the page view
		g.rotate(PApplet.radians(angle)); // angle of the page
		g.rectMode(CENTER);
		g.rect(0, 0, getWidth(), getHeight());
		g.popMatrix();
	}

	// --------------update the corner PVectors---------------
	protected void updateCorners() {
		if (topLeft == null) {
			// Initialize
			topLeft = new PVector();
			topRight = new PVector();
			bottomLeft = new PVector();
			bottomRight = new PVector();
		}
		// set values
		topLeft.x = 0 - (getWidth() / 2) * size;
		topLeft.y = 0 - (getHeight() / 2) * size;
		topRight.x = 0 + (getWidth() / 2) * size;
		topRight.y = 0 - (getHeight() / 2) * size;
		bottomLeft.x = 0 - (getWidth() / 2) * size;
		bottomLeft.y = 0 + (getHeight() / 2) * size;
		bottomRight.x = 0 + (getWidth() / 2) * size;
		bottomRight.y = 0 + (getHeight() / 2) * size;
		// rotate
		topLeft.rotate(PApplet.radians(angle));
		topRight.rotate(PApplet.radians(angle));
		bottomLeft.rotate(PApplet.radians(angle));
		bottomRight.rotate(PApplet.radians(angle));
		// translate
		topLeft.x += position.x;
		topLeft.y += position.y;
		topRight.x += position.x;
		topRight.y += position.y;
		bottomLeft.x += position.x;
		bottomLeft.y += position.y;
		bottomRight.x += position.x;
		bottomRight.y += position.y;
	}

	// ------------is a point inside the background-------------
	public boolean isInside(float x, float y) {
		PVector point = new PVector(x, y);
		point.x -= position.x;
		point.y -= position.y;
		point.rotate(PApplet.radians(-angle));

		if (-(getWidth() / 2) * size > point.x) {
			return false;
		}
		if ((getWidth() / 2) * size < point.x) {
			return false;
		}
		if (-(getHeight() / 2) * size > point.y) {
			return false;
		}
		if ((getHeight() / 2) * size < point.y) {
			return false;
		}

		return true;
	}

	// get the bounding box edges for the page
	public float getLeftmostPoint() {
		return Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
	}

	public float getRightmostPoint() {
		return Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
	}

	public float getTopmostPoint() {
		return Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
	}

	public float getBottommostPoint() {
		return Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));
	}

	// ----------is this object off camera------------

	public boolean leftOf(float x) {
		if (topLeft.x > x) {
			return false;
		}
		if (topRight.x > x) {
			return false;
		}
		if (bottomLeft.x > x) {
			return false;
		}
		if (bottomRight.x > x) {
			return false;
		}
		return true;
	}

	public boolean rightOf(float x) {
		if (topLeft.x < x) {
			return false;
		}
		if (topRight.x < x) {
			return false;
		}
		if (bottomLeft.x < x) {
			return false;
		}
		if (bottomRight.x < x) {
			return false;
		}
		return true;
	}

	public boolean above(float y) {
		if (topLeft.y > y) {
			return false;
		}
		if (topRight.y > y) {
			return false;
		}
		if (bottomLeft.y > y) {
			return false;
		}
		if (bottomRight.y > y) {
			return false;
		}
		return true;
	}

	public boolean below(float y) {
		if (topLeft.y < y) {
			return false;
		}
		if (topRight.y < y) {
			return false;
		}
		if (bottomLeft.y < y) {
			return false;
		}
		if (bottomRight.y < y) {
			return false;
		}
		return true;
	}

}
