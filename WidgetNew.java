package editor.uitop;

import editor.Editor;
import editor.Toolbar;
import editor.uibottom.EditorBottom;
import game.AppLogic;
import processing.core.PApplet;
import ui.Widget;

public class WidgetNew extends Widget {
	private EditorTop editorTop;
	private Editor editor;

	public WidgetNew(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		editorTop = (EditorTop) parent;
		this.editor = editor;
		closeAfterSubWidget = true;
		icon = p.loadImage(folder + "NewLevel.png");
	}

	@Override
	public void clicked() {
		// reset save file
		AppLogic.files.removeUri();
		editorTop.loading = false;
		editorTop.saving = false;
		// clear level
		AppLogic.game.player = null;
		AppLogic.game.world.clear();
		AppLogic.game.views.clear();
		AppLogic.game.getPageView().clearPageViewObjects();
		AppLogic.game.getPageView().resetSystems();
		// clear box2d
		AppLogic.game.buildWorld();
		// clear selected
		editor.selected = null;
		// clear view/page offset in editor bottom
		((EditorBottom) editor.editorBottom).resetViewAndPageOffset();
	}

}