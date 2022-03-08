package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class GameMenu extends Menu {
	Game game;
	String resume = "Resume";
//	String checkpoint = "Checkpoint";
	String titleScreen = "Main Menu";
	String edit = "Edit Level";
	String quit = "Quit";

	public GameMenu(PApplet p, Game game) {
		super(p);
		this.game = game;

		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
//		Button checkpointB = new Button(p.width / 2, buttonWidth, buttonHeight, checkpoint);
		Button titleScreenB = new Button(p.width / 2, buttonWidth, buttonHeight, titleScreen);
		Button editB = new Button(p.width / 2, buttonWidth, buttonHeight, edit);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
		objects.add(resumeB);
//		objects.add(checkpointB);
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

//			}else if (b.click().equals(checkpoint)) {
//				AppLogic.game.restart(); // return to last checkpoint
//				AppLogic.removeMenu(); // remove pause menu

			} else if (b.click().equals(titleScreen)) {
				game.emptyGame();
				AppLogic.titleScreen(); // open title screen menu

			} else if (b.click().equals(edit)) {
				if (!getPermission()) {
					return;
				}

				AppLogic.toggleEditor(); // enable the editor

			} else if (b.click().equals(quit)) {
				AppLogic.quit(); // exit the game
			}
		}
	}
}
