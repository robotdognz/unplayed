package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import processing.core.PApplet;
import ui.Widget;

public class WidgetLoad extends Widget {
	public WidgetLoad(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "load.png");
	}

	@Override
	public void clicked() {
		// load the level
		editor.eJSON.load(editor.game);

		// deselect old objects
		editor.currentTile = null;
		editor.currentImage = null;
		editor.currentEvent = null;
		editor.currentView = null;
		editor.currentPage = null;
		editor.selected = null;
		
		editor.game.startGame(editor.showPageView);
	}
}