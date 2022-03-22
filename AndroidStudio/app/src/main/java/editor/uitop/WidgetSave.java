package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSave extends Widget {
	private EditorTop editorTop;

	public WidgetSave(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	@Override
	public void clicked() {
		if (!AppLogic.files.hasUri()) {
			// request the file
			AppLogic.files.createSaveFile();
			editorTop.saving = true;
			editorTop.loading = false;
			p.delay(500); // delay so animation happens after the file browser is open
		} else {
			// we already have the file, just save
			editor.eJSON.save(AppLogic.files.getUri());
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (editorTop.saving) {
			if (AppLogic.files.hasUri()) {
				// save the level
				editor.eJSON.save(AppLogic.files.getUri());
				// end saving
				editorTop.saving = false;
			}
		}
	}
}
