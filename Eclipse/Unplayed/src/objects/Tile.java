package objects;

import java.io.File;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import handlers.TextureCache;
import handlers.TileHandler;
import processing.core.PApplet;
import processing.core.PGraphics;
import shiffman.box2d.Box2DProcessing;

import static processing.core.PConstants.*;

public class Tile extends Editable {
	private boolean hasTexture;
	private TileHandler tileTexture;

	// box2d
	Box2DProcessing box2d;
	BodyDef bodyDef;
	Body staticBody;

	public Tile(Box2DProcessing box2d, TextureCache texture, File file, float x, float y) {
		super(x, y, 100, 100);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

		if (box2d != null) {
			// box2d
			float box2dW = box2d.scalarPixelsToWorld(getWidth() / 2);
			float box2dH = box2d.scalarPixelsToWorld(getHeight() / 2);

			// body
			bodyDef = new BodyDef();
			bodyDef.type = BodyType.STATIC;
			bodyDef.position.set(box2d.coordPixelsToWorld(x, y));
			bodyDef.angle = 0;
			staticBody = box2d.createBody(bodyDef);

			// shape
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(box2dW, box2dH);

			// fixture
			FixtureDef boxFixtureDef = new FixtureDef();
			boxFixtureDef.shape = boxShape;
			boxFixtureDef.density = 1;
			boxFixtureDef.friction = 0.6f; // 0.6
			staticBody.createFixture(boxFixtureDef);
		}
	}

	public void drawTransparent(PGraphics graphics, float scale) {
		if (hasTexture) {
			// texture isn't missing
			if (angle == 0) { // flipX == 0 && flipY == 0 &&
				graphics.tint(255, 100);
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
				graphics.noTint();
			} else {
				graphics.pushMatrix();
				graphics.tint(255, 100);
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
//				if (angle != 0) {
				graphics.rotate(PApplet.radians(angle)); // rotate the tile
//				}
//				if (flipX != 0 || flipY != 0) {
//					graphics.scale(flipX, flipY); // flip the tile
//				}
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
			if (angle == 0) { // flipX == 0 && flipY == 0 &&
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
			} else {
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
//				if (angle != 0) {
				graphics.rotate(PApplet.radians(angle)); // rotate the tile
//				}
//				if (flipX != 0 || flipY != 0) {
//					graphics.scale(flipX, flipY); // flip the tile
//				}
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

		// draw box2d
		if (box2d != null) {
			Vec2 pos = box2d.getBodyPixelCoord(staticBody);
			graphics.pushMatrix();
			graphics.rectMode(CENTER);
			graphics.translate(pos.x, pos.y);
			graphics.fill(0, 0, 255);
			graphics.noStroke();
			graphics.rect(0, 0, getWidth(), getHeight());
			graphics.popMatrix();
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
