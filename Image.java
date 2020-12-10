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
//	private float flipX;
//	private float flipY;
//	private float angle;

	public Image(TextureCache texture, File file, int x, int y, int imageW, int imageH) {
		super(x, y, imageW, imageH);
		// this.texture = texture;

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
			graphics.imageMode(CENTER);
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			graphics.rotate(PApplet.radians(angle)); // angle of the image
			graphics.scale(flipX, flipY); // flipping the image
			graphics.image(imageTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the image
			graphics.popMatrix();
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
}
