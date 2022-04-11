package game;

import java.util.ArrayList;
import java.util.List;

import camera.Camera;
import camera.PageViewCamera;
import editor.DebugOutput;
import editor.Editor;
import editor.EditorSettings;
import game.player.ClippedDraw;
import handlers.TextureCache;
import misc.Converter;
import objects.Background;
import objects.Page;
import objects.PageViewObject;
import objects.Rectangle;
import processing.core.*;
import ui.Menu;

public class PageView {
    private PApplet p;
    private Converter convert;

    private BackgroundPaper paper;

    private ArrayList<PageViewObject> pageViewObjects;
    private int pages;

    private PageViewCamera pageCamera;
    private Rectangle previousPageArea; // used when switching between menu and level when player isn't visible

    private Menu storedMenu;
    private boolean removeMenu = false;

    // player drawing class
    public ClippedDraw clippedDraw;

    // rendering
    private float currentScale; // what scale (LOD) will objects be rendered at
    private PVector renderTopLeft;
    private PVector renderBottomRight;
    private ArrayList<PageViewObject> backgroundsToDraw;
    private ArrayList<Page> pagesToDraw;


    public PageView(PApplet p, Game game, TextureCache texture, Converter convert) {
        this.p = p;
        this.convert = convert;

        this.pageCamera = new PageViewCamera(p);

        this.paper = new BackgroundPaper(p);

        this.pageViewObjects = new ArrayList<>();
        this.pages = 0;

        this.previousPageArea = null;

        clippedDraw = new ClippedDraw(p);

        // rendering
        currentScale = 1;
        renderTopLeft = new PVector(0, 0);
        renderBottomRight = new PVector(0, 0);
        backgroundsToDraw = new ArrayList<>();
        pagesToDraw = new ArrayList<>();
    }

    public void draw() {

        p.pushMatrix(); // start working at game scale
        p.translate(p.width * 0.5f, p.height * 0.5f); // set x=0 and y=0 to the middle of the screen

        if (Camera.getGame()) {
            // auto camera
            p.scale((float) p.width / pageCamera.getScale()); // width/screen fits the level scale to the screen
            p.scale(pageCamera.getSubScale()); // apply offset for tall screen spaces
            p.translate(-pageCamera.getCenter().x, -pageCamera.getCenter().y); // moves the view around the level
        } else {
            // editor camera
            p.scale((float) p.width / Camera.getScale()); // width/screen fits the level scale to the screen
            p.scale(Camera.getSubScale()); // apply offset for tall screen spaces
            p.translate(-Camera.getCenter().x, -Camera.getCenter().y); // moves the view around the level
        }

        // draw the looping background
        paper.draw(p.getGraphics(), renderTopLeft, renderBottomRight, currentScale); // background paper effect

        // draw backgrounds that are visible
        for (PageViewObject background : backgroundsToDraw) {
            background.draw(currentScale);
        }

        // draw pages that are visible
        for (Page page : pagesToDraw) {
            page.draw(currentScale);
            if (EditorSettings.cameraLogic() && !Camera.getGame()) {
                if (page.playerVisible()) {
                    page.drawCorners(currentScale);
                    List<PageViewObject> children = page.getChildren();
                    for (PageViewObject child : children) {
                        child.drawCorners(currentScale);
                    }
                }
            }
        }

        // draw current menu, destroy it if it's off camera
        if (storedMenu != null) {
            if (!AppLogic.editorToggle || getPageCount() > 0) {
                // draw the menu in page view if there are pages, or not in editor
                storedMenu.drawInWorld(currentScale);
            }
        }

        // draw auto generated camera
        if (!Camera.getGame() && EditorSettings.cameraLogic() && getPageCount() > 0) {
            pageCamera.draw(currentScale);
            if (storedMenu != null) {
                storedMenu.drawCorners(currentScale);
            }
        }

        p.popMatrix();
    }

