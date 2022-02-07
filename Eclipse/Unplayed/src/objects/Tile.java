package objects;

import java.io.File;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import handlers.TextureCache;
import handlers.TileHandler;
import misc.CollisionEnum;
import processing.core.PApplet;
import processing.core.PGraphics;
import shiffman.box2d.Box2DProcessing;

import static processing.core.PConstants.*;

public class Tile extends Editable implements Comparable<Tile> {
	private boolean hasTexture;
	private TileHandler tileTexture;

	// box2d
	Box2DProcessing box2d;
	Body staticBody;
	float density;
	float friction;

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

			// collision shapes
			// top edge shape
			EdgeShape topEdge = new EdgeShape();
			Vec2 v1 = new Vec2(-box2dW, box2dH); // left
			Vec2 v2 = new Vec2(box2dW, box2dH); // right
			topEdge.set(v1, v2);
			// bottom edge shape
			EdgeShape bottomEdge = new EdgeShape();
			v1 = new Vec2(-box2dW, -box2dH); // left
			v2 = new Vec2(box2dW, -box2dH); // right
			bottomEdge.set(v1, v2);
			// left edge shape
			EdgeShape leftEdge = new EdgeShape();
			v1 = new Vec2(-box2dW, box2dH); // top
			v2 = new Vec2(-box2dW, -box2dH); // bottom
			leftEdge.set(v1, v2);
			// right edge shape
			EdgeShape rightEdge = new EdgeShape();
			v1 = new Vec2(box2dW, box2dH); // top
			v2 = new Vec2(box2dW, -box2dH); // bottom
			rightEdge.set(v1, v2);

			// collision fixtures
			// top edge fixture
			FixtureDef topEdgeDef = new FixtureDef();
			topEdgeDef.shape = topEdge;
			topEdgeDef.density = density;
			topEdgeDef.friction = friction;
			staticBody.createFixture(topEdgeDef);
			// bottom edge fixture
			FixtureDef bottomEdgeDef = new FixtureDef();
			bottomEdgeDef.shape = bottomEdge;
			bottomEdgeDef.density = density;
			bottomEdgeDef.friction = friction;
			staticBody.createFixture(bottomEdgeDef);
			// left edge fixture
			FixtureDef leftEdgeDef = new FixtureDef();
			leftEdgeDef.shape = leftEdge;
			leftEdgeDef.density = density;
			leftEdgeDef.friction = friction;
			staticBody.createFixture(leftEdgeDef);
			// right edge fixture
			FixtureDef rightEdgeDef = new FixtureDef();
			rightEdgeDef.shape = rightEdge;
			rightEdgeDef.density = density;
			rightEdgeDef.friction = friction;
			staticBody.createFixture(rightEdgeDef);

			// ground sensor
			// shape
			EdgeShape sensor = new EdgeShape();
			float sBox2dW = box2d.scalarPixelsToWorld((getWidth() - 0.2f) / 2);
			v1 = new Vec2(-sBox2dW, box2dH);
			v2 = new Vec2(sBox2dW, box2dH);
			sensor.set(v1, v2);
			// fixture
			FixtureDef sensorDef = new FixtureDef();
			sensorDef.shape = sensor;
			sensorDef.userData = CollisionEnum.GROUND;
			sensorDef.isSensor = true;
			staticBody.createFixture(sensorDef);

			// left side of tile sensor
			// shape
			EdgeShape leftWallSensor = new EdgeShape();
			float sBox2dH = box2d.scalarPixelsToWorld((getHeight() - 0.1f) / 2);
			v1 = new Vec2(-box2dW, box2dH); // top
			v2 = new Vec2(-box2dW, -sBox2dH); // bottom
			leftWallSensor.set(v1, v2);
			// fixture
			FixtureDef leftWallSensorDef = new FixtureDef();
			leftWallSensorDef.shape = leftWallSensor;
			leftWallSensorDef.userData = CollisionEnum.RIGHT_WALL;
			leftWallSensorDef.isSensor = true;
			staticBody.createFixture(leftWallSensorDef);

			// right side of tile sensor
			// shape
			EdgeShape rightWallSensor = new EdgeShape();
			v1 = new Vec2(box2dW, box2dH); // top
			v2 = new Vec2(box2dW, -sBox2dH); // bottom
			rightWallSensor.set(v1, v2);
			// fixture
			FixtureDef rightWallSensorDef = new FixtureDef();
			rightWallSensorDef.shape = rightWallSensor;
			rightWallSensorDef.userData = CollisionEnum.LEFT_WALL;
			rightWallSensorDef.isSensor = true;
			staticBody.createFixture(rightWallSensorDef);

			// player sensor sensor
			// shape
			PolygonShape boxShape = new PolygonShape();
			box2dW = box2d.scalarPixelsToWorld(getWidth() / 2 - 5);
			box2dH = box2d.scalarPixelsToWorld(getHeight() / 2 - 5);
			boxShape.setAsBox(box2dW, box2dH);
			// fixture
			FixtureDef boxFixtureDef = new FixtureDef();
			boxFixtureDef.shape = boxShape;
			boxFixtureDef.userData = CollisionEnum.TILE;
			sensorDef.isSensor = true;
			staticBody.createFixture(boxFixtureDef);
		}
	}

	public void destroy() {
		if (box2d != null && staticBody != null) {
			box2d.destroyBody(staticBody);
			staticBody = null;
		}
	}
	
	public TileHandler getHandler() {
		return tileTexture;
	}

	public void drawTransparent(PGraphics graphics, float scale) {
		if (hasTexture) {
			// texture isn't missing
			if (angle == 0) {
				graphics.tint(255, 100);
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
				graphics.noTint();
			} else {
				graphics.pushMatrix();
				graphics.tint(255, 100);
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				graphics.rotate(PApplet.radians(angle)); // rotate the tile
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
			if (angle == 0) {
				graphics.imageMode(CORNER);
				graphics.image(tileTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight()); // draw the tile
			} else {
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				graphics.rotate(PApplet.radians(angle)); // rotate the tile
				graphics.imageMode(CENTER);
//				graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
				tileTexture.draw(graphics, getWidth(), getHeight(), scale);
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
	
	public int getRotationMode() {
		if (tileTexture != null) {
			return tileTexture.getRotationMode();
		} else {
			return -1;
		}
	}

	@Override
	public int compareTo(Tile otherTile) {
		float X = getX();
		float otherX = otherTile.getX();
		if (X > otherX) {
			return 1;
		} else if (X < otherX) {
			return -1;
		} else {
			float Y = getY();
			float otherY = otherTile.getY();
			if (Y > otherY) {
				return 1;
			} else if (Y < otherY) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
