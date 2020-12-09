package editor.uiside;

import static processing.core.PConstants.CENTER;

import java.util.ArrayList;

import editor.Editor;
import editor.Toolbar;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import ui.Menu;
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
		Widget confirmW = new WidgetConfirm(p, editor, this);
		Widget filpH = new WidgetFlipH(p, editor, this);

		widgets.add(confirmW);
		widgets.add(filpH);
		
		this.widgetSpacing = p.width / 8;
		this.widgetOffset = p.height / 2 - widgetSpacing;

		// sprites
		// this.top = p.requestImage(folder + "???.png");
		// this.middle = p.requestImage(folder + "???.png");
		// this.bottom = p.requestImage(folder + "???.png");
		
		bounds = new Rectangle(0, p.height/2, 200, 300); //TODO: needs to scale to screen and widget amount
	}

	public void draw(PVector touch, Menu menu) {
		p.rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		p.imageMode(CENTER);
		
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).draw(100, widgetOffset + widgetSpacing * i); // TODO: they need to draw vertically
			widgets.get(i).updateActive();
			if (menu == null) {
				widgets.get(i).hover(touch);
			}
		}
	}
	
	public void touchEnded() {
		// check for clicking on widgets
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).click();
		}
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