    public void step(float deltaTime) {
        boolean adjustCamera = false;

        if (AppLogic.menuAdded() == true) {
            storedMenu = AppLogic.getMenu();

            if (!storedMenu.isBuilt()) {
                // build the page menu if it isn't already built
                Rectangle pageArea = getLevelArea();

                if (pageArea != null) {
                    storedMenu.buildPageMenu(pageCamera.getCenter(), pageArea, pageCamera);
                } else {
                    if (storedMenu.child == null) {
                        storedMenu.buildPageMenu();
                    } else {
                        Rectangle area = getFullArea();
                        storedMenu.buildPageMenu(pageCamera.getCenter(), area, pageCamera);
                    }
                }
            }

            removeMenu = false;

            float menuMinX = storedMenu.getLeftmostPoint();
            float menuMinY = storedMenu.getTopmostPoint();
            float menuMaxX = storedMenu.getRightmostPoint();
            float menuMaxY = storedMenu.getBottommostPoint();
            pageCamera.updateMenu(menuMinX, menuMinY, menuMaxX, menuMaxY);

            DebugOutput.pushMessage("Page menu built", 1);

        } else if (AppLogic.menuRemoved() == true) {
            removeMenu = true;
            adjustCamera = true;

        } else {
            for (PageViewObject object : pageViewObjects) {
                if (!(object instanceof Page)) {
                    continue;
                }
                Page page = (Page) object;

                page.updatePlayerVisibility();
                if (page.playerVisibilityChanged()) {
                    adjustCamera = true;
                }
            }

        }

        if (adjustCamera) {
            updateVisiblePages();
        }

        // move the camera
        boolean temp = pageCamera.step(deltaTime);
        // if the camera has finished moving
        if (!temp && storedMenu != null) {

            if (AppLogic.editorToggle && Editor.showPageView) {
                // in editor, and in page view
                storedMenu.skipLoadingScreen();
            } else {
                storedMenu.activate();
            }
        }

        // ------ moved from draw -------

        PVector topLeft;
        PVector bottomRight;

        if (Camera.getGame()) {
            // auto camera
            currentScale = pageCamera.getScale() / pageCamera.getSubScale() / 100;
            topLeft = convert.screenToLevel(0, 0, pageCamera.getScale(), pageCamera.getSubScale(), pageCamera.getCenter());
            bottomRight = convert.screenToLevel(p.width, p.height, pageCamera.getScale(), pageCamera.getSubScale(), pageCamera.getCenter());
        } else {
            // editor camera
            currentScale = convert.getScale();
            topLeft = convert.screenToLevel(0, 0);
            bottomRight = convert.screenToLevel(p.width, p.height);
        }
        renderTopLeft = topLeft;
        renderBottomRight = bottomRight;

        // draw backgrounds that are inside that area
        backgroundsToDraw.clear();
        for (PageViewObject background : pageViewObjects) {
            if (!(background instanceof Background)) {
                continue;
            }
            if (background.leftOf(topLeft.x)) {
                continue;
            }
            if (background.rightOf(bottomRight.x)) {
                continue;
            }
            if (background.above(topLeft.y)) {
                continue;
            }
            if (background.below(bottomRight.y)) {
                continue;
            }
            backgroundsToDraw.add(background);
        }
        // draw pages that are inside that area
        pagesToDraw.clear();
        for (PageViewObject page : pageViewObjects) {
            if (!(page instanceof Page)) {
                continue;
            }
            if (page.leftOf(topLeft.x)) {
                continue;
            }
            if (page.rightOf(bottomRight.x)) {
                continue;
            }
            if (page.above(topLeft.y)) {
                continue;
            }
            if (page.below(bottomRight.y)) {
                continue;
            }
            pagesToDraw.add((Page) page);
            ((Page) page).step();
        }

        // draw current menu, destroy it if it's off camera
        if (storedMenu != null) {
            if (!AppLogic.editorToggle || getPageCount() > 0) {
                if (removeMenu && (storedMenu.leftOf(PageViewCamera.getScreenArea().getTopLeft().x)
                        || storedMenu.rightOf(PageViewCamera.getScreenArea().getBottomRight().x)
                        || storedMenu.above(PageViewCamera.getScreenArea().getTopLeft().y)
                        || storedMenu.below(PageViewCamera.getScreenArea().getBottomRight().y))) {
                    removeMenu = false;
                    storedMenu = null;
                }
            } else {
                // remove the menu if there are no pages
                removeMenu = false;
                storedMenu = null;
            }
        }
    }

