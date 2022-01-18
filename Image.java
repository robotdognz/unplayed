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
			if (flipX == 1 && flipY == 1 && angle == 0) {
				graphics.imageMode(CORNER);
				graphics.image(imageTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
			} else {
				graphics.imageMode(CENTER);
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				if (angle != 0) {
					graphics.rotate(PApplet.radians(angle)); // rotate the image
				}
				if (flipX != 1 || flipY != 1) {
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

		// TODO: this can potentially be simplified by removing rotation completely, is
		// it really needed?
		// I'll leave it out for now and add it back in if I miss it

		if (hasTexture) {

			PImage image = imageTexture.getSprite(scale);
			float startX = getTopLeft().x;
			float startY = getTopLeft().y;
			float endX = getBottomRight().x;
			float endY = getBottomRight().y;
			int imageStartX = 0;
			int imageStartY = 0;
			int imageEndX = image.width;
			int imageEndY = image.height;

			if (getTopLeft().x < view.getTopLeft().x) {
				float temp = ((view.getTopLeft().x - getTopLeft().x) / getWidth());
				startX = view.getTopLeft().x;
				if (flipX == 1) {
					imageStartX += image.width * temp;
				} else {
					imageStartX -= image.width * temp;
				}
			}
			if (getTopLeft().y < view.getTopLeft().y) {
				float temp = ((view.getTopLeft().y - getTopLeft().y) / getHeight());
				startY = view.getTopLeft().y;
				if (flipY == 1) {
					imageStartY += image.height * temp;
				} else {
					imageStartY -= image.height * temp;
				}
			}
			if (getBottomRight().x > view.getBottomRight().x) {
				float temp = ((getBottomRight().x - view.getBottomRight().x) / getWidth());
				endX = view.getBottomRight().x;
				if (flipX == 1) {
					imageEndX -= image.width * temp;
				} else {
					imageEndX += image.width * temp;
				}
			}
			if (getBottomRight().y > view.getBottomRight().y) {
				float temp = ((getBottomRight().y - view.getBottomRight().y) / getHeight());
				endY = view.getBottomRight().y;
				if (flipY == 1) {
					imageEndY -= image.height * temp;
				} else {
					imageEndY += image.height * temp;
				}
			}
//			if (flipX != 1) {
//				int temp = imageStartX;
//				imageStartX = imageEndX;
//				imageEndX = temp;
//			}
//			if (flipY != 1) {
//				int temp = imageStartY;
//				imageStartY = imageEndY;
//				imageEndY = temp;
//			}

			// draw the tile
			graphics.imageMode(CORNERS);
			graphics.image(image, startX, startY, endX, endY, imageStartX, imageStartY, imageEndX, imageEndY);

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
