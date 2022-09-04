package ui;

import game.AppLogic;
import game.Game;
import handlers.ButtonHandler;
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

		// continue
		AppLogic.getSaveGame();
		if (AppLogic.savedLevel > 1) {
			ButtonHandler continueButtonHandler = AppLogic.texture.getButtonList().get(5);
			Button continueButton = new Button(continueButtonHandler, p.width * 0.5f, continueGame);
			objects.add(continueButton);
		}

		// new game
		ButtonHandler newGameButtonHandler = AppLogic.texture.getButtonList().get(2);
		Button newGameButton = new Button(newGameButtonHandler, p.width * 0.5f, newGame);
		objects.add(newGameButton);

		// level editor
		ButtonHandler newEditorHandler = AppLogic.texture.getButtonList().get(3);
		Button editorButton = new Button(newEditorHandler, p.width * 0.5f, editor);
		objects.add(editorButton);

		// quit
		ButtonHandler quitHandler = AppLogic.texture.getButtonList().get(4);
		Button quitButton = new Button(quitHandler, p.width * 0.5f, quit);
		objects.add(quitButton);

		constructMenu();
	}

	@Override
	public void onBackPressed() {
		// quit if in title screen
		AppLogic.quit(); // exit the game
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

					temp.buildPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea,
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