    public void updateVisiblePages() {
        Rectangle area = getPlayerVisibleArea();
        if (area == null) {
            // no pages with visible player, just do whole level instead, minus menus
            if (pageViewObjects.size() == 0) {
                // nothing in page view at all
                return;
            }

            float minX = Float.POSITIVE_INFINITY;
            float minY = Float.POSITIVE_INFINITY;
            float maxX = Float.NEGATIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;

            // get area of pages
            for (PageViewObject object : pageViewObjects) {
                minX = Math.min(minX, object.getLeftmostPoint());
                minY = Math.min(minY, object.getTopmostPoint());
                maxX = Math.max(maxX, object.getRightmostPoint());
                maxY = Math.max(maxY, object.getBottommostPoint());
            }

            float x = minX;
            float y = minY;
            float width = maxX - minX;
            float height = maxY - minY;

            area = new Rectangle(x, y, width, height);
        }
        pageCamera.updateGameplay(area.getTopLeft().x, area.getTopLeft().y, area.getBottomRight().x, area.getBottomRight().y);
    }

    public void stepPages() {
        // used to run the pages so that visibility information etc can be accurately
        // assessed, this is done by the level loading system
        for (PageViewObject object : pageViewObjects) {
            if (!(object instanceof Page)) {
                continue;
            }
            Page page = (Page) object;
            page.updatePlayerVisibility(); // .step();
        }
    }

    public Rectangle getPlayerVisibleArea() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        int visiblePage = 0;
        for (PageViewObject object : pageViewObjects) {
            if (!(object instanceof Page)) {
                continue;
            }
            Page page = (Page) object;

            if (page.playerVisible()) {
                // if this page has a visible player
                visiblePage++;
                minX = Math.min(minX, page.getLeftmostPoint());
                minY = Math.min(minY, page.getTopmostPoint());
                maxX = Math.max(maxX, page.getRightmostPoint());
                maxY = Math.max(maxY, page.getBottommostPoint());

                // do area for this page's children
                List<PageViewObject> children = page.getChildren();
                for (PageViewObject child : children) {
                    minX = Math.min(minX, child.getLeftmostPoint());
                    minY = Math.min(minY, child.getTopmostPoint());
                    maxX = Math.max(maxX, child.getRightmostPoint());
                    maxY = Math.max(maxY, child.getBottommostPoint());
                }
            }
        }

