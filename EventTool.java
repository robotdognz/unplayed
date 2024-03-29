package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;

import editor.Editor;
import editor.Editor.editorMode;
import editor.uiside.EditorSide;
import game.Game;
import handlers.TextureCache;
import objects.Event;
import objects.Rectangle;
import objects.Tile;
import objects.events.CameraChange;
import objects.events.CameraCollider;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.PApplet;
import processing.core.PVector;

public class EventTool extends AreaTool {
	Game game;
	TextureCache texture;
	private EditorSide editorSide;

	public EventTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.texture = editor.texture;
		this.editorSide = (EditorSide) editor.editorSide;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (!editor.showPageView) { // world view
			if (editor.point != null) {
				// if adjusting a PlayerEnd
				if (editorSide.adjust && editor.selected instanceof PlayerEnd) {
					if (!((PlayerEnd) editor.selected).getLevelEnd()) {
						((PlayerEnd) editor.selected).setNewPlayerArea(editor.point.copy());
						return;
					}

				}

				// if editing CameraColliders
				if (editor.selected instanceof CameraChange) {
					if (editorSide.cameraEditMode == 1) {
						// add collider
						CameraCollider toInsert = new CameraCollider(game, (CameraChange) editor.selected,
								editor.point.getX(), (int) editor.point.getY());
						HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
						editor.world.retrieve(getRectangles, toInsert);
						addCollider(toInsert, getRectangles);
						return;
					} else if (editorSide.cameraEditMode == 2) {
						// remove collider
						CameraCollider toInsert = new CameraCollider(game, (CameraChange) editor.selected,
								editor.point.getX(), (int) editor.point.getY());
						HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
						editor.world.retrieve(getRectangles, toInsert);
						eraseCollider(toInsert, getRectangles);
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
//				if (editor.point != null) {
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
//				}
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
				// remove collider when player camera change or player start
				if (toInsert instanceof CameraChange && p instanceof CameraCollider) {
					editor.world.remove(p);
					continue;
				}
				// remove camera change when placing player start
				if (toInsert instanceof PlayerStart && p instanceof CameraChange) {
					editor.world.remove(p);
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
			if (p instanceof CameraCollider) {
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
				if (p instanceof CameraCollider) {
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

	private void addCollider(CameraCollider toInsert, HashSet<Rectangle> getRectangles) {
		// find overlapping colliders
		Event foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if ((p instanceof CameraCollider || p instanceof CameraChange) && p.getTopLeft().x == toInsert.getX()
					&& p.getTopLeft().y == toInsert.getY()) {
				foundAtPoint = (Event) p;
			}
		}
		// remove what was found and place the new collider
		if (foundAtPoint != null) {
			// if there is a CameraChange in this spot, return
			if (foundAtPoint instanceof CameraChange) {
				return;
			}
			editor.world.remove(foundAtPoint);
		}
		editor.world.insert(toInsert);
	}

	private void eraseCollider(CameraCollider toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
			if (!(p instanceof CameraCollider)) {
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
		}
	}

	@Override
	public void touchEnded(PVector touch) {
//		if(editor.selected instanceof PlayerStart) {
//			((PlayerStart) editor.selected).update();
//		}
	}

	@Override
	public void draw() {
		// draw ui for editing camera area
		if (editor.showPageView && editor.eMode == Editor.editorMode.SELECT) { // pages
			super.draw();
		}
		// stop editing the camera change area if any of things aren't true
		if (editor.selected == null || !(editor.selected instanceof CameraChange)
				|| editor.eMode != editorMode.SELECT) {
			edit = null;
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
