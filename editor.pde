//------------------Editor---------------------
class Editor {
  //camera
  Camera eCamera = new FreeCamera(); 
  float minZoom = 200;
  float maxZoom = 20000;

  //controller
  Controller eController = new CameraControl(); //holds the current controller
  boolean controllerActive = true; //is the current controller active
  
  //editor settings
  boolean snap = true; //things placed in the level will snap to grid

  //widgets
  ArrayList<Widget> widgets;
  float widgetSpacing; //size of gap between widgets

  //frame count
  int frameDelay = 100;
  float frame;

  public Editor() {
    this.eCamera = new GameCamera();
    this.eController = new PlayerControl();

    this.widgets = new ArrayList<Widget>();
    this.widgetSpacing = width/(this.widgets.size()+1);
  }
  
  public void step(){
    //step the controller is there are no widget menus open
    if(controllerActive){
      eController.draw(); //draw event for controls
    }
  }

  public void draw() {
    //draw the widgets and check if eController should be active
    boolean widgetMenuOpen = false;
    for (int i = 0; i < this.widgets.size(); i++) {
      this.widgets.get(i).draw(this.widgetSpacing*(i+1), 120);
      this.widgets.get(i).updateActive();
      this.widgets.get(i).hover(lastTouch);
      if(this.widgets.get(i).isMenu() && this.widgets.get(i).isActive()){
        widgetMenuOpen = true;
      }
    }
    controllerActive = !widgetMenuOpen;

    //draw frame counter
    if (frameDelay > 30) {
      this.frame = frameRate;
      this.frameDelay = 0;
    } else {
      this.frameDelay++;
    }
    fill(255);
    textSize(50);
    textAlign(CENTER, CENTER);
    text(nf(this.frame, 2, 2), width/2, height-50);
  }

  public void touchStarted() {
  }

  public void touchEnded() {
  }

  public void touchMoved() {
  }

  public void onPinch(float x, float y, float d) {
  }
  
  public Camera getCamera() {
    return eCamera;
  }
  public Controller getController() {
    return eController;
  }
}
