package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;
import editor.Editor;
import editor.Tool;
import editor.Editor.editorMode;
import game.AppLogic;
import handlers.TileHandler;
import objects.Rectangle;
import objects.Tile;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.PVector;

public class TileTool implements Tool {
	Editor editor;

	public TileTool(Editor editor) {
		this.editor = editor;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (editor.point != null && !Editor.showPageView) {

			// figure out what to insert
			Tile toInsert = null;
			if (editor.currentTile != null) {
				// create correct tile
				toInsert = new Tile(AppLogic.game.box2d, AppLogic.texture, editor.currentTile.getFile(),
						(int) editor.point.getX(), (int) editor.point.getY());
				toInsert.setAngle(editor.currentTile.getEditorAngle());
			} else {
				// use blank tile
				toInsert = new Tile(null, null, null, (int) editor.point.getX(), (int) editor.point.getY());
			}

			// get all rectangles that overlap toInsert and pass them to the right method
			if (editor.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);

				if (editor.eMode == editorMode.ADD) { // adding tile
					add(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.ERASE) { // erasing tile
					erase(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.SELECT) { // selecting tile
					select(toInsert, getRectangles);
				}
				editor.point = null;
			}
		}
	}

	private void add(Tile toInsert, HashSet<Rectangle> getRectangles) {
		// find anything that directly overlaps the inserting tile
		Tile foundAtPoint = null;
		PlayerStart foundStart = null;
		PlayerEnd foundEnd = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()) {
				if (p instanceof Tile) {
					foundAtPoint = (Tile) p;
					break;
				} else if (p instanceof PlayerStart) {
					foundStart = (PlayerStart) p;
					break;
				} else if (p instanceof PlayerEnd) {
					foundEnd = (PlayerEnd) p;
					break;
				}
			}
		}
		// remove what was found and place the new tile
		if (editor.currentTile != null) {

			// check if there is a tile in this position in game.removed
			for (Tile t : AppLogic.game.removed) {
				if (toInsert.getTopLeft().x > t.getBottomRight().x - 1) {
					continue;
				}
				if (toInsert.getBottomRight().x < t.getTopLeft().x + 1) {
					continue;
				}
				if (toInsert.getTopLeft().y > t.getBottomRight().y - 1) {
					continue;
				}
				if (toInsert.getBottomRight().y < t.getTopLeft().y + 1) {
					continue;
				}
				// match found
				return;
			}

			// if we found a PlayerStart or PlayerEnd
			if (foundStart != null) {
				foundStart.setRequired(toInsert);
			} else if (foundEnd != null) {
				foundEnd.setRequired(toInsert);
			} else {

				// run as normal if no tile was found in game.removed
				if (foundAtPoint != null) {
					editor.world.remove(foundAtPoint);
				}
				editor.world.insert(toInsert);
			}
		}

		// select the newly inserted tile
		if (toInsert.getFile() != null) {
			editor.selected = toInsert;
		} else {
			editor.selected = null;
		}
	}

	private void erase(Tile toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
			if (!(p instanceof Tile || p instanceof PlayerStart || p instanceof PlayerEnd)) {
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

			// check if the tile is also in game.placed, if so, prevent removing it
			if (AppLogic.game.placed != null && AppLogic.game.placed.contains(p)) {
				continue;
			}

			if (p instanceof Tile) {
				editor.world.remove(p);
				if (p.equals(editor.selected)) {
					editor.selected = null;
				}
			} else if (p instanceof PlayerStart) {
				Tile required = ((PlayerStart) p).getRequired();
				if (required != null && required.equals(editor.selected)) {
					editor.selected = null;
				}
				AppLogic.game.removePlayer();
				((PlayerStart) p).setRequired(null);
			} else if (p instanceof PlayerEnd) {
				Tile required = ((PlayerEnd) p).getRequired();
				if (required != null && required.equals(editor.selected)) {
					editor.selected = null;
				}
				((PlayerEnd) p).setRequired(null);
			}

		}

	}

	private void select(Tile toInsert, HashSet<Rectangle> getRectangles) {
		// if there is noting to check
		if (getRectangles.size() < 1) {
			editor.selected = null;
			return;
		}

		// if there are things to check
		// try to find exact match
		Tile foundAtPoint = null;
		PlayerStart foundStart = null;
		PlayerEnd foundEnd = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()) {
				if (p instanceof Tile) {
					foundAtPoint = (Tile) p;
					break;
				} else if (p instanceof PlayerStart) {
					foundStart = (PlayerStart) p;
					break;
				} else if (p instanceof PlayerEnd) {
					foundEnd = (PlayerEnd) p;
					break;
				}
			}
		}

		if (foundAtPoint != null) {
			// if it found an exact match
			editor.selected = foundAtPoint;
			updateHandlerRotationAndSelected(foundAtPoint);
		} else if (foundStart != null) {
			// if it found a PlayerStart
			Tile required = foundStart.getRequired();
			if (required != null) {
				editor.selected = required;
				updateHandlerRotationAndSelected(required);
			}
		} else if (foundEnd != null) {
			// if it found a PlayerEnd
			Tile required = foundEnd.getRequired();
			if (required != null) {
				editor.selected = required;
				updateHandlerRotationAndSelected(required);
			}
		} else {
			// if there is no exact match, look for overlaps
			for (Rectangle p : getRectangles) {
				if (!(p instanceof Tile)) {
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
				updateHandlerRotationAndSelected((Tile) p);
				return;
			}
			// nothing was found, select nothing
			editor.selected = null;
		}
	}

	private void updateHandlerRotationAndSelected(Tile tile) {
		float angle = tile.getAngle();
		TileHandler handler = tile.getHandler();
		handler.setEditorAngle(angle);
		editor.currentTile = handler;
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
