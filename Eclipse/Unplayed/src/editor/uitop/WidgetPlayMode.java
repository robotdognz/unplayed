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
		editor.camera = new GameCamera();
		AppLogic.toggleEditor();
		if (AppLogic.game.player != null) {
			editor.controller = new PlayerControl(p, AppLogic.game);
			pageView.updateVisiblePages();
		}
//		if (!Editor.showPageView) { // if we are not on the page view
//			editor.switchView();
//		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// only works if there is at least one page in the page view and there is a
		// player to control
		if (pageView.getPageCount() > 0 && AppLogic.game.player != null) {
			available = true;
		} else {
			available = false;
		}
	}
}
