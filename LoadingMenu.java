package ui;

import static processing.core.PConstants.CENTER;

import game.AppLogic;
import handlers.LoadingHandler;
import handlers.TextureCache;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	private LoadingHandler loading = null;
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
		this.loading = loading;
		this.angleOffset = 10;
		constructMenu();
	}

	@Override
	public void drawPageView(float scale) {

		p.pushMatrix();
		p.translate(position.x, position.y);
		p.rotate(PApplet.radians(angle));
		if (loading != null) {
			p.imageMode(CENTER);
			loading.draw(p.g, 0, 0, 3);
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
