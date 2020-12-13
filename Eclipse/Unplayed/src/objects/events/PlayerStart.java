package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class PlayerStart extends Event {
	//private Game game;

	public PlayerStart(TextureCache texture, String name, float x, float y, Game game) {
		super(texture, name, false, x, y, 100, 100);
		//this.game = game;
		// TODO set player start position to this position
		game.setPlayerStart(x, y);
		game.createPlayer();
	}

}
