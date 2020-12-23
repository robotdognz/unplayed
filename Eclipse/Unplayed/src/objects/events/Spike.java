package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class Spike extends Event{
	public Spike(TextureCache texture, String name, int x, int y) {
		super(texture, name, true, x, y, 100, 100);
	}

	@Override
	public void activate(Game g) {
		super.activate(g);
		g.restart(); // TODO: this needs a custom method in game
	}

}
