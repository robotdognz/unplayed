package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import editor.uibottom.EditorBottom;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLoad extends Widget {
	private EditorTop editorTop;

	public WidgetLoad(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	@Override
	public void clicked() {
		// request the file
		AppLogic.files.createLoadFile();
		editorTop.loading = true;
		editorTop.saving = false;
		p.delay(500); // delay so animation happens after the file browser is open
	}

	@Override
	public void updateActive() {
		super.updateActive();
		// step
		if (editorTop.loading) {
			if (AppLogic.files.hasUri()) {
				// load the level
				String file = AppLogic.files.getPath();
				editor.eJSON.load(AppLogic.game, file);
				// deselect old objects
				editor.currentTile = null;
				editor.currentImage = null;
				editor.currentEvent = null;
				editor.currentView = null;
				editor.currentPage = null;
				editor.selected = null;
				// restart the game
				AppLogic.game.startGame();
				// end loading
				editorTop.loading = false;
				// clear view/page offset in editor bottom
				((EditorBottom) editor.editorBottom).resetViewAndPageOffset();
			}
		}
	}
}