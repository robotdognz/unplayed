package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class GameMenu extends Menu {
	private final Game game;
	private final String resume = "Resume";
	private final String titleScreen = "Main Menu";
	private final String edit = "Edit Level";
	private final String quit = "Quit";

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

			switch (b.click()) {
				case resume:
					AppLogic.removeMenu(); // remove pause menu

					break;
				case titleScreen:
					game.emptyGame();
					AppLogic.titleScreen(); // open title screen menu

					break;
				case edit:
					AppLogic.toggleEditor(); // enable the editor

					break;
				case quit:
					AppLogic.quit(); // exit the game

					break;
			}
		}
	}
}
