package editor.uitop;

import java.util.ArrayList;

import editor.Editor;
import editor.Toolbar;
import editor.Editor.editorMode;
import objects.Rectangle;
import processing.core.*;
import static processing.core.PConstants.*;

import ui.*;

public class EditorTop extends Toolbar {
	private PImage uiExtra;
	public boolean saving = false;
	public boolean loading = false;

	private int widgetY; // distance from top of screen
	private float uiExtraWidth;
	private float uiExtraHeight;

	private Widget modeWidget;

	public EditorTop(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = "ui" + '/'; // p.dataPath("ui") + '/' + "widgets" + '/';
		this.uiExtra = p.loadImage(folder + "UI_element01.png");
		// p.loadImage(p.dataPath("ui") + '/' + "UI_element01.png");

		// setup widgets
		super.widgets = new ArrayList<Widget>();

		Widget saveW = new WidgetSaveMenu(p, editor, this);
		Widget pageW = new WidgetPageView(p, editor, this);
		Widget playerW = new WidgetPlayerControl(p, editor, this);
		Widget cameraW = new WidgetCameraControl(p, editor, this);
		Widget editModeW = new WidgetEditorMode(p, editor, this);
		Widget extraW = new WidgetExtra(p, editor, this);
		Widget menuW = new WidgetPauseMenu(p, editor, this);

		widgets.add(saveW);
		widgets.add(pageW);
		widgets.add(playerW);
		widgets.add(cameraW);
		widgets.add(editModeW);
		widgets.add(extraW);
		widgets.add(menuW);

		modeWidget = editModeW;

		super.widgetSpacing = p.width / 8;
		super.widgetOffset = (p.width - widgetSpacing * 6) / 2;

		super.bounds = new Rectangle(0, 0, p.width, (int) (p.width / 6.5));

		widgetY = p.width / 12;
		uiExtraWidth = saveW.getSize() * 6.6f; // 495
		uiExtraHeight = saveW.getSize() * 1.8f; // 135
	}

	@Override
	public void draw(PVector touch, Menu menu, float deltaTime) {
		// super.draw(touch, menu);
		// draw ui extra piece
		p.imageMode(CENTER);
		p.image(uiExtra, widgetOffset + widgetSpacing * 3, widgetY, uiExtraWidth, uiExtraHeight);

		// TODO: this system is terrible. The ideal system would calculate areas around
		// open widget menus that if you touch outside of they close. A middle system
		// would do the same thing as this one, but with a recursive 'get lowest point'
		// call to the widget class that goes through all its children if it is open

		// widget menus - draw them and close them if lastTouch is below longest open
		// widget menu
		float currentWidgetHeight = 0; // used to find the bottom of the longest open widget menu
		boolean wMenuOpen = false;
		for (int i = 0; i < widgets.size(); i++) {
//			if (widgets.get(i).isActive()) {
//				ArrayList<Widget> children = widgets.get(i).getChildren();
//				if (children.size() > 0) {
//					wMenuOpen = true;
//					editor.nextTouchInactive = true; // controls won't work until the touch after widget menus are
//														// closed
//					float current = children.get(children.size() - 1).getPosition().y;
//
//					// check for an open downwards sub-menu widget
//					for (Widget w : children) {
//						if (w.isActive() && w.isMenu()) {
//							// found an open menu
//							Widget.widgetDirection direction = w.getMenuDirection();
//							if (direction == Widget.widgetDirection.DOWN) {
//								// the open menu opens downwards, so it should be added to the current height
//								ArrayList<Widget> subChildren = widgets.get(i).getChildren();
//								current = subChildren.get(subChildren.size() - 1).getPosition().y;
//							}
//						}
//					}
//
//					// update widget height
//					if (current > currentWidgetHeight) {
//						currentWidgetHeight = current;
//					}
//				}
//			}

			Widget currentWidget = widgets.get(i);
			if (currentWidget.isMenu() && currentWidget.isActive()) {
				wMenuOpen = true;
				// controls won't work until the touch after widget menus are closed
				editor.nextTouchInactive = true;

				float current = currentWidget.getLowestPoint();
				// update widget height
				if (current > currentWidgetHeight) {
					currentWidgetHeight = current;
				}
			}

			widgets.get(i).draw(deltaTime, widgetOffset + widgetSpacing * i, widgetY);
			widgets.get(i).updateActive();
			if (menu == null) {
				widgets.get(i).hover(touch);
			}
		}
		currentWidgetHeight += widgets.get(0).getSize() * 1.5; // add a little padding onto the bottom
		// if the last touch was below the longest open widget menu, close all widget
		// menus
		if (wMenuOpen && touch.y > currentWidgetHeight || menu != null) {
			for (Widget w : widgets) {
				if (w.isMenu()) {
					w.deactivate();
				}
			}
		}
		editor.controllerActive = !wMenuOpen; // if a widget menu is open, deactivate controls
	}

	@Override
	public void touchEnded() {
		// check for clicking on widgets
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).click();
		}
	}

	@Override
	public float getHeight() {
		return bounds.getHeight();
	}

	public Editor.editorMode getEditingMode() {
		ArrayList<Widget> children = modeWidget.getChildren();
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).isActive()) {
				return editorMode.values()[i];
			}
		}
		return editorMode.SELECT;
	}
}
