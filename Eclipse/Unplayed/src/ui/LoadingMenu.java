package ui;

import game.AppLogic;
import processing.core.PApplet;

public class LoadingMenu extends Menu {

	public LoadingMenu(PApplet p) {
		super(p);

		
		constructMenu();
	}

	@Override
	public void activate() {
		// remove any child menus
		child = null;

		// load in the next level
		AppLogic.startLevel();

		// snap to position relative to new level
		// snap camera to position
	}

}
