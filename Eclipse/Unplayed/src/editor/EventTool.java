package editor;

import java.util.HashSet;

import editor.Editor.editorMode;
import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.PlayerDeath;
import objects.Rectangle;
import processing.core.PVector;

public class EventTool implements Tool {
	Editor editor;
	Game game;
	TextureCache texture;

	public EventTool(Editor editor) {
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
	}

	@Override
	public void touchMoved() {
		if (editor.point != null && !editor.showPageView) {

			// figure out what to insert
			Event toInsert = null;
			if (editor.currentEvent != null) {
				// create correct event
				toInsert = editor.currentEvent.makeEvent((int) editor.point.x, (int) editor.point.y);
			} else {
				// use blank event
				toInsert = new PlayerDeath(null, null, (int) editor.point.x, (int) editor.point.y);
			}

			// get all rectangles that overlap toInsert and pass them to the right method
			if (editor.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);

				if (editor.eMode == editorMode.ADD) { // adding event
					add(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.ERASE) { // erasing event
					erase(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.SELECT) { // selecting event
					select(toInsert, getRectangles);
				}
				editor.point = null;
			}
		}
	}

	// TODO: can currently place events directly on top of each other, that could be
	// fine
	private void add(Event toInsert, HashSet<Rectangle> getRectangles) {
		// find anything that directly overlaps the inserting event
		Event foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = (Event) p;
			}
		}
		// remove what was found and place the new event
		if (editor.currentEvent != null) {
			if (foundAtPoint != null) {
				editor.world.remove(foundAtPoint);
			}
			editor.world.insert(toInsert);
		}

		// select the newly inserted event
		if (toInsert.getName() != null) {
			editor.selected = toInsert;
		} else {
			editor.selected = null;
		}
	}

	// TODO: copied from ImageTool without adjusting
	private void erase(Event toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
			if (!(p instanceof Event)) {
				continue;
			}
			if (p.getTopLeft().x > toInsert.getBottomRight().x - 1) {
				continue;
			}
			if (p.getBottomRight().x < toInsert.getTopLeft().x + 1) {
				continue;
			}
			if (p.getTopLeft().y > toInsert.getBottomRight().y - 1) {
				continue;
			}
			if (p.getBottomRight().y < toInsert.getTopLeft().y + 1) {
				continue;
			}
			editor.world.remove(p);
			if (p.equals(editor.selected)) {
				editor.selected = null;
			}
		}

	}

	// TODO: copied from ImageTool without adjusting
	private void select(Event toInsert, HashSet<Rectangle> getRectangles) {
		// if there is noting to check
		if (getRectangles.size() < 1) {
			editor.selected = null;
			return;
		}

		// if there are things to check
		// try to find exact match
		Rectangle foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = p;
			}
		}

		if (foundAtPoint != null) {
			// if it found an exact match
			editor.selected = foundAtPoint;
		} else {
			// if there is no exact match, look for overlaps
			for (Rectangle p : getRectangles) {
				if (!(p instanceof Event)) {
					continue;
				}
				if (p.getTopLeft().x > toInsert.getBottomRight().x - 1) {
					continue;
				}
				if (p.getBottomRight().x < toInsert.getTopLeft().x + 1) {
					continue;
				}
				if (p.getTopLeft().y > toInsert.getBottomRight().y - 1) {
					continue;
				}
				if (p.getBottomRight().y < toInsert.getTopLeft().y + 1) {
					continue;
				}
				// select the first overlap
				editor.selected = p;
				return;
			}
			// nothing was found, select nothing
			editor.selected = null;
		}
	}

	@Override
	public void touchEnded(PVector touch) {
	}

	@Override
	public void draw() {
	}
}
