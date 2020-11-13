//------------------AbstractWidget---------------------
abstract class Widget {
  protected PVector position; //position of the widget
  protected float wSize = 75; //60 //size of the widget
  protected float touchScale = 1.2; //1.5
  protected String folder = dataPath("ui")+'/'; //data path of widget icons
  protected PImage icon;
  protected boolean hover = false; //is the mouse over the widget
  protected boolean active = false; //is the widget active
  
  protected boolean implemented = true; //is this a fully working widget?
  
  //subWidget fields
  protected float animationSpeed = 0.8; //speed of subWidget animation
  protected widgetDirection wd = widgetDirection.DOWN; //subWidget direction, defaults to down
  protected ArrayList<Widget> subWidgets = new ArrayList<Widget>(); //if this is not null then this widget is a menu
  protected float subWidgetSpacing = 180; //how far apart to draw subWidgets
  protected boolean iconIsCurrentSubWidget = false; //does this widget icon change depending on its sub wigets?
  protected boolean closeAfterSubWidget = false; //does this sub widget close the sub widget menu after being clicked

  public Widget() {
    defaultIcon();
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= position.x-wSize*touchScale && 
      lastTouch.y >= position.y-wSize*touchScale && 
      lastTouch.x <= position.x+wSize*touchScale && 
      lastTouch.y <= position.y+wSize*touchScale) {
      hover = true;
    }
    
    //subWidget hover
    if (subWidgets.size() > 0 && active) { //if this widget is a menu and it has been opened
      for (Widget w : subWidgets) {
        w.hover(lastTouch);
      }
    }
  }

  public void draw(float wX, float wY) {
    subWidgetSpacing = widgetSpacing;
    //update position
    if (position == null) {
      position = new PVector(wX, wY);
    } else if (position.x != wX || position.y != wY) {
      position.x = lerp(position.x, wX, exp(-animationSpeed));
      position.y = lerp(position.y, wY, exp(-animationSpeed));
    }

    //subWidget draw - comes before current widget so the sub widgets slide out from behind
    if (subWidgets.size() > 0) { //if this widget is a menu and it has been opened
      for (int i = 0; i < subWidgets.size(); i++) {
        float widgetOffset = 0;
        if (active) {
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

    //draw widget ellipse
    ellipseMode(CENTER);
    fill(70);
    ellipse(position.x, position.y, wSize*1.5, wSize*1.5); 
    ellipseMode(CORNER);

    //draw widget icon
    if (!active){
      tint(155,155,155);
    }
    if(!implemented){
      tint(30,30,30);
    }
    imageMode(CENTER);
    image(icon, position.x, position.y, wSize, wSize);
    noTint();
    imageMode(CORNER);
  }

  public boolean isMenu() {
    if (subWidgets.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isActive() {
    return active;
  }

  public void deactivate() {
    active = false;
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
            active = false;
          }
        }
      }
    }
    return false;
  }

  public void clicked() {
    active = !active;
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
  
  public float getSize(){
   return wSize; 
  }
  
  public ArrayList<Widget> getChildren(){
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
    if (subWidgets.size() > 0) {
      for (Widget w : subWidgets) {
        w.updateActive();
        if (iconIsCurrentSubWidget && w.isActive()) {
          this.icon = w.getIcon();
        }
      }
    }
  }
}



//------------------Menu---------------------
class MenuWidget extends Widget {
  boolean previousStatus = false;

  public MenuWidget() {
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
}

//------------------Settings---------------------
class SettingsWidget extends Widget {
  public SettingsWidget() {
    icon = loadImage(folder+"settings.png");
    Widget w1 = new SuspendWidget();
    Widget w2 = new SnapWidget();
    Widget w3 = new PickImageWidget();
    Widget w4 = new PickEventWidget();
    Widget w5 = new PickBlockWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
  }
}
class SuspendWidget extends Widget {
  boolean previousStatus = false;

  public SuspendWidget() {
    closeAfterSubWidget = true;
    icon = loadImage(folder+"Pause.png");
  }

  public void clicked() {
    if (!active) {
      c = new FreeCamera();
    } else {
      c = new GameCamera();
    }
  }

  public void updateActive() {
    if (c instanceof FreeCamera) {
      active = true;
    } else {
      active = false;
    }
  }
}
class SnapWidget extends Widget {
  public SnapWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"snaptoGrid.png");
  }
}
class PickImageWidget extends Widget {
  public PickImageWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickImage.png");
  }
}
class PickEventWidget extends Widget {
  public PickEventWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickEvent.png");
  }
}
class PickBlockWidget extends Widget {
  public PickBlockWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickBlock.png");
  }
}

