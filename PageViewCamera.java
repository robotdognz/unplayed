package camera;

import static processing.core.PConstants.CORNERS;

import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class PageViewCamera {

	static private PApplet p;

	static private Rectangle pageArea; // always represents the current page area
	static private float sideAreaPadding; // amount to pad the sides of the camera around page area
	static private float bottomAreaPadding; // amount to pad the bottom of the camera around page area

	static private Rectangle cameraArea;
	static private Rectangle newCameraArea;

	static private PVector center;
	static private PVector newCenter;

	static private float scale;
	static private float newScale;

	static private float subScale = 1; // defaults to 1
	static private float newSubScale = 1; // defaults to 1

	static private float zoomSpeed = 0.05f; // lower is faster

	public PageViewCamera(PApplet papplet) {
		p = papplet;

		sideAreaPadding = 100;
		bottomAreaPadding = 300;

		// setup temp initial values
		pageArea = new Rectangle(0, 0, 0, 0); // (-400, -400, 900, 1000);

		cameraArea = new Rectangle(0, 0, 0, 0);
		newCameraArea = cameraArea.copy();

		center = new PVector(0, 0);
		newCenter = center.copy();

		scale = 1;
		newScale = scale;

		subScale = 1;
		newSubScale = subScale;

	}

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
		return pageArea.getTopLeft();
	}

	public PVector getBottomRight() {
		return pageArea.getBottomRight();
	}

	public boolean step(float deltaTime) {
		boolean temp = true;
		if (!cameraArea.sameDimensions(newCameraArea)) { // camera is changing
			// if there might be a difference in tall screen scale
			if ((newCameraArea.getBottomRight().y - newCameraArea.getTopLeft().y)
					/ (newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x) > (float) p.height
							/ (float) p.width) {

				newSubScale = ((float) p.height
						/ ((float) p.width / (float) (newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x)))
						/ (newCameraArea.getBottomRight().y - newCameraArea.getTopLeft().y);
			} else {
				newSubScale = 1;
			}
		}
		// vertical scale
		if (subScale != newSubScale) {
			subScale = PApplet.lerp(subScale, newSubScale, (float) (1 - Math.pow(zoomSpeed, deltaTime)));
		}
		// main scale
		if (scale != newScale) {
			scale = PApplet.lerp(scale, newScale, (float) (1 - Math.pow(zoomSpeed, deltaTime)));
		}
		// translate
		if (center != newCenter) {
			center = PVector.lerp(center, newCenter, (float) (1 - Math.pow(zoomSpeed, deltaTime)));
		}
		// black border movement
		if (!cameraArea.sameDimensions(newCameraArea)) {
			float topLeftX = PApplet.lerp(cameraArea.getTopLeft().x, newCameraArea.getTopLeft().x,
					(float) (1 - Math.pow(zoomSpeed, deltaTime)));
			float topLeftY = PApplet.lerp(cameraArea.getTopLeft().y, newCameraArea.getTopLeft().y,
					(float) (1 - Math.pow(zoomSpeed, deltaTime)));
			float bottomRightX = PApplet.lerp(cameraArea.getBottomRight().x, newCameraArea.getBottomRight().x,
					(float) (1 - Math.pow(zoomSpeed, deltaTime)));
			float bottomRightY = PApplet.lerp(cameraArea.getBottomRight().y, newCameraArea.getBottomRight().y,
					(float) (1 - Math.pow(zoomSpeed, deltaTime)));
			cameraArea.setCorners(topLeftX, topLeftY, bottomRightX, bottomRightY);
		}
		
		if (Math.abs(center.x - newCenter.x) < 1 && Math.abs(center.y - newCenter.y) < 1) { //0.5
			// this only returns false when the numbers are very similar to each other
			// which means that the camera isn't moving
			temp = false;
		}

		return temp;
	}

	public void draw() {
		// draw page area
		p.noFill();
		p.stroke(255, 0, 0);
		p.strokeWeight(3);
		p.rectMode(CORNERS);
		p.rect(pageArea.getTopLeft().x, pageArea.getTopLeft().y, pageArea.getBottomRight().x,
				pageArea.getBottomRight().y);

		// draw new camera area
		p.noFill();
		p.stroke(0, 255, 0);
		p.strokeWeight(3);
		p.rectMode(CORNERS);
		p.rect(newCameraArea.getTopLeft().x, newCameraArea.getTopLeft().y, newCameraArea.getBottomRight().x,
				newCameraArea.getBottomRight().y);

		// draw camera area
		p.noFill();
		p.stroke(0, 0, 255);
		p.strokeWeight(3);
		p.rectMode(CORNERS);
		p.rect(cameraArea.getTopLeft().x, cameraArea.getTopLeft().y, cameraArea.getBottomRight().x,
				cameraArea.getBottomRight().y);

	}

	public void update(float minX, float minY, float maxX, float maxY) {
		// update page area boundary
		pageArea.setCorners(minX, minY, maxX, maxY);

		// calculate center
		updateNewCamera();
		updateNewCenter();
		updateNewScale();
	}
	
	public void updateMenu(float minX, float minY, float maxX, float maxY) {
		// update page area boundary
		pageArea.setCorners(minX, minY, maxX, maxY);

		// calculate center
		updateNewCameraMenu();
		updateNewCenter();
		updateNewScale();
	}

	public void initCamera(float minX, float minY, float maxX, float maxY) {
		// update page area boundary
		pageArea.setCorners(minX, minY, maxX, maxY);

		// set camera area, doesn't use bottom area padding, assumes focusing on a menu
		cameraArea.setCorners(pageArea.getTopLeft().x - sideAreaPadding, pageArea.getTopLeft().y - sideAreaPadding,
				pageArea.getBottomRight().x + sideAreaPadding, pageArea.getBottomRight().y + sideAreaPadding);
		newCameraArea = cameraArea.copy();
		// set center
		int centerX = (int) ((newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x) / 2
				+ newCameraArea.getTopLeft().x);
		int centerY = (int) ((newCameraArea.getTopLeft().y - newCameraArea.getBottomRight().y) / 2
				+ newCameraArea.getBottomRight().y);
		center.x = centerX;
		center.y = centerY;
		newCenter = center.copy();
		// set scale
		scale = (int) Math.abs(newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x);
		newScale = scale;
	}

	private static void updateNewCamera() {
		newCameraArea.setCorners(pageArea.getTopLeft().x - sideAreaPadding, pageArea.getTopLeft().y - sideAreaPadding,
				pageArea.getBottomRight().x + sideAreaPadding, pageArea.getBottomRight().y + bottomAreaPadding);
	}
	
	private static void updateNewCameraMenu() {
		newCameraArea.setCorners(pageArea.getTopLeft().x - sideAreaPadding, pageArea.getTopLeft().y - sideAreaPadding,
				pageArea.getBottomRight().x + sideAreaPadding, pageArea.getBottomRight().y + sideAreaPadding);
	}

	private static void updateNewCenter() {
		int centerX = (int) ((newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x) / 2
				+ newCameraArea.getTopLeft().x);
		int centerY = (int) ((newCameraArea.getTopLeft().y - newCameraArea.getBottomRight().y) / 2
				+ newCameraArea.getBottomRight().y);
		newCenter.x = centerX;
		newCenter.y = centerY;
	}

	private static void updateNewScale() {
		newScale = (int) Math.abs(newCameraArea.getBottomRight().x - newCameraArea.getTopLeft().x);
	}

	public Rectangle getCameraArea() {
		return cameraArea;
	}

	public float getSideAreaPadding() {
		return sideAreaPadding;
	}

	public float getBottomAreaPadding() {
		return bottomAreaPadding;
	}

	static public PVector screenToLevel(float screenX, float screenY) {
		PVector output = new PVector();
		output.x = ((screenX - p.width / 2) / ((float) p.width / scale) / subScale) + center.x;
		output.y = ((screenY - p.height / 2) / ((float) p.width / scale) / subScale) + center.y;
		return output;
	}
}
