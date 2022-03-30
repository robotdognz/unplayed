package handlers;

import java.io.File;
import processing.core.PApplet;

public class LoadingHandler extends Handler implements Comparable<LoadingHandler> {
	private boolean hasButton = false;
	private boolean hasShadow = false;

	public LoadingHandler(PApplet p, TextureCache texture, File file, int width, int height, boolean button, boolean shadow) {
		super(p, texture, file, width, height,false);
			this.hasButton = button;
			this.hasShadow = shadow;
	}

	@Override
	public int compareTo(LoadingHandler otherLoadingHandler) {
		String otherName = otherLoadingHandler.getFile().toString();
		String name = datapath.toString();
		return name.compareTo(otherName);
	}

	public boolean hasButton() {
		return hasButton;
	}

	public boolean hasShadow() {
		return hasShadow;
	}

}
