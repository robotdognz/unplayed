package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import game.Player;
//import processing.core.PApplet;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		Player player = null;
		boolean ground = false;

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					player = (Player) fixtureA.getBody().getUserData();
					player.startContact();

				} else if (userData.contentEquals("ground")) {
					ground = true;

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
					player.startContact();

				} else if (userData.contentEquals("ground")) {
					ground = true;

				}
			}
		}

		// if on of them is the player and one is the ground
		if (ground && player != null) {
			player.resetJump();
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		// check fixture
		Object fixtureUserData = fixtureA.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					Player player = (Player) fixtureA.getBody().getUserData();
					player.endContact();

				}
			}
		}

		// check fixture B
		fixtureUserData = fixtureB.getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof String) {
				String userData = (String) fixtureUserData;

				if (userData.equals("player body")) {
					Player player = (Player) fixtureB.getBody().getUserData();
					player.endContact();

				}
			}
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
