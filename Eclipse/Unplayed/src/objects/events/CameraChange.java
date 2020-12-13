package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import processing.core.PGraphics;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class CameraChange extends Event {
//	private PVector cameraTopLeft;
//	private PVector cameraBottomRight;
	private Rectangle camera;

//	private int newScale;
//	private PVector newCent;
	private float cameraZoom;
	private float edgeZoom;
	// private String type; //Strings: "Static", "Full", "Horizontal", "Vertical"

	public CameraChange(TextureCache texture, String name, int x, int y) {
		super(texture, name, false, x, y, 100, 100);
		// considering separating edgeZoom into in speed and out speed

		// TODO: figure out what the default values should be
//		PVector cameraTopLeft = new PVector(-700, -200);
//		PVector cameraBottomRight = new PVector(700, 1500);
//		this.camera = new Rectangle(cameraTopLeft.x, cameraTopLeft.y, cameraBottomRight.x - cameraTopLeft.x,
//				cameraBottomRight.y - cameraTopLeft.y);
		this.camera = new Rectangle(-700, -200, 1400, 1700);
		this.cameraZoom = 2;
		this.edgeZoom = 2;

//		int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
//		int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
//		this.newCent = new PVector(centerX, centerY);
//		this.newScale = (int) Math.abs(cameraBottomRight.x - cameraTopLeft.x);

//		int centerX = (int) ((camera.getBottomRight().x - camera.getTopLeft().x) / 2 + camera.getTopLeft().x);
//		int centerY = (int) ((camera.getTopLeft().y - camera.getBottomRight().y) / 2 + camera.getBottomRight().y);

		// this.type = type;
	}

	public PVector getCameraTopLeft() {
//		return cameraTopLeft;
		return camera.getTopLeft();
	}

	public void setCameraTopLeft(PVector cameraTopLeft) {
//		this.cameraTopLeft = cameraTopLeft;
		camera.setTopLeft(cameraTopLeft);
	}

	public PVector getCameraBottomRight() {
//		return cameraBottomRight;
		return camera.getBottomRight();
	}

	public void setCameraBottomRight(PVector cameraBottomRight) {
//		this.cameraBottomRight = cameraBottomRight;
		camera.setBottomRight(cameraBottomRight);
	}

	public float getCameraZoom() {
		return cameraZoom;
	}

	public void setCameraZoom(float cameraZoom) {
		this.cameraZoom = cameraZoom;
	}

	public float getEdgeZoom() {
		return edgeZoom;
	}

	public void setEdgeZoom(float edgeZoom) {
		this.edgeZoom = edgeZoom;
	}

	// public String getType() {
	// return type;
	// }

	private PVector getCameraCentre() {
		int centerX = (int) ((camera.getBottomRight().x - camera.getTopLeft().x) / 2 + camera.getTopLeft().x);
		int centerY = (int) ((camera.getTopLeft().y - camera.getBottomRight().y) / 2 + camera.getBottomRight().y);
		return new PVector(centerX, centerY);
	}

	public void activate(Game g) {
		super.activate(g);
		if (g.camera.getGame()) {
			// change centre
			PVector center = getCameraCentre();
			if (g.newCenter != center) {
				g.newCenter = new PVector(center.x, center.y);
			}
			// change scale
			if (g.newScale != this.camera.getWidth()) {
				g.newScale = this.camera.getWidth();
			}
			g.zoomSpeed = cameraZoom;
			g.boarderZoomSpeed = edgeZoom;
			g.newTopEdge = (int) camera.getTopLeft().y;
			g.newBottomEdge = (int) camera.getBottomRight().y;
			g.newLeftEdge = (int) camera.getTopLeft().x;
			g.newRightEdge = (int) camera.getBottomRight().x;
		}
	}

	public void drawCameraArea(PGraphics g) {
		g.noStroke();
		g.rectMode(CORNERS);
		g.fill(255, 0, 0, 100);
		g.rect(camera.getTopLeft().x, camera.getTopLeft().y, camera.getBottomRight().x, camera.getBottomRight().y);
	}

}
