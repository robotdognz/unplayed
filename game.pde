class Game {
  public Player player;
  private Paper paper;

  public Quadtree world;
  public Rectangle startingWorld;
  public HashSet<Rectangle> playerObjects;
  public PageView pageView;
  public boolean displayPages;

  private boolean eventVis;
  private boolean quadVis;

  public Camera camera;
  public Rectangle screenSpace;
  public int screenSpaceOffset;
  public HashSet<Rectangle> screenObjects;

  public PVector point;

  //local variables for camera
  public float newScale;
  public PVector newCenter;
  public float zoomSpeed = 0.1; //0.1 is the default

  //local variables for camera tall screen space
  private float newSubScale = 1;

  //variables for black border
  private float topEdge;
  public float newTopEdge;
  private float bottomEdge;
  public float newBottomEdge;
  private float leftEdge;
  public float newLeftEdge;
  private float rightEdge;
  public float newRightEdge;
  public float boarderZoomSpeed = 0.1; //0.1 is default

  Game(Camera c, Vibe v) {
    //legacy variables from level class TODO: write these out eventually
    PVector playerStart = new PVector(0, 0);
    PVector cameraTopLeft = new PVector(-400, -400);
    PVector cameraBottomRight = new PVector(500, 600);
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    PVector startCenter = new PVector(centerX, centerY);
    int startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
    int bottomOfTopBar = (int)cameraTopLeft.y;
    int topOfBottomBar = (int)cameraBottomRight.y;

    //actual game class starts here
    camera = c;
    eventVis = true;
    quadVis = false;

    player = new Player(playerStart.x, playerStart.y, v);

    startingWorld = new Rectangle(playerStart.x-400, playerStart.y-400, 900, 900);
    world = new Quadtree(startingWorld);
    playerObjects = new HashSet<Rectangle>();
    
    pageView = new PageView(camera);
    displayPages = false;
    //testing page view
    pageView.addPage(new Page(this, new PVector(-400, -500), new PVector(500, 600),new PVector(0, 0), 1));

    paper = new Paper();

    //camera
    camera.setScale(startScale);
    newScale = startScale;
    camera.setCenter(startCenter);
    newCenter = new PVector(camera.getCenter().x, camera.getCenter().y);

    //calculate screen space
    screenSpaceOffset = 0; //positive makes it larger, negative makes it smaller
    PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
    float screenSpaceWidth = convert.screenToLevel(width+screenSpaceOffset*2);
    float screenSpaceHeight = convert.screenToLevel(height+screenSpaceOffset*2);
    screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);
    screenObjects = new HashSet<Rectangle>();

    topEdge = bottomOfTopBar;
    newTopEdge = topEdge;
    bottomEdge = topOfBottomBar;
    newBottomEdge = bottomEdge;
    leftEdge = camera.getCenter().x-newScale/2;
    newLeftEdge = leftEdge;
    rightEdge = camera.getCenter().x+newScale/2;
    newRightEdge = rightEdge;

    world.insert(new Tile(texture.getTileList().get(0).getFile(), 0, 100));  //TODO: to be replaced when there is a player start event
    //everything needs to be a multiple of 20 (multiple of 10 so you can always fall down holes, and 20 so you don't clip through things 90 apart because of speed 10)
  }

  public void restart() {
    //legacy variables from level class TODO: write these out eventually
    PVector playerStart = new PVector(0, 0);
    PVector cameraTopLeft = new PVector(-400, -400);
    PVector cameraBottomRight = new PVector(500, 600);
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    PVector startCenter = new PVector(centerX, centerY);
    int startScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
    int bottomOfTopBar = (int)cameraTopLeft.y;
    int topOfBottomBar = (int)cameraBottomRight.y;

    //actual restart code starts here
    player.resetVelocity();
    player.setPosition(playerStart);
    newScale = startScale;
    newCenter = startCenter;
    newTopEdge = bottomOfTopBar;
    newBottomEdge = topOfBottomBar;
    newLeftEdge = newCenter.x-newScale/2;
    newRightEdge = newCenter.x+newScale/2;
  }

  void draw() {
    if(displayPages){
      pageView.draw();
      return;
    }
    pushMatrix(); //start working at game scale
    translate(width/2, height/2); //set x=0 and y=0 to the middle of the screen

    //camera
    scale((float)width/(float)camera.getScale()); //width/screen fits the level scale to the screen
    scale(camera.getSubScale()); //apply offset for tall screen spaces
    translate(-camera.getCenter().x, -camera.getCenter().y); //moves the view around the level

    float currentScale = convert.getScale();

    //draw player and environment
    background(240);
    for (Rectangle p : screenObjects) { //draw pieces
      if (p instanceof Piece) {
        ((Piece) p).draw(mainGraphics, currentScale);
      }
    }
    for (Rectangle p : screenObjects) { //draw tiles and events on top of pieces
      if (p instanceof Tile) {
        ((Tile) p).draw(mainGraphics, currentScale);
      }
      if (p instanceof Event && eventVis) {
        ((Event) p).draw(mainGraphics, currentScale);
      }
    }
    player.draw(mainGraphics);
    paper.draw(mainGraphics, screenSpace, currentScale);

    //draw black bars
    if (camera.getGame()) {
      player.drawArrows(this);
      fill(20, 255); //10, 255
      int barSize = 1000000;
      rectMode(CORNERS);
      //top bar
      rect(-barSize+camera.getCenter().x, camera.getCenter().y-barSize, barSize+camera.getCenter().x, topEdge);
      //bottom bar
      rect(-barSize+camera.getCenter().x, bottomEdge, barSize+camera.getCenter().x, camera.getCenter().y+barSize);
      //left bar
      rect(-barSize+camera.getCenter().x, camera.getCenter().y-barSize, leftEdge, camera.getCenter().y+barSize);
      //right bar
      rect(rightEdge, camera.getCenter().y-barSize, barSize+camera.getCenter().x, camera.getCenter().y+barSize);
      rectMode(CORNER);
    }

    //draw quad tree logic for testing
    if (quadVis) {
      world.draw();
      fill(0, 0, 0, 150);
      for (Rectangle p : playerObjects) {
        rect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
      }
      rect(player.getPlayerArea().getX(), player.getPlayerArea().getY(), player.getPlayerArea().getWidth(), player.getPlayerArea().getHeight());
      //rect(screenSpace.getX(), screenSpace.getY(), screenSpace.getWidth(), screenSpace.getHeight());
    }

    //draw block placement selection if one exists
    if (point != null) {
      fill(0, 0, 0, 150);
      rect(point.x, point.y, 100, 100);
      fill(0);
      textSize(30);
      textAlign(LEFT, CENTER);
      int xCoord = (int) point.x;
      int yCoord = (int) point.y;
      String s = "[" + xCoord + ", " + yCoord + "]";
      text(s, point.x+105, point.y+50);
    }

    popMatrix(); //start working at screen scale
  }

  void step() {
    screenObjects.clear();
    world.retrieve(screenObjects, screenSpace);

    //find platforms near the player
    playerObjects.clear();
    world.retrieve(playerObjects, player.getPlayerArea());
    player.step(playerObjects, this);

    if (camera.getGame()) {
      screenMovement();
    }
    PVector topCorner = convert.screenToLevel(-screenSpaceOffset, -screenSpaceOffset);
    float screenSpaceWidth = convert.screenToLevel(width+screenSpaceOffset*2);
    float screenSpaceHeight = convert.screenToLevel(height+screenSpaceOffset*2);
    screenSpace = new Rectangle(topCorner.x, topCorner.y, screenSpaceWidth, screenSpaceHeight);
    
    pageView.step(); //step the page view
  }

  void screenMovement() {
    //tall screen space scaling
    //uses the 'new...' versions of edge variables so that
    //scaling happens immediately
    if (camera.getScale() != newScale || topEdge != newTopEdge || bottomEdge != newBottomEdge) {
      //if there might be a difference in tall screen scale
      if ((newBottomEdge-newTopEdge)/(newRightEdge-newLeftEdge) > (float)height/(float)width) {
        newSubScale = ((float)height/((float)width/(float)(newRightEdge-newLeftEdge)))/(newBottomEdge-newTopEdge);
      } else {
        newSubScale = 1;
      }
    }
    if (camera.getSubScale() != newSubScale) {
      camera.setSubScale(lerp(camera.getSubScale(), newSubScale, exp(-zoomSpeed)));
    }
    //main scale
    if (camera.getScale() != newScale) {
      camera.setScale(lerp(camera.getScale(), newScale, exp(-zoomSpeed)));
    }
    //translate
    if (camera.getCenter() != newCenter) {
      camera.setCenter(PVector.lerp(camera.getCenter(), newCenter, exp(-zoomSpeed)));
    }
    //black border movement
    if (leftEdge != newLeftEdge) {
      leftEdge = lerp(leftEdge, newLeftEdge, exp(-boarderZoomSpeed));
    }
    if (rightEdge != newRightEdge) {
      rightEdge = lerp(rightEdge, newRightEdge, exp(-boarderZoomSpeed));
    }
    if (topEdge != newTopEdge) {
      topEdge = lerp(topEdge, newTopEdge, exp(-boarderZoomSpeed));
    }
    if (bottomEdge != newBottomEdge) {
      bottomEdge = lerp(bottomEdge, newBottomEdge, exp(-boarderZoomSpeed));
    }
  }
}

//------------------PaperTilerClass---------------------
class Paper {
  //private PImage grid;
  int gridSize;

  public Paper() {
    //grid = texture.grid;
    gridSize = 400;
  }

  public void draw(PGraphics graphics, Rectangle screen, float scale) {
    //if(convert.getScale() > 30){ // stop drawing paper/tiles at this size
    //  return;
    //}
    //find x start position
    int startX = (int) Math.round((screen.getTopLeft().x-(gridSize/2))/gridSize)*gridSize;
    //find y start position
    int startY = (int) Math.round((screen.getTopLeft().y-(gridSize/2))/gridSize)*gridSize;
    //find x end position
    int endX = (int) Math.round((screen.getBottomRight().x+(gridSize/2))/gridSize)*gridSize;
    //find y end position
    int endY = (int) Math.round((screen.getBottomRight().y+(gridSize/2))/gridSize)*gridSize;
    //nested for loops to tile the images
    for (int y = startY; y < endY; y += gridSize) {
      for (int x = startX; x < endX; x += gridSize) {
        graphics.image(texture.getGrid(scale), x, y, gridSize, gridSize);
      }
    }
  }
}
