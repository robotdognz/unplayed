package ui;

import java.util.ArrayList;

import game.AppLogic;
import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.*;

public abstract class Menu {
	private PApplet p;
	protected AppLogic al;
	protected float menuTopY = 0;
	protected float menuCenterX = 0;
	protected float menuWidth = 0;
	protected float buttonHeight = 0;
	protected float buttonDistance = 0;
	protected float buttonWidth = 0;
	protected ArrayList<Button> buttons = new ArrayList<Button>();
	protected float menuHeight = 0;
	
	public Menu(PApplet p, AppLogic al) {
		this.p = p;
		this.al = al;
		buttonWidth = p.width / 2.88f; // 500
	}

	protected void constructMenu() {
		// get's called in the child class constructor
		menuCenterX = p.width / 2;
		buttonHeight = p.width / 7.2f; // 200
		menuWidth = p.width / 2.182f; // 660
		buttonDistance = p.width / 18; // 80
		
		menuHeight = buttonDistance + (buttonHeight + buttonDistance) * buttons.size();
		
		menuTopY = p.height / 2 - menuHeight / 2;
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
