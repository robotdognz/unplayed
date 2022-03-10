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

	private PImage iconBackground;
	private NewViewButton newViewButton;

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

		String folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
		iconBackground = p.loadImage(folder + "inactive.png");

		newViewButton = new NewViewButton(p);
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

			if (Editor.showPageView) {
				// do nothing in page view
			} else {
				// add new view button object to end of objects list
				objects.add(newViewButton);
			}
		}

		// draw scroll bar for that type
		p.pushMatrix();
		p.imageMode(CENTER);
		p.rectMode(CENTER);
		p.translate(-offset, 0);

		float objectWidth = selectionArea.getHeight();
		float currentY = selectionArea.getY() + objectWidth * 0.5f; // y position for object
		float areaLeftEdge = selectionArea.getTopLeft().x;
		float areaRightEdge = selectionArea.getBottomRight().x;

		// tap function icon system
		PImage cornerIcon = null;
		if (Editor.showPageView) { // in page view

		} else { // in level view
			// pencil in corners of views
			if (editor.currentTool instanceof TileTool) {
				if (editor.eMode == Editor.editorMode.ADD && editor.controller instanceof EditorControl) {
					String folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
					cornerIcon = p.loadImage(folder + "rotateClockwise.png");
				}
			} else if (editor.currentTool instanceof PageTool) {
				String folder = "ui" + '/' + "widgets" + '/'; // data path of widget icons
				cornerIcon = p.loadImage(folder + "PlaceBlock.png");
			}
		}

		for (int i = 0; i < objects.size(); i++) {
			Object object = objects.get(i);

			// x position for object
			float currentX = areaLeftEdge + objectWidth * 0.5f + (i * objectWidth);

			// check if the object is off screen, if so, don't draw it
			float objectLeftEdge = (currentX - objectWidth * 0.5f) - offset;
			float objectRightEdge = (currentX + objectWidth * 0.5f) - offset;
			if (objectLeftEdge > areaRightEdge || objectRightEdge < areaLeftEdge) {
				continue;
			}

			if (object.equals(currentHandler)) { // if this is the selected object
				// draw highlight behind
				p.noStroke();
				p.fill(0, 0, 0, 120);
				p.rect(currentX, currentY, objectWidth, objectWidth);
			}

			if (object instanceof Handler) {
				((Handler) object).drawEditor(currentX, currentY, size);
				if (cornerIcon != null && object instanceof TileHandler && object.equals(currentHandler)) {
					drawFunctionIcon(cornerIcon, currentX, currentY, objectWidth);
				}

			} else if (object instanceof View) {
				((View) object).drawToolbar(currentX, currentY, size);
				if (cornerIcon != null) {
					drawFunctionIcon(cornerIcon, currentX, currentY, objectWidth);
				}

			} else if (object instanceof NewViewButton) {
				// TODO: draw new view button
				((NewViewButton) object).draw(p, currentX, currentY, objectWidth);
			}
		}
		p.imageMode(CORNER);
		p.rectMode(CORNER);
		p.popMatrix();
	}

	private void drawFunctionIcon(PImage icon, float currentX, float currentY, float objectWidth) {
		p.imageMode(CENTER);
		p.image(iconBackground, currentX + (objectWidth * 0.30f), currentY - (objectWidth * 0.30f), size * 0.4f,
				size * 0.4f);
		p.tint(75);
		p.image(icon, currentX + (objectWidth * 0.30f), currentY - (objectWidth * 0.30f), size * 0.3f, size * 0.3f);
		p.noTint();
	}

	@Override
	public void onTap(float x, float y) {
		// select object
		if (y >= selectionArea.getY() + 70) { // the 70 acts as padding so clicking on widgets doesn't select stuff

			// figure out what type is being clicked on
			ArrayList<Object> objects = new ArrayList<Object>(); // current objects to draw in the scroll bar
			Float offset = 0.0f;
			if (editor.currentTool instanceof TileTool) {
				if (Editor.showPageView) {
					objects.addAll(loadings);
					offset = loadingOffset;
				} else {
					objects.addAll(tiles);
					offset = tileOffset;
				}

			} else if (editor.currentTool instanceof ImageTool) {

				if (Editor.showPageView) {
					objects.addAll(backgrounds);
					offset = backgroundOffset;
				} else {
					objects.addAll(images);
					offset = imageOffset;
				}

			} else if (editor.currentTool instanceof EventTool) {

				objects.addAll(events);
				offset = eventOffset;

			} else if (editor.currentTool instanceof PageTool) {
				if (Editor.showPageView) {
					// do nothing in page view
				} else {
					// pencil tool when clicking on no view in level view
					editor.controller = new EditorControl(p, editor);
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
							// do 90 degree rotation of tile handler

							if (objects.get(i).equals(editor.currentTile)) {
								// clicking on the already selected tile
								if (editor.eMode == Editor.editorMode.ADD
										&& editor.controller instanceof EditorControl) {
									// parameters are correct for rotation to happen
									TileHandler currentHandler = (TileHandler) objects.get(i);
									int angle = currentHandler.getEditorAngle() + 90;
									currentHandler.setEditorAngle(angle);
								}
							}

							editor.currentTile = (TileHandler) objects.get(i);

							// switch to pencil mode when selecting a new tile
							editor.controller = new EditorControl(p, editor);
							editor.editorSide.clearExternalModes();
							editor.eMode = editorMode.ADD;
						}
					} else if (editor.currentTool instanceof ImageTool) {

						if (Editor.showPageView) {
							editor.currentBackground = (BackgroundHandler) objects.get(i);
						} else {
							editor.currentImage = (ImageHandler) objects.get(i);

							// switch to pencil mode when selecting a new image
							editor.controller = new EditorControl(p, editor);
							editor.editorSide.clearExternalModes();
							editor.eMode = editorMode.ADD;
						}

					} else if (editor.currentTool instanceof EventTool) {
						editor.currentEvent = (EventHandler) objects.get(i);

						editor.controller = new EditorControl(p, editor);
						editor.editorSide.clearExternalModes();
						editor.eMode = editorMode.ADD;

					} else if (editor.currentTool instanceof PageTool) {
						if (Editor.showPageView) {
							View view = (View) objects.get(i);
							editor.currentView = view;

							// enable add mode so that you can place the page right away
							editor.controller = new EditorControl(p, editor);
							editor.editorSide.clearExternalModes();
							editor.eMode = editorMode.ADD;

						} else {
							View view = (View) objects.get(i);
							editor.currentView = view;

							// enable select mode so that you can resize the view right away
							editor.controller = new EditorControl(p, editor);
							editor.editorSide.clearExternalModes();
							editor.eMode = editorMode.SELECT;
							// update selected and page tool so that resizing can happen right away
							editor.selected = view;
							// pass it to the page tool area system
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
