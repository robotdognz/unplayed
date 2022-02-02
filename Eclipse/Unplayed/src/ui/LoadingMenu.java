package ui;

import game.AppLogic;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	boolean alreadyUsed = false;

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
			AppLogic.removeMenu();
		} else {
//			AppLogic.removeMenu();
		}
	}

}
