package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetExcludeObstacles extends Widget {
	EditorSide toolbar;

	public WidgetExcludeObstacles(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "ExcludeObstacels.png");
	}
}