//------------------ControlMode---------------------
class ControlWidget extends Widget {
  public ControlWidget() {
    iconIsCurrentSubWidget = true;
    icon = loadImage(folder+"playerControls.png");
    Widget w1 = new PlayerControlWidget();
    Widget w2 = new CameraControlWidget();
    Widget w3 = new EditorControlWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class PlayerControlWidget extends Widget {
  public PlayerControlWidget() {
    closeAfterSubWidget = true;
    icon = loadImage(folder+"playerControls.png");
  }

  public void clicked() {
    if (!active) {
      con = new PlayerControl();
    }
  }

  public void updateActive() {
    if (con instanceof PlayerControl) {
      active = true;
    } else {
      active = false;
    }
  }
}
class CameraControlWidget extends Widget {
  public CameraControlWidget() {
    closeAfterSubWidget = true;
    icon = loadImage(folder+"CameraControls.png");
  }

  public void clicked() {
    if (!active) {
      con = new CameraControl();
    }
  }

  public void updateActive() {
    if (con instanceof CameraControl) {
      active = true;
    } else {
      active = false;
    }
  }
}
class EditorControlWidget extends Widget {
  public EditorControlWidget() {
    closeAfterSubWidget = true;
    icon = loadImage(folder+"PlaceBlock.png");
  }

  public void clicked() {
    if (!active) {
      con = new EditorControl();
    } else {
    }
  }

  public void updateActive() {
    if (con instanceof EditorControl) {
      active = true;
    } else {
      active = false;
    }
  }
}

//------------------EditorType---------------------
class EditorTypeWidget extends Widget {
  public EditorTypeWidget() {
    icon = loadImage(folder+"colider.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new BlockModeWidget();
    Widget w2 = new ImageModeWidget();
    Widget w3 = new EventModeWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class BlockModeWidget extends Widget {
  public BlockModeWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"colider.png");
  }
}
class ImageModeWidget extends Widget {
  public ImageModeWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"image.png");
  }
}
class EventModeWidget extends Widget {
  public EventModeWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"event.png");
  }
}

//------------------EditorMode---------------------
class EditorModeWidget extends Widget {
  public EditorModeWidget() {
    icon = loadImage(folder+"Add.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new AddWidget();
    Widget w2 = new EraseWidget();
    Widget w3 = new SelectWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class AddWidget extends Widget {
  public AddWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"Add.png");
  }
}
class EraseWidget extends Widget {
  public EraseWidget(){
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"eraser.png");
  }
}
class SelectWidget extends Widget {
  public SelectWidget(){
    implemented = false;
    closeAfterSubWidget = true; 
    icon = loadImage(folder+"marque.png");
  }
}

//------------------ExtraTools---------------------
class ExtraWidget extends Widget {
  public ExtraWidget() {
    icon = loadImage(folder+"extraActions.png");
    Widget w1 = new ConfirmWidget();
    Widget w2 = new EditSelectedWidget();
    Widget w3 = new LayerForwardWidget();
    Widget w4 = new LayerBackwardWidget();
    Widget w5 = new SaveWidget();
    Widget w6 = new LoadWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
    subWidgets.add(w6);
  }
}
class ConfirmWidget extends Widget {
  public ConfirmWidget() {
    icon = loadImage(folder+"confirmTick.png");
    closeAfterSubWidget = true;
  }

  public void clicked() {
    //finalise block
    if (g.point != null) {
      Platform p = new Platform((int)g.point.x-50, (int)g.point.y-50, 100, 100);
      g.platforms.add(p);
      g.point = null;
    }
  }
}
class EditSelectedWidget extends Widget {
  public EditSelectedWidget() {
    implemented = false;
    icon = loadImage(folder+"EditSelected.png");
    closeAfterSubWidget = true;
  }
}
class LayerForwardWidget extends Widget {
  public LayerForwardWidget() {
    implemented = false;
    icon = loadImage(folder+"MoveLayerForward.png");
  }
}
class LayerBackwardWidget extends Widget {
  public LayerBackwardWidget() {
    implemented = false;
    icon = loadImage(folder+"MoveLayerBackward.png");
  }
}
class SaveWidget extends Widget {
  public SaveWidget() {
    icon = loadImage(folder+"save.png");
    wd = widgetDirection.LEFT;
    Widget w1 = new BlankWidget();
    Widget w2 = new BlankWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
  }
}
class LoadWidget extends Widget {
  public LoadWidget() {
    implemented = false;
    icon = loadImage(folder+"load.png");
  }
}

//------------------TestSubMenuWidgets---------------------
class SubMenuWidget extends Widget {
  public SubMenuWidget() {
    Widget w1 = new BlankWidget();
    Widget w2 = new BlankWidget();
    Widget w3 = new SubMenuWidget2();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2 extends Widget {
  public SubMenuWidget2() {
    wd = widgetDirection.LEFT;
    Widget w1 = new BlankWidget();
    Widget w2 = new SubMenuWidget2Up();
    Widget w3 = new SubMenuWidget3();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2Up extends Widget {
  public SubMenuWidget2Up() {
    wd = widgetDirection.UP;
    Widget w1 = new BlankWidget();
    subWidgets.add(w1);
  }
}
class SubMenuWidget3 extends Widget {
  public SubMenuWidget3() {
    Widget w1 = new BlankWidget();
    Widget w2 = new BlankWidget();
    Widget w3 = new BlankWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
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
}
