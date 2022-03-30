package handlers;

import java.io.File;

import processing.core.PApplet;

public class ButtonHandler extends Handler implements Comparable<ButtonHandler> {

    public ButtonHandler(PApplet p, TextureCache texture, File file, int width, int height) {
        super(p, texture, file, width, height, false);
    }

    @Override
    public int compareTo(ButtonHandler otherButtonHandler) {
        String otherName = otherButtonHandler.getFile().toString();
        String name = datapath.toString();
        return name.compareTo(otherName);
    }

}
