package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.TileTool;
import processing.core.PApplet;
import ui.Widget;

public class WidgetTileMode extends Widget {
	public WidgetTileMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "colider.png");
		imageInactive = null;
		active = true;
	}

	@Override
	public void clicked() {
		editor.editorSide.clearExternalModes();
		editor.currentTool = new TileTool(editor);
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.currentTool instanceof TileTool) {
			active = true;
		} else {
			active = false;
		}
	}
}