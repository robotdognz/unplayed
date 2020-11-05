//------------------PauseMenu---------------------
class PauseMenu implements Menu {
  ArrayList<Button> buttons = new ArrayList<Button>();
  String resume = "Resume";

  public PauseMenu() {
    Button r = new Button(new PVector(500, 500), 200, 100, resume);
    buttons.add(r);
  }

  public void draw() {
    fill(150);
    rect(200, 200, 800, 800);
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
        menus.clear();
      }
    }
  }
}

//probably should use a button object that can be passed 
//can use textWidth() to figure out how wide text is and center it

class Button {
  private PVector position;
  private float width, height;
  PVector bottomRight;
  private String text;
  private boolean hover = false;

  public Button(PVector position, float width, float height, String text) {
    this. position = position;
    this.width = width;
    this.height = height;
    this.text = text;
    bottomRight = new PVector(position.x+width, position.y+height);
  }

  public void draw() {
    fill(200);
    rect(position.x, position.y, width, height);
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
    if (lastTouch.x >= position.x-width && 
      lastTouch.y >= position.y-height && 
      lastTouch.x <= bottomRight.x+width && 
      lastTouch.y <= bottomRight.y+height) {
      hover = true;
    } else {
      hover = false;
    }
  }
}
