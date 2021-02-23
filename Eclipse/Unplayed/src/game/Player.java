package game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

import handlers.TextureCache;
import handlers.TileHandler;
import misc.CountdownTimer;
import misc.PlayerTileXComparator;
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

	// player fields
	private File file;
	private boolean hasTexture;
	private TileHandler tileTexture;

	private boolean left = false;
	private boolean right = false;

	// vibration
	private Vibe vibe;

	// box2d player
	private Box2DProcessing box2d; // the box2d world
	public Body dynamicBody; // the player's physics body
	private float density; // the player's density
	private float friction; // the player's friction

	public boolean locked; // does the player have locked rotation
	int contactNumber; // the number of things touching the player's body
	public int groundContacts; // the number of grounds touching the player's body
	public int wallContacts; // the number of walls touching the player's body
	public CountdownTimer groundTimer; // used to make ground collision more forgiving
	public CountdownTimer wallTimer; // used to make wall collision more forgiving
	public CountdownTimer jumpTimer; // used to correct the ground timer
	public CountdownTimer boostTimer; // used to correct the ground timer

	private ArrayList<Event> events; // list of events touching the player
	private boolean vibeFrame; // has a vibration happened yet this frame

	// environment checking
	public boolean showChecking = false;
	private HashSet<Tile> sensorContacts; // list of all the fixtures inside the player's sensor
//	private HashSet<Tile> playerContacts; // list of all the fixtures touching the player

	// tunnel checking
	private ArrayList<Tile> tunnelChecking; // list of tiles currently being checked for tunnels
	private PlayerTileXComparator xCompare; // comparator used for x axis tunnel checking

	// ground slot checking
	private ArrayList<Tile> groundChecking; // list of tiles currently being checked for ground slots
	private Body groundBarrier; // barrier used to stop the player moving past a slot
	private Fixture groundFixture; // reference to the barrier fixture

	// wall slot checking
	private ArrayList<Tile> wallChecking; // list of tiles currently being checked for wall slots
	private Body wallBarrier; // barrier used to stop the player moving past a slot
	private Fixture wallFixture; // reference to the barrier fixture

	// roof slot checking
	private ArrayList<Tile> roofChecking; // list of tiles currently being checked for roof slots
	private Body roofBarrier; // barrier used to stop the player moving past a slot
	private Fixture roofFixture; // reference to the barrier fixture
	public boolean touchingRoofBarrier; // is the player touching a roof barrier

	// movement / jumping
	private float movementSpeed;
	private float jumpPower; // the strength of the player's jump
	private boolean extraJump; // does the player have an extra jump
	private boolean verticalTunnel; // used to check if player should jump away from the wall or not
	private boolean horizontalTunnel;
//	private Vec2 previousPosition; // last player location

	Player(PApplet p, Box2DProcessing box2d, boolean locked, TextureCache texture, Tile tile, Vibe v) {
		super(tile.getX(), tile.getY(), 100, 100);
		this.file = tile.getFile();
		this.setAngle(tile.getAngle());

		vibe = v;

		if (file != null && texture != null && texture.getTileMap().containsKey(file)) {
			this.tileTexture = texture.getTileMap().get(file);
			hasTexture = true;
		} else {
			hasTexture = false;
		}

		this.events = new ArrayList<Event>();

		// box2d
		this.box2d = box2d;
		this.friction = 0.6f; // from 0 to 1
		this.density = 1; // from 0 to 1

		this.locked = locked; // is rotation locked
//		this.contactNumber = 0; // is the player touching anything

		// environment checking
		this.sensorContacts = new HashSet<Tile>();
//		this.playerContacts = new HashSet<Tile>();

		this.tunnelChecking = new ArrayList<Tile>();
		this.xCompare = new PlayerTileXComparator();

		this.groundChecking = new ArrayList<Tile>();
		this.groundBarrier = null;

		this.wallChecking = new ArrayList<Tile>();
		this.wallBarrier = null;

		this.roofChecking = new ArrayList<Tile>();
		this.roofBarrier = null;
		this.touchingRoofBarrier = false;

		// movement / jumping
		this.movementSpeed = 60.0f;
		this.jumpPower = 120;
		this.groundContacts = 0;
		this.wallContacts = 0;
		// how long to pad leaving the ground
		this.groundTimer = new CountdownTimer(0.200f); // 0.128
		// how long to pad leaving the ground
		this.wallTimer = new CountdownTimer(0.128f); // 0.064
		// how long after a jump before the ground a wall timers can be started
		this.jumpTimer = new CountdownTimer(0.064f);
		// how long after boosting to keep checking for roof slots
		this.boostTimer = new CountdownTimer(0.256f);
		this.extraJump = false;
		this.verticalTunnel = false;
		this.horizontalTunnel = false;

		create();

	}

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

