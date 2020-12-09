package objects;

import java.io.File;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PApplet;
import processing.core.PGraphics;
import static processing.core.PConstants.*;

public class Tile extends Rectangle {
	private boolean hasTexture;
	private TileHandler tileTexture;
	private float flipX;
	private float flipY;
	private float angle;

	public Tile(TextureCache texture, File file, int x, int y) {
		super(x, y, 100, 100);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

		flipX = 1;
		flipY = 1;
		angle = 0;
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.imageMode(CENTER);
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			graphics.rotate(PApplet.radians(angle)); // angle of the tile
			graphics.scale(flipX, flipY); // flipping the tile
			graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
			graphics.popMatrix();

		} else {
			// display missing texture texture
		}
	}

	public String getName() {
		return "Tile";
	}

	public File getFile() {
		if (tileTexture != null) {
			return tileTexture.getFile();
		} else {
			return null;
		}
	}

	public void flipH() {
		if (flipX == 1) {
			flipX = -1;
		} else {
			flipX = 1;
		}
	}

	public boolean isFlippedH() {
		if (flipX == 1) {
			return false;
		} else {
			return true;
		}
	}
	
	public void flipV() {
		if (flipY == 1) {
			flipY = -1;
		} else {
			flipY = 1;
		}
	}

	public boolean isFlippedV() {
		if (flipY == 1) {
			return false;
		} else {
			return true;
		}
	}
}
