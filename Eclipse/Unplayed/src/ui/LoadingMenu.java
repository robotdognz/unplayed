package ui;

import game.AppLogic;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	
	String test = "test";

	public LoadingMenu(PApplet p) {
		super(p);
		Button testb = new Button(p.width / 2, buttonWidth, buttonHeight, test);
		buttons.add(testb);
		
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
	
	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(test)) {
				AppLogic.removeMenu(); // remove pause menu
				AppLogic.removeMenu(); // remove pause menu
			}
		}
	}

}
