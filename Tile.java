package objects;

import java.io.File;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PApplet;
import processing.core.PGraphics;
import static processing.core.PConstants.*;

public class Tile extends Editable {
	private boolean hasTexture;
	private TileHandler tileTexture;

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

	@Override
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
