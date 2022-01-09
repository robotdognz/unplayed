package camera;

import processing.core.PVector;

public class PageViewCamera {
	static protected float scale;
	static protected float subScale = 1; // defaults to 1
	static protected PVector center;
	
	public float getScale() {
		return scale;
	}
	
	public float getSubScale() {
		return subScale;
	}
	
	public PVector getCenter() {
		return center;
	}

}
