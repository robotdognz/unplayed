package handlers;

import java.io.File;
import processing.core.*;

public class TileHandler extends Handler implements Comparable<TileHandler> {

	private int rotationMode; // what kind of rotation does this tile care about
	// 0 = cares about rotation, 1 = ignore 180 rotation, 2 = ignore all rotation

	public TileHandler(PApplet p, TextureCache texture, File file, int rotationMode) {
		super(p, texture, file, 1, 1);
		this.rotationMode = rotationMode;
	}
	
	public int getRotationMode() {
		return rotationMode;
	}

	@Override
	public int compareTo(TileHandler otherTileHandler) {
		String otherName = otherTileHandler.getFile().toString();
		String name = datapath.toString();
		return name.compareTo(otherName);
	}
}