//			previousPosition = box2d.getBodyPixelCoord(dynamicBody); // set last player location
		}
	}

	public void destroy() {
		if (box2d != null) {
			destroyAllBarriers(false); // get rid of barriers so they don't mess with the next player
			box2d.destroyBody(dynamicBody);
			dynamicBody = null;
		}
	}

	public void startGroundContact() {
		this.groundContacts++;
	}

	public void endGroundContact() {
		this.groundContacts--;
		// if the player has just left the ground and it wasn't because of a jump
		if (this.groundContacts == 0 && !this.jumpTimer.isRunning()) {
			groundTimer.start();
		}
	}

	public void startWallContact() {
		this.wallContacts++;
	}

	public void endWallContact() {
		this.wallContacts--;
		if (this.wallContacts == 0 && !this.jumpTimer.isRunning()) {
			wallTimer.start();
		}
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

	public void physicsStep(float delta) {
		// step timers
		jumpTimer.deltaStep(delta);
		groundTimer.deltaStep(delta);
		wallTimer.deltaStep(delta);
		boostTimer.deltaStep(delta);

		// run checks
		checkJumps();
		checkTiles();

		Vec2 vel = dynamicBody.getLinearVelocity();

		// boost up if touching roof barrier
		if (touchingRoofBarrier) {
			jumpTimer.start();
			extraJump = false;
			// reset vertical speed
			dynamicBody.setLinearVelocity(new Vec2(0, 0)); // dynamicBody.getLinearVelocity().x
			// apply impulse
			float ratio = 1 - boostTimer.deltaRemainingRatio();
			float yImpulse = dynamicBody.getMass() * (jumpPower * ratio);
			dynamicBody.applyLinearImpulse(new Vec2(0, yImpulse), dynamicBody.getWorldCenter(), true);
		}

		// do movement
		float desiredVel = 0;

		if (left) {
			if (vel.x >= -movementSpeed) {
				desiredVel = Math.max(vel.x - 2.0f, -movementSpeed);
			} else {
				return;
			}

		} else if (right) {
			if (vel.x <= movementSpeed) {
				desiredVel = Math.min(vel.x + 2.0f, movementSpeed);
			} else {
				return;
			}
		} else {
			desiredVel = vel.x * 0.999f;
		}

		float velChange = desiredVel - vel.x;
		float impulse = dynamicBody.getMass() * velChange;
		dynamicBody.applyLinearImpulse(new Vec2(impulse, 0), dynamicBody.getWorldCenter(), true);

	}

	private void checkJumps() {
		if (groundContacts > 0) {
			extraJump = true;
		}
	}

	private void checkTiles() {
		// environment checking

		// reset fields
		tunnelChecking.clear();
		verticalTunnel = false;
		horizontalTunnel = false;
		groundChecking.clear();
		wallChecking.clear();
		roofChecking.clear();

		// check there are enough tiles (need at least 2)
		if (!(sensorContacts.size() >= 2)) {
			destroyAllBarriers(true);
			return;
		}

		// check the player isn't spinning
		float av = dynamicBody.getAngularVelocity();
		if (Math.abs(av) >= 2) {
			destroyAllBarriers(true);
			return;
		}

		// check angle is appropriate
		float angle = PApplet.degrees(dynamicBody.getAngle());
		float angleRounded = Math.round(angle / 90) * 90;
		float angleRemainder = Math.abs(angle - angleRounded);
		if (angleRemainder >= 3) {
			destroyAllBarriers(true);
			return;
		}

		// run the algorithms
		// if tunnel checking locks the player's rotation, the other algorithms
		// shouldn't unlock it, that's what this variable is for
		PVector pos = box2d.getBodyPixelCoordPVector(dynamicBody);
		Vec2 vel = dynamicBody.getLinearVelocity();
		boolean resetRotation = checkTunnel(pos);
		checkForGroundSlots(pos, vel, resetRotation);
		checkForWallSlots(pos, vel, resetRotation);
		checkForRoofSlots(pos, vel);

	}

	private boolean checkTunnel(PVector pos) {

		// create a list of relevant tiles
		// edges of player
		float leftEdge = pos.x - getWidth() / 2 - 0.5f; // 0.25f
		float rightEdge = pos.x + getWidth() / 2 + 0.5f;
		float topEdge = pos.y - getHeight() / 2 - 0.5f;
		float bottomEdge = pos.y + getHeight() / 2 + 0.5f;

		for (Tile t : sensorContacts) {
			// tile left edge larger than player right edge
			if (t.getTopLeft().x > rightEdge) {
				continue;
			}
			// tile right edge small than player left edge
			if (t.getBottomRight().x < leftEdge) {
				continue;
			}
			// tile top edge larger than player bottom edge
			if (t.getTopLeft().y > bottomEdge) {
				continue;
			}
			// tile bottom edge smaller than player top edge
			if (t.getBottomRight().y < topEdge) {
				continue;
			}

			tunnelChecking.add(t);
		}

		if (tunnelChecking.size() >= 2) {
			boolean returnBoolean = true; // true if nothing found

			// ----- check for left/right (vertical tunnel)
			float previousX = 0;
			Collections.sort(tunnelChecking, xCompare);

			for (int i = 0; i < tunnelChecking.size(); i++) {
				Tile t = tunnelChecking.get(i);

				if (i > 0) {
					if (Math.abs(previousX - t.getX()) == t.getWidth() + getWidth()) {
						this.dynamicBody.setFixedRotation(true);
						verticalTunnel = true;
						returnBoolean = false;
						break;
					}
				}

				previousX = t.getTopLeft().x;

			}

			// ----- check for top/bottom (horizontal tunnel)
			float previousY = 0;
			Collections.sort(tunnelChecking);

			for (int i = 0; i < tunnelChecking.size(); i++) {
				Tile t = tunnelChecking.get(i);

				if (i > 0) {
					if (Math.abs(previousY - t.getY()) == t.getHeight() + getHeight()) {
						this.dynamicBody.setFixedRotation(true);
						horizontalTunnel = true;
						returnBoolean = false;
						break;
					}
				}

				previousY = t.getTopLeft().y;
			}

			// return boolean telling subsequent algorithms if they can unlock the player
			if (returnBoolean == false) {
				return false;
			}

		}

		// no tunnel found
		this.dynamicBody.setFixedRotation(locked);
		return true;

	}

	private void checkForGroundSlots(PVector pos, Vec2 vel, boolean resetRotation) {

		// check velocity is appropriate
		// player is moving or trying to move on the x axis
		if (!((left || right) || (Math.abs(vel.x) >= 4))) {
			destroyGroundBarrier(resetRotation);
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
			destroyGroundBarrier(resetRotation);
			return;
		}

		// create a list of relevant tiles sorted by x position
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

						// try create the barrier
						if (direction) { // moving left
							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							if (t.getBottomRight().x <= pos.x - getWidth() / 2 - 0.25f) {
								Vec2 bottom = new Vec2(t.getBottomRight().x, t.getTopLeft().y);
								Vec2 top = new Vec2(bottom.x, bottom.y - 5);
								createGroundBarrier(top, bottom);
							}

						} else { // moving right
							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							// 0.25 is added to stop a barrier being constructed when you're up against the
							// edge of the gap
							if (t.getTopLeft().x >= pos.x + getWidth() / 2 + 0.25f) {
								Vec2 bottom = new Vec2(t.getTopLeft().x, t.getTopLeft().y);
								Vec2 top = new Vec2(bottom.x, bottom.y - 5);
								createGroundBarrier(top, bottom);
							}
						}

						return;
					}
				}
			}
			previousX = t.getX();
		}

		// conditions wern't met, remove the barrier
		destroyGroundBarrier(resetRotation);
	}

	private void checkForWallSlots(PVector pos, Vec2 vel, boolean resetRotation) {

		// player is trying to move on the x axis
		if (!(left || right)) {
			destroyWallBarrier(resetRotation);
			return;
		}
		boolean direction = true; // true = left, false = right
		if (left) {
			direction = true;
		}
		if (right) {
			direction = false;
		}

		// create a list of relevant tiles sorted by x position
		for (Tile t : sensorContacts) {

			// skip the tile if it is to the back of the player
			if (direction) { // moving left
				if (t.getX() > pos.x - getWidth() / 2) {
					continue;
				}
				if (t.getX() < pos.x - getWidth() * 2) {
					continue;
				}
			} else { // moving right
				if (t.getBottomRight().x < pos.x + getWidth() / 2) {
					continue;
				}
				if (t.getBottomRight().x > pos.x + getWidth() * 2) {
					continue;
				}
			}

			// skip the tile if it is behind the player
			if (vel.y > 1) { // moving up
				if (pos.y + getHeight() * 0.60f < t.getTopLeft().y) {
					continue;
				}
			} else { // moving down
				if (pos.y - getHeight() * 0.60f > t.getBottomRight().y) {
					continue;
				}
			}

			wallChecking.add(t);
		}

		// sort the found tiles
		if (vel.y > 1) { // moving up
			Collections.sort(wallChecking, Collections.reverseOrder());
		} else { // moving down
			Collections.sort(wallChecking);
		}

		// check the list of tiles for a playerWidth sized gap
		float previousY = 0;
		for (int i = 0; i < wallChecking.size(); i++) {
			Tile t = wallChecking.get(i);
			if (i > 0) {
				// if this tile is the far side of a gap
				if (Math.abs(previousY - t.getY()) == t.getHeight() + getHeight()) {

					// make sure the gap is in front of the player
					if ((vel.y > 1 && t.getBottomRight().y < pos.y) // moving up
							|| (vel.y < 1 && t.getTopLeft().y > pos.y)) { // moving down

						// lock rotation
						this.dynamicBody.setFixedRotation(true);

						// try create the barrier
						if (vel.y > 1) { // moving up

							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							if (t.getBottomRight().y <= pos.y - getHeight() / 2) {

								if (direction) { // moving left
									Vec2 bottom = new Vec2(t.getBottomRight().x, t.getBottomRight().y);
									Vec2 top = new Vec2(bottom.x + 5, bottom.y);
									createWallBarrier(top, bottom);
								} else { // moving right
									Vec2 bottom = new Vec2(t.getTopLeft().x, t.getBottomRight().y);
									Vec2 top = new Vec2(bottom.x - 5, bottom.y);
									createWallBarrier(top, bottom);
								}
							}

						} else { // moving down

							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							if (t.getTopLeft().y >= pos.y + getHeight() / 2) {
								if (direction) { // moving left
									Vec2 bottom = new Vec2(t.getBottomRight().x, t.getTopLeft().y);
									Vec2 top = new Vec2(bottom.x + 5, bottom.y);
									createWallBarrier(top, bottom);
								} else { // moving right
									Vec2 bottom = new Vec2(t.getTopLeft().x, t.getTopLeft().y);
									Vec2 top = new Vec2(bottom.x - 5, bottom.y);
									createWallBarrier(top, bottom);
								}
							}
						}

						return;
					}
				}
			}
			previousY = t.getY();
		}

		// conditions wern't met, remove the barrier
		destroyWallBarrier(resetRotation);
	}

	private void checkForRoofSlots(PVector pos, Vec2 vel) {

		// check the player is boosting
		if (!boostTimer.isRunning()) {
			destroyRoofBarrier();
			return;
		}

		boolean direction = true; // true = left, false = right
		if (vel.x <= -4) {
			direction = true;
		}
		if (vel.x >= 4) {
			direction = false;
		}

		// create a list of relevant tiles sorted by x position
		for (Tile t : sensorContacts) {
			// skip this tile if the bottom of it is below the player's midpoint
			if (t.getBottomRight().y > pos.y) {
				continue;
			}

			// skip this tile if it is too far above the player
			if (t.getBottomRight().y < pos.y - getHeight()) {
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

			roofChecking.add(t);
		}
		// sort the found tiles
		if (direction) { // moving left
			Collections.sort(roofChecking, Collections.reverseOrder());
		} else { // moving right
			Collections.sort(roofChecking);
		}

		// check the list of tiles for a playerWidth sized gap
		float previousX = 0;
		for (int i = 0; i < roofChecking.size(); i++) {
			Tile t = roofChecking.get(i);
			if (i > 0) {
				// if this tile is the far side of a gap
				if (Math.abs(previousX - t.getX()) == t.getWidth() + getWidth()) {
					// make sure the gap is in front of the player
					if ((direction && t.getBottomRight().x < pos.x) // moving left
							|| (!direction && t.getTopLeft().x > pos.x)) { // moving right

						// lock rotation
						this.dynamicBody.setFixedRotation(true);

						// try create the barrier
						if (direction) { // moving left
							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							if (t.getBottomRight().x <= pos.x - getWidth() / 2 - 0.25f) {
								Vec2 bottom = new Vec2(t.getBottomRight().x, t.getBottomRight().y);
								Vec2 top = new Vec2(bottom.x, bottom.y + 5);
								createRoofBarrier(top, bottom);
							}

						} else { // moving right
							// final position check (stops barriers being made under player)
							// this works because it failing doesn't remove an existing barrier
							// so it only prevents barriers being made when you're already in the slot
							// 0.25 is added to stop a barrier being constructed when you're up against the
							// edge of the gap
							if (t.getTopLeft().x >= pos.x + getWidth() / 2 + 0.25f) {
								Vec2 bottom = new Vec2(t.getTopLeft().x, t.getBottomRight().y);
								Vec2 top = new Vec2(bottom.x, bottom.y + 5);
								createRoofBarrier(top, bottom);
							}
						}

						return;
					}
				}
			}
			previousX = t.getX();
		}

		// conditions wern't met, remove the barrier
		destroyRoofBarrier();
	}

	private void createGroundBarrier(Vec2 v1, Vec2 v2) {
		if (groundBarrier != null) {
			return;
		}

		// body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.userData = v1;
		groundBarrier = box2d.createBody(bodyDef);

		// shape
		EdgeShape tempBarrierEdge = new EdgeShape();
		tempBarrierEdge.set(box2d.coordPixelsToWorld(v1), box2d.coordPixelsToWorld(v2));

		// fixture
		FixtureDef tempBarrierDef = new FixtureDef();
		tempBarrierDef.shape = tempBarrierEdge;
		tempBarrierDef.density = density;
		tempBarrierDef.friction = friction;
		groundFixture = groundBarrier.createFixture(tempBarrierDef);
	}

	private void createWallBarrier(Vec2 v1, Vec2 v2) {
		if (wallBarrier != null) {
			return;
		}

		// body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.userData = v1;
		wallBarrier = box2d.createBody(bodyDef);

		// shape
		EdgeShape tempBarrierEdge = new EdgeShape();
		tempBarrierEdge.set(box2d.coordPixelsToWorld(v1), box2d.coordPixelsToWorld(v2));

		// fixture
		FixtureDef tempBarrierDef = new FixtureDef();
		tempBarrierDef.shape = tempBarrierEdge;
		tempBarrierDef.density = density;
		tempBarrierDef.friction = friction;
		wallFixture = wallBarrier.createFixture(tempBarrierDef);
	}

	private void createRoofBarrier(Vec2 v1, Vec2 v2) {
		if (roofBarrier != null) {
			return;
		}

		// body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.STATIC;
		bodyDef.userData = v1;
		roofBarrier = box2d.createBody(bodyDef);

		// shape
		EdgeShape tempBarrierEdge = new EdgeShape();
		tempBarrierEdge.set(box2d.coordPixelsToWorld(v1), box2d.coordPixelsToWorld(v2));

		// fixture
		FixtureDef tempBarrierDef = new FixtureDef();
		tempBarrierDef.shape = tempBarrierEdge;
		tempBarrierDef.density = density;
		tempBarrierDef.friction = friction;
		tempBarrierDef.userData = "roof";
		roofFixture = roofBarrier.createFixture(tempBarrierDef);
	}

	private void destroyGroundBarrier(boolean resetRotation) {
		if (groundBarrier != null) {
			box2d.destroyBody(groundBarrier);
			groundFixture = null;
			groundBarrier = null;

			if (resetRotation) {
				this.dynamicBody.setFixedRotation(locked);
			}
		}
	}

	private void destroyWallBarrier(boolean resetRotation) {
		if (wallBarrier != null) {
			box2d.destroyBody(wallBarrier);
			wallFixture = null;
			wallBarrier = null;

			if (resetRotation) {
				this.dynamicBody.setFixedRotation(locked);
			}
		}
	}

	private void destroyRoofBarrier() {
		if (roofBarrier != null) {
			box2d.destroyBody(roofBarrier);
			roofFixture = null;
			roofBarrier = null;
		}
	}

	private void destroyAllBarriers(boolean resetRotation) {
		if (resetRotation) {
			this.dynamicBody.setFixedRotation(locked);
		}
		destroyGroundBarrier(false);
		destroyWallBarrier(false);
		destroyRoofBarrier();
	}

	public void jump() {

		float xImpulse = 0;
		float yImpulse = 0;

		if (groundContacts > 0 || groundTimer.isRunning()) { // touching the ground
			// check if jumping while moving in a tunnel
			Vec2 vel = dynamicBody.getLinearVelocity();
			if (horizontalTunnel && Math.abs(vel.x) > 0.1f) { // 0.5f
				if (vel.x > 0) {
					xImpulse = dynamicBody.getMass() * (jumpPower / 2);
					boostTimer.start();
				} else {
					xImpulse = -(dynamicBody.getMass() * (jumpPower / 2));
					boostTimer.start();
				}
			} else {
				yImpulse = dynamicBody.getMass() * jumpPower;
			}
			extraJump = true;

		} else if (wallContacts > 0 || wallTimer.isRunning()) { // touching a wall

			if (left) { // pushing into a wall left
				yImpulse = dynamicBody.getMass() * jumpPower;
				if (!verticalTunnel) { // not in a tunnel
					xImpulse = (dynamicBody.getMass() * jumpPower / 2);
				}
				// extraJump = true;
				extraJump = false; // TODO: testing

			} else if (right) { // pushing into a wall right
				yImpulse = dynamicBody.getMass() * jumpPower;
				if (!verticalTunnel) { // not in a tunnel
					xImpulse = -(dynamicBody.getMass() * jumpPower / 2);
				}
				// extraJump = true;
				extraJump = false; // TODO: testing

			} else if (extraJump) { // not pushing into the wall
				yImpulse = dynamicBody.getMass() * jumpPower;
				extraJump = false;
			}

		} else { // touching nothing
			if (extraJump) {
				yImpulse = dynamicBody.getMass() * jumpPower;
				extraJump = false;
			}
		}

		if (yImpulse > 0 || xImpulse != 0) {
			jumpTimer.start();
			// reset vertical speed
			dynamicBody.setLinearVelocity(new Vec2(dynamicBody.getLinearVelocity().x, 0));
			// apply impulse
			dynamicBody.applyLinearImpulse(new Vec2(xImpulse, yImpulse), dynamicBody.getWorldCenter(), true);
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
			graphics.translate(pos.x, pos.y);
			graphics.rotate(-a);

			if (showChecking && dynamicBody.isFixedRotation()) {
				graphics.tint(200, 255, 200);
			}

			graphics.image(tileTexture.getSprite(scale), 0, 0, getWidth(), getHeight());
			graphics.noTint();

			if (showChecking && verticalTunnel) {
				graphics.noStroke();
				graphics.fill(0, 255, 0, 100);
				graphics.rectMode(CORNER);
				graphics.rect(-getWidth() / 2, -getHeight() / 2, getWidth() / 2, getHeight());
			}
			if (showChecking && horizontalTunnel) {
				graphics.noStroke();
				graphics.fill(0, 255, 0, 100);
				graphics.rectMode(CORNER);
				graphics.rect(0, -getHeight() / 2, getWidth() / 2, getHeight());
			}

			graphics.popMatrix();
		}

		// draw tile checking logic
		if (showChecking) {
			for (Tile t : sensorContacts) {
				graphics.noStroke();
				graphics.fill(150, 150, 150, 150);
				graphics.rectMode(CORNER);
				graphics.rect(t.getX(), t.getY(), t.getWidth(), t.getHeight());
			}

			// tunnel checking
			if (tunnelChecking.size() > 0) {
				for (int i = 0; i < tunnelChecking.size(); i++) {
					Tile t = tunnelChecking.get(i);
					graphics.noStroke();
					graphics.fill(0, 255, 0, 200); // green
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
					graphics.fill(0, 0, 255, 200); // blue
					graphics.rectMode(CORNER);
					graphics.rect(t.getX() + t.getWidth() / 2, t.getY(), t.getWidth() / 2, t.getHeight() / 2);
					graphics.fill(255);
					graphics.textSize(25);
					graphics.text(i, t.getX() + t.getWidth() * 0.75f, t.getY() + t.getHeight() * 0.25f);
				}
			}
			if (groundFixture != null) {
				Vec2 v1 = box2d.coordWorldToPixels(((EdgeShape) groundFixture.getShape()).m_vertex1);
				Vec2 v2 = box2d.coordWorldToPixels(((EdgeShape) groundFixture.getShape()).m_vertex2);
				graphics.stroke(0, 0, 255); // blue
				graphics.strokeWeight(4);
				graphics.line(v1.x, v1.y, v2.x, v2.y);
			}

			// wall checking
			if (wallChecking.size() > 0) {
				for (int i = 0; i < wallChecking.size(); i++) {
					Tile t = wallChecking.get(i);
					graphics.noStroke();
					graphics.fill(255, 0, 0, 200); // red
					graphics.rectMode(CORNER);
					graphics.rect(t.getX(), t.getY() + t.getHeight() / 2, t.getWidth() / 2, t.getHeight() / 2);
					graphics.fill(255);
					graphics.textSize(25);
					graphics.text(i, t.getX() + t.getWidth() * 0.25f, t.getY() + t.getHeight() * 0.75f);
				}
			}
			if (wallFixture != null) {
				Vec2 v1 = box2d.coordWorldToPixels(((EdgeShape) wallFixture.getShape()).m_vertex1);
				Vec2 v2 = box2d.coordWorldToPixels(((EdgeShape) wallFixture.getShape()).m_vertex2);
				graphics.stroke(255, 0, 0); // red
				graphics.strokeWeight(4);
				graphics.line(v1.x, v1.y, v2.x, v2.y);
			}

			// roof checking
			if (roofChecking.size() > 0) {
				for (int i = 0; i < roofChecking.size(); i++) {
					Tile t = roofChecking.get(i);
					graphics.noStroke();
					graphics.fill(255, 255, 0, 200); // yellow
					graphics.rectMode(CORNER);
					graphics.rect(t.getX() + t.getWidth() / 2, t.getY() + t.getHeight() / 2, t.getWidth() / 2,
							t.getHeight() / 2);
					graphics.fill(255);
					graphics.textSize(25);
					graphics.text(i, t.getX() + t.getWidth() * 0.75f, t.getY() + t.getHeight() * 0.75f);
				}
			}
			if (roofFixture != null) {
				Vec2 v1 = box2d.coordWorldToPixels(((EdgeShape) roofFixture.getShape()).m_vertex1);
				Vec2 v2 = box2d.coordWorldToPixels(((EdgeShape) roofFixture.getShape()).m_vertex2);
				graphics.stroke(255, 255, 0); // yellow
				graphics.strokeWeight(4);
				graphics.line(v1.x, v1.y, v2.x, v2.y);
			}
		}
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

	public float getAdjustedAngle() {
		float playerAngle = -dynamicBody.getAngle(); // get angle
		playerAngle = PApplet.degrees(playerAngle); // convert to degrees
		playerAngle = Math.round(playerAngle / 90) * 90; // round to nearest 90

		// get it into the 360 range
		while (Math.abs(playerAngle) > 270) {
			if (playerAngle > 0) {
				playerAngle -= 360;
			} else {
				playerAngle += 360;
			}
		}

		// make sure it's positive
		if (playerAngle < 0) {
			playerAngle += 360;
		}

		return playerAngle;
	}

}
