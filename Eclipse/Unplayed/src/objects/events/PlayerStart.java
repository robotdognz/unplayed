package objects.events;

import java.util.HashSet;

import game.Game;
import handlers.TextureCache;
import objects.Rectangle;

public class PlayerStart extends CameraChange {
	Game game;

	public PlayerStart(TextureCache texture, String name, float x, float y, Game game) {
		super(texture, name, x, y);
		this.game = game;
		
		//set player start
		game.setPlayerStart(this);
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
	
//	public void update() {
//		game.setPlayerStart(this);
//	}
	
	public void activate(Game g) {
		//do nothing
	}

}
