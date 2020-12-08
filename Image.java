package objects;

import java.io.File;

import handlers.ImageHandler;
import handlers.TextureCache;
import processing.core.PGraphics;

public class Image extends Rectangle {
	//private TextureCache texture;
	private boolean hasTexture;
	private ImageHandler imageTexture;
	// rotation
	// vetrtical flip
	// horazontal flip

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
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.image(imageTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
		} else {
			// display missing texture texture
		}
	}

	public String getName() {
		return "Image";
	}

	public File getFile() {
		return imageTexture.getFile();
	}
}
