package editor.tools;

import java.util.HashSet;

import editor.Editor;
import editor.Tool;
import editor.Editor.editorMode;
import game.Game;
import handlers.TextureCache;
import objects.Image;
import objects.Rectangle;
import processing.core.PVector;

public class ImageTool implements Tool {
	Editor editor;
	Game game;
	TextureCache texture;

	public ImageTool(Editor editor) {
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
	}

	@Override
	public void touchMoved() {
		if (editor.point != null && !editor.showPageView) {

			// figure out what to insert
			Image toInsert = null;
			if (editor.currentImage != null) {
				// create correct image
				toInsert = new Image(texture, editor.currentImage.getFile(), (int) editor.point.x, (int) editor.point.y,
						editor.currentImage.getWidth(), editor.currentImage.getHeight());
			} else {
				// use blank image
				toInsert = new Image(null, null, (int) editor.point.x, (int) editor.point.y, 100, 100);
			}

			// get all rectangles that overlap toInsert and pass them to the right method
			if (editor.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);

				if (editor.eMode == editorMode.ADD) { // adding image
					add(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.ERASE) { // erasing image
					erase(toInsert, getRectangles);
				} else if (editor.eMode == editorMode.SELECT) { // selecting image
					select(toInsert, getRectangles);
				}
				editor.point = null;
			}
		}
	}

	// TODO: image adding should only erase those underneath if they perfectly
	// overlap
	private void add(Image toInsert, HashSet<Rectangle> getRectangles) {
		// find anything that directly overlaps the inserting image
		Image foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = (Image) p;
			}
		}
		// remove what was found and place the new image
		if (editor.currentImage != null) {
			if (foundAtPoint != null) {
				editor.world.remove(foundAtPoint);
			}
			editor.world.insert(toInsert);
		}

		// select the newly inserted image
		if (toInsert.getFile() != null) {
			editor.selected = toInsert;
		} else {
			editor.selected = null;
		}
	}

	// TODO: image erasing should always use a 100x100 image
	private void erase(Image toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
			if (!(p instanceof Image)) {
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

	// TODO: image selecting should always use a 100x100 image
	private void select(Image toInsert, HashSet<Rectangle> getRectangles) {
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
				if (!(p instanceof Image)) {
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
}
