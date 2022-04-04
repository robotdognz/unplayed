package ui;

import static processing.core.PConstants.CENTER;

import camera.PageViewCamera;
import game.AppLogic;
import handlers.ButtonHandler;
import handlers.LoadingHandler;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class TitleMenu extends LoadingMenu {

	public TitleMenu(PApplet p, LoadingHandler loading) {
		super(p, loading);
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
