package editor.uiside;

import editor.Editor;
import editor.Editor.editorMode;
import editor.Toolbar;
import objects.Background;
import objects.Page;
import objects.events.PlayerEnd;
import processing.core.PApplet;
import ui.Widget;

public class WidgetAdjust extends Widget {
	EditorSide toolbar;

	public WidgetAdjust(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		toolbar = (EditorSide) parent;
		icon = p.loadImage(folder + "move.png");
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) { // if there is something selected
			// if it's a Page or a Background, or a PlayerEnd that is not a level end
			if (editor.selected instanceof Page || editor.selected instanceof Background
					|| (editor.selected instanceof PlayerEnd && !((PlayerEnd) editor.selected).getLevelEnd())) {
				available = true;
				if (toolbar.adjust) {
					active = true;
					editor.eMode = editorMode.EXTERNAL;
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
		if (toolbar.adjust) {
			editor.editorSide.clearExternalModes(true);
		} else {
			toolbar.adjust = true;
		}

//		toolbar.adjust = !toolbar.adjust;
	}

}
