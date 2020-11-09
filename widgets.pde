//------------------AbstractWidget---------------------
abstract class Widget{
  protected PVector position;
  protected float pWidth, pHeight;
  protected boolean hover = false;
  
  public void hover(PVector lastTouch){
    if (lastTouch.x >= position.x-pWidth*1.5 && 
    lastTouch.y >= position.y-pHeight*1.5 && 
    lastTouch.x <= position.x+pWidth*1.5 && 
    lastTouch.y <= position.y+pHeight*1.5) {
      hover = true;
    }else{
      hover = false;
    }
  }
  
  public void draw(){}
  public void click(){}
}

//------------------Pause---------------------
class Pause extends Widget{
  
  public Pause(){
    position = new PVector(120,120);
    pWidth = 60;
    pHeight = 60;
  }
  
  public void draw(){
    if(menu == null || !(menu instanceof PauseMenu)){ //if pause menu is closed, draw the widget
      fill(150);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, pWidth, pHeight);
      ellipseMode(CORNER);
    }
  }
  
  public void click(){
    if(hover){
      gPaused = true; //switch pause state
      menu = new PauseMenu();
      con = new BlankControl();
    }
  }
}
