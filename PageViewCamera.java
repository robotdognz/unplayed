package camera;

import processing.core.PVector;

public class PageViewCamera {
	static private float scale;
	static private float subScale = 1; // defaults to 1
	
	static private PVector center;
	static private PVector topLeft = new PVector(-400, -400);
	static private PVector bottomRight = new PVector(500, 600);

	public float getScale() {
		return scale;
	}

	public float getSubScale() {
		return subScale;
	}

	public PVector getCenter() {
		return center;
	}
	
	public PVector getTopLeft() {
		return topLeft;
	}
	
	public PVector getBottomRight() {
		return bottomRight;
	}

	public void step() {

	}

	public void update(float minX, float minY, float maxX, float maxY) {
		topLeft.x = minX;
		topLeft.y = minY;
		bottomRight.x = maxX;
		bottomRight.y = maxY;
	}

}
