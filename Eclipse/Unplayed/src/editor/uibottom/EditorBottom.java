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
import game.AppLogic;
import editor.tools.PageTool;
import handlers.BackgroundHandler;
import handlers.EventHandler;
import handlers.Handler;
import handlers.ImageHandler;
import handlers.LoadingHandler;
import handlers.TextureCache;
import handlers.TileHandler;

public class EditorBottom extends Toolbar {
	private Rectangle selectionArea;
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
	private ArrayList<BackgroundHandler> backgrounds; // backgrounds
	private float backgroundOffset;
	private ArrayList<LoadingHandler> loadings; // loading screens
	private float loadingOffset;
	private ArrayList<EventHandler> events; // events
	private float eventOffset;
	public ArrayList<View> views;// views
	private float viewOffset;

	public EditorBottom(PApplet p, Editor editor, TextureCache texture) {
		super(p, editor);
		super.folder = "ui" + '/'; // p.dataPath("ui") + '/';

		// setup widgets
		super.widgets = new ArrayList<Widget>();
		Widget blockW = new WidgetTileMode(p, editor, this);
		Widget imageW = new WidgetImageMode(p, editor, this);
		Widget eventW = new WidgetEventMode(p, editor, this);
		Widget viewW = new WidgetViewMode(p, editor, this);
		widgets.add(blockW);
		widgets.add(imageW);
		widgets.add(eventW);
		widgets.add(viewW);

		super.widgetOffset = p.width * 0.65f;
		super.widgetSpacing = p.width / 10.3f;// 140

		// setup toolbar
		int objectAreaHeight = (int) (p.width / 6.3f);// 230
		this.selectionArea = new Rectangle(0, p.height - objectAreaHeight, p.width, objectAreaHeight);
		this.toolbar = p.requestImage(folder + "icn_toolbar_bg.png");
		this.tab = p.requestImage(folder + "icn_tab.png");
		this.tabSize = (int) (p.width / 6.54f); // 220

		this.widgetHeight = selectionArea.getY() - (p.width / 27);// - 53

		// scroll bars
		this.size = (int) (p.width / 9.6f); // 150
		this.tiles = texture.getTileList();
		this.images = texture.getImageList();
		this.backgrounds = texture.getBackgroundList();
		this.loadings = texture.getLoadingList();
		this.events = texture.getEventList();
		this.views = AppLogic.game.views;

		int boundsHeight = (int) (p.width / 4.11f); // 350
		super.bounds = new Rectangle(0, p.height - boundsHeight, p.width, boundsHeight);
	}

	@Override
	public void draw(PVector touch, Menu menu, float deltaTime) {
		// super.draw(touch, menu);
		for (int i = 0; i < widgets.size(); i++) {
			// draw the two behind tabs
			if (!widgets.get(i).isActive()) {
				p.imageMode(CENTER);
				p.image(tab, widgetOffset + widgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
		}

		p.imageMode(CORNER);
		p.image(toolbar, selectionArea.getX(), selectionArea.getY(), selectionArea.getWidth(),
				selectionArea.getHeight());

		// widgets

		for (int i = 0; i < widgets.size(); i++) {
			// if current widget is active, draw tab at the current x position
			p.imageMode(CENTER);
			if (widgets.get(i).isActive()) {
				p.image(tab, widgetOffset + widgetSpacing * i, widgetHeight, tabSize, tabSize);
			}
			widgets.get(i).draw(deltaTime, widgetOffset + widgetSpacing * i, widgetHeight);
			widgets.get(i).updateActive();

			if (menu == null) {
				widgets.get(i).hover(touch);
			}
		}
		p.imageMode(CORNER);

		// figure out what type to show
		ArrayList<Object> objects = new ArrayList<Object>(); // current objects to draw in the scroll bar
		Float offset = 0.0f;
		Object currentHandler = null;
		if (editor.currentTool instanceof TileTool) {
			if (Editor.showPageView) {
				objects.addAll(loadings);
				offset = loadingOffset;
//				currentHandler = editor.currentLoading;
				currentHandler = AppLogic.game.currentLoading;
			} else {
				objects.addAll(tiles);
				offset = tileOffset;
				currentHandler = editor.currentTile;
			}
		} else if (editor.currentTool instanceof ImageTool) {

			if (Editor.showPageView) {
				objects.addAll(backgrounds);
				offset = backgroundOffset;
				currentHandler = editor.currentBackground;
			} else {
				objects.addAll(images);
				offset = imageOffset;
				currentHandler = editor.currentImage;
			}

		} else if (editor.currentTool instanceof EventTool) {
			objects.addAll(events);
			offset = eventOffset;
			currentHandler = editor.currentEvent;
		} else if (editor.currentTool instanceof PageTool) {
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
						selectionArea.getY() + selectionArea.getHeight() / 2, selectionArea.getHeight(),
						selectionArea.getHeight());
			}
			if (object instanceof Handler) {
				((Handler) object).drawEditor(
						selectionArea.getX() + selectionArea.getHeight() / 2 + i * selectionArea.getHeight(),
						selectionArea.getY() + selectionArea.getHeight() / 2, size);
			} else if (object instanceof View) {
				((View) object).drawToolbar(
						selectionArea.getX() + selectionArea.getHeight() / 2 + i * selectionArea.getHeight(),
						selectionArea.getY() + selectionArea.getHeight() / 2, size);
			}
		}
		p.imageMode(CORNER);
		p.rectMode(CORNER);
		p.popMatrix();
	}

