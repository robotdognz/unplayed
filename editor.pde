//------------------Editor---------------------
class Editor {
  //touch constraint variables
  public final int TOP_DEADZONE = 200; //TODO: needs to scale with screen
  public final int BOTTOM_DEADZONE = height-300; //TODO: needs to scale with screen
  public boolean nextTouchInactive = false;

  Game eGame; //reference to game, same instance of game used everywhere else

  //camera variables
  float minZoom = 3;
  float maxZoom = 100;

  //controller
  Controller eController; //holds the current controller
  boolean eControllerActive = true; //is the current controller active

  //editor settings
  boolean snap = true; //things placed in the level will snap to grid
  editorType eType = editorType.BLOCK;
  editorMode eMode = editorMode.ADD;
  imagePlane eImagePlane = imagePlane.LEVEL;

  //current object to put into level
  PieceHandler currentPiece = null;
  TileHandler currentTile = null;
  EventHandler currentEvent = null;

  //toolbars
  Toolbar editorTop;
  Toolbar editorBottom;

  //saver/loader class
  EditorJSON eJSON;

  //frame count
  int frameDelay = 100;
  float frame;

  public Editor(Game game) {
    this.eGame = game;
    this.eController = new CameraControl(this);
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
      game.point = null;
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
    text(game.scanSize + " : " + game.screenObjects.size(), width/2, height-editorBottom.getHeight()-100);
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

  public void placeObject() {
    if (game.point != null) {

      int platformX = (int) game.point.x;
      int platformY = (int) game.point.y;

      boolean spaceFree = true;
      Rectangle foundAtPoint = null;

      //create the new piece to put in
      Rectangle toInsert = null;
      if (eType == editorType.BLOCK && currentTile != null) {
        toInsert = new Tile(currentTile.getFile(), platformX, platformY);
      } else if (eType == editorType.IMAGE && currentPiece != null) {
        toInsert = new Piece(currentPiece.getFile(), platformX, platformY, currentPiece.getWidth(), currentPiece.getHeight());
      } else if (eType == editorType.EVENT && currentEvent != null) {
        toInsert = currentEvent.makeEvent(platformX, platformY);
      } else {
        game.point = null; //if there is nothing to put in, remove the point
      }

      //insert it or remove
      if (toInsert != null && game.point != null) {
        HashSet<Rectangle> getRectangles = new HashSet<Rectangle>();
        game.world.retrieve(getRectangles, toInsert);
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
            game.world.insert(toInsert);
            showToast(toInsert.toString());
          }
        } else {
          if (eMode == editorMode.ERASE && foundAtPoint != null) {
            game.world.remove(foundAtPoint);
          }
        }
        game.point = null;
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
  private Rectangle pieceArea;  //TODO: rename
  private String folder = dataPath("ui") + '/';
  private PImage toolbar;
  private float widgetHeight;

  //scroll bars
  private int size; //size to drawn object in the scroll bar
  private ArrayList<TileHandler> tiles; //tiles
  private float tileOffset;
  private ArrayList<PieceHandler> pieces; //pieces
  private float pieceOffset;
  private ArrayList<EventHandler> events; //events
  private float eventOffset;

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

    eWidgetOffset = width*0.71;
    eWidgetSpacing = 140;    //TODO: get the spacing right

    //setup toolbar
    int pieceAreaHeight = 230; //200
    pieceArea = new Rectangle(0, height-pieceAreaHeight, width, pieceAreaHeight);
    toolbar = loadImage(folder+"icn_toolbar_bg.png");

    widgetHeight = pieceArea.getY()-54; //TODO: get the height right

    //scroll bars
    size = 150;
    tiles = texture.getTileList();
    pieces = texture.getPieceList();
    events = texture.getEventList();
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

    //figure out what type to show
    ArrayList<Handler> objects = new ArrayList<Handler>(); //current objects to draw in the scroll bar
    Float offset = 0.0;
    Handler currentHandler = null;
    if (editor.eType == editorType.BLOCK) {
      objects.addAll(tiles);
      offset = tileOffset;
      currentHandler = editor.currentTile;
    } else if (editor.eType == editorType.IMAGE) {
      objects.addAll(pieces);
      offset = pieceOffset;
      currentHandler = editor.currentPiece;
    } else if (editor.eType == editorType.EVENT) {
      objects.addAll(events);
      offset = eventOffset;
      currentHandler = editor.currentEvent;
    }

    //draw scroll bar for that type
    pushMatrix();
    imageMode(CENTER);
    rectMode(CENTER);
    translate(-offset, 0);
    for (int i = 0; i < objects.size(); i++) {
      Handler object = objects.get(i);
      if (object.equals(currentHandler)) { //if this is the selected piece
        //draw highlight behind
        noStroke();
        fill(0, 0, 0, 120);
        rect(pieceArea.getX()+pieceArea.getHeight()/2 + i*pieceArea.getHeight(), pieceArea.getY()+pieceArea.getHeight()/2, pieceArea.getHeight(), pieceArea.getHeight());
      }
      object.draw(pieceArea.getX()+pieceArea.getHeight()/2 + i*pieceArea.getHeight(), pieceArea.getY()+pieceArea.getHeight()/2, size);
    }
    imageMode(CORNER);
    rectMode(CORNER);
    popMatrix();
  }

  public void onTap(float x, float y) {
    //select piece 
    if (y >= pieceArea.getY()) {
      editor.eController = new EditorControl(editor);
      editor.eMode = editorMode.ADD;

      //figure out what type is being clicked on
      ArrayList<Handler> objects = new ArrayList<Handler>(); //current objects to draw in the scroll bar
      Float offset = 0.0;
      if (editor.eType == editorType.BLOCK) {
        objects.addAll(tiles);
        offset = tileOffset;
      } else if (editor.eType == editorType.IMAGE) {
        objects.addAll(pieces);
        offset = pieceOffset;
      } else if (editor.eType == editorType.EVENT) {
        objects.addAll(events);
        offset = eventOffset;
      }

      //click on that piece
      for (int i = 0; i < objects.size(); i++) {
        float leftEdge = pieceArea.getX() + (i)*pieceArea.getHeight() - offset;
        float rightEdge = pieceArea.getX() + (i+1)*pieceArea.getHeight() - offset;
        if (x > leftEdge && x < rightEdge) {
          if (editor.eType == editorType.BLOCK) {
            editor.currentTile = (TileHandler) objects.get(i);
          } else if (editor.eType == editorType.IMAGE) {
            editor.currentPiece = (PieceHandler) objects.get(i);
          } else if (editor.eType == editorType.EVENT) {
            editor.currentEvent = (EventHandler) objects.get(i);
          }
        }
      }
    }
  }

  public void touchMoved() {
    if (touches.length == 1 && mouseY >= pieceArea.getY()) {
      if (editor.eType == editorType.BLOCK) {
        tileOffset += (pmouseX - mouseX)/3;
      } else if (editor.eType == editorType.IMAGE) {
        pieceOffset += (pmouseX - mouseX)/3;
      } else if (editor.eType == editorType.EVENT) {
        eventOffset += (pmouseX - mouseX)/3;
      }
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
