package ui;

import game.AppLogic;
import game.Game;
import objects.Rectangle;
import processing.core.PApplet;

public class LaunchMenu extends Menu {
	protected Game game;
	protected String continueGame = "Continue";
	protected String newGame = "New Game";
	protected String editor = "Level Editor";
	protected String quit = "Quit";
	protected boolean alreadyUsed = false;

	public LaunchMenu(PApplet p) {
		super(p);
		this.game = AppLogic.game;

		Button newGameB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, newGame);
		Button editorB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, editor);
		Button quitB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, quit);

		AppLogic.getSaveGame();
		if (AppLogic.savedLevel > 0) {
			Button continueB = new Button(p.width * 0.5f, buttonWidth, buttonHeight, continueGame);
			objects.add(continueB);
		}
		objects.add(newGameB);
		objects.add(editorB);
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

			if (b.click().equals(continueGame)) {
				child = null; // clear any existing menus
				AppLogic.getLevels(); // load default levels
				AppLogic.continueGame();
			} else if (b.click().equals(newGame)) {
				child = null; // clear any existing menus
				AppLogic.getLevels(); // load default levels
				AppLogic.newGame(); // start game
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
