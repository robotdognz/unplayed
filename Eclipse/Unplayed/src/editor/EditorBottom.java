package editor;

import objects.Rectangle;
import processing.core.*;
import ui.*;

import static processing.core.PConstants.*;

import java.util.ArrayList;

import controllers.EditorControl;
import editor.Editor.editorMode;
import editor.Editor.editorType;
import handlers.EventHandler;
import handlers.Handler;
import handlers.ImageHandler;
import handlers.TextureCache;
import handlers.TileHandler;
import menus.Menu;

public class EditorBottom extends Toolbar {
	private PApplet p;
	private Rectangle objectArea; // TODO: rename
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

	public EditorBottom(PApplet p, Editor editor, TextureCache texture) {
		super(editor);
		this.p = p;
		folder = p.dataPath("ui") + '/';

		// setup widgets
		this.eWidgets = new ArrayList<Widget>();
		Widget blockW = new TileModeWidget(p, editor, this);
		Widget imageW = new ImageModeWidget(p, editor, this);
		Widget eventW = new EventModeWidget(p, editor, this);
		eWidgets.add(blockW);
		eWidgets.add(imageW);
		eWidgets.add(eventW);

		eWidgetOffset = p.width * 0.71f;
		eWidgetSpacing = 140; // TODO: get the spacing right

		// setup toolbar
		int objectAreaHeight = 230; // 200
		objectArea = new Rectangle(0, p.height - objectAreaHeight, p.width, objectAreaHeight);
		toolbar = p.requestImage(folder + "icn_toolbar_bg.png");
		tab = p.requestImage(folder + "icn_tab.png");
		tabSize = 220;

		widgetHeight = objectArea.getY() - 53; // TODO: get the height right

		// scroll bars
		size = 150;
		tiles = texture.getTileList();
		images = texture.getImageList();
		events = texture.getEventList();
	}

	public void draw(PVector touch, Menu menu) {
		for (int i = 0; i < eWidgets.size(); i++) {
			// draw the two behind tabs
			if (!eWidgets.get(i).isActive()) {
				p.imageMode(CENTER);
				p.image(tab, eWidgetOffset + eWidgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
		}

		p.imageMode(CORNER);
		p.image(toolbar, objectArea.getX(), objectArea.getY(), objectArea.getWidth(), objectArea.getHeight());

		// widgets

		for (int i = 0; i < eWidgets.size(); i++) {
			// if current widget is active, draw tab at the current x position
			p.imageMode(CENTER);
			if (eWidgets.get(i).isActive()) {
				p.image(tab, eWidgetOffset + eWidgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
			eWidgets.get(i).draw(eWidgetOffset + eWidgetSpacing * i, widgetHeight);
			eWidgets.get(i).updateActive();

			if (menu == null) {
				eWidgets.get(i).hover(touch);
			}
		}
		p.imageMode(CORNER);

		// figure out what type to show
		ArrayList<Handler> objects = new ArrayList<Handler>(); // current objects to draw in the scroll bar
		Float offset = 0.0f;
		Handler currentHandler = null;
		if (editor.eType == editorType.TILE) {
			objects.addAll(tiles);
			offset = tileOffset;
			currentHandler = editor.currentTile;
		} else if (editor.eType == editorType.IMAGE) {
			objects.addAll(images);
			offset = imageOffset;
			currentHandler = editor.currentImage;
		} else if (editor.eType == editorType.EVENT) {
			objects.addAll(events);
			offset = eventOffset;
			currentHandler = editor.currentEvent;
		}

		// draw scroll bar for that type
		p.pushMatrix();
		p.imageMode(CENTER);
		p.rectMode(CENTER);
		p.translate(-offset, 0);
		for (int i = 0; i < objects.size(); i++) {
			Handler object = objects.get(i);
			if (object.equals(currentHandler)) { // if this is the selected object
				// draw highlight behind
				p.noStroke();
				p.fill(0, 0, 0, 120);
				p.rect(objectArea.getX() + objectArea.getHeight() / 2 + i * objectArea.getHeight(),
						objectArea.getY() + objectArea.getHeight() / 2, objectArea.getHeight(), objectArea.getHeight());
			}
			object.draw(objectArea.getX() + objectArea.getHeight() / 2 + i * objectArea.getHeight(),
					objectArea.getY() + objectArea.getHeight() / 2, size);
		}
		p.imageMode(CORNER);
		p.rectMode(CORNER);
		p.popMatrix();
	}

	public void onTap(float x, float y) {
		// select object
		if (y >= objectArea.getY()) {
			editor.eController = new EditorControl(p, editor);
			editor.eMode = editorMode.ADD;

			// figure out what type is being clicked on
			ArrayList<Handler> objects = new ArrayList<Handler>(); // current objects to draw in the scroll bar
			Float offset = 0.0f;
			if (editor.eType == editorType.TILE) {
				objects.addAll(tiles);
				offset = tileOffset;
			} else if (editor.eType == editorType.IMAGE) {
				objects.addAll(images);
				offset = imageOffset;
			} else if (editor.eType == editorType.EVENT) {
				objects.addAll(events);
				offset = eventOffset;
			}

			// click on that object
			for (int i = 0; i < objects.size(); i++) {
				float leftEdge = objectArea.getX() + (i) * objectArea.getHeight() - offset;
				float rightEdge = objectArea.getX() + (i + 1) * objectArea.getHeight() - offset;
				if (x > leftEdge && x < rightEdge) {
					if (editor.eType == editorType.TILE) {
						editor.currentTile = (TileHandler) objects.get(i);
					} else if (editor.eType == editorType.IMAGE) {
						editor.currentImage = (ImageHandler) objects.get(i);
					} else if (editor.eType == editorType.EVENT) {
						editor.currentEvent = (EventHandler) objects.get(i);
					}
				}
			}
		}
	}

	public void touchMoved(ArrayList<PVector> touch) {
		if (touch.size() == 1 && p.mouseY >= objectArea.getY()) {
			if (editor.eType == editorType.TILE) {
				tileOffset += (p.pmouseX - p.mouseX) / 3;
			} else if (editor.eType == editorType.IMAGE) {
				imageOffset += (p.pmouseX - p.mouseX) / 3;
			} else if (editor.eType == editorType.EVENT) {
				eventOffset += (p.pmouseX - p.mouseX) / 3;
			}
		}
	}

	public void touchEnded() {
		// check for clicking on widgets
		for (int i = 0; i < eWidgets.size(); i++) {
			eWidgets.get(i).click();
		}
	}

	public float getHeight() {
		return objectArea.getHeight();
	}
}
