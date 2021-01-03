package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLoad extends Widget {
	private FileChooser files;
	private EditorTop editorTop;

	public WidgetLoad(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		files = editor.files;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	@Override
	public void clicked() {
		// request the file
		files.createLoadFile();
		editorTop.loading = true;
		editorTop.saving = false;
		p.delay(500); // delay so animation happens after the file browser is open
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (editorTop.loading) {
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
				// restart the game
				editor.game.startGame();
				if(editor.showPageView) {
					editor.game.getPageView().forceRedraw();
				}
				// end loading
				editorTop.loading = false;
			}
		}
	}
}