        if (visiblePage > 0) {
            Rectangle output = new Rectangle(minX, minY, maxX - minX, maxY - minY);
            this.previousPageArea = output;
            return output;
        } else {
            return previousPageArea;
        }
    }

    public void initCamera() {
        if (AppLogic.hasMenu()) {
            // initialize camera for menu
            storedMenu = AppLogic.getMenu();
            storedMenu.buildPageMenu();

            removeMenu = false;

            float menuMinX = storedMenu.getLeftmostPoint();
            float menuMinY = storedMenu.getTopmostPoint();
            float menuMaxX = storedMenu.getRightmostPoint();
            float menuMaxY = storedMenu.getBottommostPoint();
            pageCamera.initCamera(menuMinX, menuMinY, menuMaxX, menuMaxY);
        }
    }

    /**
     * @return area of all objects in level, including menus
     */
    public Rectangle getFullArea() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        // get area of page view objects
        for (PageViewObject object : pageViewObjects) {
            minX = Math.min(minX, object.getLeftmostPoint());
            minY = Math.min(minY, object.getTopmostPoint());
            maxX = Math.max(maxX, object.getRightmostPoint());
            maxY = Math.max(maxY, object.getBottommostPoint());
        }

        Menu temp = storedMenu;
        while (temp != null) {
            if (temp.isBuilt()) {
                minX = Math.min(minX, storedMenu.getLeftmostPoint());
                minY = Math.min(minY, storedMenu.getTopmostPoint());
                maxX = Math.max(maxX, storedMenu.getRightmostPoint());
                maxY = Math.max(maxY, storedMenu.getBottommostPoint());
            }
            temp = temp.child; // update current
        }

        float x = minX;
        float y = minY;
        float width = maxX - minX;
        float height = maxY - minY;

        return new Rectangle(x, y, width, height);
    }

    /**
     * @return area of all objects in level, but not including menus
     */
    public Rectangle getLevelArea() {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        int objectCount = 0;
        // get area of objects in level
        for (PageViewObject object : pageViewObjects) {
            minX = Math.min(minX, object.getLeftmostPoint());
            minY = Math.min(minY, object.getTopmostPoint());
            maxX = Math.max(maxX, object.getRightmostPoint());
            maxY = Math.max(maxY, object.getBottommostPoint());

            objectCount++;
        }

        if (objectCount == 0) {
            return null;
        }

        float x = minX;
        float y = minY;
        float width = maxX - minX;
        float height = maxY - minY;

        return new Rectangle(x, y, width, height);
    }

    /**
     * Forces a recalculation of all page sizes and contents.
     * <p>
     * Used when switching between level view and page view and previewing a level in editor.
     */
    public void recalculatePageViewObjects() {
        // force recalculate pages contents and sizes
        for (PageViewObject object : pageViewObjects) {
            if (!(object instanceof Page)) {
                continue;
            }
            Page page = (Page) object;

            page.updateSizeFromView(); // makes sure the page has the correct view size
            page.updatePageContents(); // rebuilds the contents of the page
        }
        resetSystems();
    }

    public void clearMenus() {
        storedMenu = null;
        removeMenu = false;
    }

    public void addPageViewObject(PageViewObject object) {
        pageViewObjects.add(object);
        if (object instanceof Page) {
            this.pages++;
        }

        resetSystems();
    }

    public void addPageViewObjects(List<PageViewObject> objects) {
        pageViewObjects.addAll(objects);
        pages = 0;
        for (PageViewObject object : pageViewObjects) {
            if (object instanceof Page) {
                this.pages++;
            }
        }

        resetSystems();
    }

    public void removePageViewObject(PageViewObject object) {

        if (pageViewObjects.remove(object)) {

            // if it was removed
            if (object instanceof Page) {
                // if the removed was a page
                this.pages--;
            }

            // remove this object as a child from all pages
            for (PageViewObject pvo : pageViewObjects) {
                if (!(pvo instanceof Page)) {
                    continue;
                }
                Page page = (Page) pvo;
                page.removeChild(object);
            }

            if (this.pages > 0) {
                resetSystems();
            }
        }

    }

    public List<PageViewObject> getPageViewObjects() {
        return pageViewObjects;
    }

    public void clearPageViewObjects() {
        this.pageViewObjects.clear();
        this.pages = 0;
    }

    public PageViewObject getPageViewObject(float x, float y) {
        if (pageViewObjects.size() < 1) {
            return null;
        }
        for (PageViewObject object : pageViewObjects) {
            // return the first overlap
            if (object.isInside(x, y)) {
                return object;
            }

        }

        return null;
    }

    public Page getPage(float x, float y) {
        if (pageViewObjects.size() < 1) {
            return null;
        }
        for (PageViewObject object : pageViewObjects) {
            if (!(object instanceof Page)) {
                continue;
            }
            // return the first overlap
            if (((Page) object).isInside(x, y)) {
                return (Page) object;
            }

        }

        return null;
    }

    public int getPageCount() {
        return pages;
    }

    public Background getBackground(float x, float y) {
        if (pageViewObjects.size() < 1) {
            return null;
        }
        for (PageViewObject object : pageViewObjects) {
            if (!(object instanceof Background)) {
                continue;
            }
            // return the first overlap
            if (((Background) object).isInside(x, y)) {
                return (Background) object;
            }

        }

        return null;
    }

    public void offsetAll(float x, float y) {
        for (PageViewObject object : pageViewObjects) {
            PVector pos = object.getPosition();
            pos.x += x;
            pos.y += y;
            object.setPosition(pos);
        }
    }

    public void resetSystems() {
        previousPageArea = null;
        DebugOutput.pushMessage("Reset page view", 1);
        updateVisiblePages();
    }

    public void recenterObjects() {
        // re-center page view objects
        Rectangle area = getLevelArea();
        if (area != null) {
            PVector center = area.getRectangleCenter();
            // there are page view objects in the level
            offsetAll(-center.x, -center.y);
        }
    }

    public BackgroundPaper getPaper() {
        return paper;
    }

    public PageViewCamera getPageCamera() {
        return pageCamera;
    }
}
