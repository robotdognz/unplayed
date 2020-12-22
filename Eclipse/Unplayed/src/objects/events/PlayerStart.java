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
	// Game game;

	public PlayerStart(PApplet p, TextureCache texture, String name, float x, float y, Game game) {
		super(p, texture, name, x, y);
		// this.game = game;

		// remove all other player starts
		HashSet<Rectangle> returnSet = new HashSet<Rectangle>();
		game.world.getAll(returnSet);
		for (Rectangle r : returnSet) {
			if (r instanceof PlayerStart) {
				Tile oldRequired = ((PlayerStart) r).getRequired();
				game.world.remove(r);
				game.world.insert(oldRequired);
			}
		}

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
		game.setPlayerStart(this);
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

}
