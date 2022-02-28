package editor.uiside;

import java.util.HashSet;
import java.util.List;
import editor.Editor;
import editor.Toolbar;
import game.AppLogic;
import objects.Event;
import objects.Image;
import objects.Page;
import objects.PageViewObject;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.PApplet;
import ui.Widget;

public class WidgetDelete extends Widget {

	public WidgetDelete(PApplet p, Editor editor, Toolbar parent) {
		super(p, editor, parent);
		icon = p.loadImage(folder + "deleteButton.png");
	}

	@Override
	public void clicked() {
		if (editor.selected != null) {
			if (!Editor.showPageView) { // level view
				if (editor.selected instanceof Tile) {
					// check if the selected tile is contained inside a PlayerStart or PlayerEnd
					HashSet<Rectangle> returnSet = new HashSet<Rectangle>();
					AppLogic.game.getWorld().retrieve(returnSet, editor.selected);
					for (Rectangle r : returnSet) {
						if (!(r instanceof PlayerStart || r instanceof PlayerEnd)) {
							continue;
						}
						if (r.getX() != editor.selected.getX()) {
							continue;
						}
						if (r.getY() != editor.selected.getY()) {
							continue;
						}
						if (r instanceof PlayerStart) {
							Tile required = ((PlayerStart) r).getRequired();
							if (required != null && editor.selected.equals(required)) {
								((PlayerStart) r).setRequired(null);
							}
							if (AppLogic.game.player != null) {
								AppLogic.game.player.destroy();
							}
							AppLogic.game.player = null;
						} else {
							Tile required = ((PlayerEnd) r).getRequired();
							if (required != null && editor.selected.equals(required)) {
								((PlayerEnd) r).setRequired(null);
							}
						}
						break;
					}
					// remove the tile from the world
					AppLogic.game.world.remove(editor.selected);
				} else if (editor.selected instanceof Image) {
					AppLogic.game.world.remove(editor.selected);
				} else if (editor.selected instanceof Event) {
					// if the event is a player start
					if (editor.selected instanceof PlayerStart) {
						Tile oldStart = ((PlayerStart) editor.selected).getRequired();
						if (oldStart != null) {
							editor.world.insert(oldStart);
						}
						AppLogic.game.setPlayerStart(null);
						if (AppLogic.game.player != null) {
							AppLogic.game.player.destroy();
						}
						AppLogic.game.player = null;
					}
					// if the event is a player end
					if (editor.selected instanceof PlayerEnd) {
						Tile oldEnd = ((PlayerEnd) editor.selected).getRequired();
						if (oldEnd != null) {
							editor.world.insert(oldEnd);
						}
					}
					// remove the event from the world
					AppLogic.game.world.remove(editor.selected);
				} else if (editor.selected instanceof View) {
					// remove matching the pages
					List<PageViewObject> pages = AppLogic.game.getPageView().getPageViewObjects();
					for (int i = pages.size() - 1; i >= 0; --i) {
						// only do for actual pages
						if (!(pages.get(i) instanceof Page)) {
							continue;
						}
						Page page = (Page) pages.get(i);

						if (page.getView().equals(editor.selected)) {
							pages.remove(i);
						}

					}

					// remove the view
					AppLogic.game.views.remove(editor.selected);
				}
			} else { // page view
				if (editor.selected instanceof PageViewObject) {
					AppLogic.game.getPageView().removePageViewObject((Page) editor.selected);
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
