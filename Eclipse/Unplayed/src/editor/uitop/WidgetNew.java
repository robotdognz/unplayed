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
	private Editor editor;
	private Game game;

	public WidgetNew(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		files = editor.files;
		this.editor = editor;
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
		game.getPageView().clearBackgrounds();
		// clear box2d
		game.buildWorld();
		// clear selected
		editor.selected = null;
	}

}