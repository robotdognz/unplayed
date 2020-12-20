package objects;

import java.io.File;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;

import static processing.core.PConstants.*;

public class Tile extends Editable {
	private boolean hasTexture;
	private TileHandler tileTexture;
	
	private PShape tile;

	public Tile(TextureCache texture, File file, float x, float y) {
		super(x, y, 100, 100);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
			this.tile = tileTexture.createPShape(getX(), getY(), getWidth(), getHeight(), flipX, flipY, angle);
		} else {
			hasTexture = false;
		}
	}
	
	public PShape getPShape() {
		return tileTexture.createPShape(getX(), getY(), getWidth(), getHeight(), flipX, flipY, angle);
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			graphics.rotate(PApplet.radians(angle)); // angle of the tile
			graphics.scale(flipX, flipY); // flipping the tile
			graphics.imageMode(CENTER);
			graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
//			tileTexture.drawSprite(graphics, scale);
			graphics.popMatrix();
//			graphics.shape(tile);
			
		} else {
			// missing texture
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
