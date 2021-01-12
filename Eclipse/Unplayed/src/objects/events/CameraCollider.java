package objects.events;

import game.Game;
import objects.Event;
import processing.core.PGraphics;
import static processing.core.PConstants.*;

public class CameraCollider extends Event {
	private CameraChange camera;

	public CameraCollider(CameraChange camera, float x, float y) {
		super(null, "CameraCollider", false, x, y, 100, 100);
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
		graphics.fill(camera.getColor(), 100);
		graphics.rect(getX(), getY(), getWidth(), getHeight());
	}
}
