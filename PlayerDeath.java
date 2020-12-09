package objects;

import game.Game;
import handlers.TextureCache;

public class PlayerDeath extends Event {
	public PlayerDeath(TextureCache texture, String name, int x, int y) {
		super(texture, name, true, x, y, 100, 100);
	}

	public void activate(Game g) {
		super.activate(g);
		g.restart(); // TODO: this needs a custom method in game
	}
}
