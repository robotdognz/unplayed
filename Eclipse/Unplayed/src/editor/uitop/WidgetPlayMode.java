package editor.uitop;

import camera.FreeCamera;
import camera.GameCamera;
import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayMode extends Widget {
	boolean previousStatus = false;

	public WidgetPlayMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "Pause.png");
		closeAfterSubWidget = true;
	}

	public void clicked() {
		if (active) {
			editor.camera = new FreeCamera();
		} else {
			editor.camera = new GameCamera();
			editor.controller = new PlayerControl(p, editor.game);
			editor.showPageView = true;
		}
	}

	public void updateActive() {
		super.updateActive();
		if (editor.camera instanceof FreeCamera || !editor.showPageView) {
			active = false;
		} else {
			active = true;
		}
	}
}
