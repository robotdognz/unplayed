package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;
import game.Game;
import game.player.Player;
import objects.Event;
import objects.Tile;

public class MyContactListener implements ContactListener {
	Game game;

	// variables used in begin and end contact
	private Player player = null;
	private Tile tile = null;
	private Event event = null;
	private boolean playerBody = false; // one of the fixtures is the player
	private boolean playerSensor = false; // one of the fixtures is the player's sensor

	private boolean playerEdge = false;
	private boolean solid = false;

	private boolean ground = false; // one of the fixtures is the ground
	private boolean leftWall = false; // one of the fixtures is a left wall
	private boolean rightWall = false; // one of the fixtures is a right wall

	private boolean roofBarrier = false; // one of the fixtures is a roof barrier

	public MyContactListener(Game game) {
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {

		// reset fields
		this.player = null;
		this.tile = null;
		this.event = null;
		this.playerBody = false;
		this.playerSensor = false;
		this.playerEdge = false;
		this.solid = false;
		this.ground = false;
		this.leftWall = false;
		this.rightWall = false;
		this.roofBarrier = false;

		updateVariables(contact.getFixtureA());
		updateVariables(contact.getFixtureB());

		// if one of them is a player edge and one is a solid surface
		if (playerBody && solid) {
			player.startEdgeContact();
		}

		// if on of them is the player and one is the ground
		if (ground && playerBody) {
			player.startGroundContact();
		}

		// if one of them is the player and one is a roof barrier
		if (roofBarrier && playerBody) {
			player.touchingRoofBarrier = true;
		}

		// if one of them is the player and one is a left wall
		if (leftWall && playerBody) {
			player.startLeftWallContact();
		}

		// if one of them is the player and one is a right wall
		if (rightWall && playerBody) {
			player.startRightWallContact();
		}

		// if one of them is the player and one is an event
		if (playerBody && event != null) {
			player.addEvent(event);
		}

		// if one of them is the player's sensor and one is a tile
		if (playerSensor && tile != null) {
			player.addTile(tile);
		}
	}

	@Override
	public void endContact(Contact contact) {

		// reset fields
		this.player = null;
		this.tile = null;
		this.event = null;
		this.playerBody = false;
		this.playerSensor = false;
		this.playerEdge = false;
		this.solid = false;
		this.ground = false;
		this.leftWall = false;
		this.rightWall = false;
		this.roofBarrier = false;

		updateVariables(contact.getFixtureA());
		updateVariables(contact.getFixtureB());

		// if one of them is a player edge and one is a solid surface
		if (playerBody && solid) {
			player.endEdgeContact();
		}

		// if one of them is the player sensor and one is a tile
		if (playerSensor && tile != null) {
			player.removeTile(tile);
		}

		// if one of them is the player and one is a roof barrier
		if (roofBarrier && playerBody) {
			player.touchingRoofBarrier = false;
		}

		// if one of them is the player and one is the ground
		if (ground && playerBody) {
			player.endGroundContact();
		}

		// if one of them is the player and one is a left wall
		if (leftWall && playerBody) {
			player.endLeftWallContact();
		}

		// if one of them is the player and one is a right wall
		if (rightWall && playerBody) {
			player.endRightWallContact();
		}

		// if one of them is the player and one is an event
		if (playerBody && event != null) {
			player.removeEvent(event);
		}
	}

	private void updateVariables(Fixture fixture) {
		Object fixtureUserData = fixture.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;
				switch (userData) {
				case PLAYER_BODY:
					player = (Player) fixture.getBody().getUserData();
					playerBody = true;
					break;
				case PLAYER_SENSOR:
					player = (Player) fixture.getBody().getUserData();
					playerSensor = true;
					break;
				case PLAYER_EDGE:
					player = (Player) fixture.getBody().getUserData();
					playerEdge = true;
					break;
				case SOLID:
					solid = true;
					break;
				case TILE:
					tile = (Tile) fixture.getBody().getUserData();
					break;
				case GROUND:
					ground = true;
					break;
				case LEFT_WALL:
					leftWall = true;
					break;
				case RIGHT_WALL:
					rightWall = true;
					break;
				case EVENT:
					event = (Event) fixture.getBody().getUserData();
					break;
				case ROOF_BARRIER:
					roofBarrier = true;
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

		Fixture[] fixtures = { contact.getFixtureA(), contact.getFixtureB() };

		Player player = null;

		for (Fixture fixture : fixtures) {
			Object fixtureUserData = fixture.getUserData();
			if (fixtureUserData != null) {
				if (fixtureUserData instanceof CollisionEnum) {
					CollisionEnum userData = (CollisionEnum) fixtureUserData;

					if (userData == CollisionEnum.PLAYER_BODY) {
						player = (Player) fixture.getBody().getUserData();
					}
				}
			}
		}

		if (player != null) {
			player.physicsImpact(impulse.normalImpulses);
		}
	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
	}

}