	@Override
	public void onTap(float x, float y) {
		// select object
		if (y >= selectionArea.getY() + 70) { // the 70 acts as padding so clicking on widgets doesn't select stuff
			editor.controller = new EditorControl(p, editor);
			// editor.eMode = editorMode.ADD; this is in the below so it doesn't happen when
			// selecting a loading screen. It is preceded by
			// editor.editorSide.clearExternalModes(); so that it isn't overwritten by code
			// in editorSide

			// figure out what type is being clicked on
			ArrayList<Object> objects = new ArrayList<Object>(); // current objects to draw in the scroll bar
			Float offset = 0.0f;
			if (editor.currentTool instanceof TileTool) {
				if (Editor.showPageView) {
					objects.addAll(loadings);
					offset = loadingOffset;
				} else {
//					editor.editorSide.clearExternalModes();
//					editor.eMode = editorMode.ADD;

					objects.addAll(tiles);
					offset = tileOffset;
				}

			} else if (editor.currentTool instanceof ImageTool) {
//				editor.editorSide.clearExternalModes();
//				editor.eMode = editorMode.ADD;

				if (Editor.showPageView) {
					objects.addAll(backgrounds);
					offset = backgroundOffset;
				} else {
					objects.addAll(images);
					offset = imageOffset;
				}

			} else if (editor.currentTool instanceof EventTool) {
//				editor.editorSide.clearExternalModes();
//				editor.eMode = editorMode.ADD;

				objects.addAll(events);
				offset = eventOffset;

			} else if (editor.currentTool instanceof PageTool) {
				if (Editor.showPageView) {
					// do nothing in page view
				} else {
					// pencil tool when clicking on no view in level view
					editor.editorSide.clearExternalModes();
					editor.eMode = editorMode.ADD;
				}

				objects.addAll(views);
				offset = viewOffset;
			}

			// click on that object
			for (int i = 0; i < objects.size(); i++) {
				float leftEdge = selectionArea.getX() + (i) * selectionArea.getHeight() - offset;
				float rightEdge = selectionArea.getX() + (i + 1) * selectionArea.getHeight() - offset;
				if (x > leftEdge && x < rightEdge) {

					if (editor.currentTool instanceof TileTool) {
						if (Editor.showPageView) {
							AppLogic.game.currentLoading = (LoadingHandler) objects.get(i);

						} else {
							editor.currentTile = (TileHandler) objects.get(i);
							
							// switch to pencil mode when selecting a new tile
							editor.editorSide.clearExternalModes();
							editor.eMode = editorMode.ADD;
						}
					} else if (editor.currentTool instanceof ImageTool) {

						if (Editor.showPageView) {
							editor.currentBackground = (BackgroundHandler) objects.get(i);
						} else {
							editor.currentImage = (ImageHandler) objects.get(i);
							
							// switch to pencil mode when selecting a new image
							clearExternalModes();
							editor.eMode = editorMode.ADD;
						}

					} else if (editor.currentTool instanceof EventTool) {
						editor.currentEvent = (EventHandler) objects.get(i);
						
						editor.editorSide.clearExternalModes();
						editor.eMode = editorMode.ADD;

					} else if (editor.currentTool instanceof PageTool) {
						editor.currentView = (View) objects.get(i);
						if (!Editor.showPageView) {
							View view = (View) objects.get(i);
							editor.currentView = view;
							// select mode so that you can resize the view right away
							editor.eMode = editorMode.SELECT;
							// update selected and page tool so that resizing can hapen right away
							editor.selected = view;
							((PageTool) editor.currentTool).edit = view;
						}
					}
				}
			}

		}
	}

