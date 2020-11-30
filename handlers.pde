//------------------TextureStore---------------------
class TextureCache {
  //paper textures
  public PImage grid;

  //level pieces
  private File pieceDir;
  private File[] piecePaths;
  private HashMap<File, PieceHandler> pieceMap;
  private ArrayList<PieceHandler> pieceList;
  //tiles
  private File tileDir;
  private File[] tilePaths;
  private HashMap<File, TileHandler> tileMap;
  private ArrayList<TileHandler> tileList;
  //events
  private File eventDir;
  //private File[] eventPaths;
  private HashMap<File, EventHandler> eventMap;
  private ArrayList<EventHandler> eventList;

  //blocks
  public PImage defaultBlock;

  public TextureCache() {
    //sprite = requestImage("image.png") // this loads the image on a sperate thread
    //you can check if it has loaded by querrying its dimentions, they will be 0 if loading, -1 if failed to load
    //and > 0 if it has loaded 

    //paper textures
    grid = loadImage("PaperGrid_512x512.png");

    //level assets
    loadLevelPieces();
    loadTiles();
    loadEvents();

    //blocks
    defaultBlock = loadImage("player_main.png");
  }

  private void loadTiles() {
    //tiles
    tileDir = new File(dataPath("tiles")+'/');
    tilePaths = tileDir.listFiles();
    tileMap = new HashMap<File, TileHandler>();
    for (File file : tilePaths) {
      String path = file.getAbsolutePath();
      if (path.matches(".+.png$")) { //only checks for .png
        tileMap.put(file, new TileHandler(file));
      }
    }
    tileList = new ArrayList<TileHandler>(tileMap.values());
    Collections.sort(tileList);
  }

  private void loadLevelPieces() {
    //level pieces
    pieceDir = new File(dataPath("pieces")+'/');
    piecePaths = pieceDir.listFiles();
    pieceMap = new HashMap<File, PieceHandler>();
    ArrayList<Integer> temp = new ArrayList<Integer>(); //holds the numbers found in the file name
    for (File file : piecePaths) {
      String path = file.getAbsolutePath();
      if (path.matches(".+([0-9]+)x([0-9]+).png$")) { //check file ends with number "x" number ".png"
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(path);
        while (m.find()) {
          int i = Integer.parseInt(m.group());  
          temp.add(i);
        }
        if (temp.size() >= 2) {
          pieceMap.put(file, new PieceHandler(file, temp.get(temp.size()-2), temp.get(temp.size()-1)));
        }
      }
      temp.clear();
    }
    pieceList = new ArrayList<PieceHandler>(pieceMap.values());
    Collections.sort(pieceList);
  }

  private void loadEvents() {
    //get directory and make map
    eventDir = new File(dataPath("events")+'/');
    eventMap = new HashMap<File, EventHandler>();

    //player death
    File playerDeathFile = new File(eventDir+"/spikes.png");
    EventHandler playerDeath = new EventHandler(playerDeathFile) {
      public Event makeEvent(int x, int y) {//, int eventW, int eventH, PVector cameraTopLeft, PVector cameraBottomRight, float cameraZoom, float edgeZoom) {
        return new PlayerDeath(datapath, x, y);
      }
    };
    eventMap.put(playerDeathFile, playerDeath);

    //camera change
    File cameraChangeFile = new File(eventDir+"/cameraChange.png");
    EventHandler cameraChange = new EventHandler(cameraChangeFile) {
      public Event makeEvent(int x, int y) {//, int eventW, int eventH, PVector cameraTopLeft, PVector cameraBottomRight, float cameraZoom, float edgeZoom) {
        return new CameraChange(datapath, x, y, 100, 100, new PVector(-700, -200), new PVector(700, 1500), 2, 2);
      }
    };
    eventMap.put(cameraChangeFile, cameraChange);

    //make sorted list
    eventList = new ArrayList<EventHandler>(eventMap.values());
    Collections.sort(eventList);
  }

  public HashMap<File, TileHandler> getTileMap() {
    return tileMap;
  }

  public ArrayList<TileHandler> getTileList() {
    return tileList;
  }

  public HashMap<File, PieceHandler> getPieceMap() {
    return pieceMap;
  }

  public ArrayList<PieceHandler> getPieceList() {
    return pieceList;
  }

  public HashMap<File, EventHandler> getEventMap() {
    return eventMap;
  }

  public ArrayList<EventHandler> getEventList() {
    return eventList;
  }
}

interface Handler {
  public PImage getSprite(float scale);
  public File getFile();
  public int getWidth();
  public int getHeight();
  public void draw(float pX, float pY, float size);
}

//------------------TileHandler---------------------
class TileHandler implements Comparable<TileHandler>, Handler {
  File datapath;
  PImage LOD0; //256
  PImage LOD1; //128
  PImage LOD2; //64
  PImage LOD3; //32

