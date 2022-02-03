package ui;

import game.AppLogic;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	public LoadingMenu(PApplet p) {
		super(p);

		constructMenu();
	}

	@Override
	public void activate() {
		if (!alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level

			// snap to position relative to new level
			// snap camera to position

			
		}
	}

}
