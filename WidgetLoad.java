package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLoad extends Widget {
	private FileChooser files;
	private boolean loading = false;

	public WidgetLoad(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	@Override
	public void clicked() {
		// load the level
		files.createLoadFile();
		loading = true;
//		if (!file.equals("")) {
//			editor.eJSON.load(editor.game, file);
//		}

//		// deselect old objects
//		editor.currentTile = null;
//		editor.currentImage = null;
//		editor.currentEvent = null;
//		editor.currentView = null;
//		editor.currentPage = null;
//		editor.selected = null;
//
//		editor.game.startGame();
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (loading) {
			if (files.hasUri()) {
				// load the level
				String file = files.getPath();
				editor.eJSON.load(editor.game, file);
				// deselect old objects
				editor.currentTile = null;
				editor.currentImage = null;
				editor.currentEvent = null;
				editor.currentView = null;
				editor.currentPage = null;
				editor.selected = null;

				editor.game.startGame();
				loading = false;
			}
		}
	}
}