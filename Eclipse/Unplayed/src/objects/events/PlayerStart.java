package objects.events;

import java.util.HashSet;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;

public class PlayerStart extends Event {

	public PlayerStart(TextureCache texture, String name, float x, float y, Game game) {
		super(texture, name, false, x, y, 100, 100);
		
		//set player start
		game.setPlayerStart(x, y);
		game.createPlayer();
		
		//remove all other player starts
		HashSet<Rectangle> returnSet = new HashSet<Rectangle>();
		game.world.getAll(returnSet);
		for(Rectangle r : returnSet) {
			if(r instanceof PlayerStart) {
				game.world.remove(r);
			}
		}
	}

}
