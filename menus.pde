//------------------PauseMenu---------------------
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
        menus.remove(this); //this is problematic
      }
    }
  }
}

//------------------Button---------------------
class Button {
  private PVector position;
  private float bWidth, bHeight;
  PVector bottomRight;
  private String text;
  private boolean hover = false;

  public Button(PVector position, float bWidth, float bHeight, String text) {
    this. position = position;
    this.bWidth = bWidth;
    this.bHeight = bHeight;
    this.text = text;
    bottomRight = new PVector(position.x+bWidth, position.y+bHeight);
  }

  public void draw() {
    //can use textWidth() to figure out how wide text is and center it
    fill(200);
    rect(position.x, position.y, bWidth, bHeight);
    fill(50);
    text(text, position.x, position.y);
  }

  public String click() {
    if (hover) {
      return text;
    } else {
      return "";
    }
  }

  public void hover(PVector lastTouch) {
    if (lastTouch.x >= position.x-bWidth && 
      lastTouch.y >= position.y-bHeight && 
      lastTouch.x <= bottomRight.x+bWidth && 
      lastTouch.y <= bottomRight.y+bHeight) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
