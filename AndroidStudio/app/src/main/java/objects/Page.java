package objects;

import static processing.core.PConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import game.AppLogic;
import game.Game;
import game.MathsPaper;
import game.player.ClippedDraw;
import objects.events.PlayerEnd;
import processing.core.*;

import org.jbox2d.common.Vec2;

public class Page extends PageViewObject {
    private final Game game;
    private final View view;
    private final float padding;
    private Rectangle paddedView = new Rectangle(0, 0, 0, 0);
    private final HashSet<Rectangle> pageObjects;
    // private HashSet<PVector> excludedTiles; // a list of tiles to exclude while drawing

    private boolean[][] tiles;
    private boolean[][] images;
    private boolean[][] obstacles;
    private boolean[][] player;

    private List<PageViewObject> children; // all the pageViewObjects that should be visible with this page

    private final int shadowOffset; // the absolute amount to offset the shadow by
    private int shadow; // the relative amount to offset the shadow by

    // player visibility, signaling the outside
    private boolean playerVisibleExternal; // true if the page is broadcasting that the player is in it and it should be focused on by the auto camera
    private boolean playerVisibleChanged; // true if the player has left or entered the page this step

    // used for scaling
    private float actualSize = 1;

    // rendering
    private DrawType playerDraw;
    private DrawType transitionDraw;
    private final ArrayList<Image> imagesToDraw;
    private final ArrayList<Tile> tilesToDraw;
    private final ArrayList<Event> eventsToDraw;
    private final ArrayList<PlayerEnd> playerEndsToDraw;

    public Page(PApplet p, Game game, View view, PVector position) {
        super(p, position, view.getWidth(), view.getHeight());
        this.p = p;
        this.game = game;
        this.view = view;
        this.pageObjects = new HashSet<>();
        // this.excludedObjects = new HashSet<String>();

        this.shadowOffset = 9;
        this.shadow = 9;

        // setup children
        children = new ArrayList<>();

        // setup rendering
        playerDraw = DrawType.SKIP;
        transitionDraw = DrawType.SKIP;
        imagesToDraw = new ArrayList<>();
        tilesToDraw = new ArrayList<>();
        eventsToDraw = new ArrayList<>();
        playerEndsToDraw = new ArrayList<>();

        padding = 100;
        buildPaddedView();

        setPosition(position);
        updateCorners();

        updatePageContents();
        setupRemovalArrays();
    }

    private void setupRemovalArrays() {
        int cols = (int) (view.getWidth() / 100);
        int rows = (int) (view.getHeight() / 100);

        // TODO: currently this erases removals when the page is resized, this should be improved
        if (tiles == null || tiles.length != rows || tiles[0].length != cols) {
            tiles = new boolean[rows][cols];
            images = new boolean[rows][cols];
            obstacles = new boolean[rows][cols];
            player = new boolean[rows][cols];
        }

    }

    public List<PageViewObject> getChildren() {
        return children;
    }

    public void setChildren(List<PageViewObject> children) {
        this.children = children;
    }

    public void addOrRemoveChild(PageViewObject child) {
        if (child == null) {
            return;
        }
        // reset page view camera
        if (children.contains(child)) {
            children.remove(child);
        } else {
            children.add(child);
        }
        AppLogic.game.getPageView().resetSystems(); // reset page view camera
    }

    public void removeChild(PageViewObject child) {
        if (child == null) {
            return;
        }
        children.remove(child);
    }

    /**
     * Pulls all the tiles and other objects that should be rendered on this page from
     * the world quad tree.
     */
    public void updatePageContents() {
        // get objects visible to this page
        pageObjects.clear();
        game.world.retrieve(pageObjects, view);
    }

