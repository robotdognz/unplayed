package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;
import editor.Editor;
import editor.Editor.EditorMode;
import editor.Tool;
import game.AppLogic;
import handlers.EventHandler;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import objects.events.Spike;
import processing.core.PApplet;
import processing.core.PVector;

public class EventTool implements Tool {
	Editor editor;

	public EventTool(PApplet p, Editor editor) {
		this.editor = editor;
	}

	public void touchMoved(PVector touch) {
		if (!Editor.showPageView) { // world view
			if (editor.point != null) {
				// if adjusting a PlayerEnd
				if (editor.isAdjustMode() && editor.selected instanceof PlayerEnd) { // editorSide.adjust
					if (!((PlayerEnd) editor.selected).getLevelEnd()) {
						((PlayerEnd) editor.selected).setNewPlayerArea(editor.point.copy());
						return;
					}
				}

				// figure out what to insert
				Event toInsert;
				if (editor.currentEvent != null && editor.eMode == EditorMode.ADD) { // TODO: janky code to stop player
																						// start messing things up
					// create correct event
					toInsert = editor.currentEvent.makeEvent((int) editor.point.getX(), (int) editor.point.getY());
				} else {
					// use blank event
					toInsert = new PlayerDeath(null, null, null, (int) editor.point.getX(), (int) editor.point.getY());
				}

				// get all rectangles that overlap toInsert and pass them to the right method
				HashSet<Rectangle> getRectangles = new HashSet<>();
				editor.world.retrieve(getRectangles, toInsert);

				if (editor.eMode == EditorMode.ADD) { // adding event
					add(toInsert, getRectangles);
				} else if (editor.eMode == EditorMode.ERASE) { // erasing event
					erase(toInsert, getRectangles);
				} else if (editor.eMode == EditorMode.SELECT) { // selecting event
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
					// found an object in spot with same class as toInsert
					if (toInsert instanceof Spike) {
						// overwrite spikes with spikes
						editor.world.remove(p);
					} else {
						foundAtPoint = (Event) p;
					}
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
				AppLogic.game.setPlayerStart(null);
				if (AppLogic.game.player != null) {
					AppLogic.game.player.destroy();
				}
				AppLogic.game.player = null;
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
			if (foundAtPoint instanceof Event) {
				updateHandler((Event) foundAtPoint);
			}
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
				updateHandler((Event) p);
				return;
			}
			// nothing was found, select nothing
			editor.selected = null;
		}
	}

	private void updateHandler(Event event) {
		EventHandler handler = event.getHandler();
		if (event instanceof Spike) {
			float angle = ((Spike) event).getAngle();
			handler.setEditorAngle(angle);
		}
		editor.currentEvent = handler;
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

	@Override
	public void onTap(float x, float y){
	}
}
