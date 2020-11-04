class Game {
  public Player player;
  private Level level;
  public ArrayList<Platform> platforms;
  public ArrayList<Event> events;
  private boolean eventVis;

  //variables for camera
  private float screen;
  public float newScreen;
  private PVector center;
  public PVector newCenter;
  public float zoomSpeed = 0.1; //0.1 is the default

  //variables for camera tall screen space
  private float subScale = 1; //1 is the default
  private float newSubScale = subScale;

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

  Game() {
    level = new Level1();
    eventVis = true;
    
    Vibe vibe = new Vibe();

    player = new Player((int)level.getPlayerStart().x, (int)level.getPlayerStart().y, vibe);
    screen = level.getStartScale();
    newScreen = level.getStartScale();
    center = level.getStartCenter();
    newCenter = new PVector(center.x, center.y);

    topEdge = level.getTopBar();
    newTopEdge = topEdge;
    bottomEdge = level.getBottomBar();
    newBottomEdge = bottomEdge;
    leftEdge = center.x-newScreen/2;
    newLeftEdge = leftEdge;
    rightEdge = center.x+newScreen/2;
    newRightEdge = rightEdge;

    platforms = level.getPlatforms();
    events = level.getEvents();
    //everything needs to be a multiple of 20 (multiple of 10 so you can always fall down holes, and 20 so you don't clip through things 90 apart because of speed 10)
  }

  void draw() {
    pushMatrix(); //start working at game scale
    translate(width/2, height/2); //set x=0 and y=0 to the middle of the screen
    scale((float)width/(float)screen); //width/screen fits the level scale to the screen
    scale(subScale); //apply offset for tall screen spaces
    translate(-center.x, -center.y); //moves the view around the level

    background(140); //100

    //draw player and environment
    for (Platform p : platforms) {
      p.draw();
    }
    if (eventVis) {
      for (Event e : events) {
        e.draw();
      }
    }
    player.draw(this);

    //draw black bars
    fill(20, 255); //10, 255
    int barSize = 1000000;
    rectMode(CORNERS);
    //top bar
    rect(-barSize+center.x, center.y-barSize, barSize+center.x, topEdge);
    //bottom bar
    rect(-barSize+center.x, bottomEdge, barSize+center.x, center.y+barSize);
    //left bar
    rect(-barSize+center.x, center.y-barSize, leftEdge, center.y+barSize);
    //right bar
    rect(rightEdge, center.y-barSize, barSize+center.x, center.y+barSize);
    rectMode(CORNER);

    popMatrix(); //start working at screen scale
  }
  
  void step(){
    player.step(platforms, events, this);
    screenMovement();
  }

  void screenMovement() {
    //tall screen space scaling
    //uses the new... versions of edge variables so that
    //scaling happens immediately
    if (screen != newScreen || topEdge != newTopEdge || bottomEdge != newBottomEdge) {
      //if there might be a difference in tall screen scale
      if ((newBottomEdge-newTopEdge)/(newRightEdge-newLeftEdge) > (float)height/(float)width) {
        newSubScale = ((float)height/((float)width/(float)(newRightEdge-newLeftEdge)))/(newBottomEdge-newTopEdge);
      } else {
        newSubScale = 1;
      }
    }
    if (subScale != newSubScale) {
      subScale = lerp(subScale, newSubScale, exp(-zoomSpeed));
    }
    //main scale
    if (screen != newScreen) {
      screen = lerp(screen, newScreen, exp(-zoomSpeed));
    }
    //translate
    if (center != newCenter) {
      center = PVector.lerp(center, newCenter, exp(-zoomSpeed));
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
