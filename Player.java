package game;

import java.io.File;
import java.util.HashSet;

import handlers.TextureCache;
import handlers.TileHandler;
import misc.Vibe;
import objects.Editable;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import processing.core.*;
import shiffman.box2d.Box2DProcessing;

import static processing.core.PConstants.*;

import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;

public class Player extends Editable {
	private PApplet p;
	private PVector previousPosition;

	// player fields
	private Rectangle playerArea; // rectangle used for searching the level quad tree
	private int areaSize;
	private boolean drawArea = false;
	private PVector velocity;
	private int playerColor;

	private File file;
	private boolean hasTexture;
	private TileHandler tileTexture;

	private int jumpCount = 0;
	private final int playerSpeed = 10; // 10
	private final int playerGravity = 2; // 2
	private final int terminalVelocity = 50;
	private final int playerJumpPower = 30; // 30
	private boolean left = false;
	private boolean right = false;

	// vibration
	private Vibe vibe;
	private int vibration = 0; // y vibration amount(milliseconds)
	private boolean wall = false; // colliding with wall
	private float vibeVelocity = 0; // extra vibration added on after max velocity
	private float lastXPos; // x position one step back
	private float lastLastXPos; // x position two steps back

	// box2d
	public boolean physicsPlayer;
	Box2DProcessing box2d;
	Body dynamicBody;
	Fixture sensor;
	float density;
	float friction;
	float jumpPower;
	int boxJumpCount;
	boolean locked;
	int contactNumber;

//	ContactListener contactListener;

	Player(PApplet p, Box2DProcessing box2d, boolean physics, boolean locked, TextureCache texture, Tile tile, Vibe v) {
		super(tile.getX(), tile.getY(), 100, 100);
		this.p = p;
		this.file = tile.getFile();
//		if(tile.isFlippedH()) {
//			this.flipH();
//		}
//		if(tile.isFlippedV()) {
//			this.flipV();
//		}
		this.setAngle(tile.getAngle());

		areaSize = 500;

		playerArea = new Rectangle(getX() - ((areaSize - getWidth()) / 2), getY() - ((areaSize - getHeight()) / 2),
				areaSize, areaSize);

		vibe = v;
		lastXPos = tile.getX();
		lastLastXPos = lastXPos;

		velocity = new PVector(0, 0);
		playerColor = p.color(255, 94, 22);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

		previousPosition = new PVector(getX(), getY()); // used to determine if the player is still
//		showEvent = false;

		// box2d
		this.physicsPlayer = physics;
		this.box2d = box2d;
		this.friction = 0.6f; // from 0 to 1
		this.density = 1; // from 0 to 1
		this.jumpPower = 120; // 100
		this.boxJumpCount = 0;
		this.locked = locked; // is rotation locked
		this.contactNumber = 0; // is the player touching anything

		create();

	}

	// ---------physics

	public void create() {
		if (box2d != null) {
			float box2dW = box2d.scalarPixelsToWorld((getWidth() - 0.5f) / 2);
			float box2dH = box2d.scalarPixelsToWorld((getHeight() - 0.5f) / 2);

			// body
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DYNAMIC;
			bodyDef.position.set(box2d.coordPixelsToWorld(getX() + getWidth() / 2, getY() + getHeight() / 2));
			bodyDef.angle = 0;
			bodyDef.userData = this;
			this.dynamicBody = box2d.createBody(bodyDef);
			this.dynamicBody.setFixedRotation(locked);

			// shape
			PolygonShape boxShape = new PolygonShape();
			boxShape.setAsBox(box2dW, box2dH);

			// fixture
			FixtureDef boxFixtureDef = new FixtureDef();
			boxFixtureDef.shape = boxShape;
			boxFixtureDef.density = density;
			boxFixtureDef.friction = friction;
			boxFixtureDef.userData = "player body";
			dynamicBody.createFixture(boxFixtureDef);

			// sensor
			PolygonShape sensorShape = new PolygonShape();
			sensorShape.setAsBox(box2dW * 2, box2dH * 2);
			FixtureDef sensorFixtureDef = new FixtureDef();
			sensorFixtureDef.shape = sensorShape;
			sensorFixtureDef.isSensor = true;
			sensorFixtureDef.userData = "player sensor";
			this.sensor = this.dynamicBody.createFixture(sensorFixtureDef);

		}
	}

	public void destroy() {
		if (box2d != null) {
			box2d.destroyBody(dynamicBody);
			dynamicBody = null;
		}
	}

	public void resetJump() {
		this.boxJumpCount = 2;
	}

	public void startContact() {
		this.contactNumber++;
	}

	public void endContact() {
		this.contactNumber--;
	}

