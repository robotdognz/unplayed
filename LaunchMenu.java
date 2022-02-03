package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	Game game;
	String newGame = "New Game";
	String editor = "Start Editor";
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
		game.emptyGame();
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(newGame)) {
				AppLogic.newGame();
			} else if (b.click().equals(editor)) {

//				game.emptyGame();
				AppLogic.toggleEditor();

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}

	@Override
	public void activate() {
		if (!alreadyUsed) {
			alreadyUsed = true;
			child = null; // remove any child menus
//			game.emptyGame();
		}
	}
}
