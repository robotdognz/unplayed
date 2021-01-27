package game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

import handlers.TextureCache;
import handlers.TileHandler;
import misc.Vibe;
import objects.Editable;
import objects.Event;
import objects.Tile;
import processing.core.*;
import shiffman.box2d.Box2DProcessing;
import static processing.core.PConstants.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;

public class Player extends Editable {
	private PApplet p;

	// player fields
	private int playerColor;

	private File file;
	private boolean hasTexture;
	private TileHandler tileTexture;

	private boolean left = false;
	private boolean right = false;

	// vibration
	private Vibe vibe;

	// box2d
	private Box2DProcessing box2d; // the box2d world
	public Body dynamicBody; // the player's physics body
	private float density; // the player's density
	private float friction; // the player's friction
	private float jumpPower; // the strength of the player's jump
	private int boxJumpCount; // how many jumps the player can make before touching the ground
	public boolean locked; // does the player have locked rotation
	int contactNumber; // the number of things touching the player's body
	private HashSet<Tile> sensorContacts; // list of all the fixtures inside the player's sensor
//	private HashSet<Tile> playerContacts; // list of all the fixtures touching the player
	private ArrayList<Event> events; // list of events touching the player
	private boolean vibeFrame; // has a vibration happened yet this frame
	private float previousImpulse; // strenght of last collision impulse

	// slot detection
	public boolean showChecking = false;
	private float centerToCenter;
	private ArrayList<Tile> tunnelChecking; // list of tiles currently being checked for tunnels
	private ArrayList<Tile> groundChecking; // list of tiles currently being checked for ground slots

	Body tempBarrier; // barrier used to stop the player moving past a slot
	Fixture tempFixture; // reference to the barrier fixture

	Player(PApplet p, Box2DProcessing box2d, boolean locked, TextureCache texture, Tile tile, Vibe v) {
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

		vibe = v;

		playerColor = p.color(255, 94, 22);

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

		// box2d
		this.box2d = box2d;
		this.friction = 0.6f; // from 0 to 1
		this.density = 1; // from 0 to 1
		this.jumpPower = 120; // 100
		this.boxJumpCount = 0;
		this.locked = locked; // is rotation locked
		this.contactNumber = 0; // is the player touching anything
		this.sensorContacts = new HashSet<Tile>();
//		this.playerContacts = new HashSet<Tile>();
		this.tempBarrier = null;
		this.previousImpulse = 0;
		this.centerToCenter = (float) Math.hypot(getWidth(), getHeight()) + 1;
		this.tunnelChecking = new ArrayList<Tile>();
		this.groundChecking = new ArrayList<Tile>();

		events = new ArrayList<Event>();
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
			bodyDef.angle = -PApplet.radians(angle);
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
			CircleShape sensorShape = new CircleShape();
			sensorShape.m_radius = box2d.scalarPixelsToWorld(getWidth() * 2);
			FixtureDef sensorFixtureDef = new FixtureDef();
			sensorFixtureDef.shape = sensorShape;
			sensorFixtureDef.isSensor = true;
			sensorFixtureDef.userData = "player sensor";
			this.dynamicBody.createFixture(sensorFixtureDef);

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

	public void addTile(Tile tile) {
		sensorContacts.add(tile);
	}

	public void removeTile(Tile tile) {
		sensorContacts.remove(tile);
	}

//	public void addPlayerTile(Tile tile) {
//		playerContacts.add(tile);
//	}
//
//	public void removePlayerTile(Tile tile) {
//		playerContacts.remove(tile);
//	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public void removeEvent(Event event) {
		events.remove(event);
	}

	public void physicsStep() {
		// environment checking
		checkTiles();

		// movement
		Vec2 vel = dynamicBody.getLinearVelocity();
		float desiredVel = 0;
		if (left) {
			desiredVel = Math.max(vel.x - 2.0f, -60.0f);
		} else if (right) {
			desiredVel = Math.min(vel.x + 2.0f, 60.0f);
		} else {
			desiredVel = vel.x * 0.999f;
		}
		float velChange = desiredVel - vel.x;
		float impulse = dynamicBody.getMass() * velChange;
		dynamicBody.applyLinearImpulse(new Vec2(impulse, 0), dynamicBody.getWorldCenter(), true);

	}