    public void step() {
        // step rendering
        updatePlayerRendering();

        imagesToDraw.clear();
        tilesToDraw.clear();
        eventsToDraw.clear();
        playerEndsToDraw.clear();

        float viewX = view.getX();
        float viewY = view.getY();

        for (Rectangle r : pageObjects) {
            if (r.getTopLeft().x > view.getBottomRight().x - 1) {
                continue;
            }
            if (r.getBottomRight().x < view.getTopLeft().x + 1) {
                continue;
            }
            if (r.getTopLeft().y > view.getBottomRight().y - 1) {
                continue;
            }
            if (r.getBottomRight().y < view.getTopLeft().y + 1) {
                continue;
            }
            if (r instanceof Tile) {
                // add the tile to the tile draw list if it hasn't been removed
                int currentX = (int) (r.getX() - viewX) / 100;
                int currentY = (int) (r.getY() - viewY) / 100;
                if (!tiles[currentY][currentX]) {
                    tilesToDraw.add((Tile) r);
                }

                continue;
            }
            if (r instanceof Event && ((Event) r).visible) {
                eventsToDraw.add((Event) r);
                continue;
            }
            if (r instanceof PlayerEnd) {
                playerEndsToDraw.add((PlayerEnd) r);
                continue;
            }
            if (r instanceof Image) {
                imagesToDraw.add((Image) r);
            }
        }

        // remove tiles from rendering that have been removed during the level
        ArrayList<Tile> removed = AppLogic.game.removed;
        for (Tile t : removed) {
            tilesToDraw.remove(t);
        }

        // add tiles to rendering that were created during the level
        ArrayList<Tile> placed = AppLogic.game.placed;
        for (Tile t : placed) {
            if (t.getTopLeft().x > view.getBottomRight().x - 1) {
                continue;
            }
            if (t.getBottomRight().x < view.getTopLeft().x + 1) {
                continue;
            }
            if (t.getTopLeft().y > view.getBottomRight().y - 1) {
                continue;
            }
            if (t.getBottomRight().y < view.getTopLeft().y + 1) {
                continue;
            }
            tilesToDraw.add(t);
        }

    }

    /**
     * Updates the status of player visibility for the page. This is used to keep the auto camera
     * accurately informed about what pages to focus on.
     * <p>
     * When the player transition animation is active, this method uses its position instead of the
     * player's to update the player visibility variables.
     */
    public void updatePlayerVisibility() {
        // update player visibility
        if (game.player != null) {
            boolean temp = false;
            while (!temp) {
                if (game.playerTransition.isCameraDominant()) {
                    // use the transition's position instead of the player's

                    Vec2 transitionCenter = game.playerTransition.getCenter();
                    float padding = game.playerTransition.getSize();

                    if (transitionCenter.x + padding > view.getBottomRight().x) { // - 1
                        break;
                    }
                    if (transitionCenter.x - padding < view.getTopLeft().x) { // + 1
                        break;
                    }
                    if (transitionCenter.y + padding > view.getBottomRight().y) { // - 1
                        break;
                    }
                    if (transitionCenter.y - padding < view.getTopLeft().y) { // + 1
                        break;
                    }
                } else {
                    // use the player's position

                    Vec2 playerCenter = game.player.getCenter();
                    float padding = game.player.getWidth() * 0.1f;

                    if (playerCenter.x + padding > view.getBottomRight().x) { // - 1
                        break;
                    }
                    if (playerCenter.x - padding < view.getTopLeft().x) { // + 1
                        break;
                    }
                    if (playerCenter.y + padding > view.getBottomRight().y) { // - 1
                        break;
                    }
                    if (playerCenter.y - padding < view.getTopLeft().y) { // + 1
                        break;
                    }
                }
                // made it to the end, player or transition is visible on this page
                temp = true;
            }

            // if this is a change, update the variables
            if (temp != playerVisibleExternal) {
                playerVisibleExternal = temp;
                playerVisibleChanged = true;
            }

        } else {
            // no player exists, so it can't be visible
            playerVisibleExternal = false;
        }
    }

