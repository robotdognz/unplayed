package objects;

import game.Game;
import handlers.EventHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PGraphics;
import static processing.core.PConstants.*;

public abstract class Event extends Editable {
	private boolean hasTexture;
	private EventHandler eventTexture;
	private String name;
	public boolean visible;
	
//	private float flipX;
//	private float flipY;
//	private float angle;

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
		
//		flipX = 1;
//		flipY = 1;
//		angle = 0;
	}

	public String getType() {
		return "";
	}

	public void activate(Game g) {
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
//			graphics.imageMode(CORNER);
//			graphics.image(eventTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
			
			graphics.imageMode(CENTER);
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			graphics.rotate(PApplet.radians(angle)); // angle of the tile
			graphics.scale(flipX, flipY); // flipping the tile
			graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
			graphics.popMatrix();
		} else {
			// display missing texture texture
		}
	}

	public String getName() {
		return name;
	}
}