	public void moveBox() {
		Vec2 vel = dynamicBody.getLinearVelocity();
		float desiredVel = 0;
		if (left) {
			desiredVel = Math.max(vel.x - 2.0f, -60.0f); // 1.0f, -60.0f
		} else if (right) {
			desiredVel = Math.min(vel.x + 2.0f, 60.0f); // 1.0f, 60.0f
		} else {
			desiredVel = vel.x * 0.999f; // *0.98f
		}
		float velChange = desiredVel - vel.x;
		float impulse = dynamicBody.getMass() * velChange;
		dynamicBody.applyLinearImpulse(new Vec2(impulse, 0), dynamicBody.getWorldCenter(), true);
	}

	public void boxJump() {
		if (boxJumpCount > 0) {
			float impulse = dynamicBody.getMass() * jumpPower; // 50
			dynamicBody.applyLinearImpulse(new Vec2(0, impulse), dynamicBody.getWorldCenter(), true);
			boxJumpCount--;
		}
	}

	public void physicsImpact(float[] impulses) {
		// amount = 1 >
		// level = 1-255
		if (impulses[0] > 400) {
			int strength = Math.min((int) impulses[0], 255);
			if (physicsPlayer) {
				vibe.vibrate(2, strength);
			}
		}
	}

	// ---------normal-------------

	public File getFile() {
		return file;
	}

	public void jump() {
		if (!physicsPlayer) {
			// old jump
			if (jumpCount > 0) {
				jumpCount--;
				velocity.y = -playerJumpPower;
			}
		} else {
			// physics jump
			boxJump();
		}
	}

	void step(float deltaTime, HashSet<Rectangle> objects, Game g) {
		if (!physicsPlayer) {
			doPlayerStep(objects, g);
		}
	}

	private void doPlayerStep(HashSet<Rectangle> objects, Game g) {
		// store previous position, used to check if player is still
		previousPosition.x = getX();
		previousPosition.y = getY();

		// code starts here
		float previousY = getTopLeft().y;
		vibration = 0;
		if (velocity.y < terminalVelocity) {
			// limit fall speed by terminalVelocity
			velocity.y += playerGravity;
			vibeVelocity = 0;
		} else if (velocity.y + playerGravity > terminalVelocity) {
			// fall speed exactly terminalVelocity
			velocity.y = terminalVelocity;
			vibeVelocity += playerGravity / 2;
		}
		setY(getY() + velocity.y); // this comes before collision so that falling through perfect holes works
		velocity.x = 0;

		if (left) {
			velocity.x = -playerSpeed;
		}
		if (right) {
			velocity.x = playerSpeed;
		}

		// do collision
		wall = false;
		for (Rectangle p : objects) {
			if (p instanceof Tile) { // platform collison
				collision(p.getTopLeft(), p.getBottomRight());
			}
		}

		setX(getX() + velocity.x);

		// ground and roof vibration
		if (getTopLeft().y != previousY && vibration > 0) {
			vibe.vibrate(vibration);
		}
		// wall vibration
		if (wall && lastLastXPos != getTopLeft().x) {
			vibe.vibrate(1, 160);
		}

		// stores previous positions for wall vibration
		lastLastXPos = lastXPos;
		lastXPos = getTopLeft().x;

		// do event collision
		for (Rectangle p : objects) {
			if (p instanceof Event) { // event collision
				if (getTopLeft().x > p.getBottomRight().x - 1) {
					continue;
				}
				if (getBottomRight().x < p.getTopLeft().x + 1) {
					continue;
				}
				if (getTopLeft().y > p.getBottomRight().y - 1) {
					continue;
				}
				if (getBottomRight().y < p.getTopLeft().y + 1) {
					continue;
				}
				((Event) p).activate(g);
			}
		}
	}

	void collision(PVector platformTopLeft, PVector platformBottomRight) {
		// if a collision is happening
		if (platformTopLeft.y < getTopLeft().y + getHeight() + Math.max(velocity.y, 0)
				&& platformBottomRight.y > getTopLeft().y + Math.min(velocity.y, 0)
				&& platformTopLeft.x < getTopLeft().x + getWidth() + velocity.x
				&& platformBottomRight.x > getTopLeft().x + velocity.x) {

			if (platformBottomRight.y < getTopLeft().y + getHeight() / 100 - Math.min(velocity.y, 0)
					&& platformTopLeft.x < getTopLeft().x + getWidth() && platformBottomRight.x > getTopLeft().x) {
				// player is under
				if (velocity.y < 0) {
					vibration = (int) Math.max((Math.exp(Math.abs(velocity.y / 13)) / 5), 1);
				}
				setY(platformBottomRight.y);
				velocity.y = 0;
			} else if (platformTopLeft.y > getTopLeft().y + (getHeight() / 20) * 19 - Math.min(velocity.y, 0)) {
				// player is above
				if (velocity.y > 0) {
					vibration = (int) Math.max((Math.exp((velocity.y + vibeVelocity) / 15) / 1.7), 1);
				}
				setY(platformTopLeft.y - getHeight());
				velocity.y = 0;
				jumpCount = 2;
			} else if (platformTopLeft.x > getTopLeft().x + (getWidth() / 3) * 2) {
				// player is to the left
				setX(platformTopLeft.x - getWidth());
				velocity.x = 0;
				wall = true;
			} else if (platformBottomRight.x < getTopLeft().x + getWidth() / 3) {
				// player is to the right
				setX(platformBottomRight.x);
				velocity.x = 0;
				wall = true;
			} else {
				// fringe case where the player would fall through
				// aka player is in a weird place
				setY(platformTopLeft.y - getHeight());
				velocity.y = 0;
			}
		}
	}

