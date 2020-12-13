package handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.Game;
import objects.Event;
import objects.events.CameraChange;
import objects.events.PlayerDeath;
import objects.events.PlayerStart;
import processing.core.*;
//import processing.data.*;

public class TextureCache {
	PApplet p;
	Game game;

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

	// level image
	private File imageDir;
	private File[] imagePaths;
	private HashMap<File, ImageHandler> imageMap;
	private ArrayList<ImageHandler> imageList;
	// tiles
	private File tileDir;
	private File[] tilePaths;
	private HashMap<File, TileHandler> tileMap;
	private ArrayList<TileHandler> tileList;
	// events
	private File eventDir;
	// private File[] eventPaths;
	private HashMap<String, EventHandler> eventMap;
	private ArrayList<EventHandler> eventList;

	// blocks
	public PImage defaultBlock;

	public TextureCache(PApplet p) {
		this.p = p;
		// sprite = requestImage("image.png") // this loads the image on a sperate
		// thread
		// you can check if it has loaded by querrying its dimentions, they will be 0 if
		// loading, -1 if failed to load
		// and > 0 if it has loaded

		// paper textures
		gridLOD256 = p.requestImage("PaperGrid_1024x1024.png");
		gridLOD128 = p.requestImage("PaperGrid_512x512.png");
		gridLOD64 = p.requestImage("PaperGrid_256x256.png");
		gridLOD32 = p.requestImage("PaperGrid_128x128.png");
		gridLOD16 = p.requestImage("PaperGrid_64x64.png");
		gridLOD8 = p.requestImage("PaperGrid_32x32.png");

		deskBehind = p.requestImage("PagesViewBackGround.png");
		deskInfront = p.requestImage("PagesViewBackGround_shading.png");

		// level assets
		loadLevelImages();
		loadTiles();
		loadEvents();

		// player TODO: get rid of this
		defaultBlock = p.requestImage("player_main.png");
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
		// tiles
		tileDir = new File(p.dataPath("tiles") + '/');
		tilePaths = tileDir.listFiles();
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
		// level images
		imageDir = new File(p.dataPath("images") + '/');
		imagePaths = imageDir.listFiles();
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
		@SuppressWarnings("unused")
		TextureCache texture = this; // so that this can be passed to the modified event handlers below

		// get directory and make map
		eventDir = new File(p.dataPath("events") + '/');
		eventMap = new HashMap<String, EventHandler>();

		// ----------------player start
		String start = "PlayerStart";
		File playerStartFile = new File(eventDir + "/spikes.png");

		EventHandler playerStart = new EventHandler(p, this, playerStartFile) {
			public Event makeEvent(int x, int y) {
				String start = "PlayerStart";
				return new PlayerStart(texture, start, x, y, game);
			}
		};
		eventMap.put(start, playerStart);

		// ----------------player death
		String spikes = "PlayerDeath";
		File playerDeathFile = new File(eventDir + "/spikes.png");

		EventHandler playerDeath = new EventHandler(p, this, playerDeathFile) {
			public Event makeEvent(int x, int y) {
				String spikes = "PlayerDeath";
				return new PlayerDeath(texture, spikes, x, y);
			}
		};
		eventMap.put(spikes, playerDeath);

		// ----------------camera change
		String camera = "CameraChange";
		File cameraChangeFile = new File(eventDir + "/cameraChange.png");
		EventHandler cameraChange = new EventHandler(p, this, cameraChangeFile) {
			public Event makeEvent(int x, int y) {
				String camera = "CameraChange";
				return new CameraChange(texture, camera, x, y, 100, 100, new PVector(-700, -200),
						new PVector(700, 1500), 2, 2);
			}
		};
		eventMap.put(camera, cameraChange);

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
