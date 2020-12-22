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

	public Tile(TextureCache texture, File file, float x, float y) {
		super(x, y, 100, 100);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}
	}
	
	public void drawTransparent(PGraphics graphics, float scale) {
		if (hasTexture) {
			// texture isn't missing
			if (flipX == 0 && flipY == 0 && angle == 0) {
				graphics.tint(0, 100);
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
				graphics.noTint();
			} else {
				graphics.pushMatrix();
				graphics.tint(0, 100);
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				if (angle != 0) {
					graphics.rotate(PApplet.radians(angle)); // rotate the tile
				}
				if (flipX != 0 || flipY != 0) {
					graphics.scale(flipX, flipY); // flip the tile
				}
				graphics.imageMode(CENTER);
				graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
				graphics.noTint();
				graphics.popMatrix();
			}
		} else {
			// texture is missing
			graphics.noStroke();
			graphics.fill(255, 0, 0, 150);
			graphics.rectMode(CORNER);
			graphics.rect(getX(), getY(), getWidth(), getHeight());
		}
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			// texture isn't missing
			if (flipX == 0 && flipY == 0 && angle == 0) {
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
			} else {
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				if (angle != 0) {
					graphics.rotate(PApplet.radians(angle)); // rotate the tile
				}
				if (flipX != 0 || flipY != 0) {
					graphics.scale(flipX, flipY); // flip the tile
				}
				graphics.imageMode(CENTER);
				graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
				graphics.popMatrix();
			}
		} else {
			// texture is missing
			graphics.noStroke();
			graphics.fill(255, 0, 0, 150);
			graphics.rectMode(CORNER);
			graphics.rect(getX(), getY(), getWidth(), getHeight());
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
