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
	String folder = "Load Folder";
	String back = "Back";

	boolean loadingFile = true;

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
				game.emptyGame();
				AppLogic.toggleEditor();
			} else if (b.click().equals(folder)) {
				// TODO: load folder of levels and play it as a campaign
				AppLogic.files.createLoadFile();
				loadingFile = true;

				// load a level in a folder. Load all levels in that folder alphabetically
				// starting from the selected level. Send that list to AppLogic

			} else if (b.click().equals(back)) {
				AppLogic.previousMenu();
			}
		}
	}

	@Override
	public void activate() {
		// I'm using this against it's original purpose. There is no step method, so I'm
		// going to use this instead

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

//				for (int i = 0; i < levels.size(); i++) {
//					if (levels.get(i).equals(selectedFile)) {
//
//					}
//				}
//
//				int selectedFileIndex = -1;
//				boolean removeAfter = false;
//				for (int i = levels.size() - 1; i >= 0; i--) {
//					if (removeAfter) {
//						levels.remove(i);
//					}
//					if (levels.get(i).equals(selectedFile)) {
//						selectedFileIndex = i;
//						PApplet.print(i);
//					}
//				}

				// print remaining levels, for testing
				for (File level : levels) {
					PApplet.print(level.toString() + "\n");
				}

				loadingFile = false;
			}
		}

	}

}
