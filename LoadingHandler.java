package handlers;

import java.io.File;
import processing.core.PApplet;

public class LoadingHandler extends Handler implements Comparable<LoadingHandler> {
	private boolean hasButton = false;
	private boolean fullPage = false;

	public LoadingHandler(PApplet p, TextureCache texture, File file, int width, int height, int button, int fullPage) {
		super(p, texture, file, width, height);
		if (button == 1) {
			this.hasButton = true;
		}
		if (fullPage == 1) {
			this.fullPage = true;
		}
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

	public boolean fullPage() {
		return fullPage;
	}

}
