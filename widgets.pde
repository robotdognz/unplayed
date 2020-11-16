//------------------AbstractWidget---------------------
abstract class Widget {
  Editor editor;
  protected PVector position; //position of the widget
  protected float wSize = 75; //60 //size of the widget
  protected float touchScale = 1.2; //1.5
  protected String folder = dataPath("ui")+'/'; //data path of widget icons
  protected PImage icon;
  protected boolean hover = false; //is the mouse over the widget
  protected boolean active = false; //is the widget active
  protected boolean implemented = true; //is this a fully working widget? Could be used to disable widgets that don't work with the current tool/mode to make menus easier to navigate

  //subWidget fields
  protected float animationSpeed = 0.8; //speed of subWidget animation
  protected widgetDirection wd = widgetDirection.DOWN; //subWidget direction, defaults to down
  protected ArrayList<Widget> subWidgets = new ArrayList<Widget>(); //if this is not null then this widget is a menu
  protected float subWidgetSpacing = 180; //how far apart to draw subWidgets
  protected boolean iconIsCurrentSubWidget = false; //does this widget icon change depending on its sub wigets?
  protected boolean closeAfterSubWidget = false; //does this sub widget close the sub widget menu after being clicked

  public Widget(Editor editor) {
    this.editor = editor;
    defaultIcon();
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= position.x-wSize*touchScale && 
      lastTouch.y >= position.y-wSize*touchScale && 
      lastTouch.x <= position.x+wSize*touchScale && 
      lastTouch.y <= position.y+wSize*touchScale &&
      implemented) {
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
    subWidgetSpacing = editor.eWidgetSpacing;
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
    if (!active) {
      tint(155, 155, 155);
    }
    if (!implemented) {
      tint(30, 30, 30);
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
    //this method should also be used to update 'implemented'
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

  public MenuWidget(Editor editor) {
    super(editor);
    //this.editor = editor; //is this needed?
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
  public SettingsWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"settings.png");
    Widget w1 = new SuspendWidget(editor);
    Widget w2 = new SnapWidget(editor);
    Widget w3 = new PickImageWidget(editor);
    Widget w4 = new PickEventWidget(editor);
    Widget w5 = new PickBlockWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
  }
}
class SuspendWidget extends Widget {
  boolean previousStatus = false;

  public SuspendWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"Pause.png");
  }

  public void clicked() {
    if (!active) {
      gCamera = new FreeCamera();
    } else {
      gCamera = new GameCamera();
      editor.eController = new PlayerControl();
    }
  }

  public void updateActive() {
    if (gCamera instanceof FreeCamera) {
      active = true;
    } else {
      active = false;
    }
  }
}
class SnapWidget extends Widget {
  public SnapWidget(Editor editor) {
    super(editor);
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
  public PickImageWidget(Editor editor) {
    super(editor);
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickImage.png");
  }
}
class PickEventWidget extends Widget {
  public PickEventWidget(Editor editor) {
    super(editor);
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickEvent.png");
  }
}
class PickBlockWidget extends Widget {
  public PickBlockWidget(Editor editor) {
    super(editor);
    implemented = false;
    closeAfterSubWidget = true;
    icon = loadImage(folder+"pickBlock.png");
  }
}

//------------------ControlMode---------------------
class ControlWidget extends Widget {
  public ControlWidget(Editor editor) {
    super(editor);
    iconIsCurrentSubWidget = true;
    icon = loadImage(folder+"playerControls.png");
    Widget w1 = new PlayerControlWidget(editor);
    Widget w2 = new CameraControlWidget(editor);
    Widget w3 = new EditorControlWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class PlayerControlWidget extends Widget {
  public PlayerControlWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
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
  public CameraControlWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"CameraControls.png");
  }

  public void clicked() {
    if (!active) {
      editor.eController = new CameraControl(editor);
      gCamera = new FreeCamera();
    }
  }

  public void updateActive() {
    if (implemented == true && editor.eController instanceof CameraControl) {
      active = true;
    } else {
      active = false;
    }
    //if (gCamera instanceof GameCamera) {
    //  implemented = false;
    //  if (editor.eController instanceof CameraControl) {
    //    editor.eController = new PlayerControl();
    //  }
    //} else {
    //  implemented = true;
    //}
  }
}
class EditorControlWidget extends Widget {
  public EditorControlWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"PlaceBlock.png");
  }

  public void clicked() {
    if (!active) {
      editor.eController = new EditorControl(editor);
    }
  }

  public void updateActive() {
    if (editor.eController instanceof EditorControl) {
      active = true;
    } else {
      active = false;
      g.point = null;
    }
  }
}

