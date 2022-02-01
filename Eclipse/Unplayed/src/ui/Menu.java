package ui;

import java.util.ArrayList;

import camera.PageViewCamera;
import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.*;

public abstract class Menu {
	private PApplet p;
	protected AppLogic al;
	protected float buttonHeight = 0;
	protected float buttonWidth = 0;
	protected float menuTopY = 0;
	protected float menuCenterX = 0;
	protected float menuWidth = 0;
	protected float menuHeight = 0;
	protected float buttonDistance = 0;
	protected ArrayList<Button> buttons = new ArrayList<Button>();

	// page view menu
	protected Rectangle pageMenu;
	PVector position;
	float angle = 0;
	// Page corners relative to center, used to check if the page is on screen
	PVector topLeft;
	PVector topRight;
	PVector bottomLeft;
	PVector bottomRight;

	public Menu(PApplet p, AppLogic al) {
		this.p = p;
		this.al = al;
		buttonWidth = p.width / 2.88f; // 500
		buttonHeight = p.width / 7.2f; // 200
	}

	protected void constructMenu() {
		// get's called in the child class constructor
		// create basic menu
		menuCenterX = p.width / 2;
		menuWidth = p.width / 2.182f; // 660
		buttonDistance = p.width / 18; // 80
		menuHeight = buttonDistance + (buttonHeight + buttonDistance) * buttons.size();
		menuTopY = p.height / 2 - menuHeight / 2;
	}

	public void buldPageMenu(PVector cameraCenter, Rectangle pageArea, PageViewCamera camera) {
		float pageWidth = 600;
		float pageHeight = 800;

		float offset = 200;

		// figure out edge of page area that cameraCenter is closest to
		float leftDiff = Math.abs(cameraCenter.x - pageArea.getTopLeft().x) + camera.getSideAreaPadding();
		float rightDiff = Math.abs(pageArea.getBottomRight().x - cameraCenter.x) + camera.getSideAreaPadding();
//		float topDiff = Math.abs(cameraCenter.y - pageArea.getTopLeft().y) + camera.getSideAreaPadding();
//		float bottomDiff = Math.abs(pageArea.getBottomRight().y - cameraCenter.y) + camera.getBottomAreaPadding();

		position = new PVector(0, 0);

		if (leftDiff <= rightDiff) { // && leftDiff < topDiff && leftDiff < bottomDiff) {
			// left
			position = new PVector(cameraCenter.x - leftDiff - (pageWidth / 2) - offset, cameraCenter.y);

		} else { // if (rightDiff < leftDiff) { // && rightDiff < topDiff && rightDiff <
					// bottomDiff) {
			// right
			position = new PVector(cameraCenter.x + rightDiff + (pageWidth / 2) + offset, cameraCenter.y);

		}
//		} else if (topDiff < leftDiff && topDiff < rightDiff && topDiff < bottomDiff) {
//			// top
//			position = new PVector(cameraCenter.x, cameraCenter.y - topDiff);
//
//		} else {
//			// bottom
//			position = new PVector(cameraCenter.x, cameraCenter.y + bottomDiff);
//
//		}

		// create page view menu
		pageMenu = new Rectangle(position.x - pageWidth / 2, position.y - pageHeight / 2, pageWidth, pageHeight);
		updateCorners();
	}

	public void drawPageView() {
		p.noStroke();
		p.fill(150);
		p.rectMode(CORNER);
		p.rect(pageMenu.getX(), pageMenu.getY(), pageMenu.getWidth(), pageMenu.getHeight());
		// TODO: add final logic to this method
	}

	public void draw() {
		p.noStroke();
		p.fill(150);
		p.rectMode(CORNER);
		p.rect(menuCenterX - menuWidth / 2, menuTopY, menuWidth, menuHeight);
		// draw the buttons
		for (int i = 0; i < buttons.size(); i++) {
			float y = menuTopY + buttonDistance + (buttonHeight + buttonDistance) * i + buttonHeight / 2;
			buttons.get(i).draw(p, y);
		}
	}

	public void hover(PVector lastTouch) {
		for (Button b : buttons) {
			b.hover(lastTouch);
		}
	}

	public void click() {
	}

	// --------------update the corner PVectors---------------
	private void updateCorners() {
		if (topLeft == null) {
			// Initialize
			topLeft = new PVector();
			topRight = new PVector();
			bottomLeft = new PVector();
			bottomRight = new PVector();
		}
		// set values
		topLeft.x = 0 - (pageMenu.getWidth() / 2);
		topLeft.y = 0 - (pageMenu.getHeight() / 2);
		topRight.x = 0 + (pageMenu.getWidth() / 2);
		topRight.y = 0 - (pageMenu.getHeight() / 2);
		bottomLeft.x = 0 - (pageMenu.getWidth() / 2);
		bottomLeft.y = 0 + (pageMenu.getHeight() / 2);
		bottomRight.x = 0 + (pageMenu.getWidth() / 2);
		bottomRight.y = 0 + (pageMenu.getHeight() / 2);
		// rotate
		topLeft.rotate(PApplet.radians(angle));
		topRight.rotate(PApplet.radians(angle));
		bottomLeft.rotate(PApplet.radians(angle));
		bottomRight.rotate(PApplet.radians(angle));
		// translate
		topLeft.x += position.x;
		topLeft.y += position.y;
		topRight.x += position.x;
		topRight.y += position.y;
		bottomLeft.x += position.x;
		bottomLeft.y += position.y;
		bottomRight.x += position.x;
		bottomRight.y += position.y;
	}

	// get the bounding box edges for the page
	public float getLeftmostPoint() {
		return Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
	}

	public float getRightmostPoint() {
		return Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
	}

	public float getTopmostPoint() {
		return Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
	}

	public float getBottommostPoint() {
		return Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));
	}

	// ----------is this page off camera------------

	public boolean leftOf(float x) {
		if (topLeft.x > x) {
			return false;
		}
		if (topRight.x > x) {
			return false;
		}
		if (bottomLeft.x > x) {
			return false;
		}
		if (bottomRight.x > x) {
			return false;
		}
		return true;
	}

	public boolean rightOf(float x) {
		if (topLeft.x < x) {
			return false;
		}
		if (topRight.x < x) {
			return false;
		}
		if (bottomLeft.x < x) {
			return false;
		}
		if (bottomRight.x < x) {
			return false;
		}
		return true;
	}

	public boolean above(float y) {
		if (topLeft.y > y) {
			return false;
		}
		if (topRight.y > y) {
			return false;
		}
		if (bottomLeft.y > y) {
			return false;
		}
		if (bottomRight.y > y) {
			return false;
		}
		return true;
	}

	public boolean below(float y) {
		if (topLeft.y < y) {
			return false;
		}
		if (topRight.y < y) {
			return false;
		}
		if (bottomLeft.y < y) {
			return false;
		}
		if (bottomRight.y < y) {
			return false;
		}
		return true;
	}
}
