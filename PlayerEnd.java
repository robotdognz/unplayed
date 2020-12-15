package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class PlayerEnd extends Event {

	public PlayerEnd(TextureCache texture, String name, float x, float y) {
		super(texture, name, false, x, y, 100, 100);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void activate(Game g) {
		//check if the player and this have the same x, y
	}

}
