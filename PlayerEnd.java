package objects.events;

import static processing.core.PConstants.*;

import java.util.HashSet;

import org.jbox2d.common.Vec2;

import game.AppLogic;
import game.Game;
import game.player.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class PlayerEnd extends Event {
	private boolean levelEnd;
	private Tile required;
	private Rectangle newPlayerArea;
	private PVector center; // used for checking against player position

	private boolean alreadyUsed = false;

	public PlayerEnd(Game game, TextureCache texture, String name, float x, float y) {
		super(game, texture, name, false, x, y, 100, 100);

		this.levelEnd = false; // true;
		this.newPlayerArea = new Rectangle(getX() + getWidth(), getY() - getHeight(), getWidth(), getHeight());

		this.center = new PVector(getX() + getWidth() / 2, getY() + getHeight() / 2);

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
			game.world.remove(required);
		}
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		// used for the level view in editor

		if (required != null) {
			required.drawTransparent(graphics, scale);
		}

		if (!levelEnd) {
			arrow(graphics, getX() + getWidth() / 2, getY() + getHeight() / 2,
					newPlayerArea.getX() + newPlayerArea.getWidth() / 2,
					newPlayerArea.getY() + newPlayerArea.getHeight() / 2);
		}
		super.draw(graphics, scale);
	}

	public void drawPageView(PGraphics graphics, float scale) {
		// used for the page view

		graphics.pushMatrix();
		graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
//		TextureCache.drawPlayerEnd(graphics, getWidth(), getHeight());
		AppLogic.texture.getImageList().get(0).draw(graphics, 0, 0, getWidth(), getHeight(), scale);
		graphics.popMatrix();
	}

	private void arrow(PGraphics graphics, float x1, float y1, float x2, float y2) {
		graphics.stroke(0, 255, 0, 150);
		graphics.fill(0, 255, 0, 150);
		graphics.strokeWeight(4);
		graphics.line(x1, y1, x2, y2);
		graphics.pushMatrix();
		graphics.translate(x2, y2);
		float a = PApplet.atan2(x1 - x2, y2 - y1);
		graphics.rotate(a);
		graphics.triangle(0, 0, -15, -15, 15, -15);
		graphics.popMatrix();
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

	public Tile getRequired() {
		return required;
	}

	public void setRequired(Tile required) {
		this.required = required;
	}

	public boolean getLevelEnd() {
		return levelEnd;
	}

	public void setLevelEnd(boolean levelEnd) {
		this.levelEnd = levelEnd;
	}

	public void reset() {
		alreadyUsed = false;
	}

	@Override
	public void activate() {
		if (game.isPaused()) {
			return;
		}

		Player player = game.player;
		if (!player.isStill()) {
			return;
		}

		Vec2 playerPos = player.getCenter();
		if (Math.abs(playerPos.x - center.x) > 0.5f) {
			return;
		}
		if (Math.abs(playerPos.y - center.y) > 0.5f) {
			return;
		}
		if (required != null) {
			if (!player.getFile().equals(required.getFile())) {
				return;
			}

			int rotationMode = required.getRotationMode();

			if (rotationMode == 0) { // rotation matters
				float playerAngle = player.getAdjustedAngle();

				if (!(playerAngle == required.getAngle())) {
					return;
				}
			} else if (rotationMode == 1) { // only 180 degree rotation matters
				float playerAngle = player.getAdjustedAngle();

				if (!(playerAngle == required.getAngle() || playerAngle - 180 == required.getAngle()
						|| playerAngle + 180 == required.getAngle())) {
					return;
				}
			}

		}

		if (!alreadyUsed) { // only ever do this once
			alreadyUsed = true;
			if (levelEnd) { // if this is the end of the level
				game.endGame();
			} else { // if this is just part of the puzzle
				game.endPuzzle(newPlayerArea);
			}
		}
	}

}
