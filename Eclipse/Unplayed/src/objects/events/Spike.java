package objects.events;

import static processing.core.PConstants.CENTER;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import game.Game;
import game.Player;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Spike extends Event {
	private float angle;
	private Rectangle bounds;

	public Spike(Game game, TextureCache texture, String name, int x, int y) {
		super(game, texture, name, true, x, y, 100, 100);
		angle = 0;
		createBounds();
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
			this.staticBody = box2d.createBody(bodyDef);

			// shape
			PolygonShape spikeShape = new PolygonShape();

			Vec2 vertices[] = new Vec2[3];
			vertices[0] = new Vec2(0, 0);
			vertices[1] = new Vec2(-box2dW, -box2dH);
			vertices[2] = new Vec2(box2dW, -box2dH);
			spikeShape.set(vertices, 3);

//			spikeShape.setAsBox(box2dW, box2dH);

			// fixture
			FixtureDef spikeFixtureDef = new FixtureDef();
			spikeFixtureDef.shape = spikeShape;
//			spikeFixtureDef.isSensor = true;
			spikeFixtureDef.userData = "event";
			staticBody.createFixture(spikeFixtureDef);
		}

	}

	private void createBounds() {
		if (angle == 0) {
			bounds = new Rectangle(getX() + getWidth() / 3, getY() + getHeight() / 2, getWidth() / 3, getHeight() / 2);
		} else if (angle == 90) {
			bounds = new Rectangle(getX(), getY() + getHeight() / 3, getWidth() / 2, getHeight() / 3);
		} else if (angle == 180) {
			bounds = new Rectangle(getX() + getWidth() / 3, getY(), getWidth() / 3, getHeight() / 2);
		} else if (angle == 270) {
			bounds = new Rectangle(getX() + getWidth() / 2, getY() + getHeight() / 3, getWidth() / 2, getHeight() / 3);
		}
	}

	public void setAngle(float angle) {
		this.angle = angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
		createBounds();
	}

	public void addAngle(float angle) {
		this.angle += angle;
		if (this.angle - 360 >= 0) {
			this.angle -= 360;
		} else if (this.angle < 0) {
			this.angle = 360 + this.angle;
		}
		createBounds();
	}

	public float getAngle() {
		return angle;
	}

	@Override
	public void activate() {
		// check if player is inside bounds
//		Player player = game.player;
//		if (player.getTopLeft().x > bounds.getBottomRight().x - 1) {
//			return;
//		}
//		if (player.getBottomRight().x < bounds.getTopLeft().x + 1) {
//			return;
//		}
//		if (player.getTopLeft().y > bounds.getBottomRight().y - 1) {
//			return;
//		}
//		if (player.getBottomRight().y < bounds.getTopLeft().y + 1) {
//			return;
//		}

		game.restart(); // TODO: this needs a custom method in Game
	}

	@Override
	public void draw(PGraphics graphics, float scale) {
		// super.draw(graphics, scale);

		// draw bounds
		graphics.pushMatrix();
//		graphics.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		graphics.imageMode(CENTER);
		graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
		graphics.rotate(PApplet.radians(angle)); // angle of the tile
		graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
		graphics.popMatrix();
	}

}
