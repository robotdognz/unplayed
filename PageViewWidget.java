package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class PageViewWidget extends Widget {
	public PageViewWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "pageView.png");
	}

	public void clicked() {
		editor.switchView();
	}

	public void updateActive() {
		if (editor.pageView) {
			active = true;
		} else {
			active = false;
		}
	}
}