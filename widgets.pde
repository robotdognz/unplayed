//------------------Pause---------------------
class Pause implements Widget{
  Menu widgetMenu = new PauseMenu();
  PVector position;
  float pWidth, pHeight;
  PVector bottomRight;
  boolean hover = false;
  
  public Pause(){
    position = new PVector(120,120);
    pWidth = 60;
    pHeight = 60;
    bottomRight = new PVector(position.x+pWidth, position.y+pHeight);
  }
  
  public void draw(){
    if(!gPaused){ //if not paused, draw the widget
      fill(150);
      ellipseMode(CENTER);
      ellipse(position.x, position.y, pWidth, pHeight);
      ellipseMode(CORNER);
    }
  }
  
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
  
  public void click(){
    if(hover){
      gPaused = true; //switch pause state
      menu = widgetMenu;
    }
  }
}
