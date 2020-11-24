//------------------AbstractWidget---------------------
abstract class Widget {
  //TODO: currently does not scale based on screen size
  Editor editor;
  Toolbar parent;
  protected PVector position; //position of the widget
  protected float wSize = 75; //60 //size of the widget
  protected float touchScale = 1.2; //1.5
  protected String folder = dataPath("") + "ui" + '/' + "widgets" + '/'; // dataPath("ui") + '/' + "widgets" + '/'; //data path of widget icons
  protected PImage icon;
  protected PImage imageActive;
  protected PImage imageInactive;
  protected PImage imageUnavailable;
  protected boolean hover = false; //is the mouse over the widget
  protected boolean active = false; //is the widget active
  protected boolean hasSActive = false; //should active be used decoupled from widget menu opening
  protected boolean sActive = false; //secondary active that replaces the origonal for opening menus etc allowing the user to do whatever with 'active'
  protected boolean available = true; //is this a fully working widget? Could be used to disable widgets that don't work with the current tool/mode to make menus easier to navigate

  //subWidget fields
  protected float animationSpeed = 0.8; //speed of subWidget animation
  protected widgetDirection wd = widgetDirection.DOWN; //subWidget direction, defaults to down
  protected ArrayList<Widget> subWidgets = new ArrayList<Widget>(); //if this is not null then this widget is a menu
  protected float subWidgetSpacing = 180; //how far apart to draw subWidgets
  protected boolean iconIsCurrentSubWidget = false; //does this widget icon change depending on its sub wigets?
  protected boolean closeAfterSubWidget = false; //does this sub widget close the sub widget menu after being clicked

  public Widget(Editor editor, Toolbar parent) {
    imageActive = loadImage(folder+"active.png");
    imageInactive = loadImage(folder+"inactive.png");
    imageUnavailable = loadImage(folder+"unavailable.png");
    this.editor = editor;
    this.parent = parent;
    defaultIcon();
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= position.x-wSize*touchScale && 
      lastTouch.y >= position.y-wSize*touchScale && 
      lastTouch.x <= position.x+wSize*touchScale && 
      lastTouch.y <= position.y+wSize*touchScale &&
      available) {
      hover = true;
    }else{
      hover = false;
    }

