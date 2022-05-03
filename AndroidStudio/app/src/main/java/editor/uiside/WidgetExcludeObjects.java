package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeObjects extends Widget {
	EditorSide toolbar;

	public WidgetExcludeObjects(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "colider.png");

		closeAfterSubWidget = true;
	}
	
	@Override
	public void clicked() {
		editor.setRemoveObjects();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			available = true;
			active = editor.removingObjects();
		} else {
			available = false;
		}
	}
}
