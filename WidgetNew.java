package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import misc.FileChooser;
import processing.core.PApplet;
import ui.Widget;

public class WidgetNew extends Widget {
	private FileChooser files;
	private EditorTop editorTop;
	private Game game;

	public WidgetNew(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		files = editor.files;
		game = editor.game;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "NewLevel.png");
	}

	@Override
	public void clicked() {
		// reset save file
		files.removeUri();
		editorTop.loading = false;
		editorTop.saving = false;
		// clear level
		game.player = null;
		game.world.clear();
		game.views.clear();
		game.getPageView().clearPages();
	
	}

//	@Override
//	public void updateActive() {
//		super.updateActive();
//		// step
//		if (editorTop.loading) {
//			if (files.hasUri()) {
//				// load the level
//				String file = files.getPath();
//				editor.eJSON.load(editor.game, file);
//				// deselect old objects
//				editor.currentTile = null;
//				editor.currentImage = null;
//				editor.currentEvent = null;
//				editor.currentView = null;
//				editor.currentPage = null;
//				editor.selected = null;
//				// restart the game
//				editor.game.startGame();
//				// end loading
//				editorTop.loading = false;
//			}
//		}
//	}
}