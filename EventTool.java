package editor;

import java.util.HashSet;

import editor.Editor.editorMode;
import game.Game;
import handlers.TextureCache;
import objects.Rectangle;

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
	public void activate() {
		if (game.point != null && game.point.x != -1 && !editor.showPageView) {
			int platformX = (int) game.point.x;
			int platformY = (int) game.point.y;

			boolean spaceFree = true;
			Rectangle foundAtPoint = null;
			Rectangle toInsert = null;
			if (editor.currentEvent != null) {
				toInsert = editor.currentEvent.makeEvent(platformX, platformY);
			}
			if (toInsert != null && game.point != null) {
				HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
				editor.world.retrieve(getRectangles, toInsert);
				for (Rectangle p : getRectangles) {

					if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY
							&& toInsert.getClass().equals(p.getClass())) {
						spaceFree = false;
						foundAtPoint = p;
					}
				}

				if (spaceFree) { // if there isn't something already there
					if (editor.eMode == editorMode.ADD) {
						editor.world.insert(toInsert);
					}
				} else {
					if (editor.eMode == editorMode.ERASE && foundAtPoint != null) {
						editor.world.remove(foundAtPoint);
					}
				}
				game.point = null;
			}
		}
	}

}
