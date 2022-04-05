package ui;

import game.AppLogic;
import handlers.LoadingHandler;
import processing.core.PApplet;

public class TitleMenu extends LoadingMenu {

	public TitleMenu(PApplet p, LoadingHandler loading) {
		super(p, loading);
		child = null;
	}

	@Override
	public void click() {
		for (MenuObject object : objects) {
			if (!(object instanceof Button)) {
				continue;
			}
			Button b = (Button) object;

			if (b.click().equals(continueGame)) {
				child = null; // remove any child menus
				AppLogic.mainMenu(); // trigger the main menu
			}
		}
	}

	@Override
	public void activate() {
		// alreadyUsed prevents this being triggered multiple times
		if (!hasButton && !alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.mainMenu(); // trigger the main menu
		}
	}

}