//------------------EditorType---------------------
class EditorTypeWidget extends Widget {
  public EditorTypeWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"colider.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new BlockModeWidget(editor);
    Widget w2 = new ImageModeWidget(editor);
    Widget w3 = new EventModeWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class BlockModeWidget extends Widget {
  public BlockModeWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"colider.png");
  }

  public void clicked() {
    editor.eType = editorType.BLOCK;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eType == editorType.BLOCK){
      active = true;
    }else{
      active = false;
    }
  }
}
class ImageModeWidget extends Widget {
  public ImageModeWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"image.png");
  }

  public void clicked() {
    editor.eType = editorType.IMAGE;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eType == editorType.IMAGE){
      active = true;
    }else{
      active = false;
    }
  }
}
class EventModeWidget extends Widget {
  public EventModeWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"event.png");
  }

  public void clicked() {
    editor.eType = editorType.EVENT;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eType == editorType.EVENT){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------EditorMode---------------------
class EditorModeWidget extends Widget {
  public EditorModeWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"Add.png");
    iconIsCurrentSubWidget = true;
    Widget w1 = new AddWidget(editor);
    Widget w2 = new EraseWidget(editor);
    Widget w3 = new SelectWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class AddWidget extends Widget {
  public AddWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"Add.png");
  }

  public void clicked() {
    editor.eMode = editorMode.ADD;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eMode == editorMode.ADD){
      active = true;
    }else{
      active = false;
    }
  }
}
class EraseWidget extends Widget {
  public EraseWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true;
    icon = loadImage(folder+"eraser.png");
  }

  public void clicked() {
    editor.eMode = editorMode.ERASE;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eMode == editorMode.ERASE){
      active = true;
    }else{
      active = false;
    }
  }
}
class SelectWidget extends Widget {
  public SelectWidget(Editor editor) {
    super(editor);
    closeAfterSubWidget = true; 
    icon = loadImage(folder+"marque.png");
  }

  public void clicked() {
    editor.eMode = editorMode.SELECT;
    editor.eController = new EditorControl(editor);
  }
  
  public void updateActive() {
    if(editor.eMode == editorMode.SELECT){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------ExtraTools---------------------
class ExtraWidget extends Widget {
  public ExtraWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"extraActions.png");
    Widget w1 = new ConfirmWidget(editor);
    Widget w2 = new EditSelectedWidget(editor);
    Widget w3 = new LayerForwardWidget(editor);
    Widget w4 = new LayerBackwardWidget(editor);
    Widget w5 = new SaveWidget(editor);
    Widget w6 = new LoadWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
    subWidgets.add(w4);
    subWidgets.add(w5);
    subWidgets.add(w6);
  }
}
class ConfirmWidget extends Widget {
  public ConfirmWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"confirmTick.png");
    closeAfterSubWidget = true;
  }

  public void clicked() {
    //finalise block
    editor.placeBlock();
  }

  public void updateActive() {
    if (editor.eController instanceof EditorControl && !editor.snap && g.point != null) {
      implemented = true;
    } else {
      implemented = false;
    }
  }
}
class EditSelectedWidget extends Widget {
  public EditSelectedWidget(Editor editor) {
    super(editor);
    implemented = false;
    icon = loadImage(folder+"EditSelected.png");
    closeAfterSubWidget = true;
  }
}
class LayerForwardWidget extends Widget {
  public LayerForwardWidget(Editor editor) {
    super(editor);
    implemented = false;
    icon = loadImage(folder+"MoveLayerForward.png");
  }
}
class LayerBackwardWidget extends Widget {
  public LayerBackwardWidget(Editor editor) {
    super(editor);
    implemented = false;
    icon = loadImage(folder+"MoveLayerBackward.png");
  }
}
class SaveWidget extends Widget {
  public SaveWidget(Editor editor) {
    super(editor);
    icon = loadImage(folder+"save.png");
    wd = widgetDirection.LEFT;
    Widget w1 = new BlankWidget(editor);
    Widget w2 = new BlankWidget(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
  }
}
class LoadWidget extends Widget {
  public LoadWidget(Editor editor) {
    super(editor);
    implemented = false;
    icon = loadImage(folder+"load.png");
  }
}

//------------------TestSubMenuWidgets---------------------
class SubMenuWidget extends Widget {
  public SubMenuWidget(Editor editor) {
    super(editor);
    Widget w1 = new BlankWidget(editor);
    Widget w2 = new BlankWidget(editor);
    Widget w3 = new SubMenuWidget2(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2 extends Widget {
  public SubMenuWidget2(Editor editor) {
    super(editor);
    wd = widgetDirection.LEFT;
    Widget w1 = new BlankWidget(editor);
    Widget w2 = new SubMenuWidget2Up(editor);
    Widget w3 = new SubMenuWidget3(editor);
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2Up extends Widget {
  public SubMenuWidget2Up(Editor editor) {
    super(editor);
    wd = widgetDirection.UP;
    Widget w1 = new BlankWidget(editor);
    subWidgets.add(w1);
  }
}
class SubMenuWidget3 extends Widget {
  public SubMenuWidget3(Editor editor) {
    super(editor);
    Widget w1 = new BlankWidget(editor);
    Widget w2 = new BlankWidget(editor);
    Widget w3 = new BlankWidget(editor);
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
  BlankWidget(Editor editor) {
    super(editor);
  }
}
