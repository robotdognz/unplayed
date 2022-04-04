package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;

import camera.Camera;
import camera.FreeCamera;
import camera.GameCamera;
import controllers.Controller;
import controllers.PlayerControl;
import editor.Editor;
import handlers.LoadingHandler;
import handlers.TextureCache;
import misc.Converter;
import misc.DoToast;
import misc.EditorJSON;
import misc.FileChooser;
import misc.KetaiGesture;
import misc.Vibe;
import objects.*;
import processing.core.*;
import processing.event.TouchEvent;
import ui.LaunchMenu;
import ui.LoadingMenu;
import ui.Menu;
import ui.TitleMenu;

//handles all of the logic at the application level
public class AppLogic {
    private static PApplet p;
    public static Activity activity;
    private static Context context;

    public static KetaiGesture gesture;
    public static FileChooser files;
    static public Converter convert;
    public static TextureCache texture;

    static public DoToast toast;

    static private Menu menu; // current menu
    static private boolean menuAdded = false;
    static private boolean menuRemoved = false;

    public static boolean editorToggle; // is the editor enabled
    static public Game game; // holds the game class
    public static Controller controller; // holds the current controller
    public static Editor editor; // holds the editor
    public static ArrayList<PVector> touches; // all the on screen touches
    public static PVector lastTouch; // the last on screen touch

    public static InGameUI drawUI; // draws the on screen controls

    private static ArrayList<File> levels;
    private static int currentLevel;
    public static SharedPreferences settings;
    public static SharedPreferences.Editor saveGame;
    public static int savedLevel = 0;

    private static ArrayList<Uri> externalLevels;

    private static boolean skipNextFrame = false; // skips running for a step so we don't see force drawn assets or a lag spike
    private static boolean startLevel = false; // run start level this frame

    public AppLogic(PApplet pApplet, Activity masterActivity, Context masterContext) {
        p = pApplet;
        activity = masterActivity;
        context = masterContext;
        toast = new DoToast(activity);
        editorToggle = false;
    }

    static public void init() {
        editorToggle = false; // editor is closed on startup
        touches = new ArrayList<>();
        lastTouch = new PVector(0, 0);

        texture = new TextureCache(p, context);
        gesture = new KetaiGesture(p);
        files = new FileChooser(activity);

        // setup vibration class
        Vibe.setup(context);

        Camera camera = new GameCamera();
        convert = new Converter(p);
        game = new Game(p, camera, texture, convert);
        texture.passGame(game);
        controller = new PlayerControl(p, game);
        editor = null;
        drawUI = new InGameUI(p, p.width, p.height);

        // setup shared preferences (used for save games)
        settings = activity.getPreferences(0);
        saveGame = settings.edit();

        // add the title screen and initialise the camera logic
        titleScreen();
        game.getPageView().initCamera();

        p.background(100);

        // print android api version
        PApplet.println(android.os.Build.VERSION.SDK_INT);

    }

    static public void getSaveGame() {
        savedLevel = settings.getInt("level", 0);
    }

    static public void updateSaveGame() {
        if (externalLevels != null) {
            // return if we're running an external campaign, so as not to overwrite main
            // campaign progress
            return;
        }
        savedLevel = currentLevel + 1;
        saveGame.putInt("level", savedLevel);
        saveGame.apply();
    }

    static public void clearSaveGame() {
        saveGame.putInt("level", 0);
        saveGame.apply();
    }

