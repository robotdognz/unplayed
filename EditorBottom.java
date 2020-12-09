package editor.uibottom;

import objects.Rectangle;
import objects.View;
import processing.core.*;
import ui.*;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import controllers.EditorControl;
import editor.Editor;
import editor.Toolbar;
import editor.Editor.editorMode;
import editor.tools.EventTool;
import editor.tools.ImageTool;
import editor.tools.TileTool;
import editor.tools.ViewTool;
import handlers.EventHandler;
import handlers.Handler;
import handlers.ImageHandler;
import handlers.TextureCache;
import handlers.TileHandler;

public class EditorBottom extends Toolbar {
	//private PApplet p;
	private Rectangle selectionArea;
	private String folder;
	private PImage toolbar;
	private PImage tab;
	private int tabSize;
	private float widgetHeight;

	// scroll bars
	private int size; // size to drawn object in the scroll bar
	private ArrayList<TileHandler> tiles; // tiles
	private float tileOffset;
	private ArrayList<ImageHandler> images; // images
	private float imageOffset;
	private ArrayList<EventHandler> events; // events
	private float eventOffset;
	public ArrayList<View> views;// views
	private float viewOffset;

	public EditorBottom(PApplet p, Editor editor, TextureCache texture) {
		super(p, editor);
		//this.p = p;
		folder = p.dataPath("ui") + '/';

		// setup widgets
		this.widgets = new ArrayList<Widget>();
		Widget blockW = new TileModeWidget(p, editor, this);
		Widget imageW = new ImageModeWidget(p, editor, this);
		Widget eventW = new EventModeWidget(p, editor, this);
		Widget viewW = new ViewModeWidget(p, editor, this);
		widgets.add(blockW);
		widgets.add(imageW);
		widgets.add(eventW);
		widgets.add(viewW);

		widgetOffset = p.width * 0.65f;
		widgetSpacing = 140; // TODO: needs to be relative to screen size

		// setup toolbar
		int objectAreaHeight = 230; // TODO: needs to be relative to screen size
		selectionArea = new Rectangle(0, p.height - objectAreaHeight, p.width, objectAreaHeight);
		toolbar = p.requestImage(folder + "icn_toolbar_bg.png");
		tab = p.requestImage(folder + "icn_tab.png");
		tabSize = 220;

		widgetHeight = selectionArea.getY() - 53; // TODO: needs to be relative to screen size

		// scroll bars
		size = 150;
		tiles = texture.getTileList();
		images = texture.getImageList();
		events = texture.getEventList();
		views = editor.game.views;
		
		bounds = new Rectangle(0, p.height - 300, p.width, 300); //TODO: needs to scale to screen size
	}
	
//	public boolean insideBoundary(float x, float y) {
//		//TODO: need to add complex boundary checking? (might be best just to leave it as a rectangle)
//		return false;
//	}

