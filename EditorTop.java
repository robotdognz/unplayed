package editor.uitop;

import java.util.ArrayList;

import editor.Editor;
import editor.Toolbar;
import objects.Rectangle;
import processing.core.*;
import static processing.core.PConstants.*;

import ui.*;

public class EditorTop extends Toolbar {
	private PImage uiExtra;

	public EditorTop(PApplet p, Editor editor) {
		super(p, editor);
		super.folder = p.dataPath("ui") + '/' + "widgets" + '/';
		this.uiExtra = p.loadImage(p.dataPath("ui") + '/' + "UI_element01.png");

		// setup widgets
		super.widgets = new ArrayList<Widget>();

		Widget saveW = new WidgetSaveMenu(p, editor, this);
		Widget pageW = new WidgetPageView(p, editor, this);
		Widget playerW = new WidgetPlayerControl(p, editor, this);
		Widget cameraW = new WidgetCameraControl(p, editor, this);
		Widget editModeW = new WidgetEditorMode(p, editor, this);
		Widget extraW = new WidgetExtra(p, editor, this);

		widgets.add(saveW);
		widgets.add(pageW);
		widgets.add(playerW);
		widgets.add(cameraW);
		widgets.add(editModeW);
		widgets.add(extraW);

		// this.eWidgetSpacing = width/(this.eWidgets.size()+1);
		super.widgetSpacing = p.width / 8;
		super.widgetOffset = (p.width - widgetSpacing * 5) / 2;
		
		super.bounds = new Rectangle(0, 0, p.width, 200); //TODO: needs to be relative to screen size
	}

	@Override
	public void draw(PVector touch, Menu menu) {
		//super.draw(touch, menu);
		// draw ui extra piece
		p.imageMode(CENTER);
		float widgetScale = ((float) 75 * 1.5f); // wSize*1.5 //TODO: this is messed up code, do it a better way!
		p.image(uiExtra, widgetOffset + widgetSpacing * 3, 120, widgetScale * 4.4f, widgetScale * 1.2f);

		// widget menus - draw them and close them if lastTouch is below longest open
		// widget menu
		float currentWidgetHeight = 0; // used to find the bottom of the longest open widget menu
		boolean wMenuOpen = false;
		for (int i = 0; i < widgets.size(); i++) {
			if (widgets.get(i).isActive()) {
				ArrayList<Widget> children = widgets.get(i).getChildren();
				if (children.size() > 0) {
					wMenuOpen = true;
					editor.nextTouchInactive = true; // controls won't work until the touch after widget menus are
														// closed
					float current = children.get(children.size() - 1).getPosition().y;
					if (current > currentWidgetHeight) {
						currentWidgetHeight = current;
					}
				}
			}
			// eWidgets.get(i).draw(eWidgetSpacing*(i+1), 120);
			widgets.get(i).draw(widgetOffset + widgetSpacing * i, 120);
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
}
