//------------------Editor---------------------
class Editor {
  //touch constraint variables
  public final int TOP_DEADZONE = 200;
  public final int BOTTOM_DEADZONE = height-300;
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

  //current object to put into level
  PieceHandler currentPiece = null;
  Platform currentBlock = null; //TODO: should use BlockHandler when that is implemented

  //toolbars
  Toolbar editorTop;
  Toolbar editorBottom;

  //save/load
  EditorJSON eJSON;

  //frame count
  int frameDelay = 100;
  float frame;

  public Editor(Game game) {
    this.eGame = game;
    this.eController = new PlayerControl();
    this.editorTop = new EditorTop(this);
    this.editorBottom = new EditorBottom(this);
    this.eJSON = new EditorJSON();
  }

  public void step() {
    editorTop.step();
    editorBottom.step();

    //step the controller if there are no widget menus open and touch has been reenabled
    if (eControllerActive && !nextTouchInactive && mouseY > TOP_DEADZONE && mouseY < BOTTOM_DEADZONE) {
      eController.step(); //draw event for controls
    }

    frameCounter();

    if (!(eController instanceof EditorControl)) {
      g.point = null;
    }

    //figure out what is being placed
  }

  //a bunch of this probably needs to be moved to step, for logical consistency only drawing should be in draw
  public void draw() {
    //draw toolbars
    editorTop.draw();
    editorBottom.draw();

    //draw frame counter and other readouts
    fill(80);
    textSize(50);
    textAlign(CENTER, CENTER);
    text(nf(convert.getScale(), 1, 2), width/2, height-editorBottom.getHeight()-150);
    text(g.scanSize + " : " + g.screenObjects.size(), width/2, height-editorBottom.getHeight()-100);
    text("FPS: " + nf(this.frame, 1, 2), width/2, height-editorBottom.getHeight()-50);
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
    if (eControllerActive && mouseY > TOP_DEADZONE && mouseY < BOTTOM_DEADZONE) {
      eController.touchStarted(); //controlls for touch started event
    }
  }

  public void touchEnded() {
    editorTop.touchEnded();
    editorBottom.touchEnded();

    if (nextTouchInactive) {
      nextTouchInactive = false;
    }
  }

  public void touchMoved() {
    editorTop.touchMoved();
    editorBottom.touchMoved();

    if (nextTouchInactive) { //don't do controller if next touch inactive
      return;
    }
    if (eControllerActive && mouseY > TOP_DEADZONE && mouseY < BOTTOM_DEADZONE) {
      eController.touchMoved(); //controlls for touch moved event
    }
  }

  public void onPinch(float x, float y, float d) {
    if (nextTouchInactive) {
      return;
    }
    if (eControllerActive && mouseY > TOP_DEADZONE && mouseY < BOTTOM_DEADZONE) {
      eController.onPinch(x, y, d); //controlls for on pinch event
    }
  }

  public void onTap(float x, float y) {
    editorBottom.onTap(x, y);
  }

  public void placeBlock() {  //place piece / place event
    if (g.point != null) {

      int platformX = (int) g.point.x;
      int platformY = (int) g.point.y;

      boolean spaceFree = true;
      Rectangle foundAtPoint = null;

      //create the new piece to put in
      Rectangle toInsert = null;
      if (eType == editorType.BLOCK) {
        toInsert = new Platform(platformX, platformY, 100, 100);
      } else if (eType == editorType.IMAGE && currentPiece != null) {
        toInsert = new Piece(currentPiece.getFile(), platformX, platformY, currentPiece.getWidth(), currentPiece.getHeight());
      }

      //insert it or remove
      if (toInsert != null) {
        HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
        g.world.retrieve(getRectangles, toInsert);
        for (Rectangle p : getRectangles) {

          if (p.getTopLeft().x == platformX && 
          p.getTopLeft().y == platformY &&
          toInsert.getClass().equals(p.getClass())) {
            spaceFree = false;
            foundAtPoint = p;
          }
        }

        if (spaceFree) { //if there isn't something already there
          if (eMode == editorMode.ADD) {
            //Platform newPlatform = new Platform(platformX, platformY, 100, 100);
            g.world.insert(toInsert);
          }
        } else {
          if (eMode == editorMode.ERASE && foundAtPoint != null) {
            g.world.remove(foundAtPoint);
          }
        }
        g.point = null;
      }
    }
  }

  public Controller getController() {
    return eController;
  }
}

//------------------Toolbar---------------------
abstract class Toolbar {
  public ArrayList<Widget> eWidgets;
  public float eWidgetSpacing; //size of gap between widgets
  public float eWidgetOffset; //amount to offset widget drawing by

  public Editor editor;

