package objects;

import static processing.core.PConstants.CENTER;
import java.io.File;
import handlers.BackgroundHandler;
import handlers.TextureCache;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Background extends Editable {
	private PApplet p;

	private PVector position; // center of the background in page view

	// Page corners relative to center, used to check if the page is on screen
	PVector topLeft;
	PVector topRight;
	PVector bottomLeft;
	PVector bottomRight;

	private boolean hasTexture;
	private BackgroundHandler backgroundTexture;

	public Background(PApplet p, TextureCache texture, File file, PVector position) {
		super(position.x, position.y, 1, 1);
		

		this.p = p;

		if (file != null && texture != null && texture.getBackgroundMap().containsKey(file)) {
			this.backgroundTexture = texture.getBackgroundMap().get(file);
			hasTexture = true;
			// TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
			// objects are stored as 100x100 etc. This should be fixed so everything uses
			// the 1x1 system. Then remove the * 100 from the two below lines
			setWidth(backgroundTexture.getWidth() * 100);
			setHeight(backgroundTexture.getHeight() * 100);
		} else {
			hasTexture = false;
			setWidth(100);
			setHeight(100);
		}

		setPosition(position);
	}

	public void draw(float scale) {

		if (hasTexture) {
			// texture isn't missing
			p.pushMatrix();
			p.translate(position.x, position.y);
			p.scale(size); // size the page will appear in the page view
			p.rotate(PApplet.radians(angle)); // rotate the page
			p.scale(flipX, flipY); // flip the page
			p.imageMode(CENTER);
//			p.image(backgroundTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the page
			p.image(backgroundTexture.getSprite(0), 0, 0, getWidth(), getHeight()); // draw the page
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
	public void drawSelected(PGraphics g) {
		g.pushMatrix();
		g.noFill();
		g.stroke(255, 0, 0); // selection color
		g.strokeWeight(2);
		g.translate(position.x, position.y);
		g.scale(size); // size the page will appear in the page view
		g.rotate(PApplet.radians(angle)); // angle of the page
		g.rectMode(CENTER);
		g.rect(0, 0, getWidth(), getHeight());
		g.popMatrix();
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector pos) {
		if (position == null) {
			this.position = pos;
		} else {
			this.position.x = pos.x;
			this.position.y = pos.y;
		}
		updateCorners();
	}

	public void addPosition(float x, float y) {
		this.position.x += x;
		this.position.y += y;
		updateCorners();
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		updateCorners();
	}

	@Override
	public void addAngle(float angle) {
		super.addAngle(angle);
		updateCorners();
	}

	public void setSize(float size) {
		this.size = size;
		updateCorners();
	}

	public void addSize(float size) {
		if (this.size + size > 0.5) {
			this.size += size;
		} else {
			this.size = 0.5f;
		}
		updateCorners();
	}

	public float getSize() {
		return size;
	}

	@Override
	public String getName() {
		return "Background";
	}

	public File getFile() {
		if (backgroundTexture != null) {
			return backgroundTexture.getFile();
		} else {
			return null;
		}
	}

	// --------------update the corner PVectors---------------
	private void updateCorners() {
		if (topLeft == null) {
			// Initialize
			topLeft = new PVector();
			topRight = new PVector();
			bottomLeft = new PVector();
			bottomRight = new PVector();
		}
		// set values
		topLeft.x = 0 - (getWidth() / 2) * size;
		topLeft.y = 0 - (getHeight() / 2) * size;
		topRight.x = 0 + (getWidth() / 2) * size;
		topRight.y = 0 - (getHeight() / 2) * size;
		bottomLeft.x = 0 - (getWidth() / 2) * size;
		bottomLeft.y = 0 + (getHeight() / 2) * size;
		bottomRight.x = 0 + (getWidth() / 2) * size;
		bottomRight.y = 0 + (getHeight() / 2) * size;
		// rotate
		topLeft.rotate(PApplet.radians(angle));
		topRight.rotate(PApplet.radians(angle));
		bottomLeft.rotate(PApplet.radians(angle));
		bottomRight.rotate(PApplet.radians(angle));
		// translate
		topLeft.x += position.x;
		topLeft.y += position.y;
		topRight.x += position.x;
		topRight.y += position.y;
		bottomLeft.x += position.x;
		bottomLeft.y += position.y;
		bottomRight.x += position.x;
		bottomRight.y += position.y;
	}

	// ------------is a point inside the background-------------
	public boolean isInside(float x, float y) {
		PVector point = new PVector(x, y);
		point.x -= position.x;
		point.y -= position.y;
		point.rotate(PApplet.radians(-angle));

		if (-(getWidth() / 2) * size > point.x) {
			return false;
		}
		if ((getWidth() / 2) * size < point.x) {
			return false;
		}
		if (-(getHeight() / 2) * size > point.y) {
			return false;
		}
		if ((getHeight() / 2) * size < point.y) {
			return false;
		}

		return true;
	}

	// ----------is this background off camera------------

	public boolean leftOf(float x) {
		if (topLeft.x > x) {
			return false;
		}
		if (topRight.x > x) {
			return false;
		}
		if (bottomLeft.x > x) {
			return false;
		}
		if (bottomRight.x > x) {
			return false;
		}
		return true;
	}

	public boolean rightOf(float x) {
		if (topLeft.x < x) {
			return false;
		}
		if (topRight.x < x) {
			return false;
		}
		if (bottomLeft.x < x) {
			return false;
		}
		if (bottomRight.x < x) {
			return false;
		}
		return true;
	}

	public boolean above(float y) {
		if (topLeft.y > y) {
			return false;
		}
		if (topRight.y > y) {
			return false;
		}
		if (bottomLeft.y > y) {
			return false;
		}
		if (bottomRight.y > y) {
			return false;
		}
		return true;
	}

	public boolean below(float y) {
		if (topLeft.y < y) {
			return false;
		}
		if (topRight.y < y) {
			return false;
		}
		if (bottomLeft.y < y) {
			return false;
		}
		if (bottomRight.y < y) {
			return false;
		}
		return true;
	}

}
