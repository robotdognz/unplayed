package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;

public class PlayerEnd extends Event {

	public PlayerEnd(TextureCache texture, String name, float x, float y) {
		super(texture, name, false, x, y, 100, 100);
	}
	
	@Override
	public void activate(Game g) {
		if(getX() != g.player.getX()) {
			return;
		}
		if(getY() != g.player.getY()) {
			return;
		}
		if(getWidth() != g.player.getWidth()) {
			return;
		}
		if(getHeight() != g.player.getHeight()) {
			return;
		}
		//if the player is perfectly in the slot
		
		//TODO: do stuff
	}

}