  public Toolbar(Editor editor) {
    eWidgetSpacing = 0;
    eWidgetOffset = 0;
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
  public void onTap(float x, float y) {
  }
  public void onPinch(float x, float y, float d) {
  }

  public float getHeight() {
    return 0;
  }
}

class EditorTop extends Toolbar {

  public EditorTop(Editor editor) {
    super(editor);

    //setup widgets
    this.eWidgets = new ArrayList<Widget>();
    Widget menuW = new MenuWidget(editor, this);
    Widget settingsW = new SettingsWidget(editor, this);
    Widget playerW = new PlayerControlWidget(editor, this);
    Widget cameraW = new CameraControlWidget(editor, this);
    Widget editModeW = new EditorModeWidget(editor, this); 
    //Widget editTypeW = new EditorTypeWidget(editor, this); 
    Widget extraW = new ExtraWidget(editor, this); 
    eWidgets.add(menuW);
    eWidgets.add(settingsW);
    eWidgets.add(playerW);
    eWidgets.add(cameraW);
    eWidgets.add(editModeW);
    //eWidgets.add(editTypeW);
    eWidgets.add(extraW);

    //this.eWidgetSpacing = width/(this.eWidgets.size()+1);
    this.eWidgetSpacing = width/8;
    this.eWidgetOffset = (width-eWidgetSpacing*5)/2;
  }

  public void draw() {
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
      //eWidgets.get(i).draw(eWidgetSpacing*(i+1), 120);
      eWidgets.get(i).draw(eWidgetOffset+eWidgetSpacing*i, 120);
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

  public void touchEnded() {
    //check for clicking on widgets
    for (int i = 0; i < eWidgets.size(); i++) {
      eWidgets.get(i).click();
    }
  }
}

class EditorBottom extends Toolbar {
  private Rectangle pieceArea;
  private String folder = dataPath("ui") + '/';
  private PImage toolbar;
  private float widgetHeight;

  //scroll bars
  private int size; //used for drawing all types
  private ArrayList<PieceHandler> pieces; //pieces
  private float pieceOffset;
  //blocks
  //blockOffset
  //events
  //eventOffset

  public EditorBottom(Editor editor) {
    super(editor);

    //setup widgets
    this.eWidgets = new ArrayList<Widget>();
    Widget blockW = new BlockModeWidget(editor, this);
    Widget imageW = new ImageModeWidget(editor, this);
    Widget eventW = new EventModeWidget(editor, this);
    eWidgets.add(blockW);
    eWidgets.add(imageW);
    eWidgets.add(eventW);

    eWidgetOffset = width*0.7;
    eWidgetSpacing = 140;    //TODO: change this

    //setup toolbar
    int pieceAreaHeight = 200;
    pieceArea = new Rectangle(0, height-pieceAreaHeight, width, pieceAreaHeight);
    toolbar = loadImage(folder+"icn_toolbar_bg.png");

    widgetHeight = pieceArea.getY()-54; //TODO: get the height right

    //scroll bars
    size = 150;
    pieces = texture.getPieceList();
  }

  public void draw() {
    for (int i = 0; i < eWidgets.size(); i++) {
      //draw the three nehind tabs
      //eWidgetOffset+eWidgetSpacing*i, widgetHeight
    }

    image(toolbar, pieceArea.getX(), pieceArea.getY(), pieceArea.getWidth(), pieceArea.getHeight());

    //widgets
    for (int i = 0; i < eWidgets.size(); i++) {
      //if current widget is active, draw tab at the current x position

      eWidgets.get(i).draw(eWidgetOffset+eWidgetSpacing*i, widgetHeight);  
      eWidgets.get(i).updateActive();


      if (menu == null) {
        eWidgets.get(i).hover(lastTouch);
      }
    }

    //draw pieces
    pushMatrix();
    imageMode(CENTER);
    translate(-pieceOffset, 0);
    if (editor.eType == editorType.IMAGE) {
      for (int i = 0; i < pieces.size(); i++) {
        PieceHandler piece = pieces.get(i);
        piece.draw(pieceArea.getX()+pieceArea.getHeight()/2 + i*pieceArea.getHeight(), pieceArea.getY()+pieceArea.getHeight()/2, size);
      }
    }
    imageMode(CORNER);
    popMatrix();
  }

  public void onTap(float x, float y) {
    //select piece 
    if ( y >= pieceArea.getY()) { //touches.length == 1 &&
      showToast("Tap Test");
      editor.currentPiece = pieces.get(0);
      //showToast("Tap Test");
    }
  }

  public void touchMoved() {
    if (touches.length == 1 && mouseY >= pieceArea.getY()) {
      pieceOffset += (pmouseX - mouseX)/3;
    }
  }

  public void touchEnded() {
    //check for clicking on widgets
    for (int i = 0; i < eWidgets.size(); i++) {
      eWidgets.get(i).click();
    }
  }

  public float getHeight() {
    return pieceArea.getHeight();
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
