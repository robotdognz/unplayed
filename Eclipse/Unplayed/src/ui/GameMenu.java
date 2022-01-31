package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class GameMenu extends Menu {
	Game game;
	String resume = "Resume";
	String checkpoint = "Checkpoint";
	String restart = "Main Menu";
	String quit = "Quit";

	public GameMenu(PApplet p, Game game, AppLogic al) {
		super(p, al);
		this.game = game;
		
		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
		Button checkpointB = new Button(p.width / 2, buttonWidth, buttonHeight, checkpoint);
		Button restartB = new Button(p.width / 2, buttonWidth, buttonHeight, restart);
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
				AppLogic.menu = null; // remove pause menu

			} else if (b.click().equals(checkpoint)) {
				AppLogic.game.restart();; // return to last checkpoint
				AppLogic.menu = null; // remove pause menu

			} else if (b.click().equals(restart)) {
				al.init(); // rebuild the game

			} else if (b.click().equals(quit)) {
				al.quit(); // exit the game
			}
		}
	}
}
