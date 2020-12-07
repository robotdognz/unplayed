package ui;

import camera.FreeCamera;
import camera.GameCamera;
import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class PlayModeWidget extends Widget {
	boolean previousStatus = false;

	public PlayModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "Pause.png");
		closeAfterSubWidget = true;
	}

	public void clicked() {
		if (active) {
			editor.eCamera = new FreeCamera();
		} else {
			editor.eCamera = new GameCamera();
			editor.eController = new PlayerControl(p, editor.eGame);
		}
	}

	public void updateActive() {
		if (editor.eCamera instanceof FreeCamera) {
			active = false;
		} else {
			active = true;
		}
	}
}
