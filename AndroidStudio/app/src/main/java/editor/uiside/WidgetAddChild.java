package editor.uiside;

import editor.Editor;
import editor.Editor.EditorMode;
import editor.Toolbar;
import objects.Page;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAddChild extends Widget {
	EditorSide toolbar;

	public WidgetAddChild(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "Add.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) { // if there is something selected
			// if it's a Page or a Background, or a PlayerEnd that is not a level end
			if (editor.selected instanceof Page) {
				available = true;
				if (editor.isChildMode()) {
					active = true;
					editor.editorMode = EditorMode.EXTERNAL;
				} else {
					active = false;
				}
			} else {
				available = false;
			}
		} else {
			available = false;
		}

	}

	@Override
	public void clicked() {
		if (editor.isChildMode()) {
			editor.editorSide.clearExternalModes();
		} else {
			editor.setChildMode();
		}
	}

}
