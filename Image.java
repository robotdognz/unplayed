package objects;

import static processing.core.PConstants.*;

import java.io.File;

import handlers.ImageHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Image extends Editable {
	private boolean hasTexture;
	private ImageHandler imageTexture;

	public Image(TextureCache texture, File file, int x, int y, int imageW, int imageH) {
		super(x, y, imageW, imageH);

		if (file != null && texture != null && texture.getImageMap().containsKey(file)) {
			this.imageTexture = texture.getImageMap().get(file);
			setWidth(imageTexture.getWidth());
			setHeight(imageTexture.getHeight());
			hasTexture = true;
		} else {
			hasTexture = false;
		}
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			// texture isn't missing
			if (flipX == 0 && flipY == 0 && angle == 0) {
				graphics.imageMode(CORNER);
				graphics.image(imageTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
			} else {
				graphics.imageMode(CENTER);
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				if (angle != 0) {
					graphics.rotate(PApplet.radians(angle)); // rotate the image
				}
				if (flipX != 0 || flipY != 0) {
					graphics.scale(flipX, flipY); // flip the image
				}
				graphics.image(imageTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the image
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
		return "Image";
	}

	public File getFile() {
		if (imageTexture != null) {
			return imageTexture.getFile();
		} else {
			return null;
		}
	}
}
