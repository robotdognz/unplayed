package editor;

import java.util.ArrayList;

import menus.Menu;
import processing.core.*;
import static processing.core.PConstants.*;

import ui.*;

public class EditorTop extends Toolbar {
	private PApplet p;
	private String folder;
	private PImage uiExtra;

	public EditorTop(PApplet p, Editor editor) {
		super(editor);
		this.p = p;
		this.folder = p.dataPath("ui") + '/' + "widgets" + '/';
		uiExtra = p.loadImage(folder + "UI_element01.png");

		// setup widgets
		this.eWidgets = new ArrayList<Widget>();

		Widget saveW = new SaveMenuWidget(p, editor, this);
		Widget pageW = new PageViewWidget(p, editor, this);
		Widget playerW = new PlayerControlWidget(p, editor, this);
		Widget cameraW = new CameraControlWidget(p, editor, this);
		Widget editModeW = new EditorModeWidget(p, editor, this);
		Widget extraW = new ExtraWidget(p, editor, this);

		eWidgets.add(saveW);
		eWidgets.add(pageW);
		eWidgets.add(playerW);
		eWidgets.add(cameraW);
		eWidgets.add(editModeW);
		eWidgets.add(extraW);

		// this.eWidgetSpacing = width/(this.eWidgets.size()+1);
		this.eWidgetSpacing = p.width / 8;
		this.eWidgetOffset = (p.width - eWidgetSpacing * 5) / 2;
	}

	public void draw(PVector touch, Menu menu) {
		// draw ui extra piece
		p.imageMode(CENTER);
		float widgetScale = ((float) 75 * 1.5f); // wSize*1.5 //TODO: this is messed up code, do it a better way!
		p.image(uiExtra, eWidgetOffset + eWidgetSpacing * 3, 120, widgetScale * 4.4f, widgetScale * 1.2f);

		// widget menus - draw them and close them if lastTouch is below longest open
		// widget menu
		float currentWidgetHeight = 0; // used to find the bottom of the longest open widget menu
		boolean wMenuOpen = false;
		for (int i = 0; i < eWidgets.size(); i++) {
			if (eWidgets.get(i).isActive()) {
				ArrayList<Widget> children = eWidgets.get(i).getChildren();
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
			eWidgets.get(i).draw(eWidgetOffset + eWidgetSpacing * i, 120);
			eWidgets.get(i).updateActive();
			if (menu == null) {
				eWidgets.get(i).hover(touch);
			}
		}
		currentWidgetHeight += eWidgets.get(0).getSize() * 1.5; // add a little padding onto the bottom
		// if the last touch was below the longest open widget menu, close all widget
		// menus
		if (wMenuOpen && touch.y > currentWidgetHeight || menu != null) {
			for (Widget w : eWidgets) {
				if (w.isMenu()) {
					w.deactivate();
				}
			}
		}
		editor.controllerActive = !wMenuOpen; // if a widget menu is open, deactivate controls
	}

	public void touchEnded() {
		// check for clicking on widgets
		for (int i = 0; i < eWidgets.size(); i++) {
			eWidgets.get(i).click();
		}
	}
}
