package misc;

import camera.Camera;
import processing.core.*;

public class Converter {
	private PApplet p;
	private Camera cCamera;
	private float currentScale;
	private float currentSubScale;
	private PVector currentCenter;

	private PVector lastCalc;

	public Converter(PApplet p, Camera camera) {
		this.p = p;
		this.cCamera = camera;
		this.lastCalc = new PVector(0, 0);
	}

	public PVector screenToLevel(float screenX, float screenY) {
		currentScale = cCamera.getScale();
		currentSubScale = cCamera.getSubScale();
		currentCenter = cCamera.getCenter();
		lastCalc.x = ((screenX - p.width / 2) / ((float) p.width / currentScale) / currentSubScale) + currentCenter.x;
		lastCalc.y = ((screenY - p.height / 2) / ((float) p.width / currentScale) / currentSubScale) + currentCenter.y;
		return lastCalc;
	}

	public float screenToLevel(float distance) {
		currentScale = cCamera.getScale();
		currentSubScale = cCamera.getSubScale();
		float result = distance / ((float) p.width / currentScale) / currentSubScale;
		return result;
	}

	public float getScale() {
		currentScale = cCamera.getScale();
		currentSubScale = cCamera.getSubScale();
		return currentScale / currentSubScale / 100; // how many square is the width of the screen
	}

	public float getTotalFromScale(float scale) { // calculate total scale from potential scale
		currentSubScale = cCamera.getSubScale();
		return scale / currentSubScale / 100;
	}

	public float getScaleFromTotal(float totalScale) {
		currentSubScale = cCamera.getSubScale();
		return totalScale * currentSubScale * 100;
	}
	
	// public PVector levelToScreen(float levelX, levelY){

	// }
}
