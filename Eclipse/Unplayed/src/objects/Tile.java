package objects;

import java.io.File;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PGraphics;

public class Tile extends Rectangle {
	private boolean hasTexture;
	private TileHandler tileTexture;
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
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
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
}
