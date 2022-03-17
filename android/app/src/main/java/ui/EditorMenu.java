package ui;

import camera.FreeCamera;
import editor.uitop.WidgetPauseMenu;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;

public class EditorMenu extends Menu {
	Game game;
	WidgetPauseMenu m; // TODO rename this
	String resume = "Resume";
	String editor = "Edit Level";
	String restart = "Main Menu";
	String quit = "Reset Game";
//	String loading = "Loading";

	public EditorMenu(PApplet p, WidgetPauseMenu m) {
		super(p);
		this.game = AppLogic.game;
		this.m = m;

		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button restartB = new Button(p.width / 2, buttonWidth, buttonHeight, restart);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
//		Button loadingB = new Button(p.width / 2, buttonWidth, buttonHeight, loading);
		objects.add(resumeB);
		if (!AppLogic.editorToggle) {
			objects.add(editorB);
		}
		objects.add(restartB);
		objects.add(quitB);
//		objects.add(loadingB);
		constructMenu();
	}

	@Override
	public void click() {
		for (MenuObject object : objects) {
			if (!(object instanceof Button)) {
				continue;
			}
			Button b = (Button) object;

			if (b.click().equals(resume)) { // resume the game if resume button pressed
				AppLogic.removeMenu(); // remove pause menu
			} else if (b.click().equals(editor)) {
				AppLogic.editorToggle = !AppLogic.editorToggle;
				AppLogic.editor.resetUI();
//				AppLogic.toggleEditor();
				AppLogic.removeMenu(); // remove pause menu
				AppLogic.editor.camera = new FreeCamera(); // back to editor camera
			} else if (b.click().equals(restart)) {
				AppLogic.init(); // rebuild the game
			} else if (b.click().equals(quit)) {
				AppLogic.quitPurge(); // exit the game
			}
//			else if (b.click().equals(loading)) {
//				AppLogic.loadingScreen();
//			}
		}
	}
}