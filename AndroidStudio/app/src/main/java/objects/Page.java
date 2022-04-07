package objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import game.AppLogic;
import game.Game;
import game.MathsPaper;
import game.player.ClippedDraw;
import objects.events.PlayerEnd;
import processing.core.*;

import static processing.core.PConstants.*;

import org.jbox2d.common.Vec2;

public class Page extends PageViewObject {
    private final Game game;
    private final View view;
    private final float padding;
    private Rectangle paddedView = new Rectangle(0, 0, 0, 0);
    private final HashSet<Rectangle> pageObjects;
    // private HashSet<PVector> excludedTiles; // a list of tiles to exclude while drawing

    private List<PageViewObject> children; // all the pageViewObjects that should be visible with this page

    private final int shadowOffset; // the absolute amount to offset the shadow by
    private int shadow; // the relative amount to offset the shadow by

    // exclusion booleans
    public boolean showPlayer;
    public boolean showObstacles;
    public boolean showTiles;
    public boolean showImages;

    // player visibility
    private boolean playerVisibleExternal; // true if the page is broadcasting that the player is in it and it should be focused on by the auto camera
    private boolean playerVisibleChanged; // true if the player has left or entered the page this step

    // used for scaling
    private float actualSize = 1;

    public Page(PApplet p, Game game, View view, PVector position) {
        super(p, position, view.getWidth(), view.getHeight());
        this.p = p;
        this.game = game;
        this.view = view;
        this.pageObjects = new HashSet<>();
        // this.excludedObjects = new HashSet<String>();

        // booleans
        showPlayer = true;
        showObstacles = true;
        showTiles = true;
        showImages = true;

        this.shadowOffset = 9;
        this.shadow = 9;

        // setup children
        children = new ArrayList<>();

        padding = 100;
        buildPaddedView();

        setPosition(position);
        updateCorners();

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

    public void step() {
        // get objects visible to this page
        pageObjects.clear();
        game.world.retrieve(pageObjects, view);
        updatePlayerVisibility();
    }

    /**
     * Updates the status of player visibility for the page. This is used to keep the auto camera
     * accurately informed about what pages to focus on.
     *
     * When the player transition animation is active, this method uses its position instead of the
     * player's to update the player visibility variables.
     */
    private void updatePlayerVisibility() {
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
        p.scale(flipX, flipY); // flip the page

        // draw the page background
        p.fill(240);
        p.rect(0, 0, paddedView.getWidth(), paddedView.getHeight());

        p.translate((float) -(view.getX() + view.getWidth() * 0.5), (float) -(view.getY() + view.getHeight() * 0.5));

        // draw images and playerEnds
        for (Rectangle r : pageObjects) { // draw images and player end backgrounds
            if (!(r instanceof Image || r instanceof PlayerEnd)) {
                continue;
            }
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
            if (r instanceof Image && showImages) {
                ((Image) r).drawClipped(p.g, view, 3); // scale/size
                continue;
            }
            if (r instanceof PlayerEnd) {
                ((PlayerEnd) r).drawPageView(p.g, 3);
            }
        }
        for (Rectangle r : pageObjects) { // draw tiles and events
            if (!(r instanceof Tile || r instanceof Event)) {
                continue;
            }
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
            if (r instanceof Tile && showTiles) {
                ((Tile) r).draw(p.g, 3); // scale/size
                continue;
            }
            if (r instanceof Event && ((Event) r).visible && showObstacles) {
                ((Event) r).draw(p.g, 3); // scale/size
            }
        }

        // draw the player
        boolean playerVisible = false;
        if (game.player != null) {
            while (!playerVisible) {
                Vec2 playerCenter = game.player.getCenter();
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
        }
        if (playerVisible && showPlayer) {
            ClippedDraw.drawPlayerOptimised(p.g, paddedView, 3);
        }

        // draw the player transition animation
        boolean transitionVisible = false;
        if (game.playerTransition.isActive()) {
            while (!transitionVisible) {
                Vec2 transitionCenter = game.playerTransition.getCenter();
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
        }
        if (transitionVisible && showPlayer) {
            ClippedDraw.drawTransition(p.g, paddedView, 3);
        }

        // draw the grid paper effect
        MathsPaper.draw(p.g, paddedView, scale, (int) size); // paper effect

        p.popMatrix();
    }

    @Override
    public void drawSelected(PGraphics g, float scale) {
        super.drawSelected(g, scale);
        for (PageViewObject object : children) {
            object.drawSelectedAsChild(g, scale);
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

}
