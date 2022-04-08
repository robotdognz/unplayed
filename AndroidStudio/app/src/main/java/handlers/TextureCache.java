package handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;

import game.Game;
import objects.Event;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import objects.events.Spike;
import processing.core.*;

public class TextureCache {
    private final PApplet p;
    private Game game;
    private final Context context;

    // LODs, these numbers represent how many squares across is the current zoom level
    static public int LOD256 = 4;
    static public int LOD128 = 8;
    static public int LOD64 = 16;
    static public int LOD32 = 64;

    // grid paper textures
    static private PImage gridLOD256;
    static private PImage gridLOD128;
    static private PImage gridLOD64;
    static private PImage gridLOD32;
    static private PImage gridLOD16;
    static private PImage gridLOD8;

    // page view background textures
    static private PImage pageViewBackgroundLOD256;
    static private PImage pageViewBackgroundLOD128;
    static private PImage pageViewBackgroundLOD64;
    static private PImage pageViewBackgroundLOD32;

    static private PImage controlsLeft;
    static private PImage controlsJump;
    static private PImage controlsRight;
    static private PImage controlsMenu;

    // images
    private HashMap<File, ImageHandler> imageMap;
    private ArrayList<ImageHandler> imageList;
    // background images
    private HashMap<File, BackgroundHandler> backgroundMap;
    private ArrayList<BackgroundHandler> backgroundList;
    // loading screens
    private HashMap<File, LoadingHandler> loadingMap;
    private ArrayList<LoadingHandler> loadingList;
    //buttons
    private HashMap<File, ButtonHandler> buttonMap;
    private ArrayList<ButtonHandler> buttonList;
    // tiles
    private HashMap<File, TileHandler> tileMap;
    private ArrayList<TileHandler> tileList;
    // events
    private HashMap<String, EventHandler> eventMap;
    private ArrayList<EventHandler> eventList;

    public TextureCache(PApplet p, Context context) {
        this.p = p;
        this.context = context;
        // sprite = requestImage("image.png") // this loads the image on a separate
        // thread
        // you can check if it has loaded by querying its dimensions, they will be 0 if
        // loading, -1 if failed to load
        // and > 0 if it has loaded

        // paper textures
        gridLOD256 = p.loadImage("PaperGrid_1024x1024.png");
        gridLOD128 = p.loadImage("PaperGrid_512x512.png");
        gridLOD64 = p.loadImage("PaperGrid_256x256.png");
        gridLOD32 = p.loadImage("PaperGrid_128x128.png");
        gridLOD16 = p.loadImage("PaperGrid_64x64.png");
        gridLOD8 = p.loadImage("PaperGrid_32x32.png");

        // page view background texture
        int pvbSize = 4;
        pageViewBackgroundLOD256 = p.loadImage("PageViewBackground.jpg");
        pageViewBackgroundLOD256.resize(256 * pvbSize, 256 * pvbSize);
        pageViewBackgroundLOD128 = pageViewBackgroundLOD256.get();
        pageViewBackgroundLOD128.resize(128 * pvbSize, 128 * pvbSize);
        pageViewBackgroundLOD64 = pageViewBackgroundLOD256.get();
        pageViewBackgroundLOD64.resize(64 * pvbSize, 64 * pvbSize);
        pageViewBackgroundLOD32 = pageViewBackgroundLOD256.get();
        pageViewBackgroundLOD32.resize(32 * pvbSize, 32 * pvbSize);

        // control sprites
        controlsLeft = p.loadImage("ControlsLeft.png");
        controlsJump = p.loadImage("ControlsJump.png");
        controlsRight = p.loadImage("ControlsRight.png");
        controlsMenu = p.loadImage("ControlsMenu.png");

        // level assets
        loadLevelImages();
        loadBackgroundImages();
        loadLoadingImages();
        loadButtonImages();
        loadTiles();
        loadEvents();
    }

    public void passGame(Game game) {
        this.game = game;
    }

    static public PImage getPageViewBackground(float scale) {
        if (scale > 16) {
            return pageViewBackgroundLOD32; // between 16 and 32
        } else if (scale > 8) {
            return pageViewBackgroundLOD64; // between 8 and 16
        } else if (scale > 4) {
            return pageViewBackgroundLOD128; // between 4 and 8
        } else {
            return pageViewBackgroundLOD256; // less than 4
        }
    }