    /**
     * Used to check if the visibility of the player on this page has changed. It clears its result
     * each time it is called.
     *
     * @return true if the player visibility on this page has changed since this method was last called
     */
    public boolean playerVisibilityChanged() {
        // has player visibility changed since last check?
        boolean temp = playerVisibleChanged;
        playerVisibleChanged = false;
        return temp;

    }

    private void updatePlayerRendering() {
        // draw the player
        playerDraw = DrawType.SKIP;
        if (game.player != null) {
            Vec2 playerCenter = game.player.getCenter();
            boolean playerVisible = false;
            while (!playerVisible) {
                float padding = game.player.getWidth() * 0.6f;
                if (playerCenter.x - padding > paddedView.getBottomRight().x) {
                    break;
                }
                if (playerCenter.x + padding < paddedView.getTopLeft().x) {
                    break;
                }
                if (playerCenter.y - padding > paddedView.getBottomRight().y) {
                    break;
                }
                if (playerCenter.y + padding < paddedView.getTopLeft().y) {
                    break;
                }
                playerVisible = true;
            }
            if (playerVisible) {
                boolean playerAtEdge = true;
                while (playerAtEdge) {
                    float padding = 0;
                    if (playerCenter.x - padding > view.getBottomRight().x) {
                        break;
                    }
                    if (playerCenter.x + padding < view.getTopLeft().x) {
                        break;
                    }
                    if (playerCenter.y - padding > view.getBottomRight().y) {
                        break;
                    }
                    if (playerCenter.y + padding < view.getTopLeft().y) {
                        break;
                    }
                    playerAtEdge = false;
                }

                if (!playerAtEdge) {
                    // draw the player normally
                    playerDraw = DrawType.NORMAL;
                } else {
                    // draw the player clipped
                    playerDraw = DrawType.CLIPPED;
                }
//                playerDraw = DrawType.CLIPPED;
            }
        }

        // draw the player transition animation
        transitionDraw = DrawType.SKIP;
        if (game.playerTransition.isActive()) {
            Vec2 transitionCenter = game.playerTransition.getCenter();
            boolean transitionVisible = false;
            while (!transitionVisible) {
                float padding = game.playerTransition.getSize();
                if (transitionCenter.x - padding > paddedView.getBottomRight().x) {
                    break;
                }
                if (transitionCenter.x + padding < paddedView.getTopLeft().x) {
                    break;
                }
                if (transitionCenter.y - padding > paddedView.getBottomRight().y) {
                    break;
                }
                if (transitionCenter.y + padding < paddedView.getTopLeft().y) {
                    break;
                }
                transitionVisible = true;
            }
            if (transitionVisible) {
                boolean transitionAtEdge = true;
                while (transitionAtEdge) {
                    float padding = 0;
                    if (transitionCenter.x - padding > view.getBottomRight().x) {
                        break;
                    }
                    if (transitionCenter.x + padding < view.getTopLeft().x) {
                        break;
                    }
                    if (transitionCenter.y - padding > view.getBottomRight().y) {
                        break;
                    }
                    if (transitionCenter.y + padding < view.getTopLeft().y) {
                        break;
                    }
                    transitionAtEdge = false;
                }

                if (!transitionAtEdge) {
                    // draw the transition normally
                    transitionDraw = DrawType.NORMAL;
                } else {
                    // draw the transition clipped
                    transitionDraw = DrawType.CLIPPED;
                }
//                transitionDraw = DrawType.CLIPPED;
            }
        }
    }