	private void checkTiles() {
		groundChecking.clear();
		tunnelChecking.clear();

		// check there are enough tiles (need at least 2)
		if (!(sensorContacts.size() >= 2)) {
			destroyBarrier();
			return;
		}

		// check the player isn't spinning
		float av = dynamicBody.getAngularVelocity();
		if (Math.abs(av) >= 2) {
			destroyBarrier();
			return;
		}

		// check angle is appropriate
		float angle = PApplet.degrees(dynamicBody.getAngle());
		float angleRounded = Math.round(angle / 90) * 90;
		float angleRemainder = Math.abs(angle - angleRounded);
		if (!(angleRemainder < 3 && angleRemainder > -3)) {
			destroyBarrier();
			return;
		}

		// run the algorithms
		checkTunnel();
		checkForGroundSlots();
		// checkForWallSlots();
		// checkForRoofSlots();

	}

	private void checkTunnel() {

		// create a list of relevant tiles sorted by x position
		PVector pos = box2d.getBodyPixelCoordPVector(dynamicBody);
		// edges of player
		float leftEdge = pos.x - getWidth() / 2 - 0.5f;
		float rightEdge = pos.x + getWidth() / 2 + 0.5f;
		float topEdge = pos.y - getHeight() / 2 - 0.5f;
		float bottomEdge = pos.y + getHeight() / 2 + 0.5f;
		// booleans for found tiles
		boolean above = false;
		boolean below = false;
		boolean leftSide = false;
		boolean rightSide = false;
		for (Tile t : sensorContacts) {
			PVector tCenter = new PVector(t.getX() + t.getWidth() / 2, t.getY() + getHeight() / 2);
			if (pos.dist(tCenter) > centerToCenter) {
				continue;
			}

			float tLeftEdge = t.getTopLeft().x;
			if (tLeftEdge > rightEdge) {
				continue;
			}
			float tRightEdge = t.getBottomRight().x;
			if (tRightEdge < leftEdge) {
				continue;
			}
			float tTopEdge = t.getTopLeft().y;
			if (tTopEdge > bottomEdge) {
				continue;
			}
			float tBottomEdge = t.getBottomRight().y;
			if (tBottomEdge < topEdge) {
				continue;
			}

			tunnelChecking.add(t);

			if (Math.abs(tCenter.y - pos.y) < getHeight() / 2) {
				if (tLeftEdge < rightEdge + 5) {
					rightSide = true;
				}
				if (tRightEdge > leftEdge - 5) {
					leftSide = true;
				}
			}

			if (Math.abs(tCenter.x - pos.x) < getWidth() / 2) {
				if (tTopEdge < bottomEdge + 5 && tTopEdge > bottomEdge) {
					below = true;
				}
				if (tBottomEdge > topEdge - 5 && tBottomEdge < topEdge) {
					above = true;
				}
			}

		}

		if (above && below) {
			this.dynamicBody.setFixedRotation(true);
		} else if (leftSide && rightSide) {
			this.dynamicBody.setFixedRotation(true);
		} else {
			this.dynamicBody.setFixedRotation(locked);
		}

//		Collections.sort(groundChecking);

	}

