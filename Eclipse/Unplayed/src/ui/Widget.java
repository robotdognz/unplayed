package ui;

import java.util.ArrayList;
import static processing.core.PConstants.*;

import editor.DebugOutput;
import editor.Editor;
import editor.Toolbar;
import processing.core.*;

public abstract class Widget {
	protected PApplet p;
	protected Editor editor;
	Toolbar parent;
	protected PVector position; // position of the widget
	protected float wSize; // 75 //size of the widget
	protected float touchScale = 1.2f; // 1.5
	protected String folder;
	protected PImage icon;
	protected PImage imageActive;
	protected PImage imageInactive;
	protected PImage imageUnavailable;
	protected boolean hover = false; // is the mouse over the widget
	protected boolean active = false; // is the widget active
	protected boolean hasSActive = false; // should active be used decoupled from widget menu opening
	protected boolean sActive = false; // secondary active that replaces the original for opening menus etc allowing
										// the user to do whatever with 'active'
	protected boolean available = true; // is this a fully working widget? Could be used to disable widgets that don't
										// work with the current tool/mode to make menus easier to navigate

	// subWidget fields
	protected float animationSpeed = 0.0128f; // 0.8f; // speed of subWidget animation
	protected widgetDirection wd = widgetDirection.DOWN; // subWidget direction, defaults to down
	protected ArrayList<Widget> subWidgets = new ArrayList<Widget>(); // if this is not null then this widget is a menu
	protected float subWidgetSpacing; // = 180; // how far apart to draw subWidgets
	protected boolean iconIsCurrentSubWidget = false; // does this widget icon change depending on its sub widgets?
	protected boolean closeAfterSubWidget = false; // does this sub widget close the sub widget menu after being clicked

	public Widget(PApplet p, Editor editor, Toolbar parent) {
		this.p = p;
		folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
		imageActive = p.loadImage(folder + "active.png");
		imageInactive = p.loadImage(folder + "inactive.png");
		imageUnavailable = p.loadImage(folder + "unavailable.png");
		this.editor = editor;
		this.parent = parent;
		defaultIcon();

		this.wSize = p.width / 19.2f;
		this.subWidgetSpacing = p.width / 8;

	}

	public void hover(PVector lastTouch) {
		if (lastTouch.x >= position.x - wSize * touchScale && lastTouch.y >= position.y - wSize * touchScale
				&& lastTouch.x <= position.x + wSize * touchScale && lastTouch.y <= position.y + wSize * touchScale
				&& available) {
			hover = true;
		} else {
			hover = false;
		}

		// subWidget hover
		if (subWidgets.size() > 0 && ((!hasSActive && active) || (hasSActive && sActive))) {
			// if this widget is a menu and it has been opened
			for (Widget w : subWidgets) {
				w.hover(lastTouch);
			}
		}
	}

	public void draw(float deltaTime, float wX, float wY) {
		// standard draw: draws the widget and updates in in one method
		updatePosition(deltaTime, wX, wY);
		drawWidget(deltaTime, wX, wY);
	}

	public void updatePosition(float deltaTime, float wX, float wY) {
		// update widget position
		if (position == null) {
			position = new PVector(wX, wY);
		} else if (position.x != wX || position.y != wY) {
			position.x = PApplet.lerp(position.x, wX, PApplet.exp(-(animationSpeed / deltaTime)));
			position.y = PApplet.lerp(position.y, wY, PApplet.exp(-(animationSpeed / deltaTime)));
		}
	}

