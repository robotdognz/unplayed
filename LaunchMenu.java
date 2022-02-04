package ui;

import game.AppLogic;
import game.Game;
import objects.Rectangle;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	Game game;
	String newGame = "New Game";
	String editor = "Level Editor";
	String quit = "Quit";

	boolean alreadyUsed = false;

	public LaunchMenu(PApplet p) {
		super(p);
		this.game = AppLogic.game;

		Button demoB = new Button(p.width / 2, buttonWidth, buttonHeight, newGame);
		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
		buttons.add(demoB);
		buttons.add(editorB);
		buttons.add(quitB);
		constructMenu();
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(newGame)) {
				child = null; // clear any existing menus
				AppLogic.newGame();
			} else if (b.click().equals(editor)) {
				if (!(child instanceof DeveloperMenu)) {
					// remove old menus
					child = null;
					// build new dev menu
					Menu temp = new DeveloperMenu(p);
					Rectangle pageArea = game.getPageView().getFullArea();

					temp.buldPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea,
							game.getPageView().getPageCamera());
					AppLogic.addMenu(temp);
				} else {
					AppLogic.previousMenu();
				}

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}

}
