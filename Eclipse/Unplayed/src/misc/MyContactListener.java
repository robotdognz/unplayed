package misc;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import game.Player;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		// check if fixture A was a ball
		Object bodyUserData = contact.getFixtureA().getBody().getUserData();
		if (bodyUserData != null) {
			Player player = (Player) bodyUserData;
			player.startContact();
		}

		// check if fixture B was a ball
		bodyUserData = contact.getFixtureB().getBody().getUserData();
		if (bodyUserData != null) {
			Player player = (Player) bodyUserData;
			player.startContact();
		}
	}

	@Override
	public void endContact(Contact contact) {
		// check if fixture A was a ball
		Object bodyUserData = contact.getFixtureA().getBody().getUserData();
		if (bodyUserData != null) {
			Player player = (Player) bodyUserData;
			player.endContact();
		}

		// check if fixture B was a ball
		bodyUserData = contact.getFixtureB().getBody().getUserData();
		if (bodyUserData != null) {
			Player player = (Player) bodyUserData;
			player.endContact();
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
