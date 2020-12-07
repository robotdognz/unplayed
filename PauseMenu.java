package menus;

import camera.FreeCamera;
import camera.GameCamera;
import game.Game;
import game.GameLogic;
import processing.core.PApplet;
import ui.MenuWidget;

public class PauseMenu extends Menu {
	Game game;
	GameLogic gl;
	MenuWidget m;
	String resume = "Resume";
	String editor = "Toggle Editor";
	String restart = "Restart";

	public PauseMenu(PApplet p, Game game, GameLogic gameLogic, MenuWidget m) {
		super(p);
		this.game = game;
		this.gl = gameLogic;
		this.m = m;
		menuCenterX = p.width / 2;
		menuWidth = 660;
		buttonHeight = 200;
		buttonDistance = 80;
		Button resumeB = new Button(p.width / 2, 500, buttonHeight, resume);
		Button editorB = new Button(p.width / 2, 500, buttonHeight, editor);
		Button restartB = new Button(p.width / 2, 500, buttonHeight, restart);
		buttons.add(resumeB);
		buttons.add(editorB);
		buttons.add(restartB);
		height();
		menuTopY = p.height / 2 - menuHeight / 2;
	}

	public void click() {
		for (Button b : buttons) {
			if (b.click().equals(resume)) { // resume the game if resume button pressed
				m.setActive(false); // change status of pause widget
				gl.gPaused = m.getPreviousStatus();
				gl.menu = null; // remove pause menu
			} else if (b.click().equals(editor)) {
				gl.editorToggle = !gl.editorToggle;
				m.setActive(false); // change status of widget
				gl.gPaused = m.getPreviousStatus();
				gl.menu = null; // remove pause menu
				if (!gl.editorToggle) {
					game.camera = new GameCamera();
				} else {
					game.camera = new FreeCamera();
				}
			} else if (b.click().equals(restart)) {
				gl.init(); // rebuild the game 
			}
		}
	}
}
