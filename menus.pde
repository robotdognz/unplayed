//------------------AbstractMenu---------------------
abstract class Menu {
  float menuCenterX = 0;
  float menuWidth = 0;
  float buttonHeight = 0;
  float buttonDistance = 0;
  ArrayList<Button> buttons = new ArrayList<Button>();
  
  
  public void draw() {
    fill(150);
    //rectMode(CORNERS);
    float menuHeight = buttonDistance+(buttonHeight+buttonDistance)*buttons.size();
    rect(menuCenterX+menuWidth/2, 800, menuWidth, menuHeight);
    //rectMode(CORNER);
    //for (Button b : buttons) {
    //  b.draw();
    //}
    for(int i = 0; i < buttons.size(); i++){
      float y = 800 +buttonDistance+(buttonHeight+buttonDistance)*i+buttonHeight/2;
      buttons.get(i).draw(y);
    }
  }
  
  public void hover(PVector lastTouch) {
    for (Button b : buttons) {
      b.hover(lastTouch);
    }
  }
  
  public void click() {
    
  }
}

//------------------PauseMenu---------------------
class PauseMenu extends Menu{
  String resume = "Resume";
  String test = "Test";
  
  public PauseMenu() {
    menuCenterX = width/2;
    menuWidth = width-400;
    buttonHeight = 200;
    buttonDistance = 20;
    Button r = new Button(new PVector(width/2, 1000), 500, buttonHeight, resume);
    buttons.add(r);
    Button t = new Button(new PVector(width/2, 1000), 500, buttonHeight, test);
    buttons.add(t);
  }
  
  public void click() {
    for (Button b : buttons) {
      if (b.click().equals(resume)) { //resume the game if resume button pressed
        gPaused = false; //unpause
        menu = null; //remove pause menu
      }
    }
  }
}


//------------------Button---------------------
class Button {
  private PVector bCenter;
  private float bWidth, bHeight;
  private String text;
  private boolean hover = false;

  public Button(PVector bCenter, float bWidth, float bHeight, String text) {
    this.bCenter = bCenter;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.text = text;
  }

  public void draw(float y) {
    //can use textWidth() to figure out how wide text is and center it
    if(!hover){
      fill(200);
    }else{
      fill(100);
    }
    rectMode(CENTER);
    rect(bCenter.x, y, bWidth, bHeight);
    rectMode(CORNER);
    fill(50);
    textSize(60);
    textAlign(CENTER, CENTER);
    text(text, bCenter.x, bCenter.y);
    //text(text, bCenter.x-bWidth/2, bCenter.y-bHeight/2, bCenter.x+bWidth/2, bCenter.y+bHeight/2);
  }

  public String click() {
    if (hover) {
      return text;
    } else {
      return "";
    }
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= bCenter.x-bWidth/2 && 
      lastTouch.y >= bCenter.y-bHeight/2 && 
      lastTouch.x <= bCenter.x+bWidth/2 && 
      lastTouch.y <= bCenter.y+bHeight/2) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
