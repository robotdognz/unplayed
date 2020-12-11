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

	public PageTool(PApplet p, Editor editor) {
		super(p, editor);
		this.game = editor.game;
		this.editorSide = (EditorSide) editor.editorSide;
		this.pageView = game.getPageView();
		this.convert = editor.convert;
		this.currentPage = null;
	}

	@Override
	public void touchMoved() {
		if (!editor.showPageView) {// views
			super.touchMoved();
		} else { // pages
			if (!editorSide.adjust) {
				if (editor.eMode == editorMode.ADD) {
					if (editor.currentView != null) {
						if (currentPage == null) {
							float snapNo = 10;
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// round so blocks snap to grid
							float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
							float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;
							PVector center = new PVector(finalX, finalY);
							currentPage = new Page(p, game, editor.currentView.getTopLeft(),
									editor.currentView.getBottomRight(), center);
						} else {
							float snapNo = 10;
							PVector placement = convert.screenToLevel(p.mouseX, p.mouseY);
							// round so blocks snap to grid
							float finalX = Math.round((placement.x - 50) / snapNo) * snapNo;
							float finalY = Math.round((placement.y - 50) / snapNo) * snapNo;
							PVector center = new PVector(finalX, finalY);
							currentPage.setPosition(center);
						}
					}
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
				((Page) editor.selected).addSize(convert.screenToLevel(d)/p.width/2);
				PVector center = convert.screenToLevel(x, y);
				PApplet.println("" + ((Page) editor.selected).getPosition());
				((Page) editor.selected).setPosition(center);
				PApplet.println("x: " + center.x + " y: " + center.y);
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
				pageView.removePage(pages.get(i));

				// deselect the page if it is selected
				if (pages.get(i).equals(editor.selected)) {
					editor.selected = null;
				}
			}

		}
	}

	private void selectView() {
		PVector mouse = convert.screenToLevel(p.mouseX, p.mouseY);
		View found = game.getView(mouse.x, mouse.y);
		if (found != null) {
			editor.selected = found;
			editor.currentView = found;
		} else {
			editor.selected = null;
		}
	}

	private void addPage() {
		if (currentPage != null) { // if there is something to create a page from
			pageView.addPage(currentPage);
			editor.selected = currentPage;
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
			editor.selected = found;
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
