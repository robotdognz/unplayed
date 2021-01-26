package objects;

import game.Game;
import handlers.EventHandler;
import handlers.TextureCache;
import processing.core.PGraphics;
import shiffman.box2d.Box2DProcessing;

import static processing.core.PConstants.*;

import org.jbox2d.dynamics.Body;

public abstract class Event extends Rectangle {
	private boolean hasTexture;
	protected EventHandler eventTexture;
	private String name;
	public boolean visible;

	// box2d
	Box2DProcessing box2d;
	Body staticBody;

	public Event(Box2DProcessing box2d, TextureCache texture, String name, boolean visible, float x, float y,
			float width, float height) {
		super(x, y, width, height);
		this.box2d = box2d;
		this.name = name;
		this.visible = visible;

		if (name != null && texture != null && texture.getEventMap().containsKey(name)) {
			this.eventTexture = texture.getEventMap().get(name);
			hasTexture = true;
		} else {
			hasTexture = false;
		}
	}

	public void create() {
		if (box2d != null) {

		}

	}

	public void destroy() {
		if (box2d != null && staticBody != null) {
			box2d.destroyBody(staticBody);
		}
	}

	public String getType() {
		return "";
	}

	public void activate(Game g) {
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.imageMode(CENTER);
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			// graphics.rotate(PApplet.radians(angle)); // angle of the tile
			// graphics.scale(flipX, flipY); // flipping the tile
			graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
			graphics.popMatrix();
		} else {
			// display missing texture texture
		}
	}

	@Override
	public String getName() {
		return name;
	}
}
