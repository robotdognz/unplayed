package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import shiffman.box2d.Box2DProcessing;

public class PlayerDeath extends Event {
	public PlayerDeath(Box2DProcessing box2d, TextureCache texture, String name, int x, int y) {
		super(box2d, texture, name, false, x, y, 100, 100);
	}

	@Override
	public void activate(Game g) {
		super.activate(g);
		g.restart(); // TODO: this needs a custom method in game
	}
}
