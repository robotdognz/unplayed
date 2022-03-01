package ui;

import game.AppLogic;
import handlers.LoadingHandler;
import handlers.TextureCache;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	LoadingHandler loading = null;

//	public LoadingMenu(PApplet p) {
//		super(p);
//		this.angleOffset = 10;
//		constructMenu();
//	}

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
		this.angleOffset = 10;
		this.loading = loading;
		constructMenu();
	}

	@Override
	public void drawPageView(float scale) {

		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotate(PApplet.radians(angle));
		if (loading != null) {
			loading.draw(p.g, 0, 0, loading.getWidth(), loading.getHeight(), scale);
		} else {
			TextureCache.drawLoadingText(p);
		}
		p.popMatrix();

		if (child != null && child.isBuilt()) {
			child.drawPageView(scale);
		}
	}

	@Override
	public void draw() {
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
