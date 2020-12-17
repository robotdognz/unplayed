package editor.uiside;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import objects.Page;
import objects.Rectangle;
import objects.events.PlayerEnd;
import processing.core.PApplet;
//import processing.core.PImage;
import processing.core.PVector;
import ui.Menu;
import ui.Widget;

public class EditorSide extends Toolbar {
//	private PImage top;
//	private PImage middle;
//	private PImage bottom;
	// public ModifyMode mode;
	public boolean adjust;
	private String previousSelected = "";

	public EditorSide(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = p.dataPath("ui") + '/';

		// setup widgets
		super.widgets = new ArrayList<Widget>();
		// add widgets
		Widget deleteW = new WidgetDelete(p, editor, this);
		Widget finishW = new WidgetFinish(p, editor, this);
		Widget flipHW = new WidgetFlipH(p, editor, this);
		Widget flipVW = new WidgetFlipV(p, editor, this);
		Widget adjustW = new WidgetAdjust(p, editor, this);
		Widget levelendW = new WidgetLevelEnd(p, editor, this);

		widgets.add(deleteW);
		widgets.add(finishW);
		widgets.add(flipHW);
		widgets.add(flipVW);
		widgets.add(adjustW);
		widgets.add(levelendW);

		super.widgetSpacing = p.width / 8;

		float height = widgetSpacing * (widgets.size()); // +1

		super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;

		this.adjust = false; // are we adjusting a page?
		// sprites
		// this.top = p.requestImage(folder + "???.png");
		// this.middle = p.requestImage(folder + "???.png");
		// this.bottom = p.requestImage(folder + "???.png");

		super.bounds = new Rectangle(0, p.height / 2 - (height) / 2, 160, height); // TODO: needs to scale to screen
	}

	public void reset() {
		adjust = false;
	}

	@Override
	public void draw(PVector touch, Menu menu) {
		// super.draw(touch, menu);

		if (editor.selected == null) {
			reset();
		}
		if (editor.selected != null && !previousSelected.equals(editor.selected.getClass().getSimpleName())) {
			reset();
		}

		// step if controlling the editor and there is something selected
		if (editor.controller instanceof EditorControl && editor.selected != null) {
			// step
			if (!(editor.selected instanceof Page)) {
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
		if (editor.selected != null) {
			previousSelected = editor.selected.getClass().getSimpleName();
		}
	}

	@Override
	public boolean insideBoundary(float x, float y) {
		// prevent editor controls in this area if controlling the editor and something
		// is selected
		if (editor.controller instanceof EditorControl && editor.selected != null) {
			return super.insideBoundary(x, y);
		}
		return false;
	}

	@Override
	public void touchEnded() {
		// check for clicking on widgets
		if (editor.controller instanceof EditorControl) {
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).click();
			}
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

	public boolean isLevelEnd() {
		if (editor.selected != null) {
			if (editor.selected instanceof PlayerEnd) {
				return ((PlayerEnd) editor.selected).getLevelEnd();
			}
		}
		return false;
	}

	public void levelEnd(boolean levelEnd) {
		if (editor.selected != null) {
			if (editor.selected instanceof PlayerEnd) {
				((PlayerEnd) editor.selected).setLevelEnd(levelEnd);
			}
		}
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
