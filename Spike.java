package objects.events;

import static processing.core.PConstants.CENTER;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
//import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import handlers.TextureCache;
import objects.Event;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Spike extends Event {
	private float angle;
	private FixtureDef spikeDef;
//	private Fixture spikeFixture;

	public Spike(Game game, TextureCache texture, String name, int x, int y) {
		super(game, texture, name, true, x, y, 100, 100);
		angle = 0;
//		spikeFixture = null;
	}

	@Override
	public void create() {
		if (box2d != null) {
			float box2dW = box2d.scalarPixelsToWorld(getWidth() / 2);
			float box2dH = box2d.scalarPixelsToWorld(getHeight() / 2);

			// body
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.STATIC;
			bodyDef.position.set(box2d.coordPixelsToWorld(getX() + getWidth() / 2, getY() + getHeight() / 2));
			bodyDef.userData = this;
			bodyDef.angle = -PApplet.radians(angle);
			this.staticBody = box2d.createBody(bodyDef);

			// shape
			PolygonShape spikeShape = new PolygonShape();

			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(0, 0);
			vertices[1] = new Vec2(-box2dW / 2, -box2dH);
			vertices[2] = new Vec2(box2dW / 2, -box2dH);
			spikeShape.set(vertices, 3);

			// fixture
			spikeDef = new FixtureDef();
			spikeDef.shape = spikeShape;
//			spikeFixtureDef.isSensor = true;
			spikeDef.userData = "event";

//			spikeFixure = staticBody.createFixture(spikeDef);
			staticBody.createFixture(spikeDef);

		}

	}

	public void setAngle(float angle) {
		this.angle = angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
	}

	public void addAngle(float angle) {
		this.angle += angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
		if (staticBody != null) {
			destroy();
			create();
		}

	}

	public float getAngle() {
		return angle;
	}

	@Override
	public void activate() {
		game.restart(); // TODO: this needs a custom method in Game
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		// super.draw(graphics, scale);

		// draw spike
		graphics.pushMatrix();
		graphics.imageMode(CENTER);
		graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
		graphics.rotate(PApplet.radians(angle)); // angle of the tile
		graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
		graphics.popMatrix();

//		//draw hit box
//		if (spikeFixture != null) {
//			graphics.pushMatrix();
//			Vec2 pos = box2d.getBodyPixelCoord(staticBody);
//			graphics.translate(pos.x, pos.y);
//			graphics.rotate(staticBody.getAngle()); // angle of the spike
//			Vec2 v1 = box2d.coordWorldToPixels(((PolygonShape) spikeFixture.getShape()).getVertex(0));
//			Vec2 v2 = box2d.coordWorldToPixels(((PolygonShape) spikeFixture.getShape()).getVertex(1));
//			Vec2 v3 = box2d.coordWorldToPixels(((PolygonShape) spikeFixture.getShape()).getVertex(2));
//			graphics.stroke(0, 0, 255);
//			graphics.strokeWeight(4);
//			graphics.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
//			graphics.popMatrix();
//		}
	}

}
