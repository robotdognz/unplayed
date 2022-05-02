package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludePlayer extends Widget {
	EditorSide toolbar;

	public WidgetExcludePlayer(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "PLayerIcon.png");

		closeAfterSubWidget = true;
	}
	
	@Override
	public void clicked() {
		editor.setRemovePlayer();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			available = true;
			active = editor.removingPlayer();
		} else {
			available = false;
		}
	}
}
