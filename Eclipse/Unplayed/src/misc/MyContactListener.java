package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import game.Player;
import processing.core.PApplet;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Player player = null;
		boolean ground = false;

		// check if fixture A was a ball
		Object fixtureUserData = contact.getFixtureA().getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof Player) {
				player = (Player) fixtureUserData;
				player.startContact();
			} else if (fixtureUserData instanceof String) {
				if (((String) fixtureUserData).contentEquals("ground")) {
					ground = true;
				}
			}
		}

		// check if fixture B was a ball
		fixtureUserData = contact.getFixtureB().getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof Player) {
				player = (Player) fixtureUserData;
				player.startContact();
			} else if (fixtureUserData instanceof String) {
				if (((String) fixtureUserData).contentEquals("ground")) {
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
		// check if fixture A was a ball
		Object fixtureUserData = contact.getFixtureA().getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof Player) {
				Player player = (Player) fixtureUserData;
				player.endContact();
			}
		}

		// check if fixture B was a ball
		fixtureUserData = contact.getFixtureB().getUserData();
		if (fixtureUserData != null) {
			if (fixtureUserData instanceof Player) {
				Player player = (Player) fixtureUserData;
				player.endContact();
			}
		}
	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub

	}

}
