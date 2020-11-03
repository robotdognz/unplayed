class CameraChange implements Event{
  private PVector position;
  private int eventW;
  private int eventH;
  
  private PVector cameraTopLeft;
  private PVector cameraBottomRight;
  
  private int newScale;
  private PVector newCent;
  private float cameraZoom;
  private float edgeZoom;
  private String type; //Strings: "Static", "Full", "Horizontal", "Vertical"

  CameraChange(int x, int y, int eventW, int eventH, PVector cameraTopLeft, PVector cameraBottomRight, float cameraZoom, float edgeZoom){ 
    //considering separating edgeZoom into in speed and out speed
    int centerX = (int)((cameraBottomRight.x-cameraTopLeft.x)/2+cameraTopLeft.x);
    int centerY = (int)((cameraTopLeft.y-cameraBottomRight.y)/2+cameraBottomRight.y);
    this.newCent = new PVector(centerX, centerY);
    this.newScale = (int)Math.abs(cameraBottomRight.x-cameraTopLeft.x);
    this.position = new PVector(x, y);
    this.eventW = eventW;
    this.eventH = eventH;
    this.cameraTopLeft = cameraTopLeft;
    this.cameraBottomRight = cameraBottomRight;
    this.cameraZoom = cameraZoom;
    this.edgeZoom = edgeZoom;
    //this.type = type;
    
  }
  
  //use constructor overloading
  //simplest one defaults to static, default zoom speed and keyhole speed
  //and so on
  //to stop this class getting bloated use a separate method to do the true construction
  //the overloaded constructors will send this method different 
  //versions of the required arguments
  
  public PVector getTopLeft(){
    return position;
  }
  public PVector getBottomRight(){
    return new PVector(position.x+eventW, position.y+eventH);
  }
  public String getType(){
    return type;
  }
  
  public void activate(Game g){
    //change center
    if(g.newCenter != newCent){
      g.newCenter = new PVector(newCent.x,newCent.y);
    }
    //change scale
    if(newScale != g.newScreen){
      g.newScreen = newScale;
    }
    g.zoomSpeed = cameraZoom;
    g.boarderZoomSpeed = edgeZoom;
    g.newTopEdge = (int)cameraTopLeft.y;
    g.newBottomEdge = (int)cameraBottomRight.y;
    g.newLeftEdge = (int)cameraTopLeft.x;
    g.newRightEdge = (int)cameraBottomRight.x;
  }
  
  public void draw(){
    fill(255, 0, 0, 100);
    noStroke();
    rect(position.x, position.y, eventW, eventH);
  }
}
