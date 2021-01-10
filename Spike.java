package objects.events;

import static processing.core.PConstants.CENTER;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Spike extends Event {
	private float angle;
	private Rectangle bounds;

	public Spike(TextureCache texture, String name, int x, int y) {
		super(texture, name, true, x, y, 100, 100);
		angle = 0;
		createBounds();
	}

	private void createBounds() {
		if (angle == 0) {
			bounds = new Rectangle(getX() + getWidth() / 3, getY() + getHeight() / 2, getWidth() / 3, getHeight() / 2);
		} else if (angle == 90) {
			bounds = new Rectangle(getX(), getY() + getHeight() / 3, getWidth() / 2, getHeight() / 3);
		} else if (angle == 180) {
			bounds = new Rectangle(getX() + getWidth() / 3, getY(), getWidth() / 3, getHeight() / 2);
		} else if (angle == 270) {
			bounds = new Rectangle(getX() + getWidth() / 2, getY() + getHeight() / 3, getWidth() / 2, getHeight() / 3);
		}
	}

	public void setAngle(float angle) {
		this.angle = angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
		createBounds();
	}

	public void addAngle(float angle) {
		this.angle += angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
		createBounds();
	}

	public float getAngle() {
		return angle;
	}

	@Override
	public void activate(Game g) {
		// check if player is inside bounds
		Player player = g.player;
		if (player.getTopLeft().x > bounds.getBottomRight().x - 1) {
			return;
		}
		if (player.getBottomRight().x < bounds.getTopLeft().x + 1) {
			return;
		}
		if (player.getTopLeft().y > bounds.getBottomRight().y - 1) {
			return;
		}
		if (player.getBottomRight().y < bounds.getTopLeft().y + 1) {
			return;
		}

		g.restart(); // TODO: this needs a custom method in Game
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		//super.draw(graphics, scale);

		// draw bounds
		graphics.pushMatrix();
		
		graphics.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		
		graphics.imageMode(CENTER);
		graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
		graphics.rotate(PApplet.radians(angle)); // angle of the tile
		graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
		

		
		graphics.popMatrix();
	}

}
