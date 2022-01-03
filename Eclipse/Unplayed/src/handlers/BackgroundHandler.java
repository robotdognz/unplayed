package handlers;

import java.io.File;

import processing.core.PApplet;

public class BackgroundHandler extends Handler implements Comparable<BackgroundHandler> {

	public BackgroundHandler(PApplet p, TextureCache texture, File file, int width, int height) {
		super(p, texture, file, width, height);
	}

	@Override
	public int compareTo(BackgroundHandler o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
