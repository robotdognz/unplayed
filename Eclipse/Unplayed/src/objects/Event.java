package objects;

import game.Game;
import handlers.EventHandler;
import handlers.TextureCache;
import processing.core.PGraphics;

public abstract class Event extends Rectangle {
	private boolean hasTexture;
	private EventHandler eventTexture;
	private String name;
	public boolean visible;

	public Event(TextureCache texture, String name, boolean visible, float x, float y, float rWidth, float rHeight) {
		super(x, y, rWidth, rHeight);
		this.name = name;
		this.visible = visible;

		if (name != null && texture != null && texture.getEventMap().containsKey(name)) {
			this.eventTexture = texture.getEventMap().get(name);
			// setWidth(eventTexture.getWidth());
			// setHeight(eventTexture.getHeight());
			hasTexture = true;
		} else {
			hasTexture = false;
		}
	}

	public String getType() {
		return "";
	}

	public void activate(Game g) {
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.image(eventTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
		} else {
			// display missing texture texture
		}
	}

	public String getName() {
		return name;
	}
}
