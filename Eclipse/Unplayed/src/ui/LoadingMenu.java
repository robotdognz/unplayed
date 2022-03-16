package ui;

import static processing.core.PConstants.CENTER;

import camera.Camera;
import camera.PageViewCamera;
import game.AppLogic;
import handlers.ButtonHandler;
import handlers.LoadingHandler;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class LoadingMenu extends Menu {
	private String continueGame = "Continue";
	private boolean hasButton; // has 'continue' button
	private boolean hasShadow; // loading screen image should have a shadow
	private int shadow; // the relative amount to offset the shadow by

	private boolean alreadyUsed = false;
	// this boolean prevents this loading menu from infinitely restarting the level
	// each frame. With this it is only used once

	public LoadingMenu(PApplet p, LoadingHandler loading) {
		super(p);
		this.angleOffset = 7; // 10

		MenuObject loadingImage;

		if (loading != null) {
			// we have a valid loading handler to build from
			float imageWidth = loading.getWidth() * 100 * 3;
			float imageHeight = loading.getHeight() * 100 * 3;
			loadingImage = new MenuObject(imageWidth, imageHeight, loading);
			this.hasButton = loading.hasButton();
			this.hasShadow = loading.hasShadow();
		} else {
			// no valid loading handler provided, get default
			LoadingHandler temp = AppLogic.texture.getLoadingList().get(0);
			float imageWidth = temp.getWidth() * 100 * 3;
			float imageHeight = temp.getHeight() * 100 * 3;
			loadingImage = new MenuObject(imageWidth, imageHeight, temp);
			this.hasButton = temp.hasButton();
			this.hasShadow = temp.hasShadow();
		}

		this.objects.add(loadingImage);

		if (this.hasButton) {
			// setup the 'continue' button
			ButtonHandler temp = AppLogic.texture.getButtonList().get(1);
			Button continueBwImage = new Button(temp, p.width / 2, this.continueGame);
			objects.add(continueBwImage);
		}

		constructMenu();
	}

//	@Override
//	protected void setAngle(float range) {
//		// remove this override method to re-enable random angle
//	}

	@Override
	protected void constructMenu() {
		// setup all the menu dimensions and parameters

		shadow = 9;
		menuCenterX = p.width / 2;
		menuWidth = 0;
		menuHeight = 0;
		float largestWidth = 0;
		for (MenuObject object : objects) {
			menuHeight += object.getHeight();
			if (object.getWidth() > largestWidth) {
				largestWidth = object.getWidth();
			}
		}

		// add spaces between objects
		menuHeight += buttonDistance * (objects.size() - 1);

		menuWidth += largestWidth;
		menuTopY = p.height / 2 - menuHeight / 2;
	}

	@Override
	protected void setupMenuContents() {
		// called by buildMenu() which is in the parent class

		// create page view menu and buttons
		pageMenu = new Rectangle(0 - (menuWidth * 0.5f), 0 - (menuHeight * 0.5f), menuWidth, menuHeight);

		float objectYPosition = pageMenu.getY();
		for (MenuObject object : objects) {
			float objectHeight = object.getHeight();

			if (object instanceof Button) {
				Button button = (Button) object;
				button.setupPageButton(pageMenu.getTopLeft().x + pageMenu.getWidth() / 2,
						objectYPosition + objectHeight * 0.5f);
			}

			objectYPosition += buttonDistance;
			objectYPosition += objectHeight;
		}

		setAngle(0);
		updateCorners();
		built = true;
	}

	@Override
	public void drawInWorld(float scale) {

		p.pushMatrix();
		p.translate(position.x, position.y);

		// setup drawing for the loading screen
		p.imageMode(CENTER);
		float objectYPosition = -pageMenu.getHeight() * 0.5f;

		// get loading screen image and important values
		MenuObject image = objects.get(0);
		float imageWidth = image.getWidth();
		float imageHeight = image.getHeight();

		if (hasShadow) {
			// draw a shadow under the loading screen image
			p.translate(shadow, shadow);
			p.fill(0, 40);
			p.noStroke();
			p.rectMode(CENTER);
			p.rotate(PApplet.radians(angle)); // rotate the image
			p.rect(0, objectYPosition + imageHeight * 0.5f, imageWidth, imageHeight); // draw the shadow
			p.rotate(PApplet.radians(-angle)); // rotate the image back
			p.translate(-shadow, -shadow);
			p.rotate(PApplet.radians(angle)); // rotate the image

			// draw temp image background TODO: remove this
			p.fill(240);
			p.rect(0, objectYPosition + imageHeight * 0.5f, imageWidth, imageHeight);

		} else {
			p.rotate(PApplet.radians(angle)); // rotate the image
		}

		// draw the image
		image.drawOnPage(p, 0, objectYPosition + imageHeight * 0.5f);
		objectYPosition += imageHeight; // update drawing position
		
		p.rotate(PApplet.radians(-angle)); // rotate the image back

		// loading screen button
		if (hasButton) {
			// assume there will be only one button
			MenuObject button = objects.get(1);
			objectYPosition += buttonDistance;
			float buttonHeight = button.getHeight();
			button.drawOnPage(p, 0, objectYPosition + buttonHeight * 0.5f);
		}

		p.popMatrix();

		if (child != null && child.isBuilt()) {
			child.drawInWorld(scale);
		}
	}

	@Override
	public void drawOnTop() {
		// loading screens never get drawn on top
	}

	@Override
	public void hover(PVector lastTouch) {
		// interacting with in world menu is assumed,
		// loading screens never get drawn on top

		if (!hasButton) {
			// if this loading screen has no 'continue' button, exit method
			return;
		}

		PVector point = PageViewCamera.screenToLevel(lastTouch.x, lastTouch.y);
		point.x -= position.x;
		point.y -= position.y;
//		point.rotate(PApplet.radians(-angle));
		
		MenuObject button = objects.get(1);
		((Button) button).hoverPage(point); // levelTouch

//		for (MenuObject object : objects) {
//			if (!(object instanceof Button)) {
//				continue;
//			}
//			((Button) object).hoverPage(point); // levelTouch
//		}

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
		// alreadyUsed prevents this being triggered multiple times
		if (!hasButton && !alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level
		}
	}

	@Override
	public void skipLoadingScreen() {
		// shouldn't need to press the 'continue' button if in the editor, to prevent
		// editor locking up. So this is called instead of activate().

		// alreadyUsed prevents this being triggered multiple times
		if (!alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
			AppLogic.startLevel(); // load in the next level
		}
	}

}
