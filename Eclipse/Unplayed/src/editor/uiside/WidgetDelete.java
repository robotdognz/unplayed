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
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDelete extends Widget {
	Game game;

	public WidgetDelete(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		this.game = editor.game;
		icon = p.loadImage(folder + "deleteButton.png");
	}

	@Override
	public void clicked() {
		if (editor.selected != null) {
			if (!editor.showPageView) { // level view
				if (editor.selected instanceof Tile) {
					game.world.remove(editor.selected);
				} else if (editor.selected instanceof Image) {
					game.world.remove(editor.selected);
				} else if (editor.selected instanceof Event) {
					// if the event is a player start
					if (editor.selected instanceof PlayerStart) {
						Tile oldStart = ((PlayerStart) editor.selected).getRequired();
						if (oldStart != null) {
							editor.world.insert(oldStart);
						}
						editor.game.setPlayerStart(null);
						editor.game.player = null;
					}
					// if the event is a player end
					if (editor.selected instanceof PlayerEnd) {
						Tile oldEnd = ((PlayerEnd) editor.selected).getRequired();
						if (oldEnd != null) {
							editor.world.insert(oldEnd);
						}
					}
					game.world.remove(editor.selected);
				} else if (editor.selected instanceof View) {
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
				}
			} else { // page view
				if (editor.selected instanceof Page) {
					game.getPageView().removePage((Page) editor.selected);
				}
			}
			// deselect the object
			editor.selected = null;
		}
	}

	@Override
	public void updateActive() {
		super.updateActive();
		if (editor.selected != null) {
			available = true;
		} else {
			available = false;
		}
	}

}
