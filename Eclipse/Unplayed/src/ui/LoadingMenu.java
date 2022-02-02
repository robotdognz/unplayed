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
			// remove any child menus
			child = null;

			// load in the next level
			AppLogic.startLevel();

			// snap to position relative to new level
			// snap camera to position

			alreadyUsed = true;
		}
	}

}
