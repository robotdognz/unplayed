package objects.events;

import static processing.core.PConstants.CORNERS;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import processing.core.PGraphics;

public class PlayerEnd extends Event {
	private boolean levelEnd;
	private Rectangle newPlayer;

	public PlayerEnd(TextureCache texture, String name, float x, float y) {
		super(texture, name, false, x, y, 100, 100);
		levelEnd = false; //true
		newPlayer = new Rectangle(getX() + getWidth(), getY() - getHeight(), getWidth(), getHeight());
	}

	private void drawNewPlayer(PGraphics g) {
		g.noStroke();
		g.rectMode(CORNERS);
		g.fill(255, 0, 0, 100);
		g.rect(newPlayer.getTopLeft().x, newPlayer.getTopLeft().y, newPlayer.getBottomRight().x,
				newPlayer.getBottomRight().y);
	}

	@Override
	public void drawSelected(PGraphics g) {
		super.drawSelected(g);
		if (!levelEnd) {
			drawNewPlayer(g);
		}
	}

	public Rectangle getNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(Rectangle newPlayer) {
		this.newPlayer = newPlayer;
	}

	public boolean getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(boolean levelEnd) {
		this.levelEnd = levelEnd;
	}

	@Override
	public void activate(Game g) {
		Player player = g.player;
		if (!player.isStill()) {
			return;
		}
		
		//TODO: this needs to be changed to player fully overlapping the event
//		if (player.getTopLeft().x > getBottomRight().x - 1) {
//			return;
//		}
//		if (player.getBottomRight().x < getTopLeft().x + 1) {
//			return;
//		}
//		if (player.getTopLeft().y > getBottomRight().y - 1) {
//			return;
//		}
//		if (player.getBottomRight().y < getTopLeft().y + 1) {
//			return;
//		}
		if (player.getTopLeft().x != getTopLeft().x) {
			return;
		}
		
		if (player.getTopLeft().y != getTopLeft().y) {
			return;
		}

		// the player is perfectly in the slot
		if (levelEnd) { // if this is the end of the level
			g.endGame();
		} else { // if this is just part of the puzzle
			// TODO: spawn a new player
			g.setPlayerCheckpoint(newPlayer.getTopLeft());
			g.endPuzzle();
		}
	}

}