    //subWidget hover
    if (subWidgets.size() > 0 && ((!hasSActive && active) || (hasSActive && sActive))) { //if this widget is a menu and it has been opened
      for (Widget w : subWidgets) {
        w.hover(lastTouch);
      }
    }
  }

  public void draw(float wX, float wY) {
    if(parent != null){
      subWidgetSpacing = parent.eWidgetSpacing;
    }
    //update position
    if (position == null) {
      position = new PVector(wX, wY);
    } else if (position.x != wX || position.y != wY) {
      position.x = lerp(position.x, wX, exp(-animationSpeed));
      position.y = lerp(position.y, wY, exp(-animationSpeed));
    }

    //subWidget draw - comes before current widget so the sub widgets slide out from behind
    if (subWidgets.size() > 0) { //if this widget is a menu and it has been opened
      for (int i = subWidgets.size()-1; i >= 0; i--) { //go through them backwards so that they are drawn bottom to top
        float widgetOffset = 0;
        if ((!hasSActive && active) || (hasSActive && sActive)) {
          //if this widget is active, open the subWidgets
          widgetOffset = subWidgetSpacing+i*subWidgetSpacing;
        } else {
          //if the subWidget is a menu, deactivate it so it closes
          if (subWidgets.get(i).isMenu()) {
            subWidgets.get(i).deactivate();
          }
        }
        switch(wd) {
        case DOWN:
          subWidgets.get(i).draw(wX, wY+widgetOffset);
          break;
        case UP:
          subWidgets.get(i).draw(wX, wY-widgetOffset);
          break;
        case LEFT:
          subWidgets.get(i).draw(wX-widgetOffset, wY);
          break;
        case RIGHT:
          subWidgets.get(i).draw(wX+widgetOffset, wY);
          break;
        }
      }
    }
    
    imageMode(CENTER);
    drawExtra();
    if (available) {
      if (active) {
        //active
        image(imageActive, position.x, position.y, wSize*1.5, wSize*1.5);
      } else {
        //not active
        image(imageInactive, position.x, position.y, wSize*1.5, wSize*1.5);
        tint(75);
      }
    } else {
      //unavailable
      image(imageUnavailable, position.x, position.y, wSize*1.5, wSize*1.5);
      tint(180);
    }

    //draw widget icon
    image(icon, position.x, position.y, wSize, wSize);
    noTint();
    imageMode(CORNER);
  }
  
  public void drawExtra(){}

  public boolean isMenu() {
    if (subWidgets.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isActive() {
    if (hasSActive) {
      return sActive;
    } else {
      return active;
    }
  }

  public void deactivate() {
    if (hasSActive) {
      sActive = false;
    } else {
      active = false;
    }
  }

  public boolean click() {
    if (hover) {
      hover = false;
      clicked();
      return true;
    }

    if (subWidgets.size() > 0) {
      for (Widget w : subWidgets) {
        if (w.click()) { //both does the click and returns true if the click happened
          if (w.getCloseAfter()) { //if the widget that was clicked should close the widget menu, close it
            if (hasSActive) {
              sActive = false;
            } else {
              active = false;
            }
          }
        }
      }
    }
    return false;
  }

  public void clicked() {
    if (hasSActive) {
      sActive = !sActive;
    } else {
      active = !active;
    }
  }

  protected void defaultIcon() {
    icon = loadImage(folder+"diamond.png");
  }

  public PVector getPosition() {
    return position;
  }

  public boolean getCloseAfter() {
    return closeAfterSubWidget;
  }

  public PImage getIcon() {
    return icon;
  }

  public float getSize() {
    return wSize;
  }

  public ArrayList<Widget> getChildren() {
    return subWidgets;
  }

  public void setPosition(PVector position) {
    if (this.position == null) {
      this.position = new PVector(position.x, position.y);
    } else {
      this.position.x = position.x;
      this.position.y = position.y;
    }
  }

  public void updateActive() {
    //this method should also be used to update 'available'
    if (subWidgets.size() > 0) {
      for (Widget w : subWidgets) {
        w.updateActive();
        if (iconIsCurrentSubWidget && w.isActive()) {
          this.icon = w.getIcon();
        }
      }
    }
    updateActiveUser();
  }

  public void updateActiveUser() {
  }
}


//------------------Menu---------------------
class MenuWidget extends Widget {
  boolean previousStatus = false;

  public MenuWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"menu.png");
  }

  public void clicked() {
    if (!active) {
      active = true;
      previousStatus = gPaused;
      gPaused = true; //switch pause state
      menu = new PauseMenu(this);
    }
  }

  public void updateActive() {
    if (menu != null) {
      active = true;
    }
  }
}

//------------------Settings---------------------
class SettingsWidget extends Widget {
  public SettingsWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"settings.png");
    Widget w1 = new RestartWidget(editor, parent);
    Widget w2 = new PlayModeWidget(editor, parent);
    Widget w3 = new SnapWidget(editor, parent);
    Widget w4 = new PickImageWidget(editor, parent);
    Widget w5 = new PickEventWidget(editor, parent);
    Widget w6 = new PickBlockWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
    subWidgets.add(w6);
  }
}
class RestartWidget extends Widget {
  public RestartWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"ResetGame.png");
    closeAfterSubWidget = true;
  }

  public void clicked() {
    g.restart();
  }

  public void updateActive() {
  }
}
class PlayModeWidget extends Widget {
  boolean previousStatus = false;

