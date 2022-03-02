package ui;

import static processing.core.PConstants.CENTER;
import game.AppLogic;
import handlers.LoadingHandler;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
//	private LoadingHandler loading = null;
	String continueGame = "Continue";
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
//		this.loading = loading;
		this.angleOffset = 10;
		if (loading != null) {
			MenuObject loadingImage = new MenuObject(loading.getWidth(), loading.getHeight(), loading);
			objects.add(loadingImage);
			// TODO: get button information from loading
			Button continueB = new Button(p.width / 2, buttonWidth, buttonHeight, continueGame);
			objects.add(continueB);
		} else {

			if (AppLogic.texture.getLoadingList().size() > 0) {
				LoadingHandler temp = AppLogic.texture.getLoadingList().get(0);
				MenuObject loadingImage = new MenuObject(temp.getWidth(), temp.getHeight(), temp);
				objects.add(loadingImage);
				// TODO: get button information from loading
//				Button continueB = new Button(p.width / 2, buttonWidth, buttonHeight, continueGame);
//				objects.add(continueB);
			}
		}

		constructMenu();
	}

	@Override
	public void drawPageView(float scale) {

		if (objects.size() > 0) {

			p.pushMatrix();
			p.translate(position.x, position.y);
			p.rotate(PApplet.radians(angle)); // rotate the page
			// draw the buttons
			// draw the buttons
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

//			p.pushMatrix();
//			p.translate(position.x, position.y);
//			p.rotate(PApplet.radians(angle));
//			if (loading != null) {
//				p.imageMode(CENTER);
//				loading.draw(p.g, 0, 0, 3);
//			} else {
//				TextureCache.drawLoadingText(p);
//			}
//			p.popMatrix();

		}

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
