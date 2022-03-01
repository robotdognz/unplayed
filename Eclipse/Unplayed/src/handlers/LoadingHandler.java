package handlers;

import java.io.File;
import processing.core.PApplet;

public class LoadingHandler extends Handler implements Comparable<LoadingHandler> {

	public LoadingHandler(PApplet p, TextureCache texture, File file, int width, int height) {
		super(p, texture, file, width, height);
	}

	@Override
	public int compareTo(LoadingHandler otherLoadingHandler) {
		String otherName = otherLoadingHandler.getFile().toString();
		String name = datapath.toString();
		return name.compareTo(otherName);
	}
	
}
