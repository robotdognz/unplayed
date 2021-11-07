package handlers;

import java.io.File;
import processing.core.*;

public class TileHandler extends Handler implements Comparable<TileHandler> {

	public TileHandler(PApplet p, TextureCache texture, File file) {
		super(p, texture, file, 1, 1);
	}

	@Override
	public int compareTo(TileHandler otherTileHandler) {
		String otherName = otherTileHandler.getFile().toString();
		String name = datapath.toString();
		return name.compareTo(otherName);
	}

//	public void draw(float pX, float pY, float size) {
//		// draw the scaled image
//		p.image(getSprite(6), pX, pY, size, size);
//	}
}
