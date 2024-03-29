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
import objects.events.CameraChange;
import objects.events.PlayerDeath;
import objects.events.PlayerEnd;
import objects.events.PlayerStart;
import objects.events.Spike;
import processing.core.*;

public class TextureCache {
	PApplet p;
	Game game;
	Context context;

	// LODs
	public int LOD256 = 4;
	public int LOD128 = 8;
	public int LOD64 = 16;
	public int LOD32 = 64;

	// paper textures
	private PImage gridLOD256;
	private PImage gridLOD128;
	private PImage gridLOD64;
	private PImage gridLOD32;
	private PImage gridLOD16;
	private PImage gridLOD8;

	// desk textures
	private PImage deskBehind;
	private PImage deskInfront;

	// level images
	private File[] imagePaths;
	private HashMap<File, ImageHandler> imageMap;
	private ArrayList<ImageHandler> imageList;
	// tiles
	private File[] tilePaths;
	private HashMap<File, TileHandler> tileMap;
	private ArrayList<TileHandler> tileList;
	// events
	private HashMap<String, EventHandler> eventMap;
	private ArrayList<EventHandler> eventList;

	public TextureCache(PApplet p, Context context) {
		this.p = p;
		this.context = context;
		// sprite = requestImage("image.png") // this loads the image on a sperate
		// thread
		// you can check if it has loaded by querrying its dimentions, they will be 0 if
		// loading, -1 if failed to load
		// and > 0 if it has loaded

		// paper textures
		gridLOD256 = p.loadImage("PaperGrid_1024x1024.png");
		gridLOD256.resize((int) (p.width / 1.40625), (int) (p.width / 1.40625));
		gridLOD128 = p.loadImage("PaperGrid_512x512.png");
		gridLOD128.resize((int) (p.width / 2.8125), (int) (p.width / 2.8125));
		gridLOD64 = p.loadImage("PaperGrid_256x256.png");
		gridLOD64.resize((int) (p.width / 5.625), (int) (p.width / 5.625));
		gridLOD32 = p.loadImage("PaperGrid_128x128.png");
		gridLOD32.resize((int) (p.width / 11.25), (int) (p.width / 11.25));
		gridLOD16 = p.loadImage("PaperGrid_64x64.png");
		gridLOD16.resize((int) (p.width / 22.5), (int) (p.width / 22.5));
		gridLOD8 = p.loadImage("PaperGrid_32x32.png");
		gridLOD8.resize((int) (p.width / 45), (int) (p.width / 45));

		deskBehind = p.requestImage("PagesViewBackGround.png");
		deskInfront = p.requestImage("PagesViewBackGround_shading.png");

		// level assets
		loadLevelImages();
		loadTiles();
		loadEvents();
	}

	public void passGame(Game game) {
		this.game = game;
	}

	public PImage getGrid(float scale) {
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

	private void loadTiles() {

		// generate all the relative file paths
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
			tilePaths = new File[absoluteFiles.length];

			// make relative files from all of the tile strings
			for (int i = 0; i < absoluteFiles.length; i++) {
				String relativeFile = absoluteFiles[i].toString();
				relativeFile = relativeFile.replace(base + '/', "");
				tilePaths[i] = new File(relativeFile);
			}
		}

		tileMap = new HashMap<File, TileHandler>();
		for (File file : tilePaths) {
			String path = file.getAbsolutePath();
			if (path.matches(".+.png$")) { // only checks for .png
				tileMap.put(file, new TileHandler(p, this, file));
			}
		}
		tileList = new ArrayList<TileHandler>(tileMap.values());
		Collections.sort(tileList);
	}

	private void loadLevelImages() {
		// generate all the relative file paths
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
			imagePaths = new File[absoluteFiles.length];

			// make relative files from all of the tile strings
			for (int i = 0; i < absoluteFiles.length; i++) {
				String relativeFile = absoluteFiles[i].toString();
				relativeFile = relativeFile.replace(base + '/', "");
				imagePaths[i] = new File(relativeFile);
			}
		}

		imageMap = new HashMap<File, ImageHandler>();
		ArrayList<Integer> temp = new ArrayList<Integer>(); // holds the numbers found in the file name
		for (File file : imagePaths) {
			String path = file.getAbsolutePath();
			if (path.matches(".+([0-9]+)x([0-9]+).png$")) { // check file ends with number "x" number ".png"
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
		imageList = new ArrayList<ImageHandler>(imageMap.values());
		Collections.sort(imageList);
	}

	private void loadEvents() {

		// get directory and make map
		File eventDir = new File("unplayed_events" + '/');
		eventMap = new HashMap<String, EventHandler>();

		// ----------------PlayerStart----------------
		final String playerStartString = "PlayerStart";
		File playerStartFile = new File(eventDir + "/Event_playerStart.png");
		EventHandler playerStart = new EventHandler(p, this, playerStartFile) {
			@Override
			public Event makeEvent(int x, int y) {
				return new PlayerStart(game, p, texture, playerStartString, x, y);
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

		// ----------------CameraChange----------------
		final String cameraChangeString = "CameraChange";
		File cameraChangeFile = new File(eventDir + "/Event_CameraChange.png");
		EventHandler cameraChange = new EventHandler(p, this, cameraChangeFile) {
			@Override
			public Event makeEvent(int x, int y) {
				return new CameraChange(game, p, texture, cameraChangeString, x, y);
			}
		};
		eventMap.put(cameraChangeString, cameraChange);

		// ----------------Spike----------------
		final String spikeString = "Spike";
		File spikeFile = new File(eventDir + "/Event_Spikes.png");
		EventHandler spike = new EventHandler(p, this, spikeFile) {
			@Override
			public Event makeEvent(int x, int y) {
				return new Spike(game, texture, spikeString, x, y);
			}
		};
		eventMap.put(spikeString, spike);

		// make sorted list
		eventList = new ArrayList<EventHandler>(eventMap.values());
		Collections.sort(eventList);
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

	public HashMap<String, EventHandler> getEventMap() {
		return eventMap;
	}

	public ArrayList<EventHandler> getEventList() {
		return eventList;
	}

	public PImage getDeskBehind() {
		return deskBehind;
	}

	public PImage getDeskInfront() {
		return deskInfront;
	}
}
