package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import processing.core.PGraphics;
import processing.core.PVector;
import static processing.core.PConstants.*;

public class CameraChange extends Event {
	private PVector cameraTopLeft;
	private PVector cameraBottomRight;

	private int newScale;
	private PVector newCent;
	private float cameraZoom;
	private float edgeZoom;
	// private String type; //Strings: "Static", "Full", "Horizontal", "Vertical"

	public CameraChange(TextureCache texture, String name, int x, int y) {
		super(texture, name, false, x, y, 100, 100);
		// considering separating edgeZoom into in speed and out speed

		// TODO: figure out what the default values should be
		this.cameraTopLeft = new PVector(-700, -200);
		this.cameraBottomRight = new PVector(700, 1500);
		this.cameraZoom = 2;
		this.edgeZoom = 2;

		int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
		int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
		this.newCent = new PVector(centerX, centerY);
		this.newScale = (int) Math.abs(cameraBottomRight.x - cameraTopLeft.x);

		// this.type = type;
	}

	public PVector getCameraTopLeft() {
		return cameraTopLeft;
	}

	public void setCameraTopLeft(PVector cameraTopLeft) {
		this.cameraTopLeft = cameraTopLeft;
	}

	public PVector getCameraBottomRight() {
		return cameraBottomRight;
	}

	public void setCameraBottomRight(PVector cameraBottomRight) {
		this.cameraBottomRight = cameraBottomRight;
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

	public void activate(Game g) {
		super.activate(g);
		if (g.camera.getGame()) {
			// change center
			if (g.newCenter != this.newCent) {
				g.newCenter = new PVector(newCent.x, newCent.y);
			}
			// change scale
			if (g.newScale != this.newScale) {
				g.newScale = this.newScale;
			}
			g.zoomSpeed = cameraZoom;
			g.boarderZoomSpeed = edgeZoom;
			g.newTopEdge = (int) cameraTopLeft.y;
			g.newBottomEdge = (int) cameraBottomRight.y;
			g.newLeftEdge = (int) cameraTopLeft.x;
			g.newRightEdge = (int) cameraBottomRight.x;
		}
	}

	public void drawCameraArea(PGraphics g) {
		g.noStroke();
		g.rectMode(CORNER);
		g.fill(255, 0, 0, 100);
		g.rect(cameraTopLeft.x, cameraTopLeft.y, cameraBottomRight.x - cameraTopLeft.x,
				cameraBottomRight.y - cameraTopLeft.y);
	}

}
