package editor.uiside;

import java.util.List;

import editor.Editor;
import editor.Toolbar;
import game.Game;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.Tile;
import objects.View;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDelete extends Widget {
	Game game;

	public WidgetDelete(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		this.game = editor.game;
		icon = p.loadImage(folder + "deleteButton.png");
	}

	public void clicked() {
		if (editor.selected != null) {
			if (editor.selected instanceof Tile && !editor.showPageView) {
				game.world.remove(editor.selected);
			} else if (editor.selected instanceof Image && !editor.showPageView) {
				game.world.remove(editor.selected);
			} else if (editor.selected instanceof Event && !editor.showPageView) {
				game.world.remove(editor.selected);
			} else if (editor.selected instanceof View && !editor.showPageView) {
				// remove matching the pages
				List<Page> pages = game.getPageView().getPages();
				for (int i = 0; i < pages.size(); i++) {
					if (pages.get(i).getX() != editor.selected.getX()) {
						continue;
					}
					if (pages.get(i).getY() != editor.selected.getY()) {
						continue;
					}
					if (pages.get(i).getWidth() != editor.selected.getWidth()) {
						continue;
					}
					if (pages.get(i).getHeight() != editor.selected.getHeight()) {
						continue;
					}
					game.getPageView().removePage(pages.get(i));
				}
				// remove the view
				game.views.remove(editor.selected);
			} else if (editor.selected instanceof Page) {
				game.getPageView().removePage((Page) editor.selected);
			}
			// deselect the object
			editor.selected = null;
		}
	}

	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) {
			available = true;
		} else {
			available = false;
		}
	}

}
