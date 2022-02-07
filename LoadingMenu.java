package ui;

import game.AppLogic;
import handlers.TextureCache;
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
	public void drawPageView(float scale) {

		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotate(PApplet.radians(angle));
		TextureCache.drawLoadingText(p);
		p.popMatrix();

		if (child != null && child.isBuilt()) {
			child.drawPageView(scale);
		}
	}

	@Override
	public void draw() {
		// used only in editor
		drawPageView(4);
	}

	@Override
	public void activate() {
		if (!alreadyUsed) { // prevent this being triggered multiple times
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level
		}
	}

}
