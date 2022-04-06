package editor.uiside;

import static processing.core.PConstants.*;
import java.util.ArrayList;
import java.util.HashSet;
import controllers.EditorControl;
import editor.DebugOutput;
import editor.Editor;
import editor.Toolbar;
import editor.Editor.editorMode;
import game.AppLogic;
import objects.Background;
import objects.Editable;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import objects.events.Spike;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import ui.Menu;
import ui.Widget;
import editor.uitop.EditorTop;

public class EditorSide extends Toolbar {
	private PImage top;
	private PImage middle;
	private PImage bottom;
	public boolean adjust; // are we adjusting a Page or a PlayerEnd
	public boolean addChild; // are we adding children to a page
	public int cameraEditMode; // 0 = not editing, 1 = adding, 2 = removing

	private String previousSelected = "";

	private int widgetX; // distance from left edge of screen

	private ArrayList<Widget> tile;
	private ArrayList<Widget> image;
	private ArrayList<Widget> view;
	private ArrayList<Widget> page;
	private ArrayList<Widget> background;
	private ArrayList<Widget> playerEnd;
	private ArrayList<Widget> spike;
	private ArrayList<Widget> minimal; // for things that only require cross and tick

	public EditorSide(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = "ui" + '/';

		// create widgets
		Widget deleteW = new WidgetDelete(p, editor, this);
		Widget finishW = new WidgetFinish(p, editor, this);
		Widget flipHW = new WidgetFlipH(p, editor, this);
		Widget flipVW = new WidgetFlipV(p, editor, this);
		Widget clockwiseW = new WidgetRotateClockwise(p, editor, this);
		Widget counterClockwiseW = new WidgetRotateCounterClockwise(p, editor, this);
		Widget adjustW = new WidgetAdjust(p, editor, this);
		Widget addChildW = new WidgetAddChild(p, editor, this);
		Widget levelendW = new WidgetPELevelEnd(p, editor, this);
		Widget excludeW = new WidgetExcludeMenu(p, editor, this);

		// widgets for tiles
		tile = new ArrayList<Widget>();
		tile.add(deleteW);
		tile.add(finishW);
		tile.add(clockwiseW);
		tile.add(counterClockwiseW);

		// widgets for images
		image = new ArrayList<Widget>();
		image.add(deleteW);
		image.add(finishW);
		image.add(flipHW);
		image.add(flipVW);
//		image.add(clockwiseW);
//		image.add(counterClockwiseW);

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
		page.add(addChildW);
		page.add(excludeW);

		// widgets for pages
		background = new ArrayList<Widget>();
		background.add(deleteW);
		background.add(finishW);
		background.add(flipHW);
		background.add(flipVW);
		background.add(adjustW);

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
		spike.add(counterClockwiseW);

		// minimal widgets
		minimal = new ArrayList<Widget>();
		minimal.add(deleteW);
		minimal.add(finishW);

		widgets = minimal;

		super.widgetSpacing = p.width / 9;

		float height = widgetSpacing * (widgets.size());

		super.widgetOffset = p.height / 2 - (height - widgetSpacing) / 2;

		widgetX = p.width / 18;

		this.adjust = false; // are we adjusting a page or player end
		this.addChild = false; // are we adding children to a page

		// load sprites
		this.top = p.requestImage(folder + "icn_SideTabTop.png");
		this.middle = p.requestImage(folder + "icn_SideTabMiddle.png");
		this.bottom = p.requestImage(folder + "icn_SideTabBottum.png");

		super.bounds = new Rectangle(0, p.height / 2 - (height) / 2, widgetSpacing, height);
	}

	public void clearExternalModes() {
		adjust = false;
		addChild = false;
		if (editor.eMode == editorMode.EXTERNAL) {
			editor.eMode = ((EditorTop) editor.editorTop).getEditingMode();
		}
	}

	public void adjustMode() {
		DebugOutput.pushMessage("Adjust mode enabled", 3);
		adjust = true;
		addChild = false;
	}

	public void addChildMode() {
		addChild = true;
		adjust = false;
	}

	public void reset() {
		if (editor.selected != null && !previousSelected.equals(editor.selected.getClass().toString())) {
			// select correct widget list
			if (editor.selected instanceof Page) {
				widgets = page;
			} else if (editor.selected instanceof Background) {
				widgets = background;
			} else if (editor.selected instanceof View) {
				widgets = view;
			} else if (editor.selected instanceof Tile) {
				widgets = tile;
			} else if (editor.selected instanceof Image) {
				widgets = image;
			} else if (editor.selected instanceof PlayerEnd) {
				widgets = playerEnd;
			} else if (editor.selected instanceof Spike) {
				widgets = spike;
			} else {
				widgets = minimal;
			}
			cameraEditMode = 0;

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
			cameraEditMode = 0;
		}
	}

	@Override
	public void draw(PVector touch, Menu menu, float deltaTime) {
		// super.draw(touch, menu);

		// step - reset the side toolbar's options and abort drawing if nothing selected
		reset();
		if (editor.selected == null) {
			clearExternalModes();
			return;
		}

		// step if controlling the editor and there is something selected
		if (AppLogic.controller instanceof EditorControl) {

			float currentWidgetHeight = 0; // used to find the right most edge of the longest open widget menu
			boolean wMenuOpen = false;
			for (int i = 0; i < widgets.size(); i++) {
				// update the position of the widget
				widgets.get(i).updatePosition(deltaTime, widgetX, widgetOffset + widgetSpacing * i);

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
						editor.nextTouchInactive(); // controls won't work until the touch after widget menus are
															// closed
						float current = children.get(children.size() - 1).getPosition().x;
						if (current > currentWidgetHeight) {
							currentWidgetHeight = current;
						}
					}
				}

				// uses drawWidget() instead of draw() so position isn't updated
				widgets.get(i).drawWidget(deltaTime, widgetX, widgetOffset + widgetSpacing * i);
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
		if (AppLogic.controller instanceof EditorControl && editor.selected != null) {
			return super.insideBoundary(x, y);
		}
		return false;
	}

	@Override
	public void touchEnded() {
		// check for clicking on widgets
		if (AppLogic.controller instanceof EditorControl) {
			for (int i = 0; i < widgets.size(); i++) {
				widgets.get(i).click();
			}
		}
	}

	public void addAngle(float angle) {
		if (editor.selected != null) {
			if (editor.selected instanceof Editable) {
				// change the angle
				((Editable) editor.selected).addAngle(angle);

				// check if this tile is inside a player start
				if (editor.selected instanceof Tile) {

					// update angle of handler
					float selectedAngle = ((Tile) editor.selected).getAngle();
					((Tile) editor.selected).getHandler().setEditorAngle(selectedAngle);

					HashSet<Rectangle> returnObjects = new HashSet<Rectangle>();
					AppLogic.game.world.retrieve(returnObjects, editor.selected);
					for (Rectangle r : returnObjects) {
						if (!(r instanceof PlayerStart)) {
							continue;
						}
						PlayerStart ps = (PlayerStart) r;
						if (ps.getX() != editor.selected.getX()) {
							continue;
						}
						if (ps.getY() != editor.selected.getY()) {
							continue;
						}
						if (AppLogic.game.playerCheckpoint == null) {
							float tileAngle = ((Editable) editor.selected).getAngle();
							AppLogic.game.player.setAngle(tileAngle);
							return;
						}

					}
				}
			} else if (editor.selected instanceof Spike) {
				((Spike) editor.selected).addAngle(angle);
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