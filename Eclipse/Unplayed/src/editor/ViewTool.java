package editor;

import game.Game;
import handlers.TextureCache;

public class ViewTool implements Tool {
	Editor editor;
	Game game;
	TextureCache texture;

	public ViewTool(Editor editor) {
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
	}

	@Override
	public void touchMoved() {

		// first it should store the current point if it doesn't already have one

	}

	@Override
	public void touchEnded() {
		// if the stored first point is not null
		// confirm the second point (unless it's the same as the first point)
		// place the view into the world
	}

	@Override
	public void draw() {
		// draw the current selection area
	}

}
