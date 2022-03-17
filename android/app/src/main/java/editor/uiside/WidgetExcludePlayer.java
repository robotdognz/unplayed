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
	}
	
	@Override
	public void clicked() {
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			((Page) editor.selected).showPlayer = !((Page) editor.selected).showPlayer;
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			if (((Page) editor.selected).showPlayer) {
				active = true;
			}else {
				active = false;
			}
		} else {
			active = false;
		}
	}
}
