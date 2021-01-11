package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	Game game;
	String demo = "Start Demo";
	String editor = "Start Editor";
	String quit = "Quit";

	public LaunchMenu(PApplet p, Game game, AppLogic al) {
		super(p, al);
		this.game = game;
		menuCenterX = p.width / 2;

		buttonHeight = p.width / 7.2f; // 200
		float buttonWidth = p.width / 2.88f; // 500
		menuWidth = p.width / 2.182f; // 660
		buttonDistance = p.width / 18; // 80
		Button demoB = new Button(p.width / 2, buttonWidth, buttonHeight, demo);
		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
		buttons.add(demoB);
		buttons.add(editorB);
		buttons.add(quitB);
		height();
		menuTopY = p.height / 2 - menuHeight / 2;
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(demo)) {

			} else if (b.click().equals(editor)) {

				al.toggleEditor();

			} else if (b.click().equals(quit)) {
				al.quit(); // exit the game
			}
		}
	}
}
