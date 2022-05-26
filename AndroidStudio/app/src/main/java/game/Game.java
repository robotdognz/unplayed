package game;

import java.util.ArrayList;
import java.util.HashSet;

import camera.Camera;
import editor.Editor;
import game.player.PlayerTransition;
import game.player.Player;
import game.player.PlayerVibration;
import handlers.LoadingHandler;
import handlers.TextureCache;
import misc.Converter;
import misc.CountdownTimer;
import misc.MyContactListener;
import objects.Rectangle;
import objects.Tile;
import objects.View;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import processing.core.*;
import shiffman.box2d.Box2DProcessing;

import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.common.Vec2;

public class Game {
    private final PApplet p;
    public Player player;
    public Converter convert;
    private final TextureCache texture;

    public Quadtree world;
    public ArrayList<Tile> removed; // holds the tiles that the player has become and have been removed from the world
    public ArrayList<Tile> placed; // holds the tiles the player has left behind after slotting in
    public int puzzlesCompleted;
    public ArrayList<View> views;
    public Rectangle startingWorld;
    public HashSet<Rectangle> playerObjects;
    private final PageView pageView;
    private PlayerStart playerStart;
    public Tile playerCheckpoint;

    private final CountdownTimer pauseTimer; // used to pause game during puzzles
    private PauseType pauseType;
    private Rectangle playerAreaTemp;

    public LoadingHandler currentLoading = null;

    // enum used to indicate what type of pause has happened
    private enum PauseType {
        NEXT_LEVEL, RESTART_LEVEL, NEXT_PLAYER, NONE
    }

    public Camera camera;

    public Rectangle screenSpace;
    public int screenSpaceOffset;
    // screenSpaceOffset is for debug purposes, I believe (future Marco here), it
    // can be used to decrease the rendering space

    // box2d
    public Box2DProcessing box2d;
    public ContactListener contactListener;

    // delta time
//    float accumulator = 0;
    float stepSize = 1f / 240f; // 240f, 120f, 60f
    // to change the number of physics steps per render step, change the above
    // and change movementIncrease in the Player class

    // player transition animation
    public PlayerTransition playerTransition;

    // universal access delta time
    static float deltaStep = 0;

    public Game(PApplet p, Camera c, TextureCache texture, Converter convert) {
        this.p = p;
        this.camera = c;
        this.texture = texture;
        this.convert = convert;

        startingWorld = new Rectangle(-400, -400, 900, 900);
        world = new Quadtree(startingWorld);
        removed = new ArrayList<>();
        placed = new ArrayList<>();
        views = new ArrayList<>();
        playerObjects = new HashSet<>();
        player = null;

        pageView = new PageView(p, convert);

        pauseTimer = new CountdownTimer(0.4f);
        pauseType = PauseType.NONE;

        // calculate screen space
        screenSpaceOffset = 0; // positive makes it larger, negative makes it smaller
        PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
        float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
        float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
        screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

        // box2d
        buildWorld();

        playerTransition = new PlayerTransition(); //new Vec2(0, 0), new Vec2(0, 0)

        deltaStep = stepSize;
    }

    public void emptyGame() {
        world.clear(); // remove old world objects
        placed.clear(); // removed tiles that have been inserted into slots
        removed.clear(); // remove tiles that have become the player
        clearPlayerStart(); // remove the player
        playerCheckpoint = null; /// remove checkpoint
        views.clear();
        pageView.clearPageViewObjects(); // remove pages and backgrounds
        buildWorld(); // rebuild world

        currentLoading = null;
    }

    public void removePlayer() {
        if (player == null) {
            return;
        }
        player.destroy(); // remove the player physics body
        player = null; // remove the player
    }

    public void buildWorld() {
        box2d = new Box2DProcessing(p);
        box2d.createWorld();
        box2d.setGravity(0, -400);
        box2d.world.setAutoClearForces(false);

        // contact listener
        contactListener = new MyContactListener(this);
        box2d.world.setContactListener(contactListener);
    }

    public void setPlayerStart(PlayerStart start) {
        if (start != null) {
            // set player start
            playerStart = start;
            // create new player
            createPlayer(start, false);
        }
    }

