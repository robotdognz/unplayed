package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSaveAs extends Widget {
	private FileChooser files;
	private EditorTop editorTop;
	//private boolean saving = false;
	
	public WidgetSaveAs(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "saveAs.png");
	}
	
	@Override
	public void clicked() {
		// request the file
		files.createSaveFile();
		editorTop.saving = true;
		editorTop.loading = false;
		p.delay(500); // delay so animation happens after the file browser is open
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
