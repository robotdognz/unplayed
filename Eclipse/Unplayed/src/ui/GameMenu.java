package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class GameMenu extends Menu {
	Game game;
	String resume = "Resume";
	String checkpoint = "Checkpoint";
	String titleScreen = "Main Menu";
	String quit = "Quit";

	public GameMenu(PApplet p, Game game) {
		super(p);
		this.game = game;
		
		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
		Button checkpointB = new Button(p.width / 2, buttonWidth, buttonHeight, checkpoint);
		Button restartB = new Button(p.width / 2, buttonWidth, buttonHeight, titleScreen);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
		buttons.add(resumeB);
		buttons.add(checkpointB);
		buttons.add(restartB);
		buttons.add(quitB);
		constructMenu();
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(resume)) {
				AppLogic.removeMenu(); // remove pause menu

			} else if (b.click().equals(checkpoint)) {
				AppLogic.game.restart(); // return to last checkpoint
				AppLogic.removeMenu(); // remove pause menu

			} else if (b.click().equals(titleScreen)) {
				AppLogic.titleScreen(); // rebuild the game

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}
}
