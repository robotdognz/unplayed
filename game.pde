class Game {
  public Player player;
  private Level level;
  public ArrayList<Platform> platforms;
  public ArrayList<Event> events;
  private boolean eventVis;

  Camera camera;
  
  public PVector point = new PVector(0,0);

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

  Game(Camera c) {
    camera = c;
    level = new Level1();
    eventVis = true;

    Vibe vibe = new Vibe();

    player = new Player((int)level.getPlayerStart().x, (int)level.getPlayerStart().y, vibe);

    //camera
    camera.setScale(level.getStartScale());
    newScale = level.getStartScale();
    camera.setCenter(level.getStartCenter());
    newCenter = new PVector(camera.getCenter().x, camera.getCenter().y);

    topEdge = level.getTopBar();
    newTopEdge = topEdge;
    bottomEdge = level.getBottomBar();
    newBottomEdge = bottomEdge;
    leftEdge = camera.getCenter().x-newScale/2;
    newLeftEdge = leftEdge;
    rightEdge = camera.getCenter().x+newScale/2;
    newRightEdge = rightEdge;

    platforms = level.getPlatforms();
    events = level.getEvents();
    //everything needs to be a multiple of 20 (multiple of 10 so you can always fall down holes, and 20 so you don't clip through things 90 apart because of speed 10)
  }

  void draw() {
    pushMatrix(); //start working at game scale
    translate(width/2, height/2); //set x=0 and y=0 to the middle of the screen

    //camera
    scale((float)width/(float)camera.getScale()); //width/screen fits the level scale to the screen
    scale(camera.getSubScale()); //apply offset for tall screen spaces
    translate(-camera.getCenter().x, -camera.getCenter().y); //moves the view around the level

    //draw player and environment
    background(200); //140
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
    if (camera.getGame()) {
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
    
    //testing - draw block placement
    if(point.x != 0 && point.y != 0){
      fill(0,0,0,150);
      rectMode(CENTER);
      rect(point.x, point.y, 100, 100);
      rectMode(CORNER);
      fill(0);
      textSize(30);
      textAlign(LEFT, CENTER);
      String s = "[ " + point.x + ", " + point.y + " ]";
      text(s, point.x+55, point.y);
    }

    popMatrix(); //start working at screen scale
  }

  void step() {
    player.step(platforms, events, this);
    if (camera.getGame()) {
      screenMovement();
    }
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
