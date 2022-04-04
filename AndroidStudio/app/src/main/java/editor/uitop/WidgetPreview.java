package editor.uitop;

import camera.GameCamera;
import controllers.PlayerControl;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import game.PageView;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPreview extends Widget {
	boolean previousStatus = false;
	PageView pageView;

	public WidgetPreview(PApplet p, Editor editor, Toolbar parent) {
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
			AppLogic.controller = new PlayerControl(p, AppLogic.game);
			pageView.recalculatePageViewObjects();
			pageView.updateVisiblePages();
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// only works if there is at least one page in the page view
		if (pageView.getPageCount() > 0) {
			available = true;
		} else {
			available = false;
		}
	}
}
