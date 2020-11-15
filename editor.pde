//------------------Editor---------------------
class Editor {
  Game eGame; //reference to game, same instance of game used everywhere else

  //camera variables
  float minZoom = 200;
  float maxZoom = 20000;

  //controller
  Controller eController = new CameraControl(this); //holds the current controller
  boolean eControllerActive = true; //is the current controller active

  //editor settings
  boolean snap = true; //things placed in the level will snap to grid

  //widgets
  ArrayList<Widget> eWidgets;
  float eWidgetSpacing; //size of gap between widgets

  //frame count
  int frameDelay = 100;
  float frame;

  public Editor(Game game) {
    this.eGame = game;
    this.eController = new PlayerControl();
    this.eWidgets = new ArrayList<Widget>();

    //setup widgets
    Widget menuW = new MenuWidget(this);
    Widget settingsW = new SettingsWidget(this);
    Widget controlW = new ControlWidget(this);
    Widget editTypeW = new EditorTypeWidget(this); 
    Widget editModeW = new EditorModeWidget(this); 
    Widget extraW = new ExtraWidget(this); 
    eWidgets.add(menuW);
    eWidgets.add(settingsW);
    eWidgets.add(controlW);
    eWidgets.add(editTypeW);
    eWidgets.add(editModeW);
    eWidgets.add(extraW);
    
    this.eWidgetSpacing = width/(this.eWidgets.size()+1);
  }

  public void step() {
    //step the controller if there are no widget menus open
    if (eControllerActive) {
      eController.draw(); //draw event for controls
    }
  }

  public void draw() {
    //widget menus - draw them and close them is lastTouch is below longest open widget menu
    float currentWidgetHeight = 0;  
    boolean wMenuOpen = false; 
    for (int i = 0; i < eWidgets.size(); i++) {
      if (eWidgets.get(i).isActive()) {
        ArrayList<Widget> children = eWidgets.get(i).getChildren();
        if (children.size() > 0) {
          wMenuOpen = true;
          float current = children.get(children.size()-1).getPosition().y;
          if (current > currentWidgetHeight) {
            currentWidgetHeight = current;
          }
        }
      }
      eWidgets.get(i).draw(eWidgetSpacing*(i+1), 120);
      eWidgets.get(i).updateActive();
      if (menu == null) {
        eWidgets.get(i).hover(lastTouch);
      }
    }
    currentWidgetHeight += eWidgets.get(0).getSize()*1.5; //add a little on to the bottom
    if (wMenuOpen && lastTouch.y > currentWidgetHeight || menu != null) {
      for (Widget w : eWidgets) {
        if (w.isMenu()) {
          w.deactivate();
        }
      }
    }
    eControllerActive = !wMenuOpen; //is a menu is open, deactivate controls

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
    if (eControllerActive) {
      eController.touchStarted(); //controlls for touch started event
    }
  }

  public void touchEnded() {
    //check for clicking on widgets
    for (int i = 0; i < eWidgets.size(); i++) {
      eWidgets.get(i).click();
    }
  }

  public void touchMoved() {
    if (eControllerActive) {
      eController.touchMoved(); //controlls for touch moved event
    }
  }

  public void onPinch(float x, float y, float d) {
    if (eControllerActive) {
      eController.onPinch(x, y, d); //controlls for on pinch event
    }
  }

  public Controller getController() {
    return eController;
  }
}
