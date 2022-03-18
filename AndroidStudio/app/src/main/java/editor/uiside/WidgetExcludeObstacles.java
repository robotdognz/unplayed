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
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			((Page) editor.selected).showObstacles = !((Page) editor.selected).showObstacles;
			AppLogic.game.getPageView().forceRedraw();
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			if (((Page) editor.selected).showObstacles) {
				active = true;
			}else {
				active = false;
			}
		} else {
			active = false;
		}
	}
}
