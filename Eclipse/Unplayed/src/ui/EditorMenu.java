package ui;

//import camera.FreeCamera;
//import camera.GameCamera;
import editor.uitop.WidgetPauseMenu;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;

public class EditorMenu extends Menu {
	Game game;
	WidgetPauseMenu m; //TODO rename this
	String resume = "Resume";
	String editor = "Toggle Editor";
	String restart = "Main Menu";
	String quit = "Quit and Purge";
	String loading = "Loading";

	public EditorMenu(PApplet p, WidgetPauseMenu m) {
		super(p);
		this.game = AppLogic.game;
		this.m = m;
		
		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button restartB = new Button(p.width / 2, buttonWidth, buttonHeight, restart);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
		Button loadingB = new Button(p.width / 2, buttonWidth, buttonHeight, loading);
		buttons.add(resumeB);
		buttons.add(editorB);
		buttons.add(restartB);
		buttons.add(quitB);
		buttons.add(loadingB);
		constructMenu();
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(resume)) { // resume the game if resume button pressed
				AppLogic.removeMenu(); // remove pause menu
			} else if (b.click().equals(editor)) {
				AppLogic.editorToggle = !AppLogic.editorToggle;
				AppLogic.removeMenu(); // remove pause menu
			} else if (b.click().equals(restart)) {
				AppLogic.init(); // rebuild the game
			} else if (b.click().equals(quit)) {
				AppLogic.quitPurge(); // exit the game
			} else if (b.click().equals(loading)) {
				AppLogic.loadingScreen();
			}
		}
	}
}
