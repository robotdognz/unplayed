package camera;

import processing.core.PVector;

public abstract class Camera {
	static protected boolean game = true; // is the game class allowed to make changes to the camera

	// variables for camera
	static protected float scale = 1;
	static protected float subScale = 1; // defaults to 1
	static protected PVector center = new PVector(0,0);

	public static boolean getGame() {
		return game;
	}

	public static float getScale() {
		return scale;
	}

	public static void setScale(float newScale) {
		scale = newScale;
	}

	public static float getSubScale() {
		return subScale;
	}

	public static void setSubScale(float newSubScale) {
		subScale = newSubScale;
	}

	public static PVector getCenter() {
		return center;
	}

	public static void setCenter(PVector newCenter) {
		if (center == null) {
			center = new PVector(newCenter.x, newCenter.y);
		} else {
			center.x = newCenter.x;
			center.y = newCenter.y;
		}
	}

}
