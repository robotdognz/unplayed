package handlers;

import java.io.File;

import objects.Event;
import processing.core.*;

public class EventHandler extends Handler implements Comparable<EventHandler> {

	public EventHandler(PApplet p, TextureCache texture, File file) {
		super(p, texture, file, 1, 1);
	}

	public Event makeEvent(int x, int y) {
		// this method gets overridden whenever an event handler object is created
		// by the level editor
		return null;
	}

	@Override
	public int compareTo(EventHandler otherEventHandler) {
		String otherName = otherEventHandler.getFile().toString();
		String name = datapath.toString();
		return otherName.compareTo(name);
	}
}
