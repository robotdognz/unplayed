package editor.tools;

import game.Game;
import game.PageView;
import misc.Converter;
import objects.Page;
import objects.Rectangle;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import editor.Editor;
import editor.Editor.editorMode;
import editor.uiside.EditorSide;

public class PageTool extends AreaTool {
	// extends AreaTool because it functions like an AreaTool when making views
	private Converter convert;
	private Game game;
	private EditorSide editorSide;
	private PageView pageView;
	private Page currentPage;

	// variables for adjusting selected page
//	private float pX = 0;
//	private float pY = 0;

	public PageTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.editorSide = (EditorSide) editor.editorSide;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		this.currentPage = null;
	}

	@Override
	public void touchMoved(PVector touch) {
		if (!editor.showPageView) {// views
			if (editor.selected != null && editor.selected instanceof View
					&& editor.eMode == editorMode.SELECT) {
//				edit = editor.selected;
				super.touchMoved(touch);
			} else {
				edit = null;
			}
			super.touchMoved(touch);
		} else { // pages
			if (!editorSide.adjust) {
				if (editor.eMode == editorMode.ADD) {
					if (editor.currentView != null) {
						if (currentPage == null) {
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// offset placement by 50
							float finalX = placement.x - 50;
							float finalY = placement.y - 50;
							PVector center = new PVector(finalX, finalY);
							currentPage = new Page(p, game, editor.currentView.getTopLeft(),
									editor.currentView.getBottomRight(), center);
						} else {
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// round so blocks snap to grid
							float finalX = placement.x - 50;
							float finalY = placement.y - 50;
							PVector center = new PVector(finalX, finalY);
							currentPage.setPosition(center);
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

	@Override
	public void touchEnded(PVector touch) {
		if (!editor.showPageView) { // views
			if (editor.eMode == editorMode.ADD) {
				addView(touch);
			} else if (editor.eMode == editorMode.ERASE) {
				eraseView();
			} else if (editor.eMode == editorMode.SELECT) {
				selectView();
			}
		} else {// pages
			if (!editorSide.adjust) {
				if (editor.eMode == editorMode.ADD) {
					addPage();
				} else if (editor.eMode == editorMode.ERASE) {
					erasePage();
				} else if (editor.eMode == editorMode.SELECT) {
					selectPage();
				}
				currentPage = null;
			}
		}
	}

	@Override
	public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
		// page resize
		if (editor.showPageView && editorSide.adjust) {
			if (editor.selected != null && editor.selected instanceof Page) {
				((Page) editor.selected).addSize(convert.screenToLevel(d) / 500); // TODO: figure out what the 500
																					// should be
				//old code
				PVector center = convert.screenToLevel(x, y);
				((Page) editor.selected).setPosition(center);
				
//				if (pX != 0 && pY != 0) {
//					float xDist = x-pX;
//					float yDist = y-pY;
//					xDist = convert.screenToLevel(xDist);
//					yDist = convert.screenToLevel(yDist);
//					((Page) editor.selected).addPosition(xDist, yDist);
//				}
//				pX = x;
//				pY = y;
			}
		}
	}

	@Override
	public void onRotate(float x, float y, float angle) {
		// page rotate
		if (editor.showPageView && editorSide.adjust) {
			if (editor.selected != null && editor.selected instanceof Page) {
				((Page) editor.selected).addAngle(PApplet.degrees(angle));
			}
		}
	}
	
	private void addView(PVector touch) {
		// get the result of the area tool
		super.touchEnded(touch);
		Rectangle result = (Rectangle) super.getResult();
		if (result != null) {
			// check if there is already a matching view
			for (View view : game.views) {
				// if matching view found select it and return
				if (view.getX() != result.getX()) {
					continue;
				}
				if (view.getY() != result.getY()) {
					continue;
				}
				if (view.getWidth() != result.getWidth()) {
					continue;
				}
				if (view.getHeight() != result.getHeight()) {
					continue;
				}
				editor.selected = view; // select the view
				editor.currentView = view;
				return;
			}
			
			//no existing match found, make a new view
			View newView = new View(p, (int) result.getX(), (int) result.getY(), (int) result.getWidth(),
					(int) result.getHeight());
			game.views.add(newView); // add the view
			editor.selected = newView; // select the view
			editor.currentView = newView;
		}
	}

	private void eraseView() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		View found = game.getView(mouse.x, mouse.y);
		if (found != null) {
			// remove the view
			game.views.remove(found);
			// deselect it if it is selected
			if (found.equals(editor.selected)) {
				editor.selected = null;
			}
			// find and erase all matching pages
			List<Page> pages = pageView.getPages();
			for (int i = 0; i < pages.size(); i++) {
				if (pages.get(i).getX() != found.getX()) {
					continue;
				}
				if (pages.get(i).getY() != found.getY()) {
					continue;
				}
				if (pages.get(i).getWidth() != found.getWidth()) {
					continue;
				}
				if (pages.get(i).getHeight() != found.getHeight()) {
					continue;
				}

				// deselect the page if it is selected
				if (pages.get(i).equals(editor.selected)) {
					editor.selected = null;
				}
				pageView.removePage(pages.get(i));
			}

		}
	}

	private void selectView() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		View found = game.getView(mouse.x, mouse.y);
		if (found != null) {
			editor.selected = found;
			editor.currentView = found;
			edit = found;
		} else {
			editor.selected = null;
			edit = null;
		}
	}

	private void addPage() {
		if (currentPage != null) { // if there is something to create a page from
			pageView.addPage(currentPage);
			editor.selected = currentPage;
			editorSide.adjust = true;
			editor.eMode = Editor.editorMode.SELECT;
		}
	}

	private void erasePage() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		Page found = pageView.getPage(mouse.x, mouse.y);
		if (found != null) {
			pageView.removePage(found);
			if (found.equals(editor.selected)) {
				editor.selected = null;
			}
		}
	}

	private void selectPage() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		Page found = pageView.getPage(mouse.x, mouse.y);
		if (found != null) {
			editor.selected = found; //select it
			//set current view to corresponding view
			for (View view : game.views) {
				if (view.getX() != found.getX()) {
					continue;
				}
				if (view.getY() != found.getY()) {
					continue;
				}
				if (view.getWidth() != found.getWidth()) {
					continue;
				}
				if (view.getHeight() != found.getHeight()) {
					continue;
				}
				editor.currentView = view;
				return;
			}
		} else {
			editor.selected = null;
		}
	}

	@Override
	public void draw() {
		if (!editor.showPageView) { // views
			super.draw();
		} else { // pages
			if (currentPage != null) {
				currentPage.step();
				currentPage.draw(80); // draw the page at lowest LOD
			}
		}
	}

	@Override
	public Object getResult() {
		return null;
	}

}
