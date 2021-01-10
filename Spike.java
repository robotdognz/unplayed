package objects.events;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;

public class Spike extends Event {
	private float angle;
	private Rectangle bounds;

	public Spike(TextureCache texture, String name, int x, int y) {
		super(texture, name, true, x, y, 100, 100);
		angle = 0;
		bounds = new Rectangle(x+getWidth()/3, y+getHeight()/2, getWidth()/3, getHeight()/2);
	}

	public void setAngle(float angle) {
		this.angle = angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
	}

	public void addAngle(float angle) {
		this.angle += angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
	}

	public float getAngle() {
		return angle;
	}

	@Override
	public void activate(Game g) {
		//super.activate(g);
		
		//check if player is inside bounds
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
		
		g.restart(); // TODO: this needs a custom method in game
	}

}
