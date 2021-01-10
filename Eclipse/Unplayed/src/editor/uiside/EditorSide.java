package editor.uiside;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import objects.Editable;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerEnd;
import objects.events.Spike;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import ui.Menu;
import ui.Widget;

public class EditorSide extends Toolbar {
	private PImage top;
	private PImage middle;
	private PImage bottom;
	public boolean adjust;

	private String previousSelected = "";

	private int widgetX; // distance from left edge of screen

	private ArrayList<Widget> tile;
	private ArrayList<Widget> image;
	private ArrayList<Widget> view;
	private ArrayList<Widget> page;
	private ArrayList<Widget> playerEnd;
	private ArrayList<Widget> spike;
	private ArrayList<Widget> minimal; // for things that only require cross and tick

	public EditorSide(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = "ui" + '/'; // p.dataPath("ui") + '/';

		// create widgets
		Widget deleteW = new WidgetDelete(p, editor, this);
		Widget finishW = new WidgetFinish(p, editor, this);
		Widget flipHW = new WidgetFlipH(p, editor, this);
		Widget flipVW = new WidgetFlipV(p, editor, this);
		Widget clockwiseW = new WidgetRotateClockwise(p, editor, this);
		Widget counterClockwiseW = new WidgetRotateClockwise(p, editor, this);
		Widget adjustW = new WidgetAdjust(p, editor, this);
		Widget levelendW = new WidgetLevelEnd(p, editor, this);
		Widget excludeW = new WidgetExcludeMenu(p, editor, this);

		// widgets for tiles
		tile = new ArrayList<Widget>();
		tile.add(deleteW);
		tile.add(finishW);
//		tile.add(flipHW);
//		tile.add(flipVW);
		tile.add(clockwiseW);
		tile.add(counterClockwiseW);

		// widgets for images
		image = new ArrayList<Widget>();
		image.add(deleteW);
		image.add(finishW);
		image.add(flipHW);
		image.add(flipVW);
		image.add(clockwiseW);

		// widgets for views
		view = new ArrayList<Widget>();
		view.add(deleteW);
		view.add(finishW);

		// widgets for pages
		page = new ArrayList<Widget>();
		page.add(deleteW);
		page.add(finishW);
		page.add(flipHW);
		page.add(flipVW);
		page.add(adjustW);
		page.add(excludeW);

		// widgets for playerEnd
		playerEnd = new ArrayList<Widget>();
		playerEnd.add(deleteW);
		playerEnd.add(finishW);
		playerEnd.add(levelendW);
		playerEnd.add(adjustW);

		// widgets for spikes
		spike = new ArrayList<Widget>();
		spike.add(deleteW);
		spike.add(finishW);
		spike.add(clockwiseW);

		// minimal widgets
		minimal = new ArrayList<Widget>();
		minimal.add(deleteW);
		minimal.add(finishW);

		widgets = minimal;

		super.widgetSpacing = p.width / 9;

		float height = widgetSpacing * (widgets.size());

		super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;

		widgetX = p.width / 18;

		this.adjust = false; // are we adjusting a page?
		// load sprites
		this.top = p.requestImage(folder + "icn_SideTabTop.png");
		this.middle = p.requestImage(folder + "icn_SideTabMiddle.png");
		this.bottom = p.requestImage(folder + "icn_SideTabBottum.png");

		super.bounds = new Rectangle(0, p.height / 2 - (height) / 2, widgetSpacing, height);
	}

	public void reset() {
		if (editor.selected != null && !previousSelected.equals(editor.selected.getClass().toString())) {
			// select correct widget list
			if (editor.selected instanceof Page) {
				widgets = page;
			} else if (editor.selected instanceof View) {
				widgets = view;
				adjust = false;
			} else if (editor.selected instanceof Tile) {
				widgets = tile;
				adjust = false;
			} else if (editor.selected instanceof Image) {
				widgets = image;
				adjust = false;
			} else if (editor.selected instanceof PlayerEnd) {
				widgets = playerEnd;
				adjust = false;
			} else if (editor.selected instanceof Spike) {
				widgets = spike;
				adjust = false;
			} else {
				widgets = minimal;
				adjust = false;
			}

			// calculate widget positions and create new bounds
			float height = widgetSpacing * (widgets.size());
			super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;
			super.bounds = new Rectangle(0, p.height / 2 - (height) / 2, widgetSpacing, height);
			previousSelected = editor.selected.getClass().toString();

			// reset widget positions and active status
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).setPosition(-widgetSpacing, widgetOffset + widgetSpacing * i);
				widgets.get(i).updateActive();
			}

		} else if (editor.selected == null) {
			// reset widget positions
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).setPosition(-widgetSpacing, widgetOffset + widgetSpacing * i);
			}
		}
	}

	@Override
	public void draw(PVector touch, Menu menu) {
		// super.draw(touch, menu);

		// step - reset the side toolbar's options and abort drawing if nothing selected
		reset();
		if (editor.selected == null) {
			adjust = false;
			return;
		}

		// step if controlling the editor and there is something selected
		if (editor.controller instanceof EditorControl) {

			float currentWidgetHeight = 0; // used to find the right most edge of the longest open widget menu
			boolean wMenuOpen = false;
			for (int i = 0; i < widgets.size(); i++) {

				// draw editor side background
				if (widgets.get(i).getPosition() != null) {
					p.imageMode(CENTER);
					if (i == 0) {
						p.image(top, widgets.get(i).getPosition().x, widgetOffset + widgetSpacing * i, widgetSpacing,
								widgetSpacing);
					} else if (i == widgets.size() - 1) {
						p.image(bottom, widgets.get(i).getPosition().x, widgetOffset + widgetSpacing * i, widgetSpacing,
								widgetSpacing);
					} else {
						p.image(middle, widgets.get(i).getPosition().x, widgetOffset + widgetSpacing * i, widgetSpacing,
								widgetSpacing);
					}
				}

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

				widgets.get(i).draw(widgetX, widgetOffset + widgetSpacing * i);
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

	public void addAngle() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				((Editable) editor.selected).addAngle(90);
			}
		}
	}
	
	public void subAngle() {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				((Editable) editor.selected).addAngle(-90);
			}
		}
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
