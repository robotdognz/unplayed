package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	Game game;
	String newGame = "New Game";
	String editor = "Start Editor";
	String quit = "Quit";

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
				AppLogic.startGame();
			} else if (b.click().equals(editor)) {

				AppLogic.toggleEditor();

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}
}
