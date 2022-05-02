package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeObstacles extends Widget {
	EditorSide toolbar;

	public WidgetExcludeObstacles(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "ExcludeObstacels.png");
	}
	
	@Override
	public void clicked() {
//		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
//			((Page) editor.selected).showObstacles = !((Page) editor.selected).showObstacles;
//		}

		editor.setRemoveObstacles();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			available = true;
//			if (((Page) editor.selected).showObstacles) {
//				active = true;
//			}else {
//				active = false;
//			}
			active = editor.removingObstacles();
		} else {
			available = false;
		}
	}
}
