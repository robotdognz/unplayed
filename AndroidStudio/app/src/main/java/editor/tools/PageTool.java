package editor.tools;

import game.AppLogic;
import game.PageView;
import objects.Page;
import objects.PageViewObject;
import objects.Rectangle;
import objects.View;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import editor.Editor;
import editor.Editor.EditorMode;

public class PageTool extends AreaTool {
    // extends AreaTool because it functions like an AreaTool when making views
    private final PageView pageView;
    private Page currentPage;
    private boolean removalStarted; // have we started using the removal tool yet this touch?
    private boolean currentPageRemoval; // are we adding or removing removals?

    // variables for adjusting selected page
//	private float pX = 0;
//	private float pY = 0;

    public PageTool(PApplet p, Editor editor) {
        super(p, editor);
        this.pageView = AppLogic.game.getPageView();
        this.currentPage = null;
    }

    @Override
    public void touchMoved(PVector touch) {
        if (!Editor.showPageView) {// views
            if (editor.selected != null && editor.selected instanceof View && editor.editorMode == EditorMode.SELECT) {
                super.touchMoved(touch);
            } else {
                edit = null;
            }
            super.touchMoved(touch);
        } else { // pages
            if (!editor.isAdjustMode()) {
                if (editor.editorMode == EditorMode.ADD) {
                    if (editor.currentView != null) {
                        PVector placement = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
                        float finalX = placement.x - 50;
                        float finalY = placement.y - 50;
                        PVector center = new PVector(finalX, finalY);
                        if (currentPage == null) {
                            // offset placement by 50
                            currentPage = new Page(p, AppLogic.game, editor.currentView, center);
                        } else {
                            // round so blocks snap to grid
                            currentPage.setPosition(center);
                        }
                    }
                }
                if (editor.isRemovalMode() && editor.selected != null && editor.selected instanceof Page) {
                    // does a bunch of weird stuff so that setting/unsetting removal squares changes
                    // depending on what type of square you start dragging on

                    Page current = (Page) editor.selected;
                    PVector inLevelTouch = AppLogic.convert.screenToLevel(touch.x, touch.y);

                    // if removal hasn't started, start it
                    if (!removalStarted) {
                        // get current removal type
                        currentPageRemoval = !current.getSquare(inLevelTouch.x, inLevelTouch.y);
                        removalStarted = true;
                    }
                    // toggle removal square in page
                    current.setSquare(inLevelTouch.x, inLevelTouch.y, currentPageRemoval);

                }
            } else {
                // adjust the page with a single finger
                if (editor.selected != null && editor.selected instanceof Page) {
                    Page current = (Page) editor.selected;

                    // make sure the current touch is inside the bounds of the page
                    PVector inLevelTouch = AppLogic.convert.screenToLevel(touch.x, touch.y);
                    boolean isInside = current.isInside(inLevelTouch.x, inLevelTouch.y);
                    if (isInside) {
                        float xDist = p.mouseX - p.pmouseX;
                        float yDist = p.mouseY - p.pmouseY;
                        xDist = AppLogic.convert.screenToLevel(xDist / 3);
                        yDist = AppLogic.convert.screenToLevel(yDist / 3);
                        current.addPosition(xDist, yDist);
                        AppLogic.game.getPageView().resetSystems(); // reset page camera
                    }
                }
            }
        }
    }

    @Override
    public void touchEnded(PVector touch) {
        removalStarted = false;

        if (!Editor.showPageView) { // views
            if (editor.editorMode == EditorMode.ADD) {
                addView(touch);
            } else if (editor.editorMode == EditorMode.ERASE) {
                eraseView();
            } else if (editor.editorMode == EditorMode.SELECT) {
                selectView();
            }
        } else {// pages
            if (editor.editorMode == EditorMode.ADD) {
                addPage();
            } else if (editor.editorMode == EditorMode.ERASE) {
                erasePage();
            } else if (editor.editorMode == EditorMode.SELECT) {
                selectPage();
            } else if (editor.editorMode == EditorMode.EXTERNAL) {
                if (editor.isChildMode()) {
                    addOrRemoveChild();
                }
            }
            currentPage = null;
        }
    }

