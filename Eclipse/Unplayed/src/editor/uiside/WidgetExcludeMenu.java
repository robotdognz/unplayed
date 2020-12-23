package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeMenu extends Widget {
	EditorSide toolbar;

	public WidgetExcludeMenu(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "ExcludeMenu.png");
		
		Widget w1 = new WidgetExcludePlayer(p, editor, parent);
		Widget w2 = new WidgetExcludeObstacles(p, editor, parent);


		subWidgets.add(w1);
		subWidgets.add(w2);

	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null && editor.selected instanceof Page) { // if a page is selected
			available = true;
		} else {
			available = false;
		}

	}

}