    static public void getLevels() {
        levels = new ArrayList<>();
        // generate all the relative file paths
        try {
            // App mode

            AssetManager am = context.getAssets();
            String levelPath = "levels";
            String[] levelStrings = am.list(levelPath);

            if (levelStrings.length == 0) {
                throw new IOException();
            }

            // make relative files from all of the level strings
            for (String levelString : levelStrings) {
                levels.add(new File(levelPath + '/' + levelString));
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File levelPath = new File(base + "/levels" + '/');

            File[] absoluteFiles = levelPath.listFiles();

            // make relative files from all of the tile strings
            if (absoluteFiles != null) {
                for (File absoluteFile : absoluteFiles) {
                    String relativeFile = absoluteFile.toString();
                    relativeFile = relativeFile.replace(base + '/', "");
                    levels.add(new File(relativeFile));
                }
            }
        }

        Collections.sort(levels);
        externalLevels = null; //false;
    }

    static public void setExternalLevels(ArrayList<Uri> newLevels) {
        externalLevels = newLevels;
    }

    static public void continueGame() {
        currentLevel = savedLevel - 1;

        if (levels != null && levels.size() > currentLevel) {
            loadingScreen(true);
        }
    }

    static public void newGame() {
        // used by both 'New Game' in main menu and 'Load Levels' in dev menu
        currentLevel = 0;

        if ((externalLevels != null && externalLevels.size() > currentLevel) || (levels != null && levels.size() > currentLevel)) {
            loadingScreen(true);
        }
    }

    static public void nextLevel() {
        currentLevel++;

//        if ((externalLevels != null && externalLevels.size() > currentLevel) ||
//                (externalLevels == null && levels != null && levels.size() > currentLevel)) {
//            // there is an external campaign running and it has more levels
//            // or there is no external campaign and main campaign has more levels
//        } else {
//            menu = null;
//            if (externalLevels == null) {
//                // only clear save game if we're not running an external campaign
//                clearSaveGame();
//            }
////            titleScreen();
//        }
        loadingScreen(false);
    }

    static public void restartLevel() {
        // used when running level in editor

        if (editorToggle && !Editor.showPageView) {
            // playing with editor open, just restart
            startLevel();
        } else {
            // playing in preview mode with no editor visible, restart with menu
            loadingScreen(false);
        }
    }

    static public void startLevel() {
        if (editor != null) {
            // we're in the editor
            game.startGame();
        } else {
            // not in the editor

            EditorJSON json = new EditorJSON(p, texture, null);

            if (externalLevels != null && externalLevels.size() > currentLevel) {
                // in external campaign
                json.load(game, externalLevels.get(currentLevel));
            } else if (externalLevels == null && levels != null && levels.size() > currentLevel) {
                // in main campaign
                json.load(game, levels.get(currentLevel).toString());
            } else {
                // neither
                if (externalLevels == null) {
                    // only clear save game if we're not running an external campaign
                    clearSaveGame();
                } else {
                    // otherwise remove the external campaign
                    externalLevels = null;
                }
                titleScreen();
                return;
            }

            game.startGame(); // needed to reset game after loading in new level
            // step the loaded pages so we can access where the player is visible
            game.getPageView().stepPages();

            // move loaded level so that it's just off screen
            // calculate offset
            Rectangle pageArea = game.getPageView().getLevelArea();
            Rectangle playerArea = game.getPageView().getPlayerVisibleArea();
            Rectangle menuArea = menu.getArea();

            // calculate the amount of padding there will be around the player area, this is used to prevent
            // the level being created too close to the current menu when tall screen scaling is used
            Rectangle visibleArea = game.getPageView().getPageCamera().calculateScreenArea(playerArea, false);
            float horizontalOffset = visibleArea.getWidth() - playerArea.getWidth() + 200;
            // the 200 is 2x the camera edge padding (needed when not using tall screen space scaling),
            // shouldn't be hard coded like this

            // get edge of pageArea closest to center of playerArea
            PVector playerAreaCenter = playerArea.getRectangleCenter();
            PVector pageAreaCenter = pageArea.getRectangleCenter();
            PVector diff = pageAreaCenter.copy();
            diff.x -= playerAreaCenter.x;
            diff.y -= playerAreaCenter.y;

            PVector menuCenter = menuArea.getRectangleCenter();

            float offsetX;
            float offsetY;
            offsetX = menuCenter.x - playerAreaCenter.x;
            offsetY = menuCenter.y - playerAreaCenter.y;

            float bias = 200; // this bias makes menus and loading screens tend towards being placed to the right
            if (diff.x <= 0 - bias) {
                // move off to left edge of pageArea
                offsetX -= pageArea.getBottomRight().x - playerAreaCenter.x + menuArea.getWidth() / 2 + horizontalOffset;
            } else {
                // move off to right edge of pageArea
                offsetX += playerAreaCenter.x - pageArea.getTopLeft().x + menuArea.getWidth() / 2 + horizontalOffset;
            }

            game.getPageView().offsetAll(offsetX, offsetY);

            // force draw all assets in level
            List<PageViewObject> tempPageViewObjects = game.getPageView().getPageViewObjects();
            p.pushMatrix();
            p.translate(p.width * 0.5f, p.height * 0.5f);
            for (PageViewObject object : tempPageViewObjects) {
                if (object instanceof Page) {
                    ((Page) object).step();
                }
                p.pushMatrix();
                PVector pos = object.getPosition();
                p.translate(-pos.x, -pos.y);
                object.draw(TextureCache.LOD256);
                object.draw(TextureCache.LOD128);
                object.draw(TextureCache.LOD64);
                object.draw(TextureCache.LOD32);
                p.popMatrix();
            }
            // force draw next loading screen
            if (game.currentLoading != null) {
                game.currentLoading.drawAll();
            }
            p.popMatrix();

            // prevent animation jump by skipping the next frame
            skipNextFrame = true;

            // update save game to this level
            updateSaveGame();

            // remove the loading menu from this class, it will be kept in the page view
            // until it is off screen
        }
        removeMenu();
    }

    static public void titleScreen() {

        // load in the title splash screen
        File testFile = new File("TitleScreen.png");
        LoadingHandler loading = new LoadingHandler(p, texture, testFile, 8, 8, true, true);

        // create the loading screen
        Menu temp = new TitleMenu(p, loading);

        // do calculations and place the loading screen into the game world
        Rectangle pageArea = game.getPageView().getFullArea();
        temp.buildPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea, game.getPageView().getPageCamera());

        // pass the loading screen to the game logic
        addMenu(temp);
    }

