package objects;

import static processing.core.PConstants.CENTER;

import java.io.File;

import handlers.ImageHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Image extends Rectangle {
	private boolean hasTexture;
	private ImageHandler imageTexture;
	private float flipX;
	private float flipY;
	private float angle;

	public Image(TextureCache texture, File file, int x, int y, int imageW, int imageH) {
		super(x, y, imageW, imageH);
		//this.texture = texture;

		if (file != null && texture != null && texture.getImageMap().containsKey(file)) {
			this.imageTexture = texture.getImageMap().get(file);
			setWidth(imageTexture.getWidth());
			setHeight(imageTexture.getHeight());
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
			graphics.rotate(PApplet.radians(angle)); // angle of the image
			graphics.scale(flipX, flipY); // flipping the image
			graphics.image(imageTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the image
			graphics.popMatrix();
			//graphics.image(imageTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
		} else {
			// display missing texture texture
		}
	}

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
