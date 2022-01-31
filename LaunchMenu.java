package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	Game game;
	String newGame = "New Game";
	String editor = "Start Editor";
	String quit = "Quit";

	public LaunchMenu(PApplet p, AppLogic al) {
		super(p, al);
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
				al.startGame();
			} else if (b.click().equals(editor)) {

				al.toggleEditor();

			} else if (b.click().equals(quit)) {
				al.quit(); // exit the game
			}
		}
	}
}
