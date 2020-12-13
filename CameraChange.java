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

	// TODO: the constructor doesn't need all of these parameters if they are being
	// set later, remove them and initialize them with default values
	public CameraChange(TextureCache texture, String name, int x, int y, int eventW, int eventH, PVector cameraTopLeft,
			PVector cameraBottomRight, float cameraZoom, float edgeZoom) {
		super(texture, name, false, x, y, eventW, eventH);
		// considering separating edgeZoom into in speed and out speed
		int centerX = (int) ((cameraBottomRight.x - cameraTopLeft.x) / 2 + cameraTopLeft.x);
		int centerY = (int) ((cameraTopLeft.y - cameraBottomRight.y) / 2 + cameraBottomRight.y);
		this.newCent = new PVector(centerX, centerY);
		this.newScale = (int) Math.abs(cameraBottomRight.x - cameraTopLeft.x);
		this.cameraTopLeft = cameraTopLeft;
		this.cameraBottomRight = cameraBottomRight;
		this.cameraZoom = cameraZoom;
		this.edgeZoom = edgeZoom;
		// this.type = type;
	}

	public PVector getCameraTopLeft() {
		return cameraTopLeft;
	}

	public PVector getCameraBottomRight() {
		return cameraBottomRight;
	}

	public float getCameraZoom() {
		return cameraZoom;
	}

	public float getEdgeZoom() {
		return edgeZoom;
	}

	// public String getType() {
	// return type;
	// }

	public void activate(Game g) {
		super.activate(g);

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

	public void drawSelected(PGraphics g) {
		super.drawSelected(g);
		// TODO: the camera area needs to be drawn in the page view
		g.noStroke();
		g.rectMode(CORNER);
		g.fill(255, 0, 0, 100);
		g.rect(cameraTopLeft.x, cameraTopLeft.y, cameraBottomRight.x - cameraTopLeft.x,
				cameraBottomRight.y - cameraTopLeft.y);
	}
}
