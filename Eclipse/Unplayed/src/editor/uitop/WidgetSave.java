package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetSave extends Widget {
	FileChooser files;
	
	public WidgetSave(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "save.png");
	}

	@Override
	public void clicked() {
		// save the level
		String file = files.saveFile();
		p.delay(300);
		editor.eJSON.save(editor, file);
	}
}
