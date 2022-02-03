package ui;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class DeveloperMenu extends Menu {
	Game game;
	String editor = "Start Editor";
	String folder = "Load Folder";
	String back = "Back";

	boolean alreadyUsed = false;

	public DeveloperMenu(PApplet p) {
		super(p);
		this.game = AppLogic.game;

		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button folderB = new Button(p.width / 2, buttonWidth, buttonHeight, folder);
		Button backB = new Button(p.width / 2, buttonWidth, buttonHeight, back);
		buttons.add(editorB);
		buttons.add(folderB);
		buttons.add(backB);
		constructMenu();
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(editor)) {
				AppLogic.toggleEditor();
			} else if (b.click().equals(folder)) {
				// TODO: load folder of levels and play it as a campaign

			} else if (b.click().equals(back)) {
				AppLogic.previousMenu();
			}
		}
	}

}
