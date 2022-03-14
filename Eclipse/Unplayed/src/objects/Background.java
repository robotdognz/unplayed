package objects;

import static processing.core.PConstants.CENTER;
import java.io.File;
import handlers.BackgroundHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PVector;

public class Background extends PageViewObject {

	private boolean hasTexture;
	private BackgroundHandler backgroundTexture;

	public Background(PApplet p, TextureCache texture, File file, PVector position) {
		super(p, position, 1, 1);

		if (file != null && texture != null && texture.getBackgroundMap().containsKey(file)) {
			this.backgroundTexture = texture.getBackgroundMap().get(file);
			hasTexture = true;
			// TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
			// objects are stored as 100x100 etc. This should be fixed so everything uses
			// the 1x1 system. Then remove the * 100 from the two below lines
			setWidth(backgroundTexture.getWidth() * 200); // 100
			setHeight(backgroundTexture.getHeight() * 200); // 100
		} else {
			hasTexture = false;
			setWidth(100);
			setHeight(100);
		}

		setPosition(position);
	}

	@Override
	public void draw(float scale) {

		if (hasTexture) {
			// texture isn't missing
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.scale(size); // size the page will appear in the page view
			p.rotate(PApplet.radians(angle)); // rotate the page
			p.scale(flipX, flipY); // flip the page
			p.imageMode(CENTER);
			p.image(backgroundTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the page
//			p.image(backgroundTexture.getSprite(0), 0, 0, getWidth(), getHeight()); // draw the page
			p.popMatrix();
		} else {
			// texture is missing
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.scale(size); // size the background will appear in the page view
			p.rotate(PApplet.radians(angle)); // rotate the page
			p.scale(flipX, flipY); // flip the page
			p.noStroke();
			p.fill(255, 0, 0, 150);
			p.rectMode(CENTER);
			p.rect(0, 0, getWidth(), getHeight());
			p.popMatrix();
		}

	}

	@Override
	public String getName() {
		return "background";
	}

	public File getFile() {
		if (backgroundTexture != null) {
			return backgroundTexture.getFile();
		} else {
			return null;
		}
	}
}
