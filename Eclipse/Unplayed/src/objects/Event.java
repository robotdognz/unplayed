package objects;

import game.Game;
import handlers.EventHandler;
import handlers.TextureCache;
import misc.CollisionEnum;
import processing.core.PGraphics;
import shiffman.box2d.Box2DProcessing;

import static processing.core.PConstants.*;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public abstract class Event extends Rectangle {
	protected Game game;
	private boolean hasTexture;
	protected EventHandler eventTexture;
	private String name;
	public boolean visible;

	// box2d
	protected Box2DProcessing box2d;
	protected Body staticBody;

	public Event(Game game, TextureCache texture, String name, boolean visible, float x, float y, float width,
			float height) {
		super(x, y, width, height);
		this.game = game;
		if (game != null) {
			this.box2d = game.box2d;
		}
		this.name = name;
		this.visible = visible;

		if (name != null && texture != null && texture.getEventMap().containsKey(name)) {
			this.eventTexture = texture.getEventMap().get(name);
			hasTexture = true;
		} else {
			hasTexture = false;
		}
	}

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
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(box2dW, box2dH);

			// fixture
			FixtureDef boxFixtureDef = new FixtureDef();
			boxFixtureDef.shape = boxShape;
			boxFixtureDef.isSensor = true;
			boxFixtureDef.userData = CollisionEnum.EVENT; //"event";
			staticBody.createFixture(boxFixtureDef);
		}

	}

	public void destroy() {
		if (box2d != null && staticBody != null) {
			box2d.destroyBody(staticBody);
			staticBody = null;
		}
	}

	public String getType() {
		return "";
	}

	public void activate() {
	}

	public void draw(PGraphics graphics, float scale) {
		if (hasTexture) {
			graphics.imageMode(CENTER);
			graphics.pushMatrix();
			graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
			// graphics.rotate(PApplet.radians(angle)); // angle of the tile
			// graphics.scale(flipX, flipY); // flipping the tile
			graphics.image(eventTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
			graphics.popMatrix();
		} else {
			// display missing texture texture
		}
	}

	@Override
	public String getName() {
		return name;
	}
}
