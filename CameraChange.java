package objects.events;

import game.Game;
import handlers.EventHandler;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PConstants.*;

public class CameraChange extends Event {
	private Rectangle camera;
	private float cameraZoom;
	private float edgeZoom;
	private int color;
	// private String type; //Strings: "Static", "Full", "Horizontal", "Vertical"
	// //should be an enum

	public CameraChange(Game game, PApplet p, TextureCache texture, String name, float x, float y) {
		super(game, texture, name, false, x, y, 100, 100);
		// I'm considering splitting both cameraZoom and edgeZoom into 'in speed' and
		// 'out speed'

		color = p.color(p.random(255), p.random(255), p.random(255));

		// set default values
		this.camera = new Rectangle(-300, -400, 700, 900);
		this.cameraZoom = 0.032f; // 2
		this.edgeZoom = 0.032f; // 2
		// this.type = type;
	}

	public Rectangle getCameraArea() {
		return camera;
	}

	public PVector getCameraTopLeft() {
		return camera.getTopLeft();
	}

	public void setCameraTopLeft(PVector cameraTopLeft) {
		camera.setTopLeft(cameraTopLeft);
	}

	public PVector getCameraBottomRight() {
		return camera.getBottomRight();
	}

	public void setCameraBottomRight(PVector cameraBottomRight) {
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

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	private PVector getCameraCentre() {
		int centerX = (int) ((camera.getBottomRight().x - camera.getTopLeft().x) / 2 + camera.getTopLeft().x);
		int centerY = (int) ((camera.getTopLeft().y - camera.getBottomRight().y) / 2 + camera.getBottomRight().y);
		return new PVector(centerX, centerY);
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		graphics.rectMode(CORNER);
		graphics.noStroke();
		graphics.fill(color, 100);
		graphics.rect(getX(), getY(), getWidth(), getHeight());
		super.draw(graphics, scale);
	}

	@Override
	public void activate() {
		super.activate();
		if (game.camera.getGame()) {
			// change center
			PVector center = getCameraCentre();
			if (game.newCenter != center) {
				game.newCenter = new PVector(center.x, center.y);
			}
			// change scale
			if (game.newScale != this.camera.getWidth()) {
				game.newScale = this.camera.getWidth();
			}
			game.zoomSpeed = cameraZoom;
			game.boarderZoomSpeed = edgeZoom;
			game.newCameraArea.setCorners(camera.getTopLeft().x, camera.getTopLeft().y, camera.getBottomRight().x,
					camera.getBottomRight().y);
		}
	}

	public EventHandler getTexture() {
		return eventTexture;
	}

	public void drawCameraArea(PGraphics g) {
		g.noStroke();
		g.rectMode(CORNERS);
		g.fill(255, 0, 0, 100);
		g.rect(camera.getTopLeft().x, camera.getTopLeft().y, camera.getBottomRight().x, camera.getBottomRight().y);
	}

}
