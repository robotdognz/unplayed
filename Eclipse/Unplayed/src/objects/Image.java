package objects;

import static processing.core.PConstants.*;

import java.io.File;

import handlers.ImageHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Image extends Editable {
	private boolean hasTexture;
	private ImageHandler imageTexture;

	public Image(TextureCache texture, File file, int x, int y, int imageW, int imageH) {
		super(x, y, imageW, imageH);

		if (file != null && texture != null && texture.getImageMap().containsKey(file)) {
			this.imageTexture = texture.getImageMap().get(file);

			// TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
			// objects are stored as 100x100 etc. This should be fixed so everything uses
			// the 1x1 system. Then remove the * 100 from the two below lines
			setWidth(imageTexture.getWidth() * 100);
			setHeight(imageTexture.getHeight() * 100);
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

	public void drawClipped(PGraphics graphics, Rectangle view, float scale) {

		if (hasTexture) {

			PImage image = imageTexture.getSprite(scale);
			float startX = 0;
			float startY = 0;
			float endX = 0;
			float endY = 0;
			int imageStartX = 0;
			int imageStartY = 0;
			int imageEndX = image.width;
			int imageEndY = image.height;

			// texture isn't missing
//			if (flipX == 0 && flipY == 0 && angle == 0) {
			graphics.imageMode(CORNERS);
			graphics.image(image, Math.max(getTopLeft().x, view.getTopLeft().x),
					Math.max(getTopLeft().y, view.getTopLeft().y),
					Math.min(getBottomRight().x, view.getBottomRight().x),
					Math.min(getBottomRight().y, view.getBottomRight().y), imageStartX, imageStartY, imageEndX,
					imageEndY); // draw the tile
//			} else {
//				graphics.imageMode(CENTER);
//				graphics.pushMatrix();
//				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
//				if (angle != 0) {
//					graphics.rotate(PApplet.radians(angle)); // rotate the image
//				}
//				if (flipX != 0 || flipY != 0) {
//					graphics.scale(flipX, flipY); // flip the image
//				}
//				graphics.image(image, 0, 0, getWidth(), getHeight()); // draw the image
//				graphics.popMatrix();
//			}
		} else {
			// texture is missing
			graphics.noStroke();
			graphics.fill(255, 0, 0, 150);
			graphics.rectMode(CORNERS);
			graphics.rect(Math.min(getTopLeft().x, view.getTopLeft().x), Math.min(getTopLeft().y, view.getTopLeft().y),
					Math.max(getBottomRight().x, view.getBottomRight().x),
					Math.max(getBottomRight().y, view.getBottomRight().y));
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
