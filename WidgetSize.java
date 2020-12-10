package editor.uiside;

import editor.Editor;
import editor.Toolbar;
import editor.tools.SizeTool;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSize extends Widget {
	EditorSide toolbar;

	public WidgetSize(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "resize.png");
	}

	public void updateActiveUser() {
		if (editor.selected != null && editor.selected instanceof Page) {
			available = true;
			if (editor.currentTool instanceof SizeTool) {
				active = true;
			} else {
				active = false;
			}
		} else {
			available = false;
		}

	}

	public void clicked() {
		editor.currentTool = new SizeTool();
	}

}