	public void draw(PVector touch, Menu menu) {
		super.draw(touch, menu);
		for (int i = 0; i < widgets.size(); i++) {
			// draw the two behind tabs
			if (!widgets.get(i).isActive()) {
				p.imageMode(CENTER);
				p.image(tab, widgetOffset + widgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
		}

		p.imageMode(CORNER);
		p.image(toolbar, selectionArea.getX(), selectionArea.getY(), selectionArea.getWidth(), selectionArea.getHeight());

		// widgets

		for (int i = 0; i < widgets.size(); i++) {
			// if current widget is active, draw tab at the current x position
			p.imageMode(CENTER);
			if (widgets.get(i).isActive()) {
				p.image(tab, widgetOffset + widgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
			widgets.get(i).draw(widgetOffset + widgetSpacing * i, widgetHeight);
			widgets.get(i).updateActive();

			if (menu == null) {
				widgets.get(i).hover(touch);
			}
		}
		p.imageMode(CORNER);

		// figure out what type to show
		ArrayList<Object> objects = new ArrayList<Object>(); // current objects to draw in the scroll bar
		Float offset = 0.0f;
		Object currentHandler = null; //TODO: rename
		if (editor.currentTool instanceof TileTool) {
			objects.addAll(tiles);
			offset = tileOffset;
			currentHandler = editor.currentTile;
		} else if (editor.currentTool instanceof ImageTool) {
			objects.addAll(images);
			offset = imageOffset;
			currentHandler = editor.currentImage;
		} else if (editor.currentTool instanceof EventTool) {
			objects.addAll(events);
			offset = eventOffset;
			currentHandler = editor.currentEvent;
		} else if (editor.currentTool instanceof ViewTool) {
			objects.addAll(views);
			offset = viewOffset;
			currentHandler = editor.currentView;
		}

		// draw scroll bar for that type
		p.pushMatrix();
		p.imageMode(CENTER);
		p.rectMode(CENTER);
		p.translate(-offset, 0);
		for (int i = 0; i < objects.size(); i++) {
			Object object = objects.get(i);
			if (object.equals(currentHandler)) { // if this is the selected object
				// draw highlight behind
				p.noStroke();
				p.fill(0, 0, 0, 120);
				p.rect(selectionArea.getX() + selectionArea.getHeight() / 2 + i * selectionArea.getHeight(),
						selectionArea.getY() + selectionArea.getHeight() / 2, selectionArea.getHeight(), selectionArea.getHeight());
			}
			if (object instanceof Handler) {
				((Handler) object).draw(selectionArea.getX() + selectionArea.getHeight() / 2 + i * selectionArea.getHeight(),
						selectionArea.getY() + selectionArea.getHeight() / 2, size);
			} else if (object instanceof View) {
				//TODO: draw the view
				((View) object).drawToolbar(selectionArea.getX() + selectionArea.getHeight() / 2 + i * selectionArea.getHeight(),
						selectionArea.getY() + selectionArea.getHeight() / 2, size);
			}
		}
		p.imageMode(CORNER);
		p.rectMode(CORNER);
		p.popMatrix();
	}

	public void onTap(float x, float y) {
		// select object
		if (y >= selectionArea.getY()) {
			editor.controller = new EditorControl(p, editor);
			editor.eMode = editorMode.ADD;

			// figure out what type is being clicked on
			ArrayList<Object> objects = new ArrayList<Object>(); // current objects to draw in the scroll bar
			Float offset = 0.0f;
			if (editor.currentTool instanceof TileTool) {
				objects.addAll(tiles);
				offset = tileOffset;
			} else if (editor.currentTool instanceof ImageTool) {
				objects.addAll(images);
				offset = imageOffset;
			} else if (editor.currentTool instanceof EventTool) {
				objects.addAll(events);
				offset = eventOffset;
			} else if (editor.currentTool instanceof ViewTool) {
				objects.addAll(views);
				offset = viewOffset;
			}

			// click on that object
			for (int i = 0; i < objects.size(); i++) {
				float leftEdge = selectionArea.getX() + (i) * selectionArea.getHeight() - offset;
				float rightEdge = selectionArea.getX() + (i + 1) * selectionArea.getHeight() - offset;
				if (x > leftEdge && x < rightEdge) {
					if (editor.currentTool instanceof TileTool) {
						editor.currentTile = (TileHandler) objects.get(i);
					} else if (editor.currentTool instanceof ImageTool) {
						editor.currentImage = (ImageHandler) objects.get(i);
					} else if (editor.currentTool instanceof EventTool) {
						editor.currentEvent = (EventHandler) objects.get(i);
					} else if (editor.currentTool instanceof ViewTool) {
						editor.currentView = (View) objects.get(i);
					}
				}
			}
		}
	}

	public void touchMoved(ArrayList<PVector> touch) {
		if (touch.size() == 1 && p.mouseY >= selectionArea.getY()) {
			if (editor.currentTool instanceof TileTool) {
				tileOffset += (p.pmouseX - p.mouseX) / 3;
			} else if (editor.currentTool instanceof ImageTool) {
				imageOffset += (p.pmouseX - p.mouseX) / 3;
			} else if (editor.currentTool instanceof EventTool) {
				eventOffset += (p.pmouseX - p.mouseX) / 3;
			} else if (editor.currentTool instanceof ViewTool && views.size() > 0) {
				viewOffset += (p.pmouseX - p.mouseX) / 3;
			}
		}
	}

	public void touchEnded() {
		// check for clicking on widgets
		for (int i = 0; i < widgets.size(); i++) {
			widgets.get(i).click();
		}
	}

	public float getHeight() {
		return selectionArea.getHeight();
	}
}
