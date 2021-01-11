package ui;

//import camera.FreeCamera;
//import camera.GameCamera;
import editor.uitop.WidgetPauseMenu;
import game.Game;
import game.AppLogic;
import processing.core.PApplet;

public class PauseMenu extends Menu {
	Game game;
	WidgetPauseMenu m; //TODO rename this
	String resume = "Resume";
	String editor = "Toggle Editor";
	String restart = "Restart";
	String quit = "Quit and Purge";

	public PauseMenu(PApplet p, Game game, AppLogic al, WidgetPauseMenu m) {
		super(p, al);
		this.game = game;
		this.m = m;
		menuCenterX = p.width / 2;

		buttonHeight = p.width / 7.2f; // 200
		float buttonWidth = p.width / 2.88f; // 500
		menuWidth = p.width / 2.182f; // 660
		buttonDistance = p.width / 18; // 80
		Button resumeB = new Button(p.width / 2, buttonWidth, buttonHeight, resume);
		Button editorB = new Button(p.width / 2, buttonWidth, buttonHeight, editor);
		Button restartB = new Button(p.width / 2, buttonWidth, buttonHeight, restart);
		Button quitB = new Button(p.width / 2, buttonWidth, buttonHeight, quit);
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
//				m.setActive(false); // change status of pause widget
				al.gPaused = m.getPreviousStatus();
				al.menu = null; // remove pause menu
			} else if (b.click().equals(editor)) {
//				al.editorToggle = !al.editorToggle;
//				m.setActive(false); // change status of widget
//				al.gPaused = m.getPreviousStatus();
//				al.menu = null; // remove pause menu
				al.toggleEditor();
				
				
				
//				if (!app.editorToggle) {
//					game.camera = new GameCamera();
//				} else {
//					game.camera = new FreeCamera();
//				}
			} else if (b.click().equals(restart)) {
				al.init(); // rebuild the game
			} else if (b.click().equals(quit)) {
				al.quitPurge(); // exit the game
			}
		}
	}
}
