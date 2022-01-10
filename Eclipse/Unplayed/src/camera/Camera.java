package camera;

import processing.core.PVector;

public abstract class Camera {
	static protected boolean game = true; // is the game class allowed to make changes to the camera

	// variables for camera
	static protected float scale;
	static protected float subScale = 1; // defaults to 1
	static protected PVector center;

	public static boolean getGame() {
		return game;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float newScale) {
		scale = newScale;
	}

	public float getSubScale() {
		return subScale;
	}

	public void setSubScale(float newSubScale) {
		subScale = newSubScale;
	}

	public PVector getCenter() {
		return center;
	}

	public void setCenter(PVector newCenter) {
		if (center == null) {
			center = new PVector(newCenter.x, newCenter.y);
		} else {
			center.x = newCenter.x;
			center.y = newCenter.y;
		}
	}

}
