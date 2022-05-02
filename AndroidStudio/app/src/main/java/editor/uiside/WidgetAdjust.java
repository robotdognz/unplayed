package editor.uiside;

import editor.Editor;
import editor.Editor.EditorMode;
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
				if (editor.isAdjustMode()) {
					active = true;
					editor.eMode = EditorMode.EXTERNAL;
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
		if (editor.isAdjustMode()) {
			editor.editorSide.clearExternalModes();
		} else {
			editor.setAdjustMode();
		}
	}
}
