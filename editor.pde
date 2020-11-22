//------------------Editor---------------------
class Editor {
  public final int TOP_DEADZONE = 200;
  
  Game eGame; //reference to game, same instance of game used everywhere else

  //camera variables
  float minZoom = 3;
  float maxZoom = 100;

  //controller
  Controller eController = new CameraControl(this); //holds the current controller
  boolean eControllerActive = true; //is the current controller active

  //editor settings
  boolean snap = true; //things placed in the level will snap to grid
  editorType eType = editorType.BLOCK;
  editorMode eMode = editorMode.ADD;
  imagePlane eImagePlane = imagePlane.LEVEL;

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
    Widget playerW = new PlayerControlWidget(this);
    Widget cameraW = new CameraControlWidget(this);
    Widget editModeW = new EditorModeWidget(this); 
    Widget editTypeW = new EditorTypeWidget(this); 
    Widget extraW = new ExtraWidget(this); 
    eWidgets.add(menuW);
    eWidgets.add(settingsW);
    eWidgets.add(playerW);
    eWidgets.add(cameraW);
    eWidgets.add(editModeW);
    eWidgets.add(editTypeW);
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
    fill(80);
    textSize(50);
    textAlign(CENTER, CENTER);
    text(nf(convert.getScale(), 1, 2), width/2, height-150);
    text(g.scanSize + " : " + g.screenObjects.size(), width/2, height-100);
    text("FPS: " + nf(this.frame, 1, 2), width/2, height-50);
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
  
  public void onLongPress(float x, float y) {
    if (eControllerActive) {
      eController.onLongPress(x, y);
    }
    for (int i = 0; i < eWidgets.size(); i++) {
      eWidgets.get(i).longPress();
    }
  }

  public void placeBlock() {
    if (g.point != null) {

      int platformX = (int) g.point.x;
      int platformY = (int) g.point.y;

      boolean spaceFree = true;
      Rectangle foundAtPoint = null;
      
      HashSet<Rectangle> getPlatforms = new HashSet<Rectangle>();
      for (Rectangle p : g.world.retrieve(getPlatforms, new Rectangle(platformX, platformY, 100, 100))) {
        
        if (p.getTopLeft().x == platformX && p.getTopLeft().y == platformY) {
          spaceFree = false;
          foundAtPoint = p;
        }
      }

      if (spaceFree) { //if there isn't something already there
        if (eMode == editorMode.ADD) {
          Platform newPlatform = new Platform(platformX, platformY, 100, 100);
          g.world.insert(newPlatform);
        }
      }else{
        if(eMode == editorMode.ERASE && foundAtPoint != null){
          g.world.remove(foundAtPoint);
        }
      }
      g.point = null;
    }
  }

  public Controller getController() {
    return eController;
  }
}

//------------------EditorSettingsEnums---------------------
enum editorType {
  BLOCK, 
    IMAGE, 
    EVENT
}

enum editorMode {
  ADD, 
    ERASE, 
    SELECT
}

enum imagePlane {
  BACK,
  LEVEL,
  FRONT
}
