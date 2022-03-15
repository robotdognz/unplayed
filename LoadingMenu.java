package ui;

import static processing.core.PConstants.CENTER;
import game.AppLogic;
import handlers.ButtonHandler;
import handlers.LoadingHandler;
import processing.core.PApplet;

public class LoadingMenu extends Menu {
	String continueGame = "Continue";
	boolean button;
	boolean fullPage;
	boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	private int shadow; // the relative amount to offset the shadow by

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
		this.angleOffset = 10;

		MenuObject loadingImage;

		if (loading != null) {
			// we have a valid loading handler to build from
			float imageWidth = loading.getWidth() * 100 * 3;
			float imageHeight = loading.getHeight() * 100 * 3;
			loadingImage = new MenuObject(imageWidth, imageHeight, loading);
			this.button = loading.hasButton();
			this.fullPage = loading.fullPage();
		} else {
			// no valid loading handler provided, get default
			LoadingHandler temp = AppLogic.texture.getLoadingList().get(0);
			float imageWidth = temp.getWidth() * 100 * 3;
			float imageHeight = temp.getHeight() * 100 * 3;
			loadingImage = new MenuObject(imageWidth, imageHeight, temp);
			this.button = temp.hasButton();
			this.fullPage = temp.fullPage();
		}

		objects.add(loadingImage);

		if (button) {
//			Button continueB = new Button(p.width / 2, buttonWidth, buttonHeight, continueGame);
//			objects.add(continueB);

			ButtonHandler temp = AppLogic.texture.getButtonList().get(1);
			Button continueBwImage = new Button(temp, p.width / 2, continueGame);
			objects.add(continueBwImage);
		}

		if (fullPage) {
			this.shadow = 9;
		}

		constructMenu();
	}

	protected void setAngle(float range) {
//		if (previousTilt) {
//			angle = angleOffset;
//			previousTilt = !previousTilt;
//		} else {
//			angle = -angleOffset;
//			previousTilt = !previousTilt;
//		}
//
//		angle += (float) (Math.random() * range) - (range / 2);
	}

	@Override
	public void drawPageView(float scale) {

		if (fullPage) {
			// draw full page loading screen with shadow and button on the page

			if (objects.size() < 1) {
				return;
			}

			MenuObject image = objects.get(0);

			float width = image.getWidth();
			float height = image.getHeight();

			// draw the page
			p.pushMatrix();
			p.translate(position.x, position.y);

			// draw the shadow
			p.translate(shadow, shadow);
			p.fill(0, 40);
			p.noStroke();
			p.rectMode(CENTER);
			p.rotate(PApplet.radians(angle)); // rotate the page
			p.rect(0, 0, width, height); // draw the shadow
			p.rotate(PApplet.radians(-angle)); // rotate the page
			p.translate(-shadow, -shadow);
			p.rotate(PApplet.radians(angle)); // rotate the page

			// draw the page background
			p.fill(240);
			p.rect(0, 0, width, height);
			image.drawOnPage(p, 0, 0);

			// end drawing
			p.popMatrix();

		} else {
			// draw transparent background loading screen with button below image

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
		}

		if (child != null && child.isBuilt()) {
			child.drawPageView(scale);
		}
	}

	@Override
	public void draw() {
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
				AppLogic.setStartLevel(); // trigger loading in the next level
			}
		}
	}

	@Override
	public void activate() {
		// shouldn't need to press the 'continue' button if in the editor, to prevent
		// editor locking up

		// alreadyUsed prevents this being triggered multiple times
		if (!button && !alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level
		}
	}

}
