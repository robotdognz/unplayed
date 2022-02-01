package ui;

import processing.core.PApplet;

public class LoadingMenu extends Menu {

	public LoadingMenu(PApplet p) {
		super(p);
		Button test = new Button(p.width / 2, buttonWidth, buttonHeight, "test");
		buttons.add(test);
		
		constructMenu();
	}

	@Override
	public void activate() {
		// remove any child menus
		child = null;

		// load in the next level

		// snap to position relative to new level
		// snap camera to position
	}

}
