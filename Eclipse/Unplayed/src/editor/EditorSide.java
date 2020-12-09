package editor;

import static processing.core.PConstants.CENTER;

import java.util.ArrayList;

import menus.Menu;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import ui.Widget;

public class EditorSide extends Toolbar {
	private PApplet p;
	private Rectangle widgetArea;
	private String folder;
	private PImage top;
	private PImage middle;
	private PImage bottom;

	public EditorSide(PApplet p, Editor editor) {
		super(editor);
		this.p = p;
		this.folder = p.dataPath("ui") + '/';

		// setup widgets
		this.widgets = new ArrayList<Widget>();
		// add widgets

		// sprites
		// top = p.requestImage(folder + "???.png");
	}

	public void draw(PVector touch, Menu menu) {
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).draw(widgetOffset + widgetSpacing * i, 120); // TODO: they need to draw vertically
			widgets.get(i).updateActive();
			if (menu == null) {
				widgets.get(i).hover(touch);
			}
		}
	}

	public boolean insideBoundary(float x, float y) {
		// TODO: need to add a boundary rectangle and complete this method
		// should implement the basic single rectangle version in the abstract class
		// and implement the advanced version inside the sub class where necessary
		return false;
	}

	// these methods are called by the widgets inside this toolbar, the toolbar then
	// passes what they set on to the currently selected object in the editor

	public void setAngle(float angle) {
		// pass the angle straight to page if it is a page
		// round it to the nearest 90 if it's anything else
	}

	public void setArea(PVector topLeft, PVector bottomRight) {

	}

	public void setSize(float size) {
		// will only be called for page?
	}

	public void flipH(boolean flipped) {

	}

	public void flipV(boolean flipped) {

	}
}
