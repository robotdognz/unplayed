package objects.events;

import game.Game;
import objects.Event;
import processing.core.PGraphics;
import static processing.core.PConstants.*;

public class CameraCollider extends Event {
	private CameraChange camera;

	public CameraCollider(CameraChange camera, float x, float y, float width, float height) {
		super(null, "CameraCollider", false, x, y, width, height);
		this.camera = camera;
	}

	public CameraChange getCamera() {
		return camera;
	}

	@Override
	public void activate(Game g) {
		camera.activate(g);
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		graphics.rectMode(CORNER);
		graphics.noStroke();
		graphics.fill(255, 0, 0, 100); // TODO: make this orange
		graphics.rect(getX(), getY(), getWidth(), getHeight());
	}
}
