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
			int platformX = (int) game.point.x;
			int platformY = (int) game.point.y;

			// figure out what to insert
			Rectangle toInsert = null;
			if (editor.currentTile != null) {
				toInsert = new Tile(texture, editor.currentTile.getFile(), platformX, platformY);
			} else {
				toInsert = new Tile(null, null, platformX, platformY); // use blank tile
			}

			if (game.point != null) {
				// get all rectangles that overlap toInsert
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);
				

				if (editor.eMode == editorMode.ADD) { // adding tile
					//find anything that directly overlaps the inserting tile
					Rectangle foundAtPoint = null;
					for (Rectangle p : getRectangles) {
						if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY
								&& toInsert.getClass().equals(p.getClass())) {
							foundAtPoint = p;
						}
					}
					//remove what was found and place the new tile
					if (editor.currentTile != null) {
						if (foundAtPoint != null) {
							editor.world.remove(foundAtPoint);
						}
						editor.world.insert(toInsert);
					}
				} else if (editor.eMode == editorMode.ERASE) { // erasing tile
					//Rectangle foundAtPoint = null;
					for (Rectangle p : getRectangles) {
//						if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY
//								&& toInsert.getClass().equals(p.getClass())) {
//							foundAtPoint = p;
//						}
						//if the rectangle overlaps toInsert, remove it
						if(p.getTopLeft().x-1 > toInsert.getBottomRight().x
								&& p.getBottomRight().x+1 < toInsert.getTopLeft().x
								&& p.getTopLeft().y-1 > toInsert.getBottomRight().y
								&& p.getBottomRight().y+1 < toInsert.getTopLeft().y) {
							editor.world.remove(p);
						}
						
					}
					
//					if (foundAtPoint != null) {
//						editor.world.remove(foundAtPoint);
//					}
				}
				game.point = null;
			}
		}
	}

}
