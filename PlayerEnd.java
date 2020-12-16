package objects.events;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;

public class PlayerEnd extends Event {
	private boolean levelEnd;
	private Rectangle newPlayer;

	public PlayerEnd(TextureCache texture, String name, float x, float y) {
		super(texture, name, false, x, y, 100, 100);
		levelEnd = true;
		newPlayer = new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	public Rectangle getNewPlayer() {
		return newPlayer;
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
		if(levelEnd) { //if this is the end of the level
			g.endGame();
		}else { //if this is just part of the puzzle
			//TODO: spawn a new player
		}
	}

}
