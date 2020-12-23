package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class PlayerDeath extends Event {
	public PlayerDeath(TextureCache texture, String name, int x, int y) {
		super(texture, name, false, x, y, 100, 100);
	}

	@Override
	public void activate(Game g) {
		super.activate(g);
		g.restart(); // TODO: this needs a custom method in game
	}
}