    @Override
    public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
        // page resize
        if (Editor.showPageView && editor.isAdjustMode()) {
            if (editor.selected != null && editor.selected instanceof Page) {
//				((Page) editor.selected).addSize(AppLogic.convert.screenToLevel(d) / 500);

                PVector center = AppLogic.convert.screenToLevel(x, y);
                ((Page) editor.selected).setPosition(center);
                AppLogic.game.getPageView().resetSystems();

            }
        }
    }

    @Override
    public void onRotate(float x, float y, float angle) {
        // page rotate
        if (Editor.showPageView && editor.isAdjustMode()) {
            if (editor.selected != null && editor.selected instanceof Page) {
                ((Page) editor.selected).addAngle(PApplet.degrees(angle));
                AppLogic.game.getPageView().resetSystems();
            }
        }
    }

    @Override
    public void onTap(float x, float y) {
        if (Editor.showPageView && (editor.isAdjustMode() || editor.isRemovalMode())) {
            // adjusting a page
            PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
            Page found = pageView.getPage(mouse.x, mouse.y);
            if (found != null && found != editor.selected) {
                editor.selected = found; // select it
                // set current view to corresponding view
                editor.currentView = found.getView();
            }
        }
    }

    private void addView(PVector touch) {
        // get the result of the area tool
        super.touchEnded(touch);
        Rectangle result = (Rectangle) super.getResult();
        if (result != null) {
            // check if there is already a matching view
            for (View view : AppLogic.game.views) {
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

            // no existing match found, make a new view
            View newView = new View(p, (int) result.getX(), (int) result.getY(), (int) result.getWidth(),
                    (int) result.getHeight());
            AppLogic.game.views.add(newView); // add the view
            editor.selected = newView; // select the view
            editor.currentView = newView;
        }
    }

    private void eraseView() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        View found = AppLogic.game.getView(mouse.x, mouse.y);
        if (found != null) {
            // remove the view
            AppLogic.game.views.remove(found);
            // deselect it if it is selected
            if (found.equals(editor.selected)) {
                editor.selected = null;
            }

            // remove matching the pages
            List<PageViewObject> pages = pageView.getPageViewObjects();
            for (int i = pages.size() - 1; i >= 0; --i) {
                // only check actual pages
                if (!(pages.get(i) instanceof Page)) {
                    continue;
                }
                Page page = (Page) pages.get(i);

                if (page.getView().equals(found)) {
                    // deselect the page if it is selected
                    if (page.equals(editor.selected)) {
                        editor.selected = null;
                    }
                    // remove the page
                    pages.remove(i);
                }

            }

        }
    }

    private void selectView() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        View found = AppLogic.game.getView(mouse.x, mouse.y);
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
            pageView.addPageViewObject(currentPage);
            editor.selected = currentPage;
            editor.editorMode = EditorMode.SELECT;
            editor.setAdjustMode();
        }
    }

    private void erasePage() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        Page found = pageView.getPage(mouse.x, mouse.y);
        if (found != null) {
            pageView.removePageViewObject(found);
            if (found.equals(editor.selected)) {
                editor.selected = null;
            }
        }
    }

    private void selectPage() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        Page found = pageView.getPage(mouse.x, mouse.y);
        if (found != null) {
            editor.selected = found; // select it
            // set current view to corresponding view
            editor.currentView = found.getView();
            // set editor side to adjust mode
            editor.setAdjustMode();
        } else {
            editor.selected = null;
        }
    }

    private void addOrRemoveChild() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        PageViewObject found = pageView.getPageViewObject(mouse.x, mouse.y);
        if (found != null && editor.selected != null && !editor.selected.equals(found)) {
            if (editor.selected instanceof Page) {
                Page page = (Page) editor.selected;
                page.addOrRemoveChild(found);
            }
        }
    }

    @Override
    public void draw() {
        if (!Editor.showPageView) { // views
            if (editor.selected == null || editor.selected instanceof View) {
                super.draw();
            }
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
