package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import game.Player;
import objects.Tile;
//import processing.core.PApplet;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;
		Tile tile = null;
		boolean playerBody = false; // one of the fixtures is the player
		boolean ground = false; // one of the fixtures is the ground

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
					playerBody = true;
					player.startContact();

				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureA.getBody().getUserData();

				} else if (userData.contentEquals("ground")) {
					ground = true;

				}

				if (fixtureA.getBody().getUserData() instanceof Tile) {
					tile = (Tile) fixtureA.getBody().getUserData();
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
					player.startContact();

				} else if (userData.contentEquals("player sensor")) {
					player = (Player) fixtureA.getBody().getUserData();

				} else if (userData.contentEquals("ground")) {
					ground = true;

				}

				if (fixtureB.getBody().getUserData() instanceof Tile) {
					tile = (Tile) fixtureB.getBody().getUserData();
				}
			}
		}

		// if on of them is the player and one is the ground
		if (ground && playerBody) {
			player.resetJump();
		}

		if (player != null && tile != null) {
			player.addTile(tile);
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;
		Tile tile = null;

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
					player.endContact();

				} else if (fixtureA.getBody().getUserData() instanceof Tile) {
					tile = (Tile) fixtureA.getBody().getUserData();
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
					player.endContact();

				} else if (fixtureB.getBody().getUserData() instanceof Tile) {
					tile = (Tile) fixtureB.getBody().getUserData();
				}
			}
		}

		if (player != null && tile != null) {
			player.removeTile(tile);
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