	private void checkForGroundSlots() {
		groundChecking.clear();

		// check velocity is appropriate
		Vec2 vel = dynamicBody.getLinearVelocity();
		// player is moving or trying to move on the x axis
		if (!((left || right) || (Math.abs(vel.x) >= 4))) {
			destroyBarrier();
			return;
		}
		boolean direction = true; // true = left, false = right
		if (left || vel.x <= -4) {
			direction = true;
		}
		if (right || vel.x >= 4) {
			direction = false;
		}
		// player is still or falling on the y axis
		if (!(vel.y <= 2)) {
			destroyBarrier();
			return;
		}

		// create a list of relevant tiles sorted by x position
		Vec2 pos = box2d.getBodyPixelCoord(dynamicBody);
		for (Tile t : sensorContacts) {
			// skip this tile if the top of it is above the player's midpoint
			if (t.getY() < pos.y) {
				continue;
			}

			// skip this tile if it is too far below the player
			if (t.getY() > pos.y + getHeight()) {
				continue;
			}

			// skip this tile if it behind the player
			if (direction) { // moving left
				if (pos.x + getWidth() * 0.60f < t.getTopLeft().x) {
					continue;
				}
			} else { // moving right
				if (pos.x - getWidth() * 0.60f > t.getBottomRight().x) {
					continue;
				}
			}

			groundChecking.add(t);
		}
		// sort the found tiles
		if (direction) { // moving left
			Collections.sort(groundChecking, Collections.reverseOrder());
		} else { // moving right
			Collections.sort(groundChecking);
		}

		// check the list of tiles for a playerWidth sized gap
		float previousX = 0;
		for (int i = 0; i < groundChecking.size(); i++) {
			Tile t = groundChecking.get(i);
			if (i > 0) {
				// if this tile is the far side of a gap
				if (Math.abs(previousX - t.getX()) == t.getWidth() + getWidth()) {
					// make sure the gap is in front of the player
					if ((direction && t.getBottomRight().x < pos.x) // moving left
							|| (!direction && t.getTopLeft().x > pos.x)) { // moving right
						// lock rotation
						this.dynamicBody.setFixedRotation(true);

						// create the barrier
						if (direction) { // moving left
							Vec2 bottom = new Vec2(t.getBottomRight().x, t.getTopLeft().y);
							Vec2 top = new Vec2(bottom.x, bottom.y - 5);
							createBarrier(top, bottom);
						} else { // moving right
							Vec2 bottom = new Vec2(t.getTopLeft().x, t.getTopLeft().y);
							Vec2 top = new Vec2(bottom.x, bottom.y - 5);
							createBarrier(top, bottom);
						}

						return;
					}
				}
			}
			previousX = t.getX();
		}

		// conditions wern't met, remove the barrier
		destroyBarrier();
	}

	private void createBarrier(Vec2 v1, Vec2 v2) {
		if (tempBarrier != null) {
			return;
		}

		// body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.userData = v1;
		tempBarrier = box2d.createBody(bodyDef);

		// shape
		EdgeShape tempBarrierEdge = new EdgeShape();
		tempBarrierEdge.set(box2d.coordPixelsToWorld(v1), box2d.coordPixelsToWorld(v2));

		// fixture
		FixtureDef tempBarrierDef = new FixtureDef();
		tempBarrierDef.shape = tempBarrierEdge;
		tempBarrierDef.density = density;
		tempBarrierDef.friction = friction;
		tempFixture = tempBarrier.createFixture(tempBarrierDef);
	}

	private void destroyBarrier() {
		if (tempBarrier != null) {
			box2d.destroyBody(tempBarrier);
			tempFixture = null;
			tempBarrier = null;

			// TODO: this will might need to change when there are multiple algorithms
			this.dynamicBody.setFixedRotation(locked);
		}
	}

	public void jump() {
		if (boxJumpCount > 0) {
			float impulse = dynamicBody.getMass() * jumpPower;
			dynamicBody.applyLinearImpulse(new Vec2(0, impulse), dynamicBody.getWorldCenter(), true);
			boxJumpCount--;
		}
	}

