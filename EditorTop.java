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
	public boolean saving = false;
	public boolean loading = false;

	private int widgetY; // distance from top of screen
	private float uiExtraWidth;
	private float uiExtraHeight;

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

		widgets.add(saveW);
		widgets.add(pageW);
		widgets.add(playerW);
		widgets.add(cameraW);
		widgets.add(editModeW);
		widgets.add(extraW);

		super.widgetSpacing = p.width / 8;
		super.widgetOffset = (p.width - widgetSpacing * 5) / 2;

		super.bounds = new Rectangle(0, 0, p.width, (int) (p.width / 6.5)); //(int) (p.width / 7.2)

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
			widgets.get(i).draw(deltaTime, widgetOffset + widgetSpacing * i, widgetY); //FIXME: send delta time
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
}
