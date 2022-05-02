package editor.uibottom;

import editor.Editor;
import editor.Toolbar;
import editor.tools.PageTool;
import processing.core.PApplet;
import ui.Widget;

public class WidgetViewMode extends Widget {
	public WidgetViewMode(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "View.png");
		imageInactive = null;
		active = false;
	}

	@Override
	public void clicked() {
		editor.selected = null;
		editor.currentTool = new PageTool(p, editor);

		// default to select mode when using this tab
		if (Editor.showPageView) {
			editor.editorMode = Editor.EditorMode.SELECT;
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.currentTool instanceof PageTool) {
			active = true;
		} else {
			active = false;
		}
	}
}
