package ui;

import game.AppLogic;
import game.Game;
import handlers.ButtonHandler;
import processing.core.PApplet;

public class GameMenu extends Menu {
	private final Game game;
	private final String resume = "Resume";
	private final String edit = "Edit Level";
	private final String quit = "Quit";

	public GameMenu(PApplet p, Game game) {
		super(p);
		this.game = game;

		ButtonHandler resumeHandler = AppLogic.texture.getButtonList().get(5);
		Button resumeButton = new Button(resumeHandler, p.width * 0.5f, resume);

		ButtonHandler editHandler = AppLogic.texture.getButtonList().get(6);
		Button editButton = new Button(editHandler, p.width * 0.5f, edit);

		ButtonHandler quitHandler = AppLogic.texture.getButtonList().get(4);
		Button quitButton = new Button(quitHandler, p.width * 0.5f, quit);

		objects.add(resumeButton);
		objects.add(editButton);
		objects.add(quitButton);

		constructMenu();
	}

	@Override
	public void onBackPressed() {
		// go to title screen if in pause menu
		game.emptyGame();
		AppLogic.titleScreen(); // open title screen menu
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
				case edit:
					AppLogic.startEditorFromGame(); //toggleEditor(); // enable the editor

					break;
				case quit:
					game.emptyGame();
					AppLogic.titleScreen(); // open title screen menu

					break;
			}
		}
	}
}