    static public void mainMenu() {
        // create main menu screen
        Menu temp = new LaunchMenu(p);

        // do calculations and place the menu into the game world
        Rectangle pageArea = game.getPageView().getFullArea();
        temp.buildPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea, game.getPageView().getPageCamera());

        // pass the menu to the game logic
        addMenu(temp);
    }

    /**
     * Creates a loading screen that will be used to transition between the current level or menu
     * and the next level or menu.
     *
     * @param useDefault true if the loading screen should be telling the player how to hold their
     *                   phone, otherwise false
     */
    static public void loadingScreen(boolean useDefault) {
        LoadingHandler loading = game.currentLoading;

        if (useDefault) {
            // load in the phone holding instructions loading screen
            File testFile = new File("StartLoadingScreen.png");
            loading = new LoadingHandler(p, texture, testFile, 8, 8, true, true);
        }

        // create the loading screen
        Menu temp = new LoadingMenu(p, loading);

        // do calculations and place the loading screen into the game world
        Rectangle pageArea = game.getPageView().getFullArea();
        temp.buildPageMenu(game.getPageView().getPageCamera().getCenter(), pageArea, game.getPageView().getPageCamera());

        // pass the loading screen to the game logic
        addMenu(temp);
    }

    static public void toggleEditor() {
        editorToggle = !editorToggle;
        if (editorToggle) {
            Camera camera = new FreeCamera();
            editor = new Editor(p, camera, toast);

            // re-center page view objects
            game.getPageView().recenterObjects();

            // set the save file for the level being edited
            if (externalLevels != null) {
                if (externalLevels.size() > currentLevel) {
                    files.setUri(externalLevels.get(currentLevel));
                }

                externalLevels = null;
            }

        }
        menu = null;
        game.getPageView().clearMenus();
    }

    static public Editor getEditor() {
        return editor;
    }

    static public void setUri(Uri uri) {
        files.setUri(uri);
    }

    static public void setUris(ArrayList<Uri> uris) {
        files.setUris(uris);
    }

    static public void draw(float deltaTime) {
        // This is the step method for the whole game, as well as the draw method

        if (skipNextFrame) {
            skipNextFrame = false;
            return;
        }

        // touch screen
        touches.clear();
        for (TouchEvent.Pointer t : p.touches) {
            touches.add(new PVector(t.x, t.y));
        }
        if (p.touches.length > 0) {
            lastTouch = new PVector(p.touches[p.touches.length - 1].x, p.touches[p.touches.length - 1].y);
        } else {
            lastTouch = new PVector(0, 0);
        }

        // step the game and editor
        if (editorToggle && editor != null) {
            // step editor or game controller depending on editor toggle
            editor.step(touches, deltaTime);
            if (menu == null) {
                game.step(deltaTime); // step game and physics
            }
        } else {
            if (menu == null) {
                controller.step(touches);
                game.step(deltaTime); // step game and physics
            }
        }
        game.cameraStep(deltaTime); // step camera movement
        if (startLevel) {
            startLevel = false;
            startLevel();
        }

        // draw the game
        if ((editor != null && !editorToggle) || (editor != null && Editor.showPageView) || (editor == null)) {
            game.draw(); // draw the game
        }

        float controlHeight = p.height;

        if (editorToggle && editor != null) {
            editor.draw(deltaTime, lastTouch, menu);
            controlHeight = editor.getBottom();
        }

        // on screen controls
        // tell the controls the current state
        boolean drawControls = (game.player != null && controller instanceof PlayerControl && menu == null);
        boolean drawMenu = !editorToggle;
        drawUI.step(deltaTime, drawControls, controlHeight, drawMenu, lastTouch);
        // draw the controls
        drawUI.draw(p);

        // draw the menu
        if (menu != null) {
            if (!Camera.getGame()) {
                menu.drawOnTop();
            }
            menu.hover(lastTouch);
        }
    }

    static public void touchStarted() {
        // find true last touch
        if (p.touches.length >= touches.size() && p.touches.length > 1) {
            for (int i = 0; i < p.touches.length; i++) {
                boolean match = false;
                for (PVector t : touches) {
                    float currentDiff = PApplet
                            .sqrt(PApplet.sq(t.x - p.touches[i].x) + PApplet.sq(t.x - p.touches[i].x));
                    if (currentDiff < 10) {
                        match = true;
                    }
                }
                if (!match) { // no match for current touch, so it's new
                    lastTouch = new PVector(p.touches[i].x, p.touches[i].y);
                }
            }
        } else if (p.touches.length == 1) {
            lastTouch = new PVector(p.touches[p.touches.length - 1].x, p.touches[p.touches.length - 1].y);
        }

        if (menu == null) {
            if (editorToggle && editor != null) {
                editor.touchStarted(lastTouch);
            } else {
                controller.touchStarted(lastTouch);
            }
        }
    }

    static public void touchEnded() {
        if (editorToggle && editor != null) {
            editor.touchEnded(lastTouch);
        } else {
            drawUI.touchEnded();
        }

        if (menu != null) {
            menu.click();
        }
    }

    static public void touchMoved() {
        if (menu == null) {
            if (editorToggle && editor != null) {
                editor.touchMoved(lastTouch, touches);
            } else {
                controller.touchMoved(lastTouch, touches);
            }
        }
    }

    static public void onTap(float x, float y) {
        if (menu == null) {
            if (editorToggle && editor != null) {
                editor.onTap(x, y);
            }
//			else {
//				 controller.onTap(x, y);
//			}
        }
    }

    @SuppressWarnings("unused")
    static public void onDoubleTap(float x, float y) {

    }

    @SuppressWarnings("unused")
    static public void onFlick(float x, float y, float px, float py, float v) {
        // x/y start of flick
        // px/yx end of flick
        // v velocity of flick
    }

    @SuppressWarnings("unused")
    static public void onLongPress(float x, float y) {

    }

    static public void onPinch(float x, float y, float d) {
        if (menu == null) {
            if (editorToggle && editor != null) {
                editor.onPinch(touches, x, y, d);
            } else {
                controller.onPinch(touches, x, y, d);
            }
        }
    }

    static public void onRotate(float x, float y, float angle) {
        if (menu == null) {
            if (editorToggle && editor != null) {
                editor.onRotate(x, y, angle);
            } else {
                controller.onRotate(x, y, angle);
            }
        }
    }

    // menu

    static public boolean hasMenu() {
        return menu != null;
    }

    static public void addMenu(Menu newMenu) {
        // menus act as a linked list
        // add to head
        if (menu != null) {
            Menu temp = menu;
            menu = newMenu;
            menu.child = temp;
        } else {
            menu = newMenu;
        }

        menuAdded = true;
        menuRemoved = false;
    }

    static public void removeMenu() {
        // menus act as a linked list
        // remove from tail
        if (menu != null) {

            if (menu.child != null) {
                Menu temp = menu;

                while (temp.child.child != null) {
                    temp = temp.child;
                }

                temp.child = null;

            } else {
                menu = null;
            }

        }

        menuAdded = false;
        menuRemoved = true;
    }

    static public void previousMenu() {
        // menus act as a linked list
        // swap the first and second menus in the list around
        if (menu != null && menu.child != null) {
            // menu.child will become the new menu
            // menu will become the new menu.child
            Menu temp = menu; // old head
            menu = menu.child; // old body, becomes new head
            temp.child = menu.child; // make all children of old body children of old head
            menu.child = temp; // make old head child of new head
        }

        // might need these?
        menuAdded = true;
        menuRemoved = false;
    }

    static public Menu getMenu() {
        return menu;
    }

    static public boolean menuAdded() {
        boolean temp = menuAdded;
        menuAdded = false;
        return temp;
    }

    static public boolean menuRemoved() {
        boolean temp = menuRemoved;
        menuRemoved = false;
        return temp;
    }

    // quit

    static public void quit() {
        activity.finish();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static public void quitPurge() {
        // should update this so it deletes directories etc

        // purge resources
        // root
        File rootDir = new File(p.dataPath("") + '/');
        File[] rootPaths = rootDir.listFiles();
        if (rootPaths != null) {
            for (File file : rootPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // levels
        File levelDir = new File(p.dataPath("levels") + '/');
        File[] levelPaths = levelDir.listFiles();
        if (levelPaths != null) {
            for (File file : levelPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // tiles
        File tileDir = new File(p.dataPath("unplayed_tiles") + '/');
        File[] tilePaths = tileDir.listFiles();
        if (tilePaths != null) {
            for (File file : tilePaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // images
        File imageDir = new File(p.dataPath("unplayed_images") + '/');
        File[] imagePaths = imageDir.listFiles();
        if (imagePaths != null) {
            for (File file : imagePaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // background images
        File backgroundDir = new File(p.dataPath("unplayed_backgrounds") + '/');
        File[] backgroundPaths = backgroundDir.listFiles();
        if (backgroundPaths != null) {
            for (File file : backgroundPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // loading images
        File loadingDir = new File(p.dataPath("unplayed_loading") + '/');
        File[] loadingPaths = loadingDir.listFiles();
        if (loadingPaths != null) {
            for (File file : loadingPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // button images
        File buttonDir = new File(p.dataPath("unplayed_buttons") + '/');
        File[] buttonPaths = buttonDir.listFiles();
        if (buttonPaths != null) {
            for (File file : buttonPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // events
        File eventDir = new File(p.dataPath("unplayed_events") + '/');
        File[] eventPaths = eventDir.listFiles();
        if (eventPaths != null) {
            for (File file : eventPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // ui
        File uiDir = new File(p.dataPath("ui") + '/');
        File[] uiPaths = uiDir.listFiles();
        if (uiPaths != null) {
            for (File file : uiPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // widgets
        File widgetDir = new File(p.dataPath("ui") + '/' + "widgets" + '/');
        File[] widgetPaths = widgetDir.listFiles();
        if (widgetPaths != null) {
            for (File file : widgetPaths) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        // clear save game
        clearSaveGame();
        // quit the game
        quit();
    }

    public static void setStartLevel() {
        // used to make the draw/step loop run the startGame() method in it's next
        // iteration
        AppLogic.startLevel = true;
    }

}
