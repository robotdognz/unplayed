package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;

import editor.Editor;
import editor.Editor.editorMode;
import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import objects.events.CameraChange;
import objects.events.PlayerDeath;
import processing.core.PApplet;
import processing.core.PVector;

public class EventTool extends AreaTool {
	Game game;
	TextureCache texture;

	public EventTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.texture = editor.texture;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (!editor.showPageView) { // world view
			if (editor.point != null) {

				// figure out what to insert
				Event toInsert = null;
				if (editor.currentEvent != null && editor.eMode == editorMode.ADD) { //TODO: janky code to stop player start messing things up
					// create correct event
					toInsert = editor.currentEvent.makeEvent((int) editor.point.getX(), (int) editor.point.getY());
				} else {
					// use blank event
					toInsert = new PlayerDeath(null, null, (int) editor.point.getX(), (int) editor.point.getY());
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
		} else { // page view
			if (editor.selected != null && editor.selected instanceof CameraChange
					&& editor.eMode == editorMode.SELECT) {
				edit = ((CameraChange) editor.selected).getCameraArea();

				super.touchMoved(touch);
			} else {
				edit = null;
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
			edit = null;
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
			if (foundAtPoint instanceof CameraChange) {
				edit = ((CameraChange) foundAtPoint).getCameraArea();
			} else {
				edit = null;
			}
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
				if (foundAtPoint instanceof CameraChange) {
					edit = ((CameraChange) foundAtPoint).getCameraArea();
				} else {
					edit = null;
				}
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
		if (editor.showPageView && editor.eMode == Editor.editorMode.SELECT) { // pages
			super.draw();
		}
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
