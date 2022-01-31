package ui;

import java.util.ArrayList;

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

		// create page view menu
		pageMenu = new Rectangle(0, 0, 600, 800); // TODO: calculate actual dimensions
	}
	
	// get the bounding box edges for the page
	public float getLeftmostPoint() {
//		return Math.min(Math.min(topLeft.x, topRight.x), Math.min(bottomLeft.x, bottomRight.x));
		return pageMenu.getTopLeft().x;
	}

	public float getRightmostPoint() {
//		return Math.max(Math.max(topLeft.x, topRight.x), Math.max(bottomLeft.x, bottomRight.x));
		return pageMenu.getBottomRight().x;
	}

	public float getTopmostPoint() {
//		return Math.min(Math.min(topLeft.y, topRight.y), Math.min(bottomLeft.y, bottomRight.y));
		return pageMenu.getTopLeft().y;
	}

	public float getBottommostPoint() {
//		return Math.max(Math.max(topLeft.y, topRight.y), Math.max(bottomLeft.y, bottomRight.y));
		return pageMenu.getBottomRight().y;
	}
	
	public void drawPageView() {
		p.noStroke();
		p.fill(150);
		p.rectMode(CENTER);
		p.rect(pageMenu.getX(), pageMenu.getY(), pageMenu.getWidth(), pageMenu.getHeight());
		//TODO: add final logic to this method
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
}
