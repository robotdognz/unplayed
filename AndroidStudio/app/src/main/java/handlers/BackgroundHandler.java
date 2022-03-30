package handlers;

import java.io.File;

import processing.core.PApplet;

public class BackgroundHandler extends Handler implements Comparable<BackgroundHandler> {
    private boolean fixedSize;
    private boolean hasShadow;

    public BackgroundHandler(PApplet p, TextureCache texture, File file, int width, int height, boolean fixedSize, boolean shadow) {
        super(p, texture, file, width, height, false);

        this.fixedSize = fixedSize;
        this.hasShadow = shadow;
    }

    @Override
    public int compareTo(BackgroundHandler otherBackgroundHandler) {
        String otherName = otherBackgroundHandler.getFile().toString();
        String name = datapath.toString();
        return name.compareTo(otherName);
    }

    public boolean fixedSize() {
        return fixedSize;
    }

    public boolean hasShadow() {
        return hasShadow;
    }
}
