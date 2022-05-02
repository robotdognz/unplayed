package editor.tools;

import java.util.ArrayList;
import java.util.HashSet;
import editor.Editor;
import editor.Tool;
import editor.Editor.EditorMode;
import game.AppLogic;
import game.PageView;
import objects.Background;
import objects.Image;
import objects.Rectangle;
import processing.core.PApplet;
import processing.core.PVector;

public class ImageTool implements Tool {
    Editor editor;
    PApplet p;
    private Background currentBackground;
    private final PageView pageView;

    public ImageTool(PApplet p, Editor editor) {
        this.p = p;
        this.editor = editor;
        this.pageView = AppLogic.game.getPageView();
        this.currentBackground = null;
    }

    @Override
    public void touchMoved(PVector touch) {
        if (!Editor.showPageView) {// images
            if (editor.point != null) {
                // figure out what to insert
                Image toInsert;
                if (editor.currentImage != null) {
                    // create correct image

                    // TODO: textures are stored in grid amounts 1x1 etc, whereas actual world
                    // objects are stored as 100x100 etc. This should be fixed so everything uses
                    // the 1x1 system. Then remove the * 100 from the below line
                    toInsert = new Image(AppLogic.texture, editor.currentImage.getFile(), (int) editor.point.getX(),
                            (int) editor.point.getY(), editor.currentImage.getWidth() * 100,
                            editor.currentImage.getHeight() * 100);
                } else {
                    // use blank image
                    toInsert = new Image(null, null, (int) editor.point.getX(), (int) editor.point.getY(), 100, 100);
                }

                // get all rectangles that overlap toInsert and pass them to the right method
                if (editor.point != null) {
                    HashSet<Rectangle> getRectangles = new HashSet<>();
                    editor.world.retrieve(getRectangles, toInsert);

                    if (editor.editorMode == EditorMode.ADD) { // adding image
                        add(toInsert, getRectangles);
                    } else if (editor.editorMode == EditorMode.ERASE) { // erasing image
                        erase(toInsert, getRectangles);
                    } else if (editor.editorMode == EditorMode.SELECT) { // selecting image
                        select(toInsert, getRectangles);
                    }
                    editor.point = null;
                }
            }
        } else { // backgrounds
            if (!editor.isAdjustMode()) { // !editorSide.adjust
                if (editor.editorMode == EditorMode.ADD) {
                    PVector placement = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
                    float finalX = placement.x - 50;
                    float finalY = placement.y - 50;
                    PVector center = new PVector(finalX, finalY);
                    if (currentBackground == null) {
                        // offset placement by 50
                        currentBackground = new Background(p, AppLogic.texture, editor.currentBackground.getFile(),
                                center);
                    } else {
                        // round so blocks snap to grid
                        currentBackground.setPosition(center);
                    }
                }
            } else {
                // adjust the background with a single finger
                if (editor.selected != null && editor.selected instanceof Background) {
                    Background current = (Background) editor.selected;

                    // make sure the current touch is inside the bounds of the background
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

        if (Editor.showPageView) { // backgrounds

            if (!editor.isAdjustMode()) {
                if (editor.editorMode == EditorMode.ADD) {
                    addBackground();
                } else if (editor.editorMode == EditorMode.ERASE) {
                    eraseBackground();
                } else if (editor.editorMode == EditorMode.SELECT) {
                    selectBackground();
                }
                currentBackground = null;
            }
        }
    }

    private void addBackground() {
        if (currentBackground != null) { // if there is something to create a background from
            pageView.addPageViewObject(currentBackground);
            editor.selected = currentBackground;
            editor.editorMode = EditorMode.SELECT;
            editor.setAdjustMode();
        }
    }

    private void eraseBackground() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        Background found = pageView.getBackground(mouse.x, mouse.y);
        if (found != null) {
            pageView.removePageViewObject(found);
            if (found.equals(editor.selected)) {
                editor.selected = null;
            }
        }
    }

    private void selectBackground() {
        PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
        Background found = pageView.getBackground(mouse.x, mouse.y);
        if (found != null) {
            editor.selected = found; // select it
            // set editor side to adjust mode
            editor.setAdjustMode();
        } else {
            editor.selected = null;
        }
    }

    @Override
    public void draw() {
        if (Editor.showPageView) { // backgrounds
            if (currentBackground != null) {
                currentBackground.draw(16); // 80 // draw the background while placing
            }
        }
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public void onPinch(ArrayList<PVector> touches, float x, float y, float d) {
        // background resize
        if (Editor.showPageView && editor.isAdjustMode()) {
            if (editor.selected != null && editor.selected instanceof Background) {
                Background background = (Background) editor.selected;
                if (!background.fixedSize()) {
                    background.addSize(AppLogic.convert.screenToLevel(d) / 500);
                    // TODO: figure out what the 500 should be
                }

                PVector center = AppLogic.convert.screenToLevel(x, y);
                ((Background) editor.selected).setPosition(center);

            }
        }
    }

    @Override
    public void onRotate(float x, float y, float angle) {
        // background rotate
        if (Editor.showPageView && editor.isAdjustMode()) {
            if (editor.selected != null && editor.selected instanceof Background) {
                ((Background) editor.selected).addAngle(PApplet.degrees(angle));
            }
        }
    }

    @Override
    public void onTap(float x, float y) {
        if (Editor.showPageView && editor.isAdjustMode()) {
            // adjusting a background
            PVector mouse = AppLogic.convert.screenToLevel(p.mouseX, p.mouseY);
            Background found = pageView.getBackground(mouse.x, mouse.y);
            if (found != null && found != editor.selected) {
                editor.selected = found; // select it
            }
        }
    }
}
