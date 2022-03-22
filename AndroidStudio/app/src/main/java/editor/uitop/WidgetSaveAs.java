package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSaveAs extends Widget {
	private EditorTop editorTop;
	
	public WidgetSaveAs(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "saveAs.png");
	}
	
	@Override
	public void clicked() {
		// request the file
		AppLogic.files.createSaveFile();
		editorTop.saving = true;
		editorTop.loading = false;
		p.delay(500); // delay so animation happens after the file browser is open
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (editorTop.saving) {
			if (AppLogic.files.hasUri()) {
				// save the level
				editor.eJSON.save(editor, AppLogic.files.getUri());
				// end saving
				editorTop.saving = false;
			}
		}
	}
}
