package objects.events;

import static processing.core.PConstants.*;

import java.util.HashSet;

import org.jbox2d.common.Vec2;

import game.Game;
import game.Player;
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
	private long lastTime = 0;
	private PVector center; // used for checking against player position
	private int rotationMode; // what kind of rotation does this player end care about

	public PlayerEnd(Game game, TextureCache texture, String name, float x, float y) {
		super(game, texture, name, false, x, y, 100, 100);
		this.rotationMode = 0;
		// 0 = cares about rotation, 1 = ignore 180 rotation, 2 = ignore all rotation
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

	public int getRotationMode() {
		return rotationMode;
	}

	public void setRotationMode(int newRotationMode) {
		this.rotationMode = newRotationMode;
	}

	@Override
	public void activate() {
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

			if (rotationMode == 0) { // rotation matters
				float playerAngle = player.getAdjustedAngle();

				if (!(playerAngle == required.getAngle())) {
					return;
				}
			} else if (rotationMode == 1) { // only 180 degree rotation matters
				float playerAngle = player.getAdjustedAngle();

				if (!(playerAngle == required.getAngle() || playerAngle - 180 == required.getAngle())) {
					return;
				}
			}

		}

		// the player is perfectly in the slot
		if (System.currentTimeMillis() > lastTime + 2000) {
			lastTime = System.currentTimeMillis();
			if (levelEnd) { // if this is the end of the level
				game.endGame();
			} else { // if this is just part of the puzzle
				game.endPuzzle(newPlayerArea);
			}
		}
	}

}
