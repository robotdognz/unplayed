package ui;

import static processing.core.PConstants.CENTER;
import game.AppLogic;
import handlers.LoadingHandler;
import handlers.TextureCache;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	String continueGame = "Continue";
	boolean button;
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
		this.angleOffset = 10;

		MenuObject loadingImage;

		if (loading != null) {
			// we have a valid loading handler to build from
			float imageWidth = loading.getWidth() * 100 * 4;
			float imageHeight = loading.getHeight() * 100 * 4;
			loadingImage = new MenuObject(imageWidth, imageHeight, loading);
			// TODO: get button information from loading
			button = true;
		} else {
			// no valid loading handler provided, get default
			LoadingHandler temp = AppLogic.texture.getLoadingList().get(0);
			float imageWidth = temp.getWidth() * 100 * 4;
			float imageHeight = temp.getHeight() * 100 * 4;
			loadingImage = new MenuObject(imageWidth, imageHeight, temp);
			// TODO: get button information from loading
			button = true;
		}

		objects.add(loadingImage);

		if (button) {
			Button continueB = new Button(p.width / 2, buttonWidth, buttonHeight, continueGame);
			objects.add(continueB);
		}

		constructMenu();
	}

	@Override
	public void drawPageView(float scale) {

		if (objects.size() > 0) {

			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate(PApplet.radians(angle)); // rotate the page

			// draw the images and buttons
			p.imageMode(CENTER);
			float objectYPosition = -pageMenu.getHeight() / 2;
			for (MenuObject object : objects) {
				objectYPosition += buttonDistance;
				float objectHeight = object.getHeight();
				object.drawOnPage(p, 0, objectYPosition + objectHeight * 0.5f);
				objectYPosition += objectHeight;
			}

			p.popMatrix();

		} else {

			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate(PApplet.radians(angle));
			TextureCache.drawLoadingText(p);
			p.popMatrix();

		}

		if (child != null && child.isBuilt()) {
			child.drawPageView(scale);
		}
	}

	@Override
	public void draw() {
	}
	
	public void click() {
		for (MenuObject object : objects) {
			if (!(object instanceof Button)) {
				continue;
			}
			Button b = (Button) object;

			if (b.click().equals(continueGame)) {
				child = null; // remove any child menus
				AppLogic.startLevel(); // load in the next level
			}
		}
	}

	@Override
	public void activate() {
		if (!button && !alreadyUsed) { // prevent this being triggered multiple times
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level
		}
	}

}
