package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class PlayerDeath extends Event {
	public PlayerDeath(Game game, TextureCache texture, String name, int x, int y) {
		super(game, texture, name, false, x, y, 100, 100);
	}

	@Override
	public boolean activate() {
		super.activate();
//		game.restart(); // TODO: this needs a custom method in game
		game.queueRestart();
		return true;
	}
}