	public void drawWidget(float deltaTime, float wX, float wY) {
		// draw the widget
		if (parent != null) {
			subWidgetSpacing = parent.widgetSpacing;
		}
		// subWidget draw - comes before current widget so the sub widgets slide out
		// from behind
		if (subWidgets.size() > 0) { // if this widget is a menu and it has been opened
			// go through them backwards so that they are drawn bottom to top
			for (int i = subWidgets.size() - 1; i >= 0; i--) {
				float widgetOffset = 0;
				if ((!hasSActive && active) || (hasSActive && sActive)) {
					// if this widget is active, open the subWidgets
					widgetOffset = subWidgetSpacing + i * subWidgetSpacing;
				} else {
					// if the subWidget is a menu and it is active, deactivate it so it closes
					if (subWidgets.get(i).isMenu() && subWidgets.get(i).isActive()) {
						subWidgets.get(i).deactivate();
					}
				}
				switch (wd) {
				case DOWN:
					subWidgets.get(i).draw(deltaTime, wX, wY + widgetOffset);
					break;
				case UP:
					subWidgets.get(i).draw(deltaTime, wX, wY - widgetOffset);
					break;
				case LEFT:
					subWidgets.get(i).draw(deltaTime, wX - widgetOffset, wY);
					break;
				case RIGHT:
					subWidgets.get(i).draw(deltaTime, wX + widgetOffset, wY);
					break;
				}
			}
		}

		p.imageMode(CENTER);
		if (available) {
			if (active) {
				// active
				p.image(imageActive, position.x, position.y, wSize * 1.5f, wSize * 1.5f);
			} else {
				// not active
				if (imageInactive != null) {
					p.image(imageInactive, position.x, position.y, wSize * 1.5f, wSize * 1.5f);
				}
				p.tint(75);
			}
		} else {
			// unavailable
			p.image(imageUnavailable, position.x, position.y, wSize * 1.5f, wSize * 1.5f);
			p.tint(180);
		}

		// draw widget icon
		p.image(icon, position.x, position.y, wSize, wSize);
		p.noTint();
		p.imageMode(CORNER);
	}

	public boolean isMenu() {
		if (subWidgets.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isActive() {
		if (hasSActive) {
			return sActive;
		} else {
			return active;
		}
	}

	public void deactivate() {
		if (hasSActive) {
			sActive = false;
		} else {
			active = false;
		}
	}

	public boolean click() {
		if (hover) {
			hover = false;
			clicked();
			return true;
		}

		if (subWidgets.size() > 0) {
			for (Widget w : subWidgets) {
				if (w.click()) { // both does the click and returns true if the click happened
					if (w.getCloseAfter()) { // if the widget that was clicked should close the widget menu, close it
						DebugOutput.pushMessage("CloseAfterSubWidget was true", 1);
						if (hasSActive) {
							sActive = false;
						} else {
							active = false;
						}
					}
				}
			}
		}
		return false;
	}

	public void clicked() {
		if (hasSActive) {
			sActive = !sActive;
		} else {
			active = !active;
		}
	}

	protected void defaultIcon() {
		icon = p.loadImage(folder + "diamond.png");
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector position) {
		if (this.position == null) {
			this.position = new PVector(position.x, position.y);
		} else {
			this.position.x = position.x;
			this.position.y = position.y;
		}
		if (subWidgets != null) {
			for (Widget w : subWidgets) {
				w.setPosition(this.position);
			}
		}
	}

	public void setPosition(float x, float y) {
		if (this.position == null) {
			this.position = new PVector(x, y);
		} else {
			this.position.x = x;
			this.position.y = y;
		}
		if (subWidgets != null) {
			for (Widget w : subWidgets) {
				w.setPosition(this.position);
			}
		}
	}

	public boolean getCloseAfter() {
		return closeAfterSubWidget;
	}

	public PImage getIcon() {
		return icon;
	}

	public float getSize() {
		return wSize;
	}

	public ArrayList<Widget> getChildren() {
		return subWidgets;
	}

	public void reset() {
		if (position == null) {
			return;
		}
		for (Widget w : subWidgets) {
			w.setPosition(position);
		}
	}

	public void updateActive() {
		// this method should also be used to update 'available'
		if (subWidgets.size() > 0) {
			for (Widget w : subWidgets) {
				w.updateActive();
				if (iconIsCurrentSubWidget && w.isActive()) {
					this.icon = w.getIcon();
				}
			}
		}
	}

	public void setActive(boolean newActive) {
		active = newActive;
	}

	public Widget.widgetDirection getMenuDirection() {
		return wd;
	}

	public float getLowestPoint() {
		float output = position.y;
		if (isActive() && isMenu()) {
			// if this widget is an open menu, go through children
			for (Widget w : subWidgets) {
				float childPos = w.getPosition().y;
				if (childPos > output) {
					output = childPos;
				}
			}
		}
		DebugOutput.pushMessage("Getting lowest widget point", 1);
		return output;
	}

	public enum widgetDirection {
		DOWN, UP, LEFT, RIGHT
	}
}
