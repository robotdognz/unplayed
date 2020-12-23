package editor.uiside;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import objects.Page;
import objects.Rectangle;
import objects.View;
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
	public boolean adjust;

	private String previousSelected = "";

	private ArrayList<Widget> editable; // tiles, images
	private ArrayList<Widget> view;
	private ArrayList<Widget> page;
	private ArrayList<Widget> playerEnd;
	private ArrayList<Widget> minimal; // for things that only require cross and tick

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
		Widget excludeW = new WidgetExcludeMenu(p, editor, this);

		// default widget list with everything
		widgets.add(deleteW);
		widgets.add(finishW);
		widgets.add(flipHW);
		widgets.add(flipVW);
		widgets.add(adjustW);
		widgets.add(levelendW);
		widgets.add(excludeW);

		// widgets for editable
		editable.add(deleteW);
		editable.add(finishW);
		editable.add(flipHW);
		editable.add(flipVW);

		// widgets for views
		view.add(deleteW);
		view.add(finishW);

		// widgets for pages
		page.add(deleteW);
		page.add(finishW);
		page.add(flipHW);
		page.add(flipVW);
		page.add(adjustW);
		page.add(excludeW);

		// widgets for playerEnd
		playerEnd.add(deleteW);
		playerEnd.add(finishW);
		playerEnd.add(adjustW);
		playerEnd.add(levelendW);

		// minimal widgets
		minimal.add(deleteW);
		minimal.add(finishW);

		super.widgetSpacing = p.width / 8;

		float height = widgetSpacing * (widgets.size());

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
		if (editor.selected != null) {
			if(editor.selected instanceof Editable) {
				widgets = editable;
			}else if(editor.selected instanceof View) {
				widgets = view;
			}else if(editor.selected instanceof Page) {
				widgets = page;
			}else if(editor.selected instanceof PlayerEnd) {
				widgets = playerEnd;
			}else {
				widgets = minimal;
			}
			float height = widgetSpacing * (widgets.size());
			super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;
		}
	}

	@Override
	public void draw(PVector touch, Menu menu) {
		// super.draw(touch, menu);
		// TODO: I've gone a bit overkill with all the reset(), these must be a more
		// elegant solution

		// step - reset the side toolbar's options and abort drawing if nothing selected
		if (editor.selected == null
				|| (editor.selected != null && previousSelected != editor.selected.getClass().toString())) {
			reset();
			previousSelected = editor.selected.getClass().toString();
			return;
		} else if (!(editor.selected instanceof Page || editor.selected instanceof PlayerEnd)) {
			reset();
		}

		// step if controlling the editor and there is something selected
		if (editor.controller instanceof EditorControl) {

			p.imageMode(CENTER);

			float currentWidgetHeight = 0; // used to find the right most edge of the longest open widget menu
			boolean wMenuOpen = false;
			for (int i = 0; i < widgets.size(); i++) {
				if (widgets.get(i).isActive()) {
					ArrayList<Widget> children = widgets.get(i).getChildren();
					if (children.size() > 0) {
						wMenuOpen = true;
						editor.nextTouchInactive = true; // controls won't work until the touch after widget menus are
															// closed
						float current = children.get(children.size() - 1).getPosition().x;
						if (current > currentWidgetHeight) {
							currentWidgetHeight = current;
						}
					}
				}

				widgets.get(i).draw(80, widgetOffset + widgetSpacing * i);
				widgets.get(i).updateActive();
				if (menu == null) {
					widgets.get(i).hover(touch);
				}
			}
			currentWidgetHeight += widgets.get(0).getSize() * 1.5; // add a little padding onto the bottom
			// if the last touch was below the longest open widget menu, close all widget
			// menus
			if (wMenuOpen && touch.x > currentWidgetHeight || menu != null) {
				for (Widget w : widgets) {
					if (w.isMenu()) {
						w.deactivate();
					}
				}
			}
			editor.controllerActive = !wMenuOpen; // if a widget menu is open, deactivate controls

		} else {
			reset();
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