	public PVector getVelocity() {
		return velocity;
	}

	public boolean isStill() {
		if (previousPosition.x != getX()) {
			return false;
		}
		if (previousPosition.y != getY()) {
			return false;
		}
		return true;
	}

	public void draw(PGraphics graphics, float scale) {
		if (!physicsPlayer) {

			// draw player
			graphics.imageMode(CORNER);
			if (hasTexture) {
				graphics.imageMode(CENTER);
				graphics.pushMatrix();
				graphics.translate(getX() + getWidth() / 2, getY() + getHeight() / 2);
				graphics.rotate(PApplet.radians(angle)); // angle of the tile
//				graphics.scale(flipX, flipY); // flipping the tile
				graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight()); // draw the tile
				graphics.popMatrix();
			} else {
				// missing texture
				graphics.noStroke();
				graphics.fill(255, 0, 0, 150);
				graphics.rectMode(CORNER);
				graphics.rect(getX(), getY(), getWidth(), getHeight());
			}

			if (drawArea) {
				graphics.fill(0, 0, 0, 150);
				graphics.rect(playerArea.getX(), playerArea.getY(), playerArea.getWidth(), playerArea.getHeight());
			}

		} else {

			// draw box2d players
			if (dynamicBody != null) {
				Vec2 pos = box2d.getBodyPixelCoord(dynamicBody);
				float a = dynamicBody.getAngle();
				graphics.pushMatrix();
				graphics.imageMode(CENTER);
				graphics.translate(pos.x, pos.y);
				graphics.rotate(-a);
				if (contactNumber > 0) {
					graphics.tint(255, 200, 200);
				} else {
					graphics.tint(200, 255, 200);
				}

				graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight());
				graphics.noTint();

				graphics.popMatrix();
			}
		}
	}

	public void drawArrows(Game g) {
		p.fill(playerColor);
		// draw player-off-screen arrows
//		if (getTopLeft().x + getWidth() - 10 <= g.leftEdge) {
//			// left edge
//			p.triangle(g.leftEdge + 20, getTopLeft().y + getHeight() / 2, g.leftEdge + 60,
//					getTopLeft().y + getHeight() / 2 - 40, g.leftEdge + 60, getTopLeft().y + getHeight() / 2 + 40);
//		}
//		if (getTopLeft().x + 10 >= g.rightEdge) {
//			// right edge
//			p.triangle(g.rightEdge - 20, getTopLeft().y + getHeight() / 2, g.rightEdge - 60,
//					getTopLeft().y + getHeight() / 2 - 40, g.rightEdge - 60, getTopLeft().y + getHeight() / 2 + 40);
//		}
//		if (getTopLeft().y + getHeight() - 10 <= g.topEdge) {
//			// top edge
//			p.triangle(getTopLeft().x + getWidth() / 2, g.topEdge + 20, getTopLeft().x + 40 + getWidth() / 2,
//					g.topEdge + 60, getTopLeft().x - 40 + getWidth() / 2, g.topEdge + 60);
//		}
//		if (getTopLeft().y + 10 >= g.bottomEdge) {
//			// top edge
//			p.triangle(getTopLeft().x + getWidth() / 2, g.bottomEdge - 20, getTopLeft().x + 40 + getWidth() / 2,
//					g.bottomEdge - 60, getTopLeft().x - 40 + getWidth() / 2, g.bottomEdge - 60);
//		}
		// need to add corner arrows
	}

	public Rectangle getPlayerArea() {
		playerArea.setX(getX() - (areaSize - 100) / 2);
		playerArea.setY(getY() - (areaSize - 100) / 2);
		return playerArea;
	}

	public void resetVelocity() {
		velocity.x = 0;
		velocity.y = 0;
	}

	public void left() {
		left = true;
		right = false;
	}

	public void right() {
		left = false;
		right = true;
	}

	public void still() {
		left = false;
		right = false;
	}
}
