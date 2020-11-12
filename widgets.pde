//------------------AbstractWidget---------------------
abstract class Widget{
  protected PVector position; //position of the widget
  protected float wSize = 60; //size of the widget
  protected String folder = dataPath("ui")+'/'; //data path of widget icons
  protected PImage off; //inactive image
  protected PImage on; //activated image
  protected boolean hover = false; //is the mouse over the widget
  protected boolean active = false; //is the widget active
  
  //subWidget fields
  protected float animationSpeed = 0.8; //speed of subWidget animation
  protected widgetDirection wd = widgetDirection.DOWN; //subWidget direction, defaults to down
  protected ArrayList<Widget> subWidgets = new ArrayList<Widget>(); //if this is not null then this widget is a menu
  protected float subWidgetSpacing = 180; //how far apart to draw subWidgets
  protected boolean iconIsCurrentSubWidget = false; //does this widget icon change depending on its subwigets?
  
  public Widget(){
    defaultIcon();
  }
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x-wSize*1.5 && 
    lastTouch.y >= position.y-wSize*1.5 && 
    lastTouch.x <= position.x+wSize*1.5 && 
    lastTouch.y <= position.y+wSize*1.5) {
      hover = true;
    }else{
      hover = false;
    }
    
    //subWidget hover
    if(subWidgets.size() > 0 && active){ //if this widget is a menu and it has been opened
      for(Widget w : subWidgets){
        w.hover(lastTouch);
      }
    }
  }
  
  public void draw(float wX, float wY){
    subWidgetSpacing = widgetSpacing;
    //update position
    if(position == null){
      position = new PVector(wX, wY);
    }else if(position.x != wX || position.y != wY){
      position.x = lerp(position.x, wX, exp(-animationSpeed));
      position.y = lerp(position.y, wY, exp(-animationSpeed));
    }
    
    //subWidget draw - comes before current widget so the sub widgets slide out from behind
    if(subWidgets.size() > 0){ //if this widget is a menu and it has been opened
      for(int i = 0; i < subWidgets.size(); i++){
        float widgetOffset = 0;
        if(active){
          //if this widget is active, open the subWidgets
          widgetOffset = subWidgetSpacing+i*subWidgetSpacing;
        }else{
          //if the subWidget is a menu, deactivate it so it closes
          if(subWidgets.get(i).isMenu()){
            subWidgets.get(i).deactivate();
          }
        }
        switch(wd){
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
    imageMode(CENTER);
    if(active){
      image(on, position.x, position.y, wSize, wSize);
    }else{
      image(off, position.x, position.y, wSize, wSize);
    }
    imageMode(CORNER);
  }
  
  public boolean isMenu(){
    if(subWidgets.size() > 0){
      return true;
    }else{
      return false;
    }
  }
  
  public boolean isActive(){
    return active;
  }
  
  public void deactivate(){
    active = false;
  }
  
  public void click(){
    if(hover){
      clicked();
    }
    if(subWidgets.size() > 0){
      for(Widget w : subWidgets){
        w.click();
      }
    }
  }
  
  public void clicked(){
    active = !active;
  }
  
  protected void defaultIcon(){
    off = loadImage(folder+"diamondClick.png");
    on = loadImage(folder+"diamond.png");
  }
  
  public PVector getPosition(){
    return position;
  }
  
  public PImage getOn(){
    return on;
  }
  public PImage getOff(){
    return off;
  }
  
  public void setPosition(PVector position){
    if(this.position == null){
      this.position = new PVector(position.x, position.y);
    }else{
      this.position.x = position.x;
      this.position.y = position.y;
    }
  }
  
  public void updateActive(){
    if(subWidgets.size() > 0){
      for(Widget w : subWidgets){
        w.updateActive();
        if(iconIsCurrentSubWidget && w.isActive()){
          this.on = w.getOn();
          this.off = w.getOff();
        }
      }
    }
  }
}

//------------------TestSubMenuWidgets---------------------
class SubMenuWidget extends Widget{
  public SubMenuWidget(){
    Widget w1 = new BlankWidget();
    Widget w2 = new BlankWidget();
    Widget w3 = new SubMenuWidget2();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2 extends Widget{
  public SubMenuWidget2(){
    wd = widgetDirection.LEFT;
    Widget w1 = new BlankWidget();
    Widget w2 = new SubMenuWidget2Up();
    Widget w3 = new SubMenuWidget3();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}
class SubMenuWidget2Up extends Widget{
  public SubMenuWidget2Up(){
    wd = widgetDirection.UP;
    Widget w1 = new BlankWidget();
    subWidgets.add(w1);
  }
}
class SubMenuWidget3 extends Widget{
  public SubMenuWidget3(){
    Widget w1 = new BlankWidget();
    Widget w2 = new BlankWidget();
    Widget w3 = new BlankWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3);
  }
}

//------------------Menu---------------------
class MenuWidget extends Widget{
  boolean previousStatus = false;
  
  public MenuWidget(){
    off = loadImage(folder+"menuClick.png");
    on = loadImage(folder+"menu.png");
  }
  
  public void clicked(){
    if(!active){
      active = true;
      previousStatus = gPaused;
      gPaused = true; //switch pause state
      menu = new PauseMenu(this);
    }
  }
}

//------------------Settings---------------------
class SettingsWidget extends Widget{
  public SettingsWidget(){
    Widget w1 = new SuspendWidget();
    subWidgets.add(w1);
  }
}
class SuspendWidget extends Widget{
  boolean previousStatus = false;
  
  public SuspendWidget(){
    off = loadImage(folder+"PauseClick.png");
    on = loadImage(folder+"Pause.png");
  }
  
  public void clicked(){
    if(!active){
      c = new FreeCamera();
    }else{
      c = new GameCamera();
    }
  }
  
  public void updateActive(){
    if(c instanceof FreeCamera){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------ControlMode---------------------
class ControlWidget extends Widget{
  public ControlWidget(){
    iconIsCurrentSubWidget = true;
    Widget w1 = new PlayerControlWidget();
    Widget w2 = new CameraControlWidget();
    Widget w3 = new EditorControlWidget();
    subWidgets.add(w1);
    subWidgets.add(w2);
    subWidgets.add(w3); 
  }
}
class PlayerControlWidget extends Widget{
  public PlayerControlWidget(){
    off = loadImage(folder+"playerControlsClick.png");
    on = loadImage(folder+"playerControls.png");
  }
  
  public void clicked(){
    if(!active){
      con = new PlayerControl();
    }
  }
  
  public void updateActive(){
    if(con instanceof PlayerControl){
      active = true;
    }else{
      active = false;
    }
  }
}
class CameraControlWidget extends Widget{
  public CameraControlWidget(){
    off = loadImage(folder+"CameraControlsClick.png");
    on = loadImage(folder+"CameraControls.png");
  }
  
  public void clicked(){
    if(!active){
      con = new CameraControl();
    }
  }
  
  public void updateActive(){
    if(con instanceof CameraControl){
      active = true;
    }else{
      active = false;
    }
  }
}
class EditorControlWidget extends Widget{
  public EditorControlWidget(){
    off = loadImage(folder+"PlaceBlockClick.png");
    on = loadImage(folder+"PlaceBlock.png");
  }
  
  public void clicked(){
    if(!active){
      con = new EditorControl();
    }else{
      //finalise block
      if(g.point != null){
        Platform p = new Platform((int)g.point.x-50, (int)g.point.y-50, 100, 100);
        g.platforms.add(p);
        g.point = null;
      }
    }
  }
  
  public void updateActive(){
    if(con instanceof EditorControl){
      active = true;
    }else{
      active = false;
    }
  }
}

//------------------WidgetDirectionEnum---------------------
enum widgetDirection{
    DOWN,
    UP,
    LEFT,
    RIGHT
}

//------------------BlankWidget---------------------
class BlankWidget extends Widget{
  
}
