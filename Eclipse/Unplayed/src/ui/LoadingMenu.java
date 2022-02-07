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
	public void drawPageView() {
		
		p.pushMatrix();
//		p.imageMode(CENTER);
		p.translate(position.x, position.y);
		TextureCache.drawLoadingText(p);
		p.popMatrix();
		// draw the buttons

		if (child != null && child.isBuilt()) {
			child.drawPageView();
		}
	}
	
	@Override
	public void draw() {
		// used only in editor
		drawPageView();
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
