package misc;

import camera.Camera;
import processing.core.*;

// TODO: this class can probably be improved by being made static, like DebugOutput

public class Converter {
	private PApplet p;
	private float currentScale;
	private float currentSubScale;
	private PVector currentCenter;

	private PVector lastCalc;

	public Converter(PApplet p) {
		this.p = p;
		this.lastCalc = new PVector(0, 0);
	}

	public PVector screenToLevel(float screenX, float screenY) {
		currentScale = Camera.getScale();
		currentSubScale = Camera.getSubScale();
		currentCenter = Camera.getCenter();
		lastCalc.x = ((screenX - p.width / 2) / ((float) p.width / currentScale) / currentSubScale) + currentCenter.x;
		lastCalc.y = ((screenY - p.height / 2) / ((float) p.width / currentScale) / currentSubScale) + currentCenter.y;
		return lastCalc.copy();
	}
	
	public PVector screenToLevel(float screenX, float screenY, float scale, float subScale, PVector center) {
		lastCalc.x = ((screenX - p.width / 2) / ((float) p.width / scale) / subScale) + center.x;
		lastCalc.y = ((screenY - p.height / 2) / ((float) p.width / scale) / subScale) + center.y;
		return lastCalc.copy();
	}

	public float screenToLevel(float distance) {
		currentScale = Camera.getScale();
		currentSubScale = Camera.getSubScale();
		float result = distance / ((float) p.width / currentScale) / currentSubScale;
		return result;
	}

	public float getScale() {
		currentScale = Camera.getScale();
		currentSubScale = Camera.getSubScale();
		return currentScale / currentSubScale / 100; // how many square is the width of the screen
	}

	public float getTotalFromScale(float scale) { // calculate total scale from potential scale
		currentSubScale = Camera.getSubScale();
		return scale / currentSubScale / 100;
	}

	public float getScaleFromTotal(float totalScale) {
		currentSubScale = Camera.getSubScale();
		return totalScale * currentSubScale * 100;
	}
	
	// public PVector levelToScreen(float levelX, levelY){

	// }
}
