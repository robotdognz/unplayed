package editor.uitop;

import camera.FreeCamera;
import camera.GameCamera;
import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import game.PageView;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPlayMode extends Widget {
	boolean previousStatus = false;
	PageView pageView;

	public WidgetPlayMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "Pause.png");
		closeAfterSubWidget = true;
		pageView = AppLogic.game.getPageView();
	}

	@Override
	public void clicked() {
		if (active) {
			editor.camera = new FreeCamera();
		} else {
			editor.camera = new GameCamera();
			AppLogic.toggleEditor();
			if (AppLogic.game.player != null) {
				editor.controller = new PlayerControl(p, AppLogic.game);
				pageView.updateVisiblePages();
			}
			if (!Editor.showPageView) { // if we are not on the page view
				editor.switchView();
			}
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.camera instanceof FreeCamera || !Editor.showPageView) {
			active = false;
			if (editor.camera instanceof GameCamera) {
				editor.camera = new FreeCamera();
			}
		} else {
			active = true;
		}
	}
}
