abstract class Event extends Rectangle {
  private boolean hasTexture;
  private EventHandler eventTexture;

  public Event(String name, float x, float y, float rWidth, float rHeight) {
    super(x, y, rWidth, rHeight);

    if (name != null && texture.getEventMap().containsKey(name)) {
      this.eventTexture = texture.getEventMap().get(name);
      //setWidth(eventTexture.getWidth());
      //setHeight(eventTexture.getHeight());
      hasTexture = true;
    } else {
      hasTexture = false;
    }
  }
  
  public String getType() {
    return "";
  }

  public void activate(Game g) {
    //showToast(getX() + " " + getY() + " " + getWidth() + " " + getHeight());
  }

  public void draw(float scale) {
    if (hasTexture) {
      image(eventTexture.getSprite(scale), getX(), getY(), getWidth(), getHeight());
    } else {
      //display missing texture texture
    }
  }
}

//------------------PlayerDeath---------------------
class PlayerDeath extends Event {
  PlayerDeath (String name, int x, int y) {
    super(name, x, y, 100, 100);
  }

  public void activate(Game g) {
    super.activate(g);
    g.restart(); //TODO: this needs a custom method in game
  }
}

//------------------CameraChange---------------------
class CameraChange extends Event {
  //private PImage sprite;

  private PVector cameraTopLeft;
  private PVector cameraBottomRight;

  private int newScale;
  private PVector newCent;
  private float cameraZoom;
  private float edgeZoom;
  //private String type; //Strings: "Static", "Full", "Horizontal", "Vertical"

  CameraChange(String name, int x, int y, int eventW, int eventH, PVector cameraTopLeft, PVector cameraBottomRight, float cameraZoom, float edgeZoom) { 
    super(name, x, y, eventW, eventH);
    //considering separating edgeZoom into in speed and out speed
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.newCent = new PVector(centerX, centerY);
    this.newScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
    this.cameraTopLeft = cameraTopLeft;
    this.cameraBottomRight = cameraBottomRight;
    this.cameraZoom = cameraZoom;
    this.edgeZoom = edgeZoom;
    //this.type = type;
    //this.sprite = loadImage("cameraChange.png");
  }

  //use constructor overloading
  //simplest one defaults to static, default zoom speed and keyhole speed
  //and so on
  //to stop this class getting bloated use a separate method to do the true construction
  //the overloaded constructors will send this method different 
  //versions of the required arguments

  //public String getType() {
  //  return type;
  //}

  public void activate(Game g) {
    super.activate(g);
    //these values should continue to be stored in game, they get pushed to the camera by game on the next step
    //that way camera changes will take effect when moving back to game camera from editor camera

    //change center
    if (g.newCenter != this.newCent) {
      g.newCenter = new PVector(newCent.x, newCent.y);
    }
    //change scale
    if (g.newScale != this.newScale) {
      g.newScale = this.newScale;
    }
    g.zoomSpeed = cameraZoom;
    g.boarderZoomSpeed = edgeZoom;
    g.newTopEdge = (int)cameraTopLeft.y;
    g.newBottomEdge = (int)cameraBottomRight.y;
    g.newLeftEdge = (int)cameraTopLeft.x;
    g.newRightEdge = (int)cameraBottomRight.x;
  }

  //public void draw() {
  //  if (getWidth() != 100 || getHeight() != 100) {
  //    fill(255, 0, 0, 100);
  //    noStroke();
  //    rect(getX(), getY(), getWidth(), getHeight());
  //  } else {
  //    image(sprite, getX(), getY(), 100, 100);
  //  }
  //}
}
