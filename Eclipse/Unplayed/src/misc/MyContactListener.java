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
//import processing.core.PApplet;

public class MyContactListener implements ContactListener {
	Game game;

	public MyContactListener(Game game) {
		this.game = game;
	}

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

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
					playerBody = true;
//					player.startContact();

				} else if (userData.equals("tile")) {
					tile = (Tile) fixtureA.getBody().getUserData();

				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureA.getBody().getUserData();
					playerSensor = true;

				} else if (userData.contentEquals("ground")) {
					ground = true;

				} else if (userData.contentEquals("wall")) {
					wall = true;

				} else if (userData.contentEquals("event")) {
					event = (Event) fixtureA.getBody().getUserData();
				}

			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureB.getBody().getUserData();
					playerBody = true;
//					player.startContact();

				} else if (userData.equals("tile")) {
					tile = (Tile) fixtureB.getBody().getUserData();
					
				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureB.getBody().getUserData();
					playerSensor = true;

				} else if (userData.contentEquals("ground")) {
					ground = true;

				} else if (userData.contentEquals("wall")) {
					wall = true;

				} else if (userData.contentEquals("event")) {
					event = (Event) fixtureB.getBody().getUserData();
				}

			}
		}

		// if on of them is the player and one is the ground
		if (ground && playerBody) {
//			player.resetJump();
			player.startGroundContact();
		}
		
		// if on of them is the player and one is a wall
		if (wall && playerBody) {
//			player.resetJump();
			player.startWallContact();
		}

//		// if one of them is the player and one is a tile
//		if (playerBody && tile != null) {
//			player.addPlayerTile(tile);
//		}

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

		// check fixture A
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
					playerBody = true;
//					player.endContact();

				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureA.getBody().getUserData();
					playerSensor = true;

				} else if (userData.contentEquals("event")) {
					event = (Event) fixtureA.getBody().getUserData();

				} else if (userData.equals("tile")) {
					tile = (Tile) fixtureA.getBody().getUserData();
					
				} else if (userData.contentEquals("ground")) {
					ground = true;

				} else if (userData.contentEquals("wall")) {
					wall = true;

				}
			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureB.getBody().getUserData();
					playerBody = true;
//					player.endContact();

				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureB.getBody().getUserData();
					playerSensor = true;

				} else if (userData.contentEquals("event")) {
					event = (Event) fixtureB.getBody().getUserData();

				} else if (userData.equals("tile")) {
					tile = (Tile) fixtureB.getBody().getUserData();
					
				} else if (userData.contentEquals("ground")) {
					ground = true;

				} else if (userData.contentEquals("wall")) {
					wall = true;

				}
			}
		}

		// if one of them is the player sensor and one is a tile
		if (playerSensor && tile != null) {
			player.removeTile(tile);
		}
		
		// if on of them is the player and one is the ground
		if (ground && playerBody) {
			player.endGroundContact();
		}
		
		// if on of them is the player and one is a wall
		if (wall && playerBody) {
			player.endWallContact();
		}

//		// if one of them is the player and one is a tile
//		if (playerBody && tile != null) {
//			player.removePlayerTile(tile);
//		}

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
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
				}
			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
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
