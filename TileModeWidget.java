package ui;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;

public class TileModeWidget extends Widget {
	public TileModeWidget(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "colider.png");
		imageInactive = null;
		active = true;
	}

	public void clicked() {
		editor.eType = Editor.editorType.TILE;
	}

	public void updateActive() {
		if (editor.eType == Editor.editorType.TILE) {
			active = true;
		} else {
			active = false;
		}
	}
}