    public void draw(float scale) {

        // draw the page
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.scale(size); // size the page will appear in the page view

        // draw the shadow
        p.translate(shadow, shadow);
        p.fill(0, 40);
        p.noStroke();
        p.rectMode(CENTER);
        p.rotate(PApplet.radians(angle)); // rotate the page
        p.rect(0, 0, paddedView.getWidth(), paddedView.getHeight());
        p.rotate(PApplet.radians(-angle)); // rotate the page
        p.translate(-shadow, -shadow);

        p.rotate(PApplet.radians(angle)); // rotate the page

        // draw the page itself
        if (flipX != 0 || flipY != 0) {
            p.scale(flipX, flipY); // flip the page
        }

        // draw the page background
        p.fill(240);
        p.rect(0, 0, paddedView.getWidth(), paddedView.getHeight());

        p.translate((float) -(view.getX() + view.getWidth() * 0.5), (float) -(view.getY() + view.getHeight() * 0.5));

        // draw page contents
        for (Image image : imagesToDraw) {
            image.drawClipped(p.g, view, 3); // scale/size
        }
        for (PlayerEnd playerEnd : playerEndsToDraw) {
            playerEnd.drawPageView(p.g, 3); // scale/size
        }
        for (Tile tile : tilesToDraw) {
            tile.draw(p.g, 3); // scale/size
        }
        for (Event event : eventsToDraw) {
            event.draw(p.g, 3); // scale/size
        }

        // draw player and transition
        // player
        switch (playerDraw) {
            case SKIP:
                break;
            case NORMAL:
                AppLogic.game.player.draw(p.g, 3);
                break;
            case CLIPPED:
                ClippedDraw.drawPlayerOptimised(p.g, paddedView, 3);
                break;
        }

        // transition
        switch (transitionDraw) {
            case SKIP:
                break;
            case NORMAL:
                Vec2 transitionCenter = game.playerTransition.getCenter();
                float transitionSize = game.playerTransition.getSize();
                AppLogic.playerFace.drawTransition(p.g, transitionCenter.x, transitionCenter.y, transitionSize, transitionSize);
                break;
            case CLIPPED:
                ClippedDraw.drawTransition(p.g, paddedView, 3);
                break;
        }

        // draw the grid paper effect
        MathsPaper.draw(p.g, paddedView, scale, (int) size); // paper effect
//        p.resetShader();
//        p.shape(paper);

        p.popMatrix();
    }

    @Override
    public void drawSelected(PGraphics g, float scale) {
        g.pushMatrix();
        g.noFill();
        g.stroke(255, 0, 0); // selection color, red
        g.strokeWeight(getSelectionStrokeWeight(scale));
        g.translate(position.x, position.y);
        g.scale(size); // size the page will appear in the page view
        g.rotate(PApplet.radians(angle)); // angle of the page
        g.rectMode(CENTER);
        g.rect(0, 0, getWidth(), getHeight());

        // TODO: draw removed squares if in removal mode, depending on what removal mode we are in
        if (AppLogic.editor.isRemovalMode()) {

            boolean[][] current = new boolean[0][0];
            switch (AppLogic.editor.getRemoveMode()) {
                case TILE:
                    current = tiles;
                    break;
                case IMAGE:
                    current = images;
                    break;
                case OBSTACLE:
                    current = obstacles;
                    break;
                case PLAYER:
                    current = player;
                    break;
            }

            g.fill(255, 0, 0, 100); // removal color, red
            g.noStroke();

            int xOffset = (int) (-view.getWidth() + 100) / 2;
            int yOffset = (int) (-view.getHeight() + 100) / 2;

            for (int i = 0; i < current.length; i++) { // rows
                for (int j = 0; j < current[0].length; j++) { // cols
                    if (current[i][j]) {
                        g.rect(xOffset + j * 100, yOffset + i * 100, 100, 100);
                    }
                }
            }

        }

        g.popMatrix();

        for (PageViewObject object : children) {
            object.drawSelectedAsChild(g, scale);
        }
    }

