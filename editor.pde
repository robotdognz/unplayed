//------------------Editor---------------------
class Editor {
  //touch constraint variables
  public final int TOP_DEADZONE = 200;
  public final int BOTTOM_DEADZONE = height-200;
  public boolean nextTouchInactive = false;

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

  ////widgets
  //ArrayList<Widget> eWidgets;
  //float eWidgetSpacing; //size of gap between widgets

  //toolbars
  ArrayList<Toolbar> toolbars;

  //frame count
  int frameDelay = 100;
  float frame;

  public Editor(Game game) {
    this.eGame = game;
    this.eController = new PlayerControl();
    //this.eWidgets = new ArrayList<Widget>();

    this.toolbars = new ArrayList<Toolbar>();
    toolbars.add(new EditorTop(this));
    toolbars.add(new EditorBottom(this));

    ////setup widgets
    //Widget menuW = new MenuWidget(this);
    //Widget settingsW = new SettingsWidget(this);
    //Widget playerW = new PlayerControlWidget(this);
    //Widget cameraW = new CameraControlWidget(this);
    //Widget editModeW = new EditorModeWidget(this); 
    //Widget editTypeW = new EditorTypeWidget(this); 
    //Widget extraW = new ExtraWidget(this); 
    //eWidgets.add(menuW);
    //eWidgets.add(settingsW);
    //eWidgets.add(playerW);
    //eWidgets.add(cameraW);
    //eWidgets.add(editModeW);
    //eWidgets.add(editTypeW);
    //eWidgets.add(extraW);

    //this.eWidgetSpacing = width/(this.eWidgets.size()+1);
  }

  public void step() {
    //step the controller if there are no widget menus open and touch has been reenabled
    if (eControllerActive && !nextTouchInactive) {
      eController.step(); //draw event for controls
    }

    frameCounter();

    if (!(eController instanceof EditorControl)) {
      g.point = null;
    }
  }

  //a bunch of this probably needs to be moved to step, for logical consistency only drawing should be in draw
  public void draw() {
    ////widget menus - draw them and close them if lastTouch is below longest open widget menu
    //float currentWidgetHeight = 0; //used to find the bottom of the longest open widget menu
    //boolean wMenuOpen = false; 
    //for (int i = 0; i < eWidgets.size(); i++) {
    //  if (eWidgets.get(i).isActive()) {
    //    ArrayList<Widget> children = eWidgets.get(i).getChildren();
    //    if (children.size() > 0) {
    //      wMenuOpen = true;
    //      nextTouchInactive = true; //controls won't work until the touch after widget menus are closed
    //      float current = children.get(children.size()-1).getPosition().y;
    //      if (current > currentWidgetHeight) {
    //        currentWidgetHeight = current;
    //      }
    //    }
    //  }
    //  eWidgets.get(i).draw(eWidgetSpacing*(i+1), 120);
    //  eWidgets.get(i).updateActive();
    //  if (menu == null) {
    //    eWidgets.get(i).hover(lastTouch);
    //  }
    //}
    //currentWidgetHeight += eWidgets.get(0).getSize()*1.5; //add a little padding onto the bottom
    ////if the last touch was below the longest open widget menu, close all widget menus
    //if (wMenuOpen && lastTouch.y > currentWidgetHeight || menu != null) {
    //  for (Widget w : eWidgets) {
    //    if (w.isMenu()) {
    //      w.deactivate();
    //    }
    //  }
    //}
    //eControllerActive = !wMenuOpen; //if a widget menu is open, deactivate controls

    for (Toolbar t : toolbars) {
      t.draw();
    }

    //draw frame counter and other readouts
    fill(80);
    textSize(50);
    textAlign(CENTER, CENTER);
    text(nf(convert.getScale(), 1, 2), width/2, height-150);
    text(g.scanSize + " : " + g.screenObjects.size(), width/2, height-100);
    text("FPS: " + nf(this.frame, 1, 2), width/2, height-50);
  }

  private void frameCounter() {
    //update frame rate average
    if (frameDelay > 30) {
      this.frame = frameRate;
      this.frameDelay = 0;
    } else {
      this.frameDelay++;
    }
  }