	@Override
	public void touchMoved(ArrayList<PVector> touch) {
		if (touch.size() == 1 && p.mouseY >= selectionArea.getY()) {
			if (editor.currentTool instanceof TileTool) {
				if (Editor.showPageView) {

					float objectsWidth = loadings.size() * selectionArea.getHeight();
					if (objectsWidth > selectionArea.getWidth()) {
						// scroll
						loadingOffset += (p.pmouseX - p.mouseX) / 3;
						// prevent scrolling off right edge
						if (loadingOffset > objectsWidth - selectionArea.getWidth() + 1) {
							loadingOffset = objectsWidth - selectionArea.getWidth();
						}
						// prevent scrolling off left edge
						if (loadingOffset < 0) {
							loadingOffset = 0;
						}
					}

				} else {

					float objectsWidth = tiles.size() * selectionArea.getHeight();
					if (objectsWidth > selectionArea.getWidth()) {
						// scroll
						tileOffset += (p.pmouseX - p.mouseX) / 3;
						// prevent scrolling off right edge
						if (tileOffset > objectsWidth - selectionArea.getWidth() + 1) {
							tileOffset = objectsWidth - selectionArea.getWidth();
						}
						// prevent scrolling off left edge
						if (tileOffset < 0) {
							tileOffset = 0;
						}
					}

				}

			} else if (editor.currentTool instanceof ImageTool) {

				if (Editor.showPageView) {

					float objectsWidth = backgrounds.size() * selectionArea.getHeight();
					if (objectsWidth > selectionArea.getWidth()) {
						// scroll
						backgroundOffset += (p.pmouseX - p.mouseX) / 3;
						// prevent scrolling off right edge
						if (backgroundOffset > objectsWidth - selectionArea.getWidth() + 1) {
							backgroundOffset = objectsWidth - selectionArea.getWidth();
						}
						// prevent scrolling off left edge
						if (backgroundOffset < 0) {
							backgroundOffset = 0;
						}
					}

				} else {

					float objectsWidth = images.size() * selectionArea.getHeight();
					if (objectsWidth > selectionArea.getWidth()) {
						// scroll
						imageOffset += (p.pmouseX - p.mouseX) / 3;
						// prevent scrolling off right edge
						if (imageOffset > objectsWidth - selectionArea.getWidth() + 1) {
							imageOffset = objectsWidth - selectionArea.getWidth();
						}
						// prevent scrolling off left edge
						if (imageOffset < 0) {
							imageOffset = 0;
						}
					}

				}

			} else if (editor.currentTool instanceof EventTool) {
				float objectsWidth = events.size() * selectionArea.getHeight();
				if (objectsWidth > selectionArea.getWidth()) {
					// scroll
					eventOffset += (p.pmouseX - p.mouseX) / 3;
					// prevent scrolling off right edge
					if (eventOffset > objectsWidth - selectionArea.getWidth() + 1) {
						eventOffset = objectsWidth - selectionArea.getWidth();
					}
					// prevent scrolling off left edge
					if (eventOffset < 0) {
						eventOffset = 0;
					}
				}
			} else if (editor.currentTool instanceof PageTool && views.size() > 0) {
				float objectsWidth = views.size() * selectionArea.getHeight();
				if (objectsWidth > selectionArea.getWidth()) {
					// scroll
					viewOffset += (p.pmouseX - p.mouseX) / 3;
					// prevent scrolling off right edge
					if (viewOffset > objectsWidth - selectionArea.getWidth() + 1) {
						viewOffset = objectsWidth - selectionArea.getWidth();
					}
					// prevent scrolling off left edge
					if (viewOffset < 0) {
						viewOffset = 0;
					}
				}
			}
		}
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
		return selectionArea.getHeight();
	}
}