    public void clearPlayerStart() {
        // fully remove the player
        if (this.player != null) {
            this.player.destroy();
        }
        this.player = null;
        this.playerStart = null;
    }

    public Rectangle getPlayerStart() {
        return playerStart;
    }

    public void startGame() {
        // clear player
        if (this.player != null) {
            this.player.destroy();
            player = null;
        }

        // clear checkpoint
        playerCheckpoint = null;

        // reset removed and placed tiles
        for (Tile t : placed) {
            world.remove(t);
        }
        for (Tile t : removed) {
            // don't insert tiles that were created by PlayerEnds
            if (placed.contains(t)) {
                continue;
            }
            world.insert(t);
        }
        placed.clear();
        removed.clear();

        // reset player ends
        HashSet<Rectangle> allObjects = new HashSet<>();
        world.getAll(allObjects);
        for (Rectangle temp : allObjects) {
            if (temp instanceof PlayerEnd) {
                ((PlayerEnd) temp).reset();
            }
        }

        // Initialize player
        if (playerStart != null) {
            createPlayer(playerStart, false);
        }

        puzzlesCompleted = 0;

    }

    /**
     * Called when the level should be ended
     */
    public void endLevel() {
        // if already in the process of doing something, return
        if (pauseTimer.isRunning()) {
            return;
        }

        if (AppLogic.getEditor() == null) { // in a normal game
            pauseTimer.start();
            pauseType = PauseType.NEXT_LEVEL;
            playerTransition.update(player.getCenter(), null, PlayerTransition.Type.END);
            player.setActive(false);
        } else { // in the editor
            if (AppLogic.editorToggle && !Editor.showPageView) {
                AppLogic.toast.showToast("Level Complete");
            }
            pauseTimer.start();
            pauseType = PauseType.RESTART_LEVEL;
        }
    }

    public void endPuzzle(Rectangle playerArea) {
        if (world.playerEndCount() - puzzlesCompleted == 1) {
            endLevel();
            return;
        }

        pauseTimer.start();
        pauseType = PauseType.NEXT_PLAYER;
        playerAreaTemp = playerArea.copy();
    }

    private void nextPlayer() {
        HashSet<Rectangle> returnObjects = new HashSet<>();
        world.retrieve(returnObjects, playerAreaTemp);
        Tile found = null;
        for (Rectangle r : returnObjects) {
            if (!(r instanceof Tile)) {
                continue;
            }
            if (r.getTopLeft().x > playerAreaTemp.getBottomRight().x - 1) {
                continue;
            }
            if (r.getBottomRight().x < playerAreaTemp.getTopLeft().x + 1) {
                continue;
            }
            if (r.getTopLeft().y > playerAreaTemp.getBottomRight().y - 1) {
                continue;
            }
            if (r.getBottomRight().y < playerAreaTemp.getTopLeft().y + 1) {
                continue;
            }
            found = ((Tile) r);
        }
        // if the next block the player will become has been found
        if (found != null) {

            // update the checkpoint
            this.playerCheckpoint = found;

            // make the matching tile to fill the slot
            int tileX = (Math.round((player.getCenter().x - player.getWidth() / 2) / 10) * 10);
            int tileY = (Math.round((player.getCenter().y - player.getHeight() / 2) / 10) * 10);
            Tile newTile = new Tile(box2d, texture, player.getFile(), tileX, tileY);
            newTile.setAngle(player.getAdjustedAngle(true));
            // insert the new tile into the world and add it to placed
            world.insert(newTile);
            placed.add(newTile);

            // create the new player
            puzzlesCompleted++;
            createPlayer(found, false);
        }
    }