  public void touchStarted() {
    if (nextTouchInactive) {
      return;
    }
    if (eControllerActive) {
      eController.touchStarted(); //controlls for touch started event
    }
  }

  public void touchEnded() {
    if (nextTouchInactive) {
      nextTouchInactive = false;
    }
  }

  public void touchMoved() {
    if (nextTouchInactive) {
      return;
    }
    if (eControllerActive && mouseY > TOP_DEADZONE) {
      eController.touchMoved(); //controlls for touch moved event
    }
  }

  public void onPinch(float x, float y, float d) {
    if (nextTouchInactive) {
      return;
    }
    if (eControllerActive) {
      eController.onPinch(x, y, d); //controlls for on pinch event
    }
  }

  public void onTap(float x, float y) {
    ////check for clicking on widgets
    //for (int i = 0; i < eWidgets.size(); i++) {
    //  eWidgets.get(i).click();
    //}
    
    for (Toolbar t : toolbars) {
      t.onTap(x, y);
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
      } else {
        if (eMode == editorMode.ERASE && foundAtPoint != null) {
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

//------------------Toolbar---------------------
abstract class Toolbar {
  public float eWidgetSpacing; //size of gap between widgets
  
  public Editor editor;
  
  public Toolbar(Editor editor) {
    this.editor = editor;
  }

  public void step() {
  }

  public void draw() {
  }
  public void touchStarted() {
  }
  public void touchEnded() {
  }
  public void touchMoved() {
  }
  public void onPinch(float x, float y, float d) {
  }
  public void onTap(float x, float y) {
  }
}

class EditorTop extends Toolbar {
  //widgets
  public ArrayList<Widget> eWidgets;
  public float eWidgetSpacing; //size of gap between widgets
  
  public EditorTop(Editor editor){
    super(editor);
    this.eWidgets = new ArrayList<Widget>();
    //setup widgets
    
    Widget menuW = new MenuWidget(editor, this);
    Widget settingsW = new SettingsWidget(editor, this);
    Widget playerW = new PlayerControlWidget(editor, this);
    Widget cameraW = new CameraControlWidget(editor, this);
    Widget editModeW = new EditorModeWidget(editor, this); 
    Widget editTypeW = new EditorTypeWidget(editor, this); 
    Widget extraW = new ExtraWidget(editor, this); 
    eWidgets.add(menuW);
    eWidgets.add(settingsW);
    eWidgets.add(playerW);
    eWidgets.add(cameraW);
    eWidgets.add(editModeW);
    eWidgets.add(editTypeW);
    eWidgets.add(extraW);

    this.eWidgetSpacing = width/(this.eWidgets.size()+1);
  }
  
  public void draw(){
    //widget menus - draw them and close them if lastTouch is below longest open widget menu
    float currentWidgetHeight = 0; //used to find the bottom of the longest open widget menu
    boolean wMenuOpen = false; 
    for (int i = 0; i < eWidgets.size(); i++) {
      if (eWidgets.get(i).isActive()) {
        ArrayList<Widget> children = eWidgets.get(i).getChildren();
        if (children.size() > 0) {
          wMenuOpen = true;
          editor.nextTouchInactive = true; //controls won't work until the touch after widget menus are closed
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
    currentWidgetHeight += eWidgets.get(0).getSize()*1.5; //add a little padding onto the bottom
    //if the last touch was below the longest open widget menu, close all widget menus
    if (wMenuOpen && lastTouch.y > currentWidgetHeight || menu != null) {
      for (Widget w : eWidgets) {
        if (w.isMenu()) {
          w.deactivate();
        }
      }
    }
    editor.eControllerActive = !wMenuOpen; //if a widget menu is open, deactivate controls
  }
  
  public void onTap(float x, float y) {
    //check for clicking on widgets
    for (int i = 0; i < eWidgets.size(); i++) {
      eWidgets.get(i).click();
    }
  }
}

class EditorBottom extends Toolbar {
  int tHeight;

  public EditorBottom(Editor editor){
    super(editor);
    tHeight = 200;
  }

  public void draw() {
    fill(100);
    rect(0, height-tHeight, width, tHeight);
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