    public boolean getSquare(float x, float y) {

        PVector point = new PVector(x, y);
        point.x -= position.x;
        point.y -= position.y;
        point.rotate(PApplet.radians(-angle));

        if (-(view.getWidth() / 2) * size > point.x) {
            return true;
        }
        if ((view.getWidth() / 2) * size < point.x) {
            return true;
        }
        if (-(view.getHeight() / 2) * size > point.y) {
            return true;
        }
        if ((view.getHeight() / 2) * size < point.y) {
            return true;
        }

        // the point touched is inside the page, find exact square to toggle
        int squareX = (int) Math.round((point.x + view.getWidth() / 2 - 50) / 100);
        int squareY = (int) Math.round((point.y + view.getHeight() / 2 - 50) / 100);

        // prevent out of bounds access
        if (squareX < 0 || squareY < 0) {
            return true;
        }
        if (squareY >= tiles.length) {
            return true;
        }
        if (squareX >= tiles[0].length) {
            return true;
        }

        // toggle the square
        switch (AppLogic.editor.getRemoveMode()) {
            case TILE:
                return tiles[squareY][squareX];
            case IMAGE:
                return images[squareY][squareX];
            case OBSTACLE:
                return obstacles[squareY][squareX];
            case PLAYER:
                return player[squareY][squareX];
        }
        return true;
    }

    public void setSquare(float x, float y, boolean setting) {

        PVector point = new PVector(x, y);
        point.x -= position.x;
        point.y -= position.y;
        point.rotate(PApplet.radians(-angle));

        if (-(view.getWidth() / 2) * size > point.x) {
            return;
        }
        if ((view.getWidth() / 2) * size < point.x) {
            return;
        }
        if (-(view.getHeight() / 2) * size > point.y) {
            return;
        }
        if ((view.getHeight() / 2) * size < point.y) {
            return;
        }

        // the point touched is inside the page, find exact square to toggle
        int squareX = (int) Math.round((point.x + view.getWidth() / 2 - 50) / 100);
        int squareY = (int) Math.round((point.y + view.getHeight() / 2 - 50) / 100);

        // prevent out of bounds access
        if (squareX < 0 || squareY < 0) {
            return;
        }
        if (squareY >= tiles.length) {
            return;
        }
        if (squareX >= tiles[0].length) {
            return;
        }

        // toggle the square
        switch (AppLogic.editor.getRemoveMode()) {
            case TILE:
                tiles[squareY][squareX] = setting;
                break;
            case IMAGE:
                images[squareY][squareX] = setting;
                break;
            case OBSTACLE:
                obstacles[squareY][squareX] = setting;
                break;
            case PLAYER:
                player[squareY][squareX] = setting;
                break;
        }

    }

    public boolean playerVisible() {
        return playerVisibleExternal;
    }

    public void setSize(float size) {
        this.actualSize = size;
        this.size = Math.round(actualSize);

//		this.size = size;

        updateCorners();
        updateShadow();
    }

    public void addSize(float size) {

        if (this.actualSize + size >= 1 && this.actualSize + size <= 2) {
            this.actualSize += size;
            this.size = Math.round(actualSize);

        } else if (this.actualSize + size < 1) {
            this.actualSize = 1;
            this.size = Math.round(actualSize);
        } else {
            this.actualSize = 2;
            this.size = Math.round(actualSize);
        }

//		if (this.size + size > 1) { // 0.5f
//			this.size += size;
//		} else {
//			this.size = 1; // 0.5f
//		}

        updateCorners();
        updateShadow();
    }

    public void updateSizeFromView() {
        setCorners(view.getTopLeft(), view.getBottomRight());
        buildPaddedView();
        updateCorners();
        setupRemovalArrays();
    }

    /**
     * Builds the page rendering rectangle from the view, with padding
     */
    private void buildPaddedView() {
        Rectangle temp = view.copy();
        temp.enlarge(padding);
        this.paddedView = temp;
        setWidth(paddedView.getWidth());
        setHeight(paddedView.getHeight());
    }

    public View getView() {
        return view;
    }

    private void updateShadow() {
        this.shadow = (int) (shadowOffset / size);
    }

    @Override
    public String getName() {
        return "page";
    }

    private enum DrawType {
        SKIP, // don't draw
        NORMAL, // draw using normal fast drawing
        CLIPPED // draw using the heavy clipped drawing algorithm
    }

}
