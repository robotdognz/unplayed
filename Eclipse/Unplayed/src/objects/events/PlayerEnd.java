package objects.events;

import static processing.core.PConstants.CORNERS;

import java.util.HashSet;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import processing.core.PGraphics;

public class PlayerEnd extends Event {
	private Game game;
	private boolean levelEnd;
	private Tile required;
	private Rectangle newPlayerArea;

	public PlayerEnd(TextureCache texture, String name, float x, float y, Game game) {
		super(texture, name, false, x, y, 100, 100);
		this.game = game;
		levelEnd = true;
		newPlayerArea = new Rectangle(getX() + getWidth(), getY() - getHeight(), getWidth(), getHeight());

		// get tile that is at the current position
		HashSet<Rectangle> returnSet = new HashSet<Rectangle>();
		game.world.retrieve(returnSet, this);
		for (Rectangle r : returnSet) {
			if (!(r instanceof Tile)) {
				continue;
			}
			if (r.getX() != getX()) {
				continue;
			}
			if (r.getY() != getY()) {
				continue;
			}
			this.required = (Tile) r;
			break;
		}

		if (required != null) {
			this.game.world.remove(required);
		}
	}

	public Tile getRequired() {
		return required;
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		if (required != null) {
			required.drawTransparent(graphics, scale);
		}
		super.draw(graphics, scale);
	}

	private void drawNewPlayerArea(PGraphics g) {
		g.noStroke();
		g.rectMode(CORNERS);
		g.fill(255, 0, 0, 100);
		g.rect(newPlayerArea.getTopLeft().x, newPlayerArea.getTopLeft().y, newPlayerArea.getBottomRight().x,
				newPlayerArea.getBottomRight().y);
	}

	@Override
	public void drawSelected(PGraphics g) {
		super.drawSelected(g);
		if (!levelEnd) {
			drawNewPlayerArea(g);
		}
	}

	public Rectangle getNewPlayerArea() {
		return newPlayerArea;
	}

	public void setNewPlayerArea(Rectangle newPlayerArea) {
		this.newPlayerArea = newPlayerArea;
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
			g.endPuzzle(newPlayerArea);
		}
	}

}
