package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSave extends Widget {
	private FileChooser files;
	private boolean saving = false;

	public WidgetSave(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	@Override
	public void clicked() {
		// request the file
		files.createSaveFile();
		saving = true;
		p.delay(500); // delay so animation happens after the file browser is open
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (saving) {
			if (files.hasUri()) {
				// save the level
				String file = files.getPath();
				editor.eJSON.save(editor, file);
				// end saving
				saving = false;
			}
		}
	}
}