    static public PImage getGrid(float scale) {
        if (scale > 64) {
            return gridLOD8; // larger than 64
        } else if (scale > 32) {
            return gridLOD16; // between 32 and 64
        } else if (scale > 16) {
            return gridLOD32; // between 16 and 32
        } else if (scale > 8) {
            return gridLOD64; // between 8 and 16
        } else if (scale > 4) {
            return gridLOD128; // between 4 and 8
        } else {
            return gridLOD256; // less than 4
        }
    }

    static public PImage getControlsLeft() {
        return controlsLeft;
    }

    static public PImage getControlsJump() {
        return controlsJump;
    }

    static public PImage getControlsRight() {
        return controlsRight;
    }

    static public PImage getControlsMenu() {
        return controlsMenu;
    }


    private void loadTiles() {

        // generate all the relative file paths
        // tiles
        File[] tilePaths;
        try {
            // App mode

            AssetManager am = context.getAssets();
            String tilePath = "unplayed_tiles";
            String[] tileStrings = am.list(tilePath);

            if (tileStrings.length == 0) {
                throw new IOException();
            }

            tilePaths = new File[tileStrings.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < tileStrings.length; i++) {
                tilePaths[i] = new File(tilePath + '/' + tileStrings[i]);
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File tilePath = new File(base + "/unplayed_tiles" + '/');

            File[] absoluteFiles = tilePath.listFiles();
            assert absoluteFiles != null;
            tilePaths = new File[absoluteFiles.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < absoluteFiles.length; i++) {
                String relativeFile = absoluteFiles[i].toString();
                relativeFile = relativeFile.replace(base + '/', "");
                tilePaths[i] = new File(relativeFile);
            }
        }

        tileMap = new HashMap<>();
        for (File file : tilePaths) {
            String path = file.getAbsolutePath();

            if (path.matches(".+[0-2]\\.png$")) { // check for .png with 0-2 before it
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m = pattern.matcher(path);
                int lastInt = -1;
                while (m.find()) {
                    lastInt = Integer.parseInt(m.group());
                }
                if (lastInt >= 0 && lastInt <= 2) {
                    tileMap.put(file, new TileHandler(p, this, file, lastInt));
                }
            }
        }
        tileList = new ArrayList<>(tileMap.values());
        Collections.sort(tileList);
    }

    private void loadLevelImages() {
        // generate all the relative file paths
        // level images
        File[] imagePaths;
        try {
            // App mode

            AssetManager am = context.getAssets();
            String imagePath = "unplayed_images";
            String[] imageStrings = am.list(imagePath);

            if (imageStrings.length == 0) {
                throw new IOException();
            }

            imagePaths = new File[imageStrings.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < imageStrings.length; i++) {
                imagePaths[i] = new File(imagePath + '/' + imageStrings[i]);
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File imagePath = new File(base + "/unplayed_images" + '/');

            File[] absoluteFiles = imagePath.listFiles();
            assert absoluteFiles != null;
            imagePaths = new File[absoluteFiles.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < absoluteFiles.length; i++) {
                String relativeFile = absoluteFiles[i].toString();
                relativeFile = relativeFile.replace(base + '/', "");
                imagePaths[i] = new File(relativeFile);
            }
        }

        imageMap = new HashMap<>();
        ArrayList<Integer> temp = new ArrayList<>(); // holds the numbers found in the file name
        for (File file : imagePaths) {
            String path = file.getAbsolutePath();
            if (path.matches(".+([0-9]+)x([0-9]+)\\.png$")) { // check file ends with number "x" number ".png"
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m = pattern.matcher(path);
                while (m.find()) {
                    int i = Integer.parseInt(m.group());
                    temp.add(i);
                }
                if (temp.size() >= 2) {
                    imageMap.put(file,
                            new ImageHandler(p, this, file, temp.get(temp.size() - 2), temp.get(temp.size() - 1)));
                }
            }
            temp.clear();
        }
        imageList = new ArrayList<>(imageMap.values());
        Collections.sort(imageList);
    }

    private void loadEvents() {

        // get directory and make map
        File eventDir = new File("unplayed_events" + '/');
        eventMap = new HashMap<>();

        // ----------------PlayerStart----------------
        final String playerStartString = "PlayerStart";
        File playerStartFile = new File(eventDir + "/Event_playerStart.png");
        EventHandler playerStart = new EventHandler(p, this, playerStartFile) {
            @Override
            public Event makeEvent(int x, int y) {
                return new PlayerStart(game, texture, playerStartString, x, y);
            }
        };
        eventMap.put(playerStartString, playerStart);

        // ----------------PlayerEnd----------------
        final String playerEndString = "PlayerEnd";
        File playerEndFile = new File(eventDir + "/Event_playerFinish.png");
        EventHandler playerEnd = new EventHandler(p, this, playerEndFile) {
            @Override
            public Event makeEvent(int x, int y) {
                return new PlayerEnd(game, texture, playerEndString, x, y);
            }
        };
        eventMap.put(playerEndString, playerEnd);

        // ----------------PlayerDeath----------------
        final String playerDeathString = "PlayerDeath";
        File playerDeathFile = new File(eventDir + "/Event_PlayerDeath.png");
        EventHandler playerDeath = new EventHandler(p, this, playerDeathFile) {
            @Override
            public Event makeEvent(int x, int y) {
                return new PlayerDeath(game, texture, playerDeathString, x, y);
            }
        };
        eventMap.put(playerDeathString, playerDeath);

        // ----------------Spike----------------
        final String spikeString = "Spike";
        File spikeFile = new File(eventDir + "/Event_Spikes.png");
        EventHandler spike = new EventHandler(p, this, spikeFile) {
            @Override
            public Event makeEvent(int x, int y) {
                return new Spike(game, texture, spikeString, x, y, editorRotation);
            }
        };
        eventMap.put(spikeString, spike);

        // make sorted list
        eventList = new ArrayList<>(eventMap.values());
        Collections.sort(eventList);
    }

    private void loadBackgroundImages() {
        // generate all the relative file paths
        File[] backgroundPaths;
        try {
            // App mode

            AssetManager am = context.getAssets();
            String backgroundPath = "unplayed_backgrounds";
            String[] backgroundStrings = am.list(backgroundPath);

            if (backgroundStrings.length == 0) {
                throw new IOException();
            }

            backgroundPaths = new File[backgroundStrings.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < backgroundStrings.length; i++) {
                backgroundPaths[i] = new File(backgroundPath + '/' + backgroundStrings[i]);
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File backgroundPath = new File(base + "/unplayed_backgrounds" + '/');

            File[] absoluteFiles = backgroundPath.listFiles();
            assert absoluteFiles != null;
            backgroundPaths = new File[absoluteFiles.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < absoluteFiles.length; i++) {
                String relativeFile = absoluteFiles[i].toString();
                relativeFile = relativeFile.replace(base + '/', "");
                backgroundPaths[i] = new File(relativeFile);
            }
        }

        backgroundMap = new HashMap<>();
        ArrayList<Integer> temp = new ArrayList<>(); // holds the numbers found in the file name
        for (File file : backgroundPaths) {
            String path = file.getAbsolutePath();
            if (path.matches(".+([0-9]+)x([0-9]+)_([0-9])_([0-9])\\.png$")) { // check file ends with number "x" number ".png"
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m = pattern.matcher(path);
                while (m.find()) {
                    int i = Integer.parseInt(m.group());
                    temp.add(i);
                }
                if (temp.size() >= 3) {
                    // get values and use them to build the object
                    int width = temp.get(temp.size() - 4);
                    int height = temp.get(temp.size() - 3);
                    boolean fixedSize = temp.get(temp.size() - 2) == 1;
                    boolean shadow = temp.get(temp.size() - 1) == 1;
                    backgroundMap.put(file, new BackgroundHandler(p, this, file, width, height, fixedSize, shadow));
                }
            }
            temp.clear();
        }
        backgroundList = new ArrayList<>(backgroundMap.values());
        Collections.sort(backgroundList);
    }

    private void loadLoadingImages() {
        // generate all the relative file paths
        File[] loadingPaths;
        try {
            // App mode

            AssetManager am = context.getAssets();
            String loadingPath = "unplayed_loading";
            String[] loadingStrings = am.list(loadingPath);

            if (loadingStrings.length == 0) {
                throw new IOException();
            }

            loadingPaths = new File[loadingStrings.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < loadingStrings.length; i++) {
                loadingPaths[i] = new File(loadingPath + '/' + loadingStrings[i]);
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File loadingPath = new File(base + "/unplayed_loading" + '/');

            File[] absoluteFiles = loadingPath.listFiles();
            assert absoluteFiles != null;
            loadingPaths = new File[absoluteFiles.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < absoluteFiles.length; i++) {
                String relativeFile = absoluteFiles[i].toString();
                relativeFile = relativeFile.replace(base + '/', "");
                loadingPaths[i] = new File(relativeFile);
            }
        }

        loadingMap = new HashMap<>();
        ArrayList<Integer> temp = new ArrayList<>(); // holds the numbers found in the file name
        for (File file : loadingPaths) {
            String path = file.getAbsolutePath();
            if (path.matches(".+([0-9]+)x([0-9]+)_([0-9])_([0-9])\\.png$")) { // check file ends with number "x" number ".png"
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m = pattern.matcher(path);
                while (m.find()) {
                    int i = Integer.parseInt(m.group());
                    temp.add(i);
                }
                if (temp.size() >= 3) {
                    // get values and use them to build the object
                    int width = temp.get(temp.size() - 4);
                    int height = temp.get(temp.size() - 3);
                    boolean button = temp.get(temp.size() - 2) == 1;
                    boolean shadow = temp.get(temp.size() - 1) == 1;
                    loadingMap.put(file, new LoadingHandler(p, this, file, width, height, button, shadow));
                }
            }
            temp.clear();
        }
        loadingList = new ArrayList<>(loadingMap.values());
        Collections.sort(loadingList);
    }

    private void loadButtonImages() {
        // generate all the relative file paths
        // buttons
        File[] buttonPaths;
        try {
            // App mode

            AssetManager am = context.getAssets();
            String buttonPath = "unplayed_buttons";
            String[] buttonStrings = am.list(buttonPath);

            if (buttonStrings.length == 0) {
                throw new IOException();
            }

            buttonPaths = new File[buttonStrings.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < buttonStrings.length; i++) {
                buttonPaths[i] = new File(buttonPath + '/' + buttonStrings[i]);
            }

        } catch (IOException e) {
            // Preview mode

            String base = p.sketchPath("");
            File buttonPath = new File(base + "/unplayed_buttons" + '/');

            File[] absoluteFiles = buttonPath.listFiles();
            assert absoluteFiles != null;
            buttonPaths = new File[absoluteFiles.length];

            // make relative files from all of the tile strings
            for (int i = 0; i < absoluteFiles.length; i++) {
                String relativeFile = absoluteFiles[i].toString();
                relativeFile = relativeFile.replace(base + '/', "");
                buttonPaths[i] = new File(relativeFile);
            }
        }

        buttonMap = new HashMap<>();
        ArrayList<Integer> temp = new ArrayList<>(); // holds the numbers found in the file name
        for (File file : buttonPaths) {
            String path = file.getAbsolutePath();
            if (path.matches(".+([0-9]+)x([0-9]+)\\.png$")) { // check file ends with number "x" number ".png"
                Pattern pattern = Pattern.compile("\\d+");
                Matcher m = pattern.matcher(path);
                while (m.find()) {
                    int i = Integer.parseInt(m.group());
                    temp.add(i);
                }
                if (temp.size() >= 2) {
                    buttonMap.put(file,
                            new ButtonHandler(p, this, file, temp.get(temp.size() - 2), temp.get(temp.size() - 1)));
                }
            }
            temp.clear();
        }
        buttonList = new ArrayList<>(buttonMap.values());
        Collections.sort(buttonList);
    }

    public HashMap<File, TileHandler> getTileMap() {
        return tileMap;
    }

    public ArrayList<TileHandler> getTileList() {
        return tileList;
    }

    public HashMap<File, ImageHandler> getImageMap() {
        return imageMap;
    }

    public ArrayList<ImageHandler> getImageList() {
        return imageList;
    }

    public HashMap<File, BackgroundHandler> getBackgroundMap() {
        return backgroundMap;
    }

    public ArrayList<BackgroundHandler> getBackgroundList() {
        return backgroundList;
    }

    public HashMap<File, LoadingHandler> getLoadingMap() {
        return loadingMap;
    }

    public ArrayList<LoadingHandler> getLoadingList() {
        return loadingList;
    }

    public HashMap<File, ButtonHandler> getButtonMap() {
        return buttonMap;
    }

    public ArrayList<ButtonHandler> getButtonList() {
        return buttonList;
    }

    public HashMap<String, EventHandler> getEventMap() {
        return eventMap;
    }

    public ArrayList<EventHandler> getEventList() {
        return eventList;
    }

    public void drawAll(PApplet p) {

        // draw all tiles
        for (TileHandler t : tileList) {
            t.draw(p.g, 0, 0, TextureCache.LOD256);
            Object object = p.g.getCache(t.getSprite(TextureCache.LOD256));
            p.g.setCache(t.getSprite(TextureCache.LOD256), object);


//            t.draw(p.g, x, y, TextureCache.LOD128);
//            t.draw(p.g, x, y, TextureCache.LOD64);
//            t.draw(p.g, x, y, TextureCache.LOD32);
        }

    }

}
