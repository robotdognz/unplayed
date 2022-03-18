package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetPageView extends Widget {
	public WidgetPageView(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "pageView.png");
	}

	@Override
	public void clicked() {
		editor.switchView();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (Editor.showPageView) {
			active = true;
		} else {
			active = false;
		}
	}
}
