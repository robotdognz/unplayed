package objects.events;

import java.util.HashSet;

import game.Game;
import handlers.TextureCache;
import objects.Rectangle;
import objects.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PlayerStart extends CameraChange {
	private Tile required;
	private Game game;

	public PlayerStart(PApplet p, TextureCache texture, String name, float x, float y, Game game) {
		super(p, texture, name, x, y);
		this.game = game;

		// remove all other player starts
		HashSet<Rectangle> returnSet = new HashSet<Rectangle>();
		game.world.getAll(returnSet);
		for (Rectangle r : returnSet) {
			if (r instanceof PlayerStart) {
				Tile oldRequired = ((PlayerStart) r).getRequired();
				game.world.remove(r);
				if (oldRequired != null) {
					game.world.insert(oldRequired);
				}
			}
		}
		// remove the old player
		game.player = null;

		// get tile that is at the current position
		returnSet.clear();
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
			required = (Tile) r;
			game.world.remove(required);
			break;
		}

		// set player start
		if (this.required != null) {
			game.setPlayerStart(this);
		}
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		if (required != null) {
			required.drawTransparent(graphics, scale);
		}
		super.draw(graphics, scale);
	}

	public Tile getRequired() {
		return required;
	}

	public void setRequired(Tile required) {
		this.required = required;

		if (this.required != null) {
			// start a new game with the new player
			game.setPlayerStart(this);
			game.startGame();
		}
	}

}