  public PlayModeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"Pause.png");
    closeAfterSubWidget = true;
  }

  public void clicked() {
    if (active) {
      gCamera = new FreeCamera();
    } else {
      gCamera = new GameCamera();
      editor.eController = new PlayerControl();
    }
  }

  public void updateActive() {
    if (gCamera instanceof FreeCamera) {
      active = false;
    } else {
      active = true;
    }
  }
}
class SnapWidget extends Widget {
  public SnapWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"snaptoGrid.png");
  }

  public void clicked() {
    editor.snap = !editor.snap;
  }

  public void updateActive() {
    if (editor.snap) {
      active = true;
    } else {
      active = false;
    }
  }
}
class PickImageWidget extends Widget {
  public PickImageWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickImage.png");
  }
}
class PickEventWidget extends Widget {
  public PickEventWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickEvent.png");
  }
}
class PickBlockWidget extends Widget {
  public PickBlockWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickBlock.png");
  }
}

//------------------EditorControls---------------------
class PlayerControlWidget extends Widget {
  public PlayerControlWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"playerControls.png");
  }

  public void clicked() {
    if (!active) {
      editor.eController = new PlayerControl();
    }
  }

  public void updateActive() {
    if (editor.eController instanceof PlayerControl) {
      active = true;
    } else {
      active = false;
    }
  }
}
class CameraControlWidget extends Widget {
  PImage uiExtra = loadImage(folder+"UI_element01.png");
  public CameraControlWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"CameraControls.png");
  }

  public void clicked() {
    if (!active) {
      editor.eController = new CameraControl(editor);
      gCamera = new FreeCamera();
    }
  }

  public void updateActive() {
    if (available == true && editor.eController instanceof CameraControl) {
      active = true;
    } else {
      active = false;
    }
  }
  
  public void drawExtra(){
    float widgetScale = wSize*1.5;
    image(uiExtra, position.x, position.y, widgetScale*4.4, widgetScale*1.2); //1.25333333  //4.426666
  }
}

//------------------EditorMode---------------------
class EditorModeWidget extends Widget {
  public EditorModeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"PlaceBlock.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new AddWidget(editor, parent);
    Widget w2 = new EraseWidget(editor, parent);
    Widget w3 = new SelectWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);

    hasSActive = true;
  }

  public void clicked() {
    if (active == false) {
      active = true;
      editor.eController = new EditorControl(editor);
    } else {
      sActive = !sActive;
    }
  }

  public void updateActiveUser() {
    if (editor.eController instanceof EditorControl) {
      active = true;
    } else {
      active = false;
    }
  }
}
class AddWidget extends Widget {
  public AddWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"PlaceBlock.png");
  }

  public void clicked() {
    editor.eMode = editorMode.ADD;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eMode == editorMode.ADD) {
      active = true;
    } else {
      active = false;
    }
  }
}
class EraseWidget extends Widget {
  public EraseWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"eraser.png");
  }

  public void clicked() {
    editor.eMode = editorMode.ERASE;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eMode == editorMode.ERASE) {
      active = true;
    } else {
      active = false;
    }
  }
}
class SelectWidget extends Widget {
  public SelectWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true; 
    icon = loadImage(folder+"select.png");
  }

  public void clicked() {
    editor.eMode = editorMode.SELECT;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eMode == editorMode.SELECT) {
      active = true;
    } else {
      active = false;
    }
  }
}

//------------------EditorType---------------------
class EditorTypeWidget extends Widget {
  public EditorTypeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"colider.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new BlockModeWidget(editor, parent);
    Widget w2 = new ImageModeWidget(editor, parent);
    Widget w3 = new EventModeWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class BlockModeWidget extends Widget {
  public BlockModeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"colider.png");
  }

  public void clicked() {
    editor.eType = editorType.BLOCK;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eType == editorType.BLOCK) {
      active = true;
    } else {
      active = false;
    }
  }
}
class ImageModeWidget extends Widget {
  public ImageModeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"image.png");
  }

  public void clicked() {
    editor.eType = editorType.IMAGE;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eType == editorType.IMAGE) {
      active = true;
    } else {
      active = false;
    }
  }
}
class EventModeWidget extends Widget {
  public EventModeWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"event.png");
  }

  public void clicked() {
    editor.eType = editorType.EVENT;
    editor.eController = new EditorControl(editor);
  }

  public void updateActive() {
    if (editor.eType == editorType.EVENT) {
      active = true;
    } else {
      active = false;
    }
  }
}

