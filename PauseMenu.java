package ui;

//import camera.FreeCamera;
//import camera.GameCamera;
import editor.uitop.WidgetPauseMenu;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;

public class PauseMenu extends Menu {
	Game game;
	AppLogic app;
	WidgetPauseMenu m;
	String resume = "Resume";
	String editor = "Toggle Editor";
	String restart = "Restart";
	String quit = "Quit and Purge";

	public PauseMenu(PApplet p, Game game, AppLogic app, WidgetPauseMenu m) {
		super(p);
		this.game = game;
		this.app = app;
		this.m = m;
		menuCenterX = p.width / 2;
		menuWidth = 660;
		buttonHeight = 200;
		buttonDistance = 80;
		Button resumeB = new Button(p.width / 2, 500, buttonHeight, resume);
		Button editorB = new Button(p.width / 2, 500, buttonHeight, editor);
		Button restartB = new Button(p.width / 2, 500, buttonHeight, restart);
		Button quitB = new Button(p.width / 2, 500, buttonHeight, quit);
		buttons.add(resumeB);
		buttons.add(editorB);
		buttons.add(restartB);
		buttons.add(quitB);
		height();
		menuTopY = p.height / 2 - menuHeight / 2;
	}

	@Override
	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(resume)) { // resume the game if resume button pressed
				m.setActive(false); // change status of pause widget
				app.gPaused = m.getPreviousStatus();
				app.menu = null; // remove pause menu
			} else if (b.click().equals(editor)) {
				app.editorToggle = !app.editorToggle;
				m.setActive(false); // change status of widget
				app.gPaused = m.getPreviousStatus();
				app.menu = null; // remove pause menu
//				if (!app.editorToggle) {
//					game.camera = new GameCamera();
//				} else {
//					game.camera = new FreeCamera();
//				}
			} else if (b.click().equals(restart)) {
				app.init(); // rebuild the game
			} else if (b.click().equals(quit)) {
				app.quitPurge(); // exit the game
			}
		}
	}
}
