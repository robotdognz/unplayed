package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import game.Game;
import game.Player;
import objects.Event;
import objects.Tile;

public class MyContactListener implements ContactListener {
	Game game;

	public MyContactListener(Game game) {
		this.game = game;
	}
	
	// TODO: change these if else chains to switch cases

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;
		Tile tile = null;
		Event event = null;
		boolean playerBody = false; // one of the fixtures is the player
		boolean playerSensor = false; // one of the fixtures is the player's sensor
		boolean ground = false; // one of the fixtures is the ground
		boolean wall = false; // one of the fixtures is a wall
		boolean roofBarrier = false; // one of the fixtures is a roof barrier

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureA.getBody().getUserData();
					playerBody = true;

				} else if (userData == CollisionEnum.TILE) {
					tile = (Tile) fixtureA.getBody().getUserData();

				} else if (userData == CollisionEnum.PLAYER_SENSOR) {
					player = (Player) fixtureA.getBody().getUserData();
					playerSensor = true;

				} else if (userData == CollisionEnum.GROUND) { 
					ground = true;

				} else if (userData == CollisionEnum.WALL) {
					wall = true;

				} else if (userData == CollisionEnum.EVENT) { 
					event = (Event) fixtureA.getBody().getUserData();

				} else if (userData == CollisionEnum.ROOF_BARRIER) { 
					roofBarrier = true;

				}

			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureB.getBody().getUserData();
					playerBody = true;

				} else if (userData == CollisionEnum.TILE) {
					tile = (Tile) fixtureB.getBody().getUserData();

				} else if (userData == CollisionEnum.PLAYER_SENSOR) {
					player = (Player) fixtureB.getBody().getUserData();
					playerSensor = true;

				} else if (userData == CollisionEnum.GROUND) {
					ground = true;

				} else if (userData == CollisionEnum.WALL) {
					wall = true;

				} else if (userData == CollisionEnum.EVENT) {
					event = (Event) fixtureB.getBody().getUserData();

				} else if (userData == CollisionEnum.ROOF_BARRIER) {
					roofBarrier = true;

				}

			}
		}

		// if on of them is the player and one is the ground
		if (ground && playerBody) {
			player.startGroundContact();
		}

		// if on of them is the player and one is a roof barrier
		if (roofBarrier && playerBody) {
			player.touchingRoofBarrier = true;
		}

		// if on of them is the player and one is a wall
		if (wall && playerBody) {
			player.startWallContact();
		}

		// if on of them is the player and one is an event
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
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;
		Tile tile = null;
		Event event = null;
		boolean playerBody = false; // one of the fixtures is the player
		boolean playerSensor = false; // one of the fixtures is the player's sensor
		boolean ground = false; // one of the fixtures is the ground
		boolean wall = false; // one of the fixtures is a wall
		boolean roofBarrier = false; // one of the fixtures is a roof barrier

		// check fixture A
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureA.getBody().getUserData();
					playerBody = true;

				} else if (userData == CollisionEnum.PLAYER_SENSOR) {
					player = (Player) fixtureA.getBody().getUserData();
					playerSensor = true;

				} else if (userData == CollisionEnum.EVENT) {
					event = (Event) fixtureA.getBody().getUserData();

				} else if (userData == CollisionEnum.TILE) {
					tile = (Tile) fixtureA.getBody().getUserData();

				} else if (userData == CollisionEnum.GROUND) {
					ground = true;

				} else if (userData == CollisionEnum.WALL) {
					wall = true;

				} else if (userData == CollisionEnum.ROOF_BARRIER) {
					roofBarrier = true;

				}
			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureB.getBody().getUserData();
					playerBody = true;

				} else if (userData == CollisionEnum.PLAYER_SENSOR) {
					player = (Player) fixtureB.getBody().getUserData();
					playerSensor = true;

				} else if (userData == CollisionEnum.EVENT) {
					event = (Event) fixtureB.getBody().getUserData();

				} else if (userData == CollisionEnum.TILE) {
					tile = (Tile) fixtureB.getBody().getUserData();

				} else if (userData == CollisionEnum.GROUND) {
					ground = true;

				} else if (userData == CollisionEnum.WALL) {
					wall = true;

				} else if (userData == CollisionEnum.ROOF_BARRIER) {
					roofBarrier = true;

				}
			}
		}

		// if one of them is the player sensor and one is a tile
		if (playerSensor && tile != null) {
			player.removeTile(tile);
		}

		// if on of them is the player and one is a roof barrier
		if (roofBarrier && playerBody) {
			player.touchingRoofBarrier = false;
		}

		// if on of them is the player and one is the ground
		if (ground && playerBody) {
			player.endGroundContact();
		}

		// if on of them is the player and one is a wall
		if (wall && playerBody) {
			player.endWallContact();
		}

		// if one of them is the player and one is an event
		if (playerBody && event != null) {
			player.removeEvent(event);
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;

		// check fixture A
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureA.getBody().getUserData();
				}
			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof CollisionEnum) {
				CollisionEnum userData = (CollisionEnum) fixtureUserData;

				if (userData == CollisionEnum.PLAYER_BODY) {
					player = (Player) fixtureB.getBody().getUserData();
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
