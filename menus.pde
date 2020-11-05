//------------------PauseMenu---------------------
//abstract class might work better for menus
//might be able to bake in drawing code
class PauseMenu implements Menu {
  ArrayList<Button> buttons = new ArrayList<Button>();
  String resume = "Resume";

  public PauseMenu() {
    Button r = new Button(new PVector(500, 1000), 500, 200, resume);
    buttons.add(r);
  }

  public void draw() {
    fill(150);
    rectMode(CORNERS);
    rect(200, 800, width-200, height-800);
    rectMode(CORNER);
    for (Button b : buttons) {
      b.draw();
    }
  }

  public void hover(PVector lastTouch) {
    for (Button b : buttons) {
      b.hover(lastTouch);
    }
  }

  public void click() {
    for (Button b : buttons) {
      if (b.click().equals(resume)) { //resume the game if resume button pressed
        gPaused = false;
        menu = null; //this is problematic
      }
    }
  }
}

//------------------Button---------------------
class Button {
  private PVector bPosition;
  private float bWidth, bHeight;
  PVector bottomRight;
  private String text;
  private boolean hover = false;

  public Button(PVector position, float bWidth, float bHeight, String text) {
    this.bPosition = position;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.text = text;
    bottomRight = new PVector(bPosition.x+bWidth, bPosition.y+bHeight);
  }

  public void draw() {
    //can use textWidth() to figure out how wide text is and center it
    if(!hover){
      fill(200);
    }else{
      fill(100);
    }
    rectMode(CENTER);
    rect(width/2, bPosition.y, bWidth, bHeight);
    rectMode(CORNER);
    fill(50);
    textSize(60);
    textAlign(CENTER, CENTER);
    text(text, width/2, bPosition.y);
  }

  public String click() {
    if (hover) {
      return text;
    } else {
      return "";
    }
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= bPosition.x && 
      lastTouch.y >= bPosition.y && 
      lastTouch.x <= bottomRight.x && 
      lastTouch.y <= bottomRight.y) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
