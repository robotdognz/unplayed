package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class GameMenu extends Menu {
	Game game;
	String resume = "Resume";
	String titleScreen = "Main Menu";
	String edit = "Edit Level";
	String quit = "Quit";

	public GameMenu(PApplet p, Game game) {
		super(p);
		this.game = game;

		Button resumeB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, resume);
		Button titleScreenB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, titleScreen);
		Button editB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, edit);
		Button quitB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, quit);
		objects.add(resumeB);
		objects.add(titleScreenB);
		objects.add(editB);
		objects.add(quitB);

		constructMenu();
	}

	@Override
	public void click() {
		for (MenuObject object : objects) {
			if (!(object instanceof Button)) {
				continue;
			}
			Button b = (Button) object;

			if (b.click().equals(resume)) {
				AppLogic.removeMenu(); // remove pause menu

			} else if (b.click().equals(titleScreen)) {
				game.emptyGame();
				AppLogic.titleScreen(); // open title screen menu

			} else if (b.click().equals(edit)) {
				AppLogic.toggleEditor(); // enable the editor

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}
}
