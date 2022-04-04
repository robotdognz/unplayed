package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeImages extends Widget {
	EditorSide toolbar;

	public WidgetExcludeImages(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "image.png");
	}
	
	@Override
	public void clicked() {
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			((Page) editor.selected).showImages = !((Page) editor.selected).showImages;
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			if (((Page) editor.selected).showImages) {
				active = true;
			}else {
				active = false;
			}
		} else {
			active = false;
		}
	}
}