//------------------ExtraTools---------------------
class ExtraWidget extends Widget {
  public ExtraWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"extraActions.png");
    Widget w1 = new ConfirmWidget(editor, parent);
    Widget w2 = new EditSelectedWidget(editor, parent);
    Widget w3 = new LayerForwardWidget(editor, parent);
    Widget w4 = new LayerBackwardWidget(editor, parent);
    Widget w5 = new SaveMenuWidget(editor, parent);
    Widget w6 = new PlaneMenuWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
    subWidgets.add(w6);
  }
}
class ConfirmWidget extends Widget {
  public ConfirmWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"confirmTick.png");
    closeAfterSubWidget = true;
  }

  public void clicked() {
    //finalise block
    editor.placeBlock();
  }

  public void updateActive() {
    if (editor.eController instanceof EditorControl && !editor.snap && g.point != null) {
      available = true;
    } else {
      available = false;
    }
  }
}
class EditSelectedWidget extends Widget {
  public EditSelectedWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"EditSelected.png");
    closeAfterSubWidget = true;
  }
}
class LayerForwardWidget extends Widget {
  public LayerForwardWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"MoveLayerForward.png");
  }
}
class LayerBackwardWidget extends Widget {
  public LayerBackwardWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"MoveLayerBackward.png");
  }
}
class SaveMenuWidget extends Widget {
  public SaveMenuWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"save.png");
    wd = widgetDirection.LEFT;
    Widget w1 = new SaveWidget(editor, parent);
    Widget w2 = new SaveAsWidget(editor, parent);
    Widget w3 = new LoadWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SaveWidget extends Widget {
  public SaveWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"save.png");
  }
}
class SaveAsWidget extends Widget {
  public SaveAsWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"saveAs.png");
  }
}
class LoadWidget extends Widget {
  public LoadWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    available = false;
    icon = loadImage(folder+"load.png");
  }
}

class PlaneMenuWidget extends Widget {
  public PlaneMenuWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"save.png");
    iconIsCurrentSubWidget = true;
    wd = widgetDirection.LEFT;
    Widget w1 = new BackgroundWidget(editor, parent);
    Widget w2 = new LevelWidget(editor, parent);
    Widget w3 = new ForegroundWidget(editor, parent);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class ForegroundWidget extends Widget {
  public ForegroundWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"WorkFront.png");
  }
  public void clicked() {
    editor.eImagePlane = imagePlane.FRONT;
    editor.eType = editorType.IMAGE;
  }
  public void updateActive() {
    if (editor.eImagePlane == imagePlane.FRONT) {
      active = true;
    } else {
      active = false;
    }
  }
}
class LevelWidget extends Widget {
  public LevelWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"WorkMid.png");
  }
  public void clicked() {
    editor.eImagePlane = imagePlane.LEVEL;
    editor.eType = editorType.IMAGE;
  }
  public void updateActive() {
    if (editor.eImagePlane == imagePlane.LEVEL) {
      active = true;
    } else {
      active = false;
    }
  }
}
class BackgroundWidget extends Widget {
  public BackgroundWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
    icon = loadImage(folder+"WorkBack.png");
  }
  public void clicked() {
    editor.eImagePlane = imagePlane.BACK;
    editor.eType = editorType.IMAGE;
  }
  public void updateActive() {
    if (editor.eImagePlane == imagePlane.BACK) {
      active = true;
    } else {
      active = false;
    }
  }
}

//------------------WidgetDirectionEnum---------------------
enum widgetDirection {
  DOWN, 
    UP, 
    LEFT, 
    RIGHT
}

//------------------BlankWidget---------------------
class BlankWidget extends Widget {
  BlankWidget(Editor editor, Toolbar parent) {
    super(editor, parent);
  }
}
