package objects;

import java.io.File;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PGraphics;

public class Tile extends Rectangle {
	private boolean hasTexture;
	private TileHandler tileTexture;
	private float flipX;
	private float flipY;
	// rotation
	// vetrtical flip
	// horazontal flip

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
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.pushMatrix();
			graphics.translate(getX(), getY());
			graphics.scale(flipX, flipY); // flipping the tile
			//graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
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
}
