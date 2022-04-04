package objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jbox2d.common.Vec2;
import game.AppLogic;
import game.Game;
import objects.events.PlayerEnd;
import processing.core.*;
import static processing.core.PConstants.*;

public class Page extends PageViewObject {
    private Game game;
    private View view;
    private float padding;
    private Rectangle paddedView = new Rectangle(0, 0, 0, 0);
    private HashSet<Rectangle> pageObjects;
    // private HashSet<PVector> excludedTiles; // a list of tiles to exclude while drawing

    private List<PageViewObject> children; // all the pageViewObjects that should be visible with this page

    private int shadowOffset; // the absolute amount to offset the shadow by
    private int shadow; // the relative amount to offset the shadow by

    // exclusion booleans
    public boolean showPlayer;
    public boolean showObstacles;
    public boolean showTiles;
    public boolean showImages;

    // player visibility
    private boolean playerVisibleExternal;
    private boolean playerVisibleChanged;

    // player drawing algorithm
    private int playerRez;
    private PGraphics player;
    private PGraphics playerMask;

    private float actualSize = 1;

    public Page(PApplet p, Game game, View view, PVector position) {
        super(p, position, view.getWidth(), view.getHeight());
        this.p = p;
        this.game = game;
        this.view = view;
        this.pageObjects = new HashSet<Rectangle>();
        // this.excludedObjects = new HashSet<String>();

        // booleans
        showPlayer = true;
        showObstacles = true;
        showTiles = true;
        showImages = true;

        this.shadowOffset = 9;
        this.shadow = 9;

        // create player drawer
        playerRez = 256;
        player = p.createGraphics(playerRez, playerRez, P2D);
        playerMask = p.createGraphics(playerRez, playerRez, P2D);

        // setup children
        children = new ArrayList<PageViewObject>();

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
        if (children.contains(child)) {
            children.remove(child);
            AppLogic.game.getPageView().resetSystems(); // reset page view camera
        } else {
            children.add(child);
            AppLogic.game.getPageView().resetSystems(); // reset page view camera
        }
    }

    public void removeChild(PageViewObject child) {
        if (child == null) {
            return;
        }
        if (children.contains(child)) {
            children.remove(child);
            return;
        }
    }

    public void step() {
        // get objects visible to this page
        pageObjects.clear();
        game.world.retrieve(pageObjects, view);
        updatePlayerVisibility();
    }

    private void updatePlayerVisibility() {
        // update player visibility
        if (game.player != null) {
            boolean temp = false;
            while (temp == false) {
                if (game.player.getCenter().x + game.player.getWidth() * 0.1 > view.getBottomRight().x) { // - 1
                    break;
                }
                if (game.player.getCenter().x - game.player.getWidth() * 0.1 < view.getTopLeft().x) { // + 1
                    break;
                }
                if (game.player.getCenter().y + game.player.getWidth() * 0.1 > view.getBottomRight().y) { // - 1
                    break;
                }
                if (game.player.getCenter().y - game.player.getWidth() * 0.1 < view.getTopLeft().y) { // + 1
                    break;
                }

                temp = true;
            }

            if (temp != playerVisibleExternal) {
                playerVisibleExternal = temp;
                playerVisibleChanged = true;
            }

        } else {
            playerVisibleExternal = false;
        }
    }

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

        boolean playerVisible = false;
        if (game.player != null) {
            while (playerVisible == false) {
                if (game.player.getCenter().x - game.player.getWidth() * 0.6 > paddedView.getBottomRight().x) {
                    break;
                }
                if (game.player.getCenter().x + game.player.getWidth() * 0.6 < paddedView.getTopLeft().x) {
                    break;
                }
                if (game.player.getCenter().y - game.player.getWidth() * 0.6 > paddedView.getBottomRight().y) {
                    break;
                }
                if (game.player.getCenter().y + game.player.getWidth() * 0.6 < paddedView.getTopLeft().y) {
                    break;
                }
                playerVisible = true;
            }
        }

        // draw player and paper effect
        if (playerVisible && game.player != null && showPlayer) {
            drawPlayer(p.g, 3);
        }
        // game.paper.draw(p.g, view, scale / size, (int) size);
        game.paper.draw(p.g, paddedView, scale, (int) size); // paper effect

        p.popMatrix();
    }

    private void drawPlayer(PGraphics graphics, float scale) {

        Vec2 center = game.player.getCenter();
        float angle = game.player.getDrawingAngle();

        // draw the mask at the player position and add masking
        playerMask.beginDraw();
        playerMask.background(0); // black
        playerMask.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center

        float xDiff = paddedView.getX() - center.x;
        float yDiff = paddedView.getY() - center.y;

        playerMask.noStroke();
        playerMask.rotate(angle);
        playerMask.scale(256 / 100f);
        playerMask.fill(255); // white
        playerMask.rect(xDiff, yDiff, paddedView.getWidth(), paddedView.getHeight());
        playerMask.endDraw();

        // draw the player
        player.beginDraw();
        player.translate(playerMask.width * 0.5f, playerMask.height * 0.5f); // set to center
        player.background(240, 0);
        player.scale(256 / 100f);
        game.player.drawNoTransform(player, scale);
        player.endDraw();

        player.mask(playerMask);

        graphics.pushMatrix();
        graphics.imageMode(CENTER);
        graphics.translate(center.x, center.y);
        graphics.rotate(-angle);
        graphics.scale(100 / 256f);
        graphics.image(player, 0, 0);
        graphics.popMatrix();

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
