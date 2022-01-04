package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;

import editor.DebugOutput;
import editor.Editor;
import editor.Tool;
import editor.Editor.editorMode;
import editor.uiside.EditorSide;
import game.Game;
import game.PageView;
import handlers.TextureCache;
import misc.Converter;
import objects.Background;
import objects.Image;
import objects.Page;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class ImageTool implements Tool {
	Editor editor;
	Game game;
	TextureCache texture;
	
	PApplet p;
	private Converter convert;
	private EditorSide editorSide;
	private Background currentBackground;
	private PageView pageView;

	public ImageTool(PApplet p, Editor editor) {
		this.p = p;
		this.editor = editor;
		this.game = editor.game;
		this.texture = editor.texture;
		this.editorSide = (EditorSide) editor.editorSide;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		this.currentBackground = null;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (!editor.showPageView) {// images
			if (editor.point != null) {
				// figure out what to insert
				Image toInsert = null;
				if (editor.currentImage != null) {
					// create correct image

					// TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
					// objects are stored as 100x100 etc. This should be fixed so everything uses
					// the 1x1 system. Then remove the * 100 from the below line
					toInsert = new Image(texture, editor.currentImage.getFile(), (int) editor.point.getX(),
							(int) editor.point.getY(), editor.currentImage.getWidth() * 100,
							editor.currentImage.getHeight() * 100);
				} else {
					// use blank image
					toInsert = new Image(null, null, (int) editor.point.getX(), (int) editor.point.getY(), 100, 100);
				}

				// get all rectangles that overlap toInsert and pass them to the right method
				if (editor.point != null) {
					HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
					editor.world.retrieve(getRectangles, toInsert);

					if (editor.eMode == editorMode.ADD) { // adding image
						add(toInsert, getRectangles);
					} else if (editor.eMode == editorMode.ERASE) { // erasing image
						erase(toInsert, getRectangles);
					} else if (editor.eMode == editorMode.SELECT) { // selecting image
						select(toInsert, getRectangles);
					}
					editor.point = null;
				}
			}
		} else { // backgrounds
			if (!editorSide.adjust) {
				if (editor.eMode == editorMode.ADD) {
					if (editor.currentView != null) {
						if (currentBackground == null) {
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// offset placement by 50
							float finalX = placement.x - 50;
							float finalY = placement.y - 50;
							PVector center = new PVector(finalX, finalY);
							currentBackground = new Background(p, texture, editor.currentBackground.getFile(), center);
						} else {
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// round so blocks snap to grid
							float finalX = placement.x - 50;
							float finalY = placement.y - 50;
							PVector center = new PVector(finalX, finalY);
							currentBackground.setPosition(center);
						}
					}
				}
			} else {
				// adjust the page with a single finger
				if (editor.selected != null && editor.selected instanceof Page) {
					float xDist = p.mouseX - p.pmouseX;
					float yDist = p.mouseY - p.pmouseY;
					xDist = convert.screenToLevel(xDist / 3);
					yDist = convert.screenToLevel(yDist / 3);
					((Page) editor.selected).addPosition(xDist, yDist);
				}
			}
		}
	}

	// TODO: image adding should only erase those underneath if they perfectly
	// overlap
	private void add(Image toInsert, HashSet<Rectangle> getRectangles) {
		// find anything that directly overlaps the inserting image
		Image foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = (Image) p;
			}
		}
		// remove what was found and place the new image
		if (editor.currentImage != null) {
			if (foundAtPoint != null) {
				editor.world.remove(foundAtPoint);
			}
			editor.world.insert(toInsert);
		}

		// select the newly inserted image
		if (toInsert.getFile() != null) {
			editor.selected = toInsert;
		} else {
			editor.selected = null;
		}
	}

	// TODO: image erasing should always use a 100x100 image
	private void erase(Image toInsert, HashSet<Rectangle> getRectangles) {
		for (Rectangle p : getRectangles) {
			// if the rectangle overlaps toInsert, remove it
			if (!(p instanceof Image)) {
				continue;
			}
			if (p.getTopLeft().x > toInsert.getBottomRight().x - 1) {
				continue;
			}
			if (p.getBottomRight().x < toInsert.getTopLeft().x + 1) {
				continue;
			}
			if (p.getTopLeft().y > toInsert.getBottomRight().y - 1) {
				continue;
			}
			if (p.getBottomRight().y < toInsert.getTopLeft().y + 1) {
				continue;
			}
			editor.world.remove(p);
			if (p.equals(editor.selected)) {
				editor.selected = null;
			}
		}

	}

	// TODO: image selecting should always use a 100x100 image
	private void select(Image toInsert, HashSet<Rectangle> getRectangles) {
		// if there is noting to check
		if (getRectangles.size() < 1) {
			editor.selected = null;
			return;
		}

		// if there are things to check
		// try to find exact match
		Rectangle foundAtPoint = null;
		for (Rectangle p : getRectangles) {
			if (p.getTopLeft().x == toInsert.getX() && p.getTopLeft().y == toInsert.getY()
					&& toInsert.getClass().equals(p.getClass())) {
				foundAtPoint = p;
			}
		}

		if (foundAtPoint != null) {
			// if it found an exact match
			editor.selected = foundAtPoint;
		} else {
			// if there is no exact match, look for overlaps
			for (Rectangle p : getRectangles) {
				if (!(p instanceof Image)) {
					continue;
				}
				if (p.getTopLeft().x > toInsert.getBottomRight().x - 1) {
					continue;
				}
				if (p.getBottomRight().x < toInsert.getTopLeft().x + 1) {
					continue;
				}
				if (p.getTopLeft().y > toInsert.getBottomRight().y - 1) {
					continue;
				}
				if (p.getBottomRight().y < toInsert.getTopLeft().y + 1) {
					continue;
				}
				// select the first overlap
				editor.selected = p;
				return;
			}
			// nothing was found, select nothing
			editor.selected = null;
		}

	}

	@Override
	public void touchEnded(PVector touch) {

		if (editor.showPageView) { // backgrounds
			DebugOutput.pushMessage("Image tool touch", 3);
//			if (!editorSide.adjust) {
			if (editor.eMode == editorMode.ADD) {
				addBackground();
			} else if (editor.eMode == editorMode.ERASE) {
//					eraseBackground();
			} else if (editor.eMode == editorMode.SELECT) {
//					selectBackground();
			}
//				currentPage = null;
//			}
		}
	}

	private void addBackground() {
		if (currentBackground != null) { // if there is something to create a page from
			pageView.addBackground(currentBackground);
			editor.selected = currentBackground;
			editorSide.adjust = true;
			editor.eMode = Editor.editorMode.SELECT;
			DebugOutput.pushMessage("Background added", 3);
		}
	}

	@Override
	public void draw() {
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
	}

	@Override
	public void onRotate(float x, float y, float angle) {
	}
}