    /**
     * Creates new player. Used for moving to the next player as well as respawning after death.
     *
     * @param playerArea the area to take the tile that will become the next player from
     * @param death has the player died and is now respawning
     */
    public void createPlayer(Rectangle playerArea, boolean death) {
        // store previous player position
        Vec2 previousPlayerPosition = null;
        if (player != null) {
            previousPlayerPosition = player.getCenter();
        }

        if (playerArea instanceof PlayerStart) {
            Tile current = ((PlayerStart) playerArea).getRequired();
            if (this.player != null) {
                this.player.destroy();

            }
            // make new player
            player = new Player(p, box2d, texture, current);

            // setup player transition
            Vec2 newPlayerPosition = player.getCenter();
            if (previousPlayerPosition != null) {
//                PApplet.print("Player death (first player) animation");
                playerTransition.update(previousPlayerPosition, newPlayerPosition, PlayerTransition.Type.DEATH);
            } else {
                // it is the start of the level, tell playerTransition to do a start animation
//                PApplet.print("Level intro animation");
                playerTransition.update(null, newPlayerPosition, PlayerTransition.Type.START);
            }

        } else if (playerArea instanceof Tile) {
            HashSet<Rectangle> returnObjects = new HashSet<>();
            world.retrieve(returnObjects, playerArea);
            Tile found = null;
            for (Rectangle r : returnObjects) {
                if (!(r instanceof Tile)) {
                    continue;
                }
                if (r.getTopLeft().x > playerArea.getBottomRight().x - 1) {
                    continue;
                }
                if (r.getBottomRight().x < playerArea.getTopLeft().x + 1) {
                    continue;
                }
                if (r.getTopLeft().y > playerArea.getBottomRight().y - 1) {
                    continue;
                }
                if (r.getBottomRight().y < playerArea.getTopLeft().y + 1) {
                    continue;
                }

                found = ((Tile) r);
            }
            if (found != null) {
                removed.add(found);
                world.remove(found);
                if (playerCheckpoint != null) {
                    if (this.player != null) {
                        this.player.destroy();
                    }

                    // make new player
                    player = new Player(p, box2d, texture, playerCheckpoint);

                    // setup player transition
                    if (previousPlayerPosition != null) {
                        Vec2 newPlayerPosition = player.getCenter();
                        if (!death) {
//                            PApplet.print("Player typical transition");
                            playerTransition.update(previousPlayerPosition, newPlayerPosition, PlayerTransition.Type.TRANSITION);
                        } else {
//                            PApplet.print("Player death animation");
                            playerTransition.update(previousPlayerPosition, newPlayerPosition, PlayerTransition.Type.DEATH);
                        }
                    }
                } else if (playerStart != null) {
                    Tile current = playerStart.getRequired();
                    if (current != null) {
                        if (this.player != null) {
                            this.player.destroy();
                        }

                        // make new player
                        player = new Player(p, box2d, texture, current);

                        // setup player transition
                        if (previousPlayerPosition != null) {
                            Vec2 newPlayerPosition = player.getCenter();
//                            PApplet.print("Player transition, Haven't seen this one used before");
                            playerTransition.update(previousPlayerPosition, newPlayerPosition, PlayerTransition.Type.DEATH);
                        }
                    }
                }

            }
        }
    }

    public void stopPlayer() {
        if (player != null) {
            player.still();
        }
    }

    /**
     * Restarts the player. Used for when the player is killed and also by the player reset action
     * in the level editor.
     */
    public void restart() {
        if (playerCheckpoint != null) { // if there is a player checkpoint
            Rectangle previousPlayer = removed.get(removed.size() - 1);
            removed.remove(previousPlayer);
            world.insert(previousPlayer);
            createPlayer(playerCheckpoint, true);
        } else if (playerStart != null) { // if there is a player start
            createPlayer(playerStart, true);
        }
    }

    public void draw() {
        pageView.draw();
    }

