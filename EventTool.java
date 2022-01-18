package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;
import editor.Editor;
import editor.Editor.editorMode;
import editor.Tool;
import editor.uiside.EditorSide;
import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.PApplet;
import processing.core.PVector;

public class EventTool implements Tool {
	Game game;
	TextureCache texture;
	Editor editor;
	private EditorSide editorSide;

	public EventTool(PApplet p, Editor editor) {
		this.game = editor.game;
		this.editor = editor;
		this.texture = editor.texture;
		this.editorSide = (EditorSide) editor.editorSide;
	}

	public void touchMoved(PVector touch) {
		if (!Editor.showPageView) { // world view
			if (editor.point != null) {
				// if adjusting a PlayerEnd
				if (editorSide.adjust && editor.selected instanceof PlayerEnd) {
					if (!((PlayerEnd) editor.selected).getLevelEnd()) {
						((PlayerEnd) editor.selected).setNewPlayerArea(editor.point.copy());
						return;
					}
				}

				// figure out what to insert
				Event toInsert = null;
				if (editor.currentEvent != null && editor.eMode == editorMode.ADD) { // TODO: janky code to stop player
																						// start messing things up
					// create correct event
					toInsert = editor.currentEvent.makeEvent((int) editor.point.getX(), (int) editor.point.getY());
				} else {
					// use blank event
					toInsert = new PlayerDeath(null, null, null, (int) editor.point.getX(), (int) editor.point.getY());
				}

				// get all rectangles that overlap toInsert and pass them to the right method
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

	// TODO: can currently place events directly on top of each other, could be fine
	// if changes, it will need to return the Tile inside PlayerStart and PlayerEnd
	// before placing the new event
	private void add(Event toInsert, HashSet<Rectangle> getRectangles) {

		// find anything that directly overlaps the inserting event
		Event foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p instanceof Event && p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()) {

				if (toInsert.getClass().equals(p.getClass())) {
					foundAtPoint = (Event) p;
					continue;
				}
			}
		}
		// prevent placing new event if a matching event is found at this spot
		if (editor.currentEvent != null) {
			if (foundAtPoint != null) {
				return;
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

			if (p instanceof PlayerStart) {
				Tile oldStart = ((PlayerStart) p).getRequired();
				if (oldStart != null) {
					editor.world.insert(oldStart);
				}
				editor.game.setPlayerStart(null);
				if (editor.game.player != null) {
					editor.game.player.destroy();
				}
				editor.game.player = null;
			}
			if (p instanceof PlayerEnd) {
				Tile oldEnd = ((PlayerEnd) p).getRequired();
				if (oldEnd != null) {
					editor.world.insert(oldEnd);
				}
			}
			editor.world.remove(p);
			if (p.equals(editor.selected)) {
				editor.selected = null;
			}
		}

	}

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
			return;
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

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
	}

	@Override
	public void onRotate(float x, float y, float angle) {
	}
}
