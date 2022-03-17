package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import game.AppLogic;
import game.Game;
import processing.core.PApplet;

public class DeveloperMenu extends Menu {
	Game game;
	String editor = "Start Editor";
	String folder = "Load Level";
	String back = "Back";

	boolean loadingFile = false; // true if this menu should start loading a campaign from a file
	boolean askedForPermission = false; // true if this menu has asked for file permissions
//	boolean alreadyUsed = false;

	public DeveloperMenu(PApplet p) {
		super(p);
		this.game = AppLogic.game;

		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button folderB = new Button(p.width / 2, buttonWidth, buttonHeight, folder);
		Button backB = new Button(p.width / 2, buttonWidth, buttonHeight, back);
		objects.add(editorB);
		objects.add(folderB);
		objects.add(backB);
		constructMenu();
	}

	@Override
	public void click() {
		for (MenuObject object : objects) {
			if (!(object instanceof Button)) {
				continue;
			}
			Button b = (Button) object;

			if (b.click().equals(editor)) {
				if (!getPermission()) {
					return;
				}

				game.emptyGame();
				AppLogic.toggleEditor();
			} else if (b.click().equals(folder)) {
				if (!getPermission()) {
					return;
				}

				AppLogic.files.createLoadFile();
				loadingFile = true;
			} else if (b.click().equals(back)) {
				AppLogic.previousMenu();
			}
		}
	}

	@Override
	public void activate() {
		// I'm using this against it's original purpose. There is no step method, so I'm
		// going to use this instead

//		// check and get permissions
//		if (!askedForPermission) {
//			getPermission();
//			askedForPermission = true;
//		}

		if (loadingFile) {
			if (AppLogic.files.hasUri()) {
				File selectedFile = new File(AppLogic.files.getPath()); // selected file path
				String fileName = selectedFile.getName(); // get current file on it's own
				String folder = selectedFile.toString().replace(fileName, ""); // get base folder on it's own

				// print for testing
				PApplet.print("\n");
				PApplet.print(fileName + "\n");
				PApplet.print(folder + "\n");
				PApplet.print("\n");

				// get all files in folder
				File levelPath = new File(folder);
				File[] absoluteFiles = levelPath.listFiles();
				ArrayList<File> levels = new ArrayList<File>(Arrays.asList(absoluteFiles));
				Collections.sort(levels);

				// remove levels before selected level
				while (true) {
					if (levels.get(0).equals(selectedFile)) {
						break;
					} else {
						levels.remove(0);
					}
				}

				// print remaining levels, for testing
				for (File level : levels) {
					PApplet.print(level.toString() + "\n");
				}

				// pass found levels to AppLogic
				AppLogic.setLevels(levels);

				// end loading
				loadingFile = false;

				child = null; // clear any existing menus
				AppLogic.newGame(); // start game
			}
		}
	}
}