    public void step(float deltaTime) {
        if (pauseTimer.isFinished()) {
            switch (pauseType) {
                case NONE:
                    pauseTimer.stop();
                    break;
                case NEXT_PLAYER:
                    nextPlayer();
                    pauseType = PauseType.NONE;
                    pauseTimer.stop();
                    break;
                case NEXT_LEVEL:
                    if (playerTransition.isActive()) {
                        break;
                    }
                    AppLogic.nextLevel();
                    pauseType = PauseType.NONE;
                    pauseTimer.stop();
                    break;
                case RESTART_LEVEL:
                    AppLogic.restartLevel();
                    pauseType = PauseType.NONE;
                    pauseTimer.stop();
                    break;
            }
        }

        // step player non-physics logic
        if (player != null) {
            player.step(deltaTime);
        }

        // step physics, only when there is no player transition
        int steps = calculateSteps(deltaTime);
        while (steps > 0) {
            if (player != null) {
                player.physicsStep(stepSize);
                PlayerVibration.EndStep();
            }
            box2d.step(stepSize, 8, 3);
//            PApplet.print("End Step");
            steps--;
        }
//        PApplet.print("End Frame");
        box2d.world.clearForces();


        // update screen space
        PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
        float screenSpaceWidth = convert.screenToLevel(p.width + screenSpaceOffset * 2);
        float screenSpaceHeight = convert.screenToLevel(p.height + screenSpaceOffset * 2);
        screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);

        // step the pause timer
        pauseTimer.deltaStep(deltaTime);

        // step player transition
        playerTransition.step(deltaTime);

    }

    public void cameraStep(float deltaTime) {
        // step the page view
        pageView.step(deltaTime);
    }

    public Player getPlayer() {
        return player;
    }

    public Quadtree getWorld() {
        return world;
    }

    public PageView getPageView() {
        return pageView;
    }

    public View getView(float x, float y) {
        if (views.size() < 1) {
            return null;
        }
        View best = null; // best found match
        for (View view : views) {
            if (view.getTopLeft().x > x) {
                continue;
            }
            if (view.getBottomRight().x < x) {
                continue;
            }
            if (view.getTopLeft().y > y) {
                continue;
            }
            if (view.getBottomRight().y < y) {
                continue;
            }

            // find the view that on top
            if (best != null) {
                if (view.getX() > best.getX() || view.getY() > best.getY()) {
                    best = view;
                } else if (view.getWidth() < best.getWidth() || view.getHeight() < best.getHeight()) {
                    best = view;
                }
            } else {
                best = view;
            }
        }
        return best;
    }

    public void setViews(ArrayList<View> views) {
        this.views.clear();
        this.views.addAll(views);
    }

    public boolean isPaused() {
        return pauseTimer.isRunning();
    }

    public static float DeltaStep(){
        return deltaStep;
    }

    private int calculateSteps(float elapsed) {
        // Our simulation frequency is 240Hz, a (four one sixth) ms period.

        // We will pretend our display sync rate is one of these:
        if (elapsed > 7.5 * stepSize)
            return 8;  // 240 hz = [30 Hz ( .. to 32 Hz )],             120 hz = [15 Hz ( .. to 16 Hz )]
        else if (elapsed > 6.5 * stepSize)
            return 7; // 240 hz = [34.29 Hz ( 32 Hz to 36.92 Hz )],     120 hz = [17 Hz ( 16 Hz to 18.46 Hz )]
        else if (elapsed > 5.5 * stepSize)
            return 6; // 240 hz = [40 Hz ( 36.92 Hz to 43.64 Hz )],     120 hz = [20 Hz ( 18.46 Hz to 21.82 Hz )]
        else if (elapsed > 4.5 * stepSize)
            return 5; // 240 hz = [48 Hz ( 43.64 Hz to 53.33 Hz )],     120 hz = [24 Hz ( 21.82 Hz to 26.67 Hz )]
        else if (elapsed > 3.5 * stepSize)
            return 4; // 240 hz = [60 Hz ( 53.33 Hz to 68.57 Hz )],     120 hz = [30 Hz ( 26.67 Hz to 34.29 Hz )]
        else if (elapsed > 2.5 * stepSize)
            return 3; // 240 hz = [90 Hz ( 68.57 Hz to 96 Hz )],        120 hz = [41 Hz ( 34.29 Hz to 48 Hz )]
        else if (elapsed > 1.5 * stepSize)
            return 2; // 240 hz = [120 Hz ( 96 Hz to 160 Hz )],         120 hz = [60 Hz ( 48 Hz to 80 Hz )]
        else
            return 1; // 240 hz = [240 Hz ( 160 Hz to .. )],            120 hz = [120 Hz ( 80 Hz to .. )]
    }

}
