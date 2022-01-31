package ui;

import game.AppLogic;
import objects.Rectangle;
import processing.core.PApplet;

public class PaperMenu extends Menu {

	private Rectangle menuPage;

	public PaperMenu(PApplet p, AppLogic al) {
		super(p, al); // Auto-generated constructor stub
		
		// default menuPage size
		menuPage = new Rectangle(0, 0, 700, 800);
	}

}
