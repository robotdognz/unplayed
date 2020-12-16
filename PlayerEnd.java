package objects.events;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;

public class PlayerEnd extends Event {
	private boolean levelEnd;
	private Rectangle newPlayer;

	public PlayerEnd(TextureCache texture, String name, float x, float y) {
		super(texture, name, false, x, y, 100, 100);
		levelEnd = true;
		newPlayer = new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	public Rectangle getNewPlayer() {
		return newPlayer;
	}

	@Override
	public void activate(Game g) {
		Player player = g.player;
		if(!player.isStill()) {
			return;
		}
		if (player.getTopLeft().x > getBottomRight().x - 1) {
			return;
		}
		if (player.getBottomRight().x < getTopLeft().x + 1) {
			return;
		}
		if (player.getTopLeft().y > getBottomRight().y - 1) {
			return;
		}
		if (player.getBottomRight().y < getTopLeft().y + 1) {
			return;
		}
		
		// the player is perfectly in the slot
		if (levelEnd) { // if this is the end of the level
			g.endGame();
		} else { // if this is just part of the puzzle
			// TODO: spawn a new player
		}
	}

}