	public void physicsImpact(float[] impulses) {
		// find total impulse power
		float total = 0;
		for (float impulse : impulses) {
			total += impulse;
		}

		// TODO: this doesn't work because if you jump in one spot at the same height,
		// it stops the vibration
//		// check if we already did one like this
//		float impulseDifference = Math.abs(total - previousImpulse);
//		if (previousImpulse != 0 && impulseDifference < 4) {
//			PApplet.println(total + " skipped by previousImpulse");
//			return;
//		} else {
////			previousImpulse = total;
//		}

		if (total > 800 && !vibeFrame) { // 400

			// Math.abs returns positive no matter what goes in
			// Math.log returns the log of the number it is given
			int strength = (int) Math.max(Math.abs(total / 1000), 1); // 800
			vibe.vibrate(strength);
//			PApplet.println(total + " " + strength);
			vibeFrame = true;
//			previousImpulse = total;
			return;
		}

	}

	public File getFile() {
		return file;
	}

	void step() {
		vibeFrame = false; // clear vibeFrame

		// check all the events the player is colliding with
		try {
			Iterator<Event> it = events.iterator();
			while (it.hasNext()) {
				it.next().activate();
			}
		} catch (ConcurrentModificationException e) {

		}
	}

	public boolean isStill() {
		Vec2 vel = dynamicBody.getLinearVelocity();
		if (Math.abs(vel.x) >= 0.1f) {
			return false;
		}
		if (Math.abs(vel.y) >= 0.1f) {
			return false;
		}
		return true;
	}

	public void draw(PGraphics graphics, float scale) {

		// draw box2d player
		if (hasTexture) {
			Vec2 pos = box2d.getBodyPixelCoord(dynamicBody);
			float a = dynamicBody.getAngle();
			graphics.pushMatrix();
			graphics.imageMode(CENTER);
			graphics.translate(pos.x, pos.y); // pos.x + 0.5f, pos.y + 0.5f
//			graphics.rotate(PApplet.radians(angle));
			graphics.rotate(-a);

			if (showChecking && dynamicBody.isFixedRotation()) {
				graphics.tint(200, 255, 200);
			}

			graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight());
			graphics.noTint();

			graphics.popMatrix();
		}

		// draw tile checking logic
		if (showChecking) {
			for (Tile t : sensorContacts) {
				graphics.noStroke();
				graphics.fill(255, 0, 0, 150);
				graphics.rectMode(CORNER);
				graphics.rect(t.getX(), t.getY(), t.getWidth(), t.getHeight());
			}

			// tunnel checking
			if (tunnelChecking.size() > 0) {
				for (int i = 0; i < tunnelChecking.size(); i++) {
					Tile t = tunnelChecking.get(i);
					graphics.noStroke();
					graphics.fill(0, 255, 0, 200);
					graphics.rectMode(CORNER);
					graphics.rect(t.getX(), t.getY(), t.getWidth() / 2, t.getHeight() / 2);
					graphics.fill(255);
					graphics.textSize(25);
					graphics.text(i, t.getX() + t.getWidth() * 0.25f, t.getY() + t.getHeight() * 0.25f);
				}
			}
			// ground checking
			if (groundChecking.size() > 0) {
				for (int i = 0; i < groundChecking.size(); i++) {
					Tile t = groundChecking.get(i);
					graphics.noStroke();
					graphics.fill(0, 0, 255, 200);
					graphics.rectMode(CORNER);
					graphics.rect(t.getX() + t.getWidth() / 2, t.getY(), t.getWidth() / 2, t.getHeight() / 2);
					graphics.fill(255);
					graphics.textSize(25);
					graphics.text(i, t.getX() + t.getWidth() * 0.75f, t.getY() + t.getHeight() * 0.25f);
				}
			}
			if (tempFixture != null) {
				Vec2 v1 = box2d.coordWorldToPixels(((EdgeShape) tempFixture.getShape()).m_vertex1);
				Vec2 v2 = box2d.coordWorldToPixels(((EdgeShape) tempFixture.getShape()).m_vertex2);
				graphics.stroke(255, 0, 0);
				graphics.strokeWeight(4);
				graphics.line(v1.x, v1.y, v2.x, v2.y);
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

	public Vec2 getCenter() {
		return box2d.getBodyPixelCoord(dynamicBody);
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
