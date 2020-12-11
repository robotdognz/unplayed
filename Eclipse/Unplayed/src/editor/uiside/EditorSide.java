package editor.uiside;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import objects.Rectangle;
import processing.core.PApplet;
//import processing.core.PImage;
import processing.core.PVector;
import ui.Menu;
import ui.Widget;

public class EditorSide extends Toolbar {
//	private PImage top;
//	private PImage middle;
//	private PImage bottom;
	//public ModifyMode mode;
	public boolean adjust;
//	public enum ModifyMode {
//		NONE, ROTATE, SIZE, MOVE
//	}

	public EditorSide(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = p.dataPath("ui") + '/';

		// setup widgets
		super.widgets = new ArrayList<Widget>();
		// add widgets
		Widget deleteW = new WidgetDelete(p, editor, this);
		Widget flipHW = new WidgetFlipH(p, editor, this);
		Widget flipVW = new WidgetFlipV(p, editor, this);
		Widget adjustW = new WidgetAdjust(p, editor, this);
//		Widget sizeW = new WidgetSize(p, editor, this);

		widgets.add(deleteW);
		widgets.add(flipHW);
		widgets.add(flipVW);
		widgets.add(adjustW);
//		widgets.add(sizeW);

		super.widgetSpacing = p.width / 8;

		float height = widgetSpacing * (widgets.size()); // +1

		super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;

		this.adjust = false;
		// sprites
		// this.top = p.requestImage(folder + "???.png");
		// this.middle = p.requestImage(folder + "???.png");
		// this.bottom = p.requestImage(folder + "???.png");

		super.bounds = new Rectangle(0, p.height / 2 - (height) / 2, 160, height); // TODO: needs to scale to screen
	}
	
	public void reset() {
		adjust = false;
	}

	public void draw(PVector touch, Menu menu) {
		// super.draw(touch, menu);
		
		//step
		if(editor.selected == null) {
			adjust = false;
		}
		
		p.imageMode(CENTER);

		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).draw(80, widgetOffset + widgetSpacing * i);
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
//		if (editor.selected != null) {
//			if (editor.selected instanceof Page) {
//				((Editable) editor.selected).setSize(float size);
//			}
//		}
	}

	// methods for the widget to access
	public boolean isFlippedH() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				return ((Editable) editor.selected).isFlippedH();
			}
		}
		return false;
	}

	public void flipH() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				((Editable) editor.selected).flipH();
			}
		}
	}

	public boolean isFlippedV() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				return ((Editable) editor.selected).isFlippedV();
			}
		}
		return false;
	}

	public void flipV() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				((Editable) editor.selected).flipV();
			}
		}
	}
}
