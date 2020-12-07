package editor;

import java.util.HashSet;

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
	PVector point;

	public ImageTool(Editor editor) {
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
		this.point = game.point;
	}

	@Override
	public void activate() {
		if (game.point != null && !editor.showPageView) {
			int platformX = (int) game.point.x;
			int platformY = (int) game.point.y;

			boolean spaceFree = true;
			Rectangle foundAtPoint = null;
			Rectangle toInsert = null;
			if (editor.currentImage != null) {
				toInsert = new Image(texture, editor.currentImage.getFile(), platformX, platformY, editor.currentImage.getWidth(),
						editor.currentImage.getHeight());
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
