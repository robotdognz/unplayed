package editor;

import java.util.HashSet;

import editor.Editor.editorMode;
import game.Game;
import handlers.TextureCache;
import objects.Rectangle;
import objects.Tile;

public class TileTool implements Tool {
	Editor editor;
	Game game;
	TextureCache texture;

	public TileTool(Editor editor) {
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
	}

	@Override
	public void activate() {
		if (game.point != null && !editor.showPageView) {

			// figure out what to insert
			Rectangle toInsert = null;
			if (editor.currentTile != null) {
				// create correct tile
				toInsert = new Tile(texture, editor.currentTile.getFile(), (int) game.point.x, (int) game.point.y);
			} else {
				// use blank tile
				toInsert = new Tile(null, null, (int) game.point.x, (int) game.point.y);
			}

			// get all rectangles that overlap toInsert and pass them to the right method
			if (game.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);

				if (editor.eMode == editorMode.ADD) { // adding tile
					add(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.ERASE) { // erasing tile
					erase(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.SELECT) { // selecting tile
					select(toInsert, getRectangles);
				}
				game.point = null;
			}
		}
	}

	private void add(Rectangle toInsert, HashSet<Rectangle> getRectangles) {
		// find anything that directly overlaps the inserting tile
		Rectangle foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = p;
			}
		}
		// remove what was found and place the new tile
		if (editor.currentTile != null) {
			if (foundAtPoint != null) {
				editor.world.remove(foundAtPoint);
			}
			editor.world.insert(toInsert);
		}
		
		//select the newly inserted tile
		editor.selected = toInsert;
	}

	private void erase(Rectangle toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
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

	private void select(Rectangle toInsert, HashSet<Rectangle> getRectangles) {
		Rectangle foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = p;
			}
		}
		if (foundAtPoint == null) {
			for (Rectangle p : getRectangles) {
				editor.selected = p;
				return;
			}
		}
	}

}
