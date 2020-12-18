package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSave extends Widget {
	private FileChooser files;
	private EditorTop editorTop;
	//private boolean saving = false;

	public WidgetSave(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	@Override
	public void clicked() {
		if (!files.hasUri()) {
			// request the file
			files.createSaveFile();
			editorTop.saving = true;
			editorTop.loading = false;
			p.delay(500); // delay so animation happens after the file browser is open
		} else {
			// we already have the file, just save
			String file = files.getPath();
			editor.eJSON.save(editor, file);
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (editorTop.saving) {
			if (files.hasUri()) {
				// save the level
				String file = files.getPath();
				editor.eJSON.save(editor, file);
				// end saving
				editorTop.saving = false;
			}
		}
	}
}
