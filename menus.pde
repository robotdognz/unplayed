//------------------AbstractMenu---------------------
abstract class Menu {
  protected float menuTopY = 0;
  protected float menuCenterX = 0;
  protected float menuWidth = 0;
  protected float buttonHeight = 0;
  protected float buttonDistance = 0;
  protected ArrayList<Button> buttons = new ArrayList<Button>();
  protected float menuHeight = 0;
  
  protected void height(){
    menuHeight = buttonDistance+(buttonHeight+buttonDistance)*buttons.size();
  }
  
  public void draw() {
    fill(150);
    rect(menuCenterX-menuWidth/2, menuTopY, menuWidth, menuHeight);
    //draw the buttons
    for(int i = 0; i < buttons.size(); i++){
      float y = menuTopY+buttonDistance+(buttonHeight+buttonDistance)*i+buttonHeight/2;
      buttons.get(i).draw(y);
    }
  }
  
  public void hover(PVector lastTouch) {
    for (Button b : buttons) {
      b.hover(lastTouch);
    }
  }
  
  public void click() {}
}

//------------------PauseMenu---------------------
class PauseMenu extends Menu{
  MenuWidget m;
  String resume = "Resume";
  String editor = "Toggle Editor";
  
  public PauseMenu(MenuWidget m) {
    this.m = m;
    menuCenterX = width/2;
    menuWidth = 660;
    buttonHeight = 200;
    buttonDistance = 80;
    Button resumeB = new Button(width/2, 500, buttonHeight, resume);
    buttons.add(resumeB);
    Button editorB = new Button(width/2, 500, buttonHeight, editor);
    buttons.add(editorB);
    height();
    menuTopY = height/2-menuHeight/2;
  }
  
  public void click() {
    for (Button b : buttons) {
      if (b.click().equals(resume)) { //resume the game if resume button pressed
        m.active = false; //change status of pause widget
        gPaused = m.previousStatus;
        menu = null; //remove pause menu
      }else if(b.click().equals(editor)){
        editorToggle = !editorToggle;
        m.active = false; //change status of pause widget
        gPaused = m.previousStatus;
        menu = null; //remove pause menu
        if(!editorToggle){
          c = new GameCamera();
          con = new GameControl();
        }
      }
    }
  }
}

//------------------Button---------------------
class Button {
  //private PVector bCenter;
  private float xCenter;
  private float yCenter = 0;
  private float bWidth, bHeight;
  private String text;
  private boolean hover = false;

  public Button(float xCenter, float bWidth, float bHeight, String text) {
    this.xCenter = xCenter;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.text = text;
  }

  public void draw(float y) {
    //can use textWidth() to figure out how wide text is and center it
    yCenter = y;
    if(!hover){
      fill(200);
    }else{
      fill(100);
    }
    rectMode(CENTER);
    rect(xCenter, yCenter, bWidth, bHeight);
    rectMode(CORNER);
    fill(50);
    textSize(60);
    textAlign(CENTER, CENTER);
    text(text, xCenter, yCenter);
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
    if (lastTouch.x >= xCenter-bWidth/2 && 
      lastTouch.y >= yCenter-bHeight/2 && 
      lastTouch.x <= xCenter+bWidth/2 && 
      lastTouch.y <= yCenter+bHeight/2) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
