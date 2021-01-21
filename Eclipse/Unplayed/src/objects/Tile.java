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
	Body staticBody;
	float density;
	float friction;

	EdgeShape topEdge;

	public Tile(Box2DProcessing box2d, TextureCache texture, File file, float x, float y) {
		super(x, y, 100, 100);
		this.box2d = box2d;
		friction = 0.6f;
		density = 1;

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

	}

	public void create() {
		if (box2d != null) {
			// body
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.STATIC;
			bodyDef.position.set(box2d.coordPixelsToWorld(getX() + getWidth() / 2, getY() + getHeight() / 2));
			bodyDef.angle = 0;
			bodyDef.userData = this;
			staticBody = box2d.createBody(bodyDef);

			// half dimensions of tile
			float box2dW = box2d.scalarPixelsToWorld(getWidth() / 2);
			float box2dH = box2d.scalarPixelsToWorld(getHeight() / 2);

			// shapes

			// top edge
			topEdge = new EdgeShape();
			Vec2 v1 = new Vec2(-box2dW, box2dH);
			Vec2 v2 = new Vec2(box2dW, box2dH);
			topEdge.set(v1, v2);
			
			Vec2 v0 = new Vec2(-box2dW, box2dH+1);
			Vec2 v3 = new Vec2(box2dW, box2dH+1);
			topEdge.m_vertex0.set( v0 );
			topEdge.m_vertex3.set( v3 );
			topEdge.m_hasVertex0 = true;
			topEdge.m_hasVertex3 = true;
			
			// bottom edge
			EdgeShape bottomEdge = new EdgeShape();
			v1 = new Vec2(-box2dW, -box2dH);
			v2 = new Vec2(box2dW, -box2dH);
			bottomEdge.set(v1, v2);
			// left edge
			EdgeShape leftEdge = new EdgeShape();
			v1 = new Vec2(-box2dW, box2dH);
			v2 = new Vec2(-box2dW, -box2dH);
			leftEdge.set(v1, v2);
			// right edge
			EdgeShape rightEdge = new EdgeShape();
			v1 = new Vec2(box2dW, box2dH);
			v2 = new Vec2(box2dW, -box2dH);
			rightEdge.set(v1, v2);

			// fixtures

			// top edge
			FixtureDef topEdgeDef = new FixtureDef();
			topEdgeDef.shape = topEdge;
			topEdgeDef.density = density;
			topEdgeDef.friction = friction;
			staticBody.createFixture(topEdgeDef);
			// bottom edge
			FixtureDef bottomEdgeDef = new FixtureDef();
			bottomEdgeDef.shape = bottomEdge;
			bottomEdgeDef.density = density;
			bottomEdgeDef.friction = friction;
			staticBody.createFixture(bottomEdgeDef);
			// left edge
			FixtureDef leftEdgeDef = new FixtureDef();
			leftEdgeDef.shape = leftEdge;
			leftEdgeDef.density = density;
			leftEdgeDef.friction = friction;
			staticBody.createFixture(leftEdgeDef);
			// right edge
			FixtureDef rightEdgeDef = new FixtureDef();
			rightEdgeDef.shape = rightEdge;
			rightEdgeDef.density = density;
			rightEdgeDef.friction = friction;
			staticBody.createFixture(rightEdgeDef);

			// sensor
			EdgeShape sensor = new EdgeShape();
			float sBox2dW = box2d.scalarPixelsToWorld((getWidth() - 1) / 2);
			v1 = new Vec2(-sBox2dW, box2dH);
			v2 = new Vec2(sBox2dW, box2dH);
			sensor.set(v1, v2);
			FixtureDef sensorDef = new FixtureDef();
			sensorDef.shape = sensor;
			sensorDef.userData = "ground";
			sensorDef.isSensor = true;
			staticBody.createFixture(sensorDef);
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
