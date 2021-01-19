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

		create();
	}

	public void create() {
		if (box2d != null) {
			// body
			bodyDef = new BodyDef();
			// bodyDef.type = BodyType.STATIC;
			// bodyDef.position.set(box2d.coordPixelsToWorld(x, y));
			// bodyDef.angle = 0;
			staticBody = box2d.createBody(bodyDef);

			// shape
			// PolygonShape boxShape = new PolygonShape();
			// boxShape.setAsBox(box2dW, box2dH);

			// top edge
			EdgeShape topEdge = new EdgeShape();
			Vec2 v1 = box2d.coordPixelsToWorld(getX(), getY());
			Vec2 v2 = box2d.coordPixelsToWorld(getX() + getWidth(), getY());
			topEdge.set(v1, v2);

			// right edge
			EdgeShape rightEdge = new EdgeShape();
			v1 = box2d.coordPixelsToWorld(getX() + getWidth(), getY());
			v2 = box2d.coordPixelsToWorld(getX() + getWidth(), getY() + getHeight());
			rightEdge.set(v1, v2);

			// left edge
			EdgeShape leftEdge = new EdgeShape();
			v1 = box2d.coordPixelsToWorld(getX(), getY());
			v2 = box2d.coordPixelsToWorld(getX(), getY() + getHeight());
			leftEdge.set(v1, v2);

			// bottom edge
			EdgeShape bottomEdge = new EdgeShape();
			v1 = box2d.coordPixelsToWorld(getX(), getY() + getHeight());
			v2 = box2d.coordPixelsToWorld(getX() + getWidth(), getY() + getHeight());
			bottomEdge.set(v1, v2);

			// top edge fixture
			FixtureDef topEdgeDef = new FixtureDef();
			topEdgeDef.shape = topEdge;
			topEdgeDef.density = 1;
			topEdgeDef.friction = 0.6f;
			staticBody.createFixture(topEdgeDef);

			// right edge fixture
			FixtureDef rightEdgeDef = new FixtureDef();
			rightEdgeDef.shape = rightEdge;
			rightEdgeDef.density = 1;
			rightEdgeDef.friction = 0.6f;
			staticBody.createFixture(rightEdgeDef);

			// left edge fixture
			FixtureDef leftEdgeDef = new FixtureDef();
			leftEdgeDef.shape = leftEdge;
			leftEdgeDef.density = 1;
			leftEdgeDef.friction = 0.6f;
			staticBody.createFixture(leftEdgeDef);

			// bottom edge fixture
			FixtureDef bottomEdgeDef = new FixtureDef();
			bottomEdgeDef.shape = bottomEdge;
			bottomEdgeDef.density = 1;
			bottomEdgeDef.friction = 0.6f;
			staticBody.createFixture(bottomEdgeDef);
		}
	}

	public void destroy() {
		if (box2d != null) {
			box2d.destroyBody(staticBody);
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
