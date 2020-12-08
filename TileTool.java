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

			boolean spaceFree = true;
			Rectangle foundAtPoint = null;
			Rectangle toInsert = null;
			if (editor.currentTile != null) {
				toInsert = new Tile(texture, editor.currentTile.getFile(), platformX, platformY);
			} else {
				toInsert = new Tile(null, null, platformX, platformY); // blank tile
			}
			if (game.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);
				for (Rectangle p : getRectangles) {

					if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY
							&& toInsert.getClass().equals(p.getClass())) {
						spaceFree = false;
						foundAtPoint = p;
					}
				}
				// was if(spaceFree)
				if (editor.eMode == editorMode.ADD) { // if there isn't something already there
					if (editor.currentTile != null) {
						if (foundAtPoint != null) {
							editor.world.remove(foundAtPoint);
						}
						editor.world.insert(toInsert);
					}
				} else if (editor.eMode == editorMode.ERASE) {
					if (foundAtPoint != null) {
						editor.world.remove(foundAtPoint);
					}
				}
				game.point = null;
			}
		}
	}

}