  public TileHandler(File file) {
    datapath = file;
    String path = file.getAbsolutePath();

    try {
      LOD0 = loadImage(path);  //256
      LOD0.resize(256, 256);

      LOD1 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //128
      LOD1.resize(128, 128);

      LOD2 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //64
      LOD2.resize(64, 64);

      LOD3 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //32
      LOD3.resize(32, 32);
    }
    catch(Exception e) {
      //set sprite to file not found image
    }
  }

  @Override
    public int compareTo(TileHandler otherTileHandler) {
    String otherName = otherTileHandler.getFile().toString();
    String name = datapath.toString();
    return otherName.compareTo(name);
  }

  public PImage getSprite(float scale) {
    if (scale < 5) {
      return LOD0;
    } else {
      return LOD3;
    }
    //should return the relevant lod
    //potentially should only load the image in when first called
  }

  public File getFile() {
    return datapath;
  }

  public void draw(float pX, float pY, float size) {
    //draw the scaled image
    image(LOD3, pX, pY, size, size);
  }

  public int getWidth() {
    return 100;
  }

  public int getHeight() {
    return 100;
  }
}

//------------------PieceHandler---------------------
class PieceHandler implements Comparable<PieceHandler>, Handler {
  File datapath;
  PImage LOD0; //256
  PImage LOD1; //128
  PImage LOD2; //64
  PImage LOD3; //32
  int pWidth;
  int pHeight;

  public PieceHandler(File file, int pWidth, int pHeight) {
    datapath = file;
    this.pWidth = pWidth*100;  //these are turned from grid amound to draw units for the level
    this.pHeight = pHeight*100;
    String path = file.getAbsolutePath();

    try {
      LOD0 = loadImage(path);  //256
      LOD0.resize(256, 256);

      LOD1 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //128
      LOD1.resize(128, 128);

      LOD2 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //64
      LOD2.resize(64, 64);

      LOD3 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //32
      LOD3.resize(32, 32);
    }
    catch(Exception e) {
      //set sprite to file not found image
    }
  }

  public int getWidth() {
    return pWidth;
  }

  public int getHeight() {
    return pHeight;
  }

  public PImage getSprite(float scale) {
    if (scale < 5) {
      return LOD0;
    } else {
      return LOD3;
    }
  }

  public File getFile() {
    return datapath;
  }

  public String toString() {
    return "[" + pWidth + ", " + pHeight + "]";
  }

  public void draw(float pX, float pY, float size) {
    //calculate how to scale the image so it appears in the scroll bar correctly
    float scaleFactor;
    if (getWidth() >= getHeight()) {
      scaleFactor = size/getWidth();
    } else {
      scaleFactor = size/getHeight();
    }
    //draw the scaled image
    image(LOD3, pX, pY, pWidth*scaleFactor, pHeight*scaleFactor);
  }

  @Override
    public int compareTo(PieceHandler otherPieceHandler) {
    float otherArea = otherPieceHandler.getWidth()*otherPieceHandler.getHeight();
    float area = getWidth()*getHeight();
    if (otherArea > area) {
      return -1;
    } else if (otherArea < area) {
      return 1;
    } else {
      return 0;
    }
  }
}

//------------------EventHandler---------------------
class EventHandler implements Comparable<EventHandler>, Handler {
  File datapath;
  PImage LOD0; //256
  PImage LOD1; //128
  PImage LOD2; //64
  PImage LOD3; //32
  int pWidth;
  int pHeight;

  public EventHandler(File file) {
    datapath = file;
    this.pWidth = 100;  //these are turned from grid amound to draw units for the level
    this.pHeight = 100;
    String path = file.getAbsolutePath();

    try {
      LOD0 = loadImage(path);  //256
      LOD0.resize(256, 256);

      LOD1 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //128
      LOD1.resize(128, 128);

      LOD2 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //64
      LOD2.resize(64, 64);

      LOD3 = LOD0.get(0, 0, LOD0.width, LOD0.height);  //32
      LOD3.resize(32, 32);
    }
    catch(Exception e) {
      //set sprite to file not found image
    }
  }

  public Event makeEvent(int x, int y) {//, int eventW, int eventH, PVector cameraTopLeft, PVector cameraBottomRight, float cameraZoom, float edgeZoom) {
    return null;
  }

  public int getWidth() {
    return pWidth;
  }

  public int getHeight() {
    return pHeight;
  }

  public PImage getSprite(float scale) {
    if (scale < 5) {
      return LOD0;
    } else {
      return LOD3;
    }
  }

  public File getFile() {
    return datapath;
  }

  public void draw(float pX, float pY, float size) {
    //calculate how to scale the image so it appears in the scroll bar correctly

    if (LOD3 != null) {
      float scaleFactor;
      if (getWidth() >= getHeight()) {
        scaleFactor = size/getWidth();
      } else {
        scaleFactor = size/getHeight();
      }
      //draw the scaled image
      image(LOD3, pX, pY, pWidth*scaleFactor, pHeight*scaleFactor);
    } else {
      showToast("Failed to load: " + datapath);
    }
  }

  @Override
    public int compareTo(EventHandler otherEventHandler) {
    String otherName = otherEventHandler.getFile().toString();
    String name = datapath.toString();
    return otherName.compareTo(name);
  }
}
