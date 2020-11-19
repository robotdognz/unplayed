public class Quadtree {
  private QuadNode root;

  public Quadtree(Rectangle bounds) {
    root = new QuadNode(bounds, null, this); //top level node has null for parent
  }

  public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, Rectangle player) {
    root.retrieve(returnObjects, player);
    return returnObjects;
  }

  public void insert(Rectangle current) {
    root.insert(current);
  }

  public void remove(Rectangle current) {
    root.remove(current);
  }

  public void setRoot(QuadNode newRoot) {
    this.root = newRoot;
  }

  public void draw() {
    root.draw();
  }

  public int size() {
    //root.size();
    return 0;
  }
}

public class QuadNode {
  private int MAX_OBJECTS = 20;

  QuadNode parent;
  Quadtree tree;

  Rectangle bounds;
  ArrayList<Rectangle> objects;

  QuadNode topLeft = null; //null so we can check if this node has been split
  QuadNode topRight;
  QuadNode bottomLeft;
  QuadNode bottomRight;

  public QuadNode(Rectangle bounds, QuadNode parent, Quadtree tree) { 
    this.parent = parent;
    this.bounds = bounds;
    this.objects = new ArrayList<Rectangle>();
    this.tree = tree;
  }

  public void addNodes(QuadNode topLeft, QuadNode topRight, QuadNode bottomLeft, QuadNode bottomRight) {
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
  }

  public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, Rectangle player) {
    if (insideBounds(player)) { //if the player is inside the bounds of this quadnode
      if (topLeft != null) { //if this node has branches
        topLeft.retrieve(returnObjects, player);
        topRight.retrieve(returnObjects, player);
        bottomLeft.retrieve(returnObjects, player);
        bottomRight.retrieve(returnObjects, player);
      } else { //else return the objects from this node
        returnObjects.addAll(objects);
      }
    }
    return returnObjects;
  }

  public void insert(Rectangle current) {
    if (insideBounds(current)) { //if it is inside the current node bounds
      if (topLeft != null) {
        topLeft.insert(current);
        topRight.insert(current);
        bottomLeft.insert(current);
        bottomRight.insert(current);
      } else {
        objects.add(current);
        if (objects.size() < MAX_OBJECTS) { //if this node can take it without splitting
          return;
        } else { //else split
          split();
        }
      }
    } else { //if it is outside the current node bounds
      //check if this is the root
      if (parent == null) { //if it is grow
        grow(current);
      }
    }
  }

  public void remove(Rectangle current) {
    if (insideBounds(current)) { //if it is inside the current node bounds
      if (topLeft != null) {
        topLeft.remove(current);
        topRight.remove(current);
        bottomLeft.remove(current);
        bottomRight.remove(current);
      } else {
        for (int i = 0; i < objects.size(); i++) {
          if (current.equals(objects.get(i)) || objects.get(i).getX() == current.getX() && objects.get(i).getY() == current.getY()) {
            objects.remove(i);
          }
        }
      }
    }
  }

  private void split() {
    float subWidth = bounds.getWidth() / 2;
    float subHeight = bounds.getHeight() / 2;
    float x = bounds.getX();
    float y = bounds.getY();
    topLeft = new QuadNode(new Rectangle(x, y, subWidth, subHeight), this, tree);
    topRight = new QuadNode(new Rectangle(x+subWidth, y, subWidth, subHeight), this, tree);
    bottomLeft = new QuadNode(new Rectangle(x, y+subHeight, subWidth, subHeight), this, tree);
    bottomRight = new QuadNode(new Rectangle(x+subWidth, y+subHeight, subWidth, subHeight), this, tree);
    for (Rectangle r : objects) {
      topLeft.insert(r);
      topRight.insert(r);
      bottomLeft.insert(r);
      bottomRight.insert(r);
    }
    objects.clear();
  }

  private void grow(Rectangle current) {
    // If object is left of this node
    if (current.getX() < bounds.getX()) {
      // If object is to the top of this node
      if (current.getY() < bounds.getY()) {
        // Grow towards top left
        growTopLeft(current);
      } else {
        // Grow towards bottom left
        growBottomLeft(current);
      }
      // If object is right of this node
    } else if (current.getX() > (bounds.getX() + bounds.getWidth())) {
      // If object is to the top of this node
      if (current.getY() < bounds.getY()) {
        // Grow towards top right
        growTopRight(current);
      } else {
        // Grow towards bottom right
        growBottomRight(current);
      }

      // If object is within x-axis but top of node
    } else if (current.getY() < bounds.getY()) {
      // Grow towards top right (top left is just as valid though)
      growTopRight(current);

      // If object is within x-axis but bottom of node
    } else if (current.getY() + current.getHeight() > bounds.getY() + bounds.getHeight()) {
      // Grow towards bottom right (bottom left is just as valid though)
      growBottomRight(current);
    }
  }

  private void growTopLeft(Rectangle current) {
    float bWidth = bounds.getWidth();
    float bHeight = bounds.getHeight();

    Rectangle newBounds;
    QuadNode newTopLeft;
    QuadNode newTopRight;
    QuadNode newBottomLeft;
    QuadNode newBottomRight;

    newBounds = new Rectangle(bounds.getX()-bWidth, bounds.getY()-bHeight, bWidth*2, bHeight*2);
    QuadNode newRoot = new QuadNode(newBounds, null, tree);
    this.parent = newRoot;
    tree.setRoot(newRoot);

    Rectangle topLeft = new Rectangle(newBounds.getX(), newBounds.getY(), bWidth, bHeight);
    Rectangle topRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY(), bWidth, bHeight);
    Rectangle bottomLeft = new Rectangle(newBounds.getX(), newBounds.getY()+bHeight, bWidth, bHeight);

    newTopLeft = new QuadNode(topLeft, newRoot, tree);
    newTopRight = new QuadNode(topRight, newRoot, tree);
    newBottomLeft = new QuadNode(bottomLeft, newRoot, tree);
    newBottomRight = this;
    newRoot.addNodes(newTopLeft, newTopRight, newBottomLeft, newBottomRight);

    //add existing overlapping rectangles to the new leavs
    ArrayList<Rectangle> toAdd = new ArrayList<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    newTopLeft.insert(current);
  }
  private void growTopRight(Rectangle current) {
    float bWidth = bounds.getWidth();
    float bHeight = bounds.getHeight();

    Rectangle newBounds;
    QuadNode newTopLeft;
    QuadNode newTopRight;
    QuadNode newBottomLeft;
    QuadNode newBottomRight;

    newBounds = new Rectangle(bounds.getX(), bounds.getY()-bHeight, bWidth*2, bHeight*2);
    QuadNode newRoot = new QuadNode(newBounds, null, tree);
    this.parent = newRoot;
    tree.setRoot(newRoot);

    Rectangle topLeft = new Rectangle(newBounds.getX(), newBounds.getY(), bWidth, bHeight);
    Rectangle topRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY(), bWidth, bHeight);
    Rectangle bottomRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY()+bHeight, bWidth, bHeight);

    newTopLeft = new QuadNode(topLeft, newRoot, tree);
    newTopRight = new QuadNode(topRight, newRoot, tree);
    newBottomLeft = this;
    newBottomRight = new QuadNode(bottomRight, newRoot, tree);
    newRoot.addNodes(newTopLeft, newTopRight, newBottomLeft, newBottomRight);

    //add existing overlapping rectangles to the new leavs
    ArrayList<Rectangle> toAdd = new ArrayList<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomRight);

    //insert the new rectangle
    newTopLeft.insert(current);
  }
  private void growBottomLeft(Rectangle current) {
    float bWidth = bounds.getWidth();
    float bHeight = bounds.getHeight();

    Rectangle newBounds;
    QuadNode newTopLeft;
    QuadNode newTopRight;
    QuadNode newBottomLeft;
    QuadNode newBottomRight;

    newBounds = new Rectangle(bounds.getX()-bWidth, bounds.getY(), bWidth*2, bHeight*2);
    QuadNode newRoot = new QuadNode(newBounds, null, tree);
    this.parent = newRoot;
    tree.setRoot(newRoot);

    Rectangle topLeft = new Rectangle(newBounds.getX(), newBounds.getY(), bWidth, bHeight);
    Rectangle bottomLeft = new Rectangle(newBounds.getX(), newBounds.getY()+bHeight, bWidth, bHeight);
    Rectangle bottomRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY()+bHeight, bWidth, bHeight);

    newTopLeft = new QuadNode(topLeft, newRoot, tree);
    newTopRight = this;
    newBottomLeft = new QuadNode(bottomLeft, newRoot, tree);
    newBottomRight = new QuadNode(bottomRight, newRoot, tree);
    newRoot.addNodes(newTopLeft, newTopRight, newBottomLeft, newBottomRight);

    //add existing overlapping rectangles to the new leavs
    ArrayList<Rectangle> toAdd = new ArrayList<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, bottomLeft);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    newTopLeft.insert(current);
  }
  private void growBottomRight(Rectangle current) {
    float bWidth = bounds.getWidth();
    float bHeight = bounds.getHeight();

    Rectangle newBounds;
    QuadNode newTopLeft;
    QuadNode newTopRight;
    QuadNode newBottomLeft;
    QuadNode newBottomRight;

    newBounds = new Rectangle(bounds.getX(), bounds.getY(), bWidth*2, bHeight*2);
    QuadNode newRoot = new QuadNode(newBounds, null, tree);
    this.parent = newRoot;
    tree.setRoot(newRoot);

    Rectangle topRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY(), bWidth, bHeight);
    Rectangle bottomLeft = new Rectangle(newBounds.getX(), newBounds.getY()+bHeight, bWidth, bHeight);
    Rectangle bottomRight = new Rectangle(newBounds.getX()+bWidth, newBounds.getY()+bHeight, bWidth, bHeight);

    newTopLeft = this;
    newTopRight = new QuadNode(topRight, newRoot, tree);
    newBottomLeft = new QuadNode(bottomLeft, newRoot, tree);
    newBottomRight = new QuadNode(bottomRight, newRoot, tree);
    newRoot.addNodes(newTopLeft, newTopRight, newBottomLeft, newBottomRight);

    //add existing overlapping rectangles to the new leavs
    ArrayList<Rectangle> toAdd = new ArrayList<Rectangle>();
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomLeft);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    newTopLeft.insert(current);
  }

  private boolean insideBounds(Rectangle current) {
    if (current.getBottomRight().x >= bounds.getTopLeft().x && 
      current.getTopLeft().x <= bounds.getBottomRight().x && 
      current.getBottomRight().y >= bounds.getTopLeft().y &&
      current.getTopLeft().y <= bounds.getBottomRight().y) {
      return true;
    }
    return false;
  }

  public void draw() {
    if (topLeft != null) {
      topLeft.draw();
      topRight.draw();
      bottomLeft.draw();
      bottomRight.draw();
    } else {
      noFill();
      stroke(0);
      strokeWeight(10);
      rect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
      noStroke();
    }
  }
}


//------------------Rectangle---------------------
public class Rectangle {
  private float rWidth, rHeight;
  private PVector topLeft, bottomRight;

  public Rectangle(float x, float y, float rWidth, float rHeight) {
    this.rWidth = rWidth;
    this.rHeight = rHeight;
    this.topLeft = new PVector(x, y);
    this.bottomRight = new PVector(x+rWidth, y+rHeight);
  }

  //getters
  public float getX() {
    return topLeft.x;
  }
  public float getY() {
    return topLeft.y;
  }
  public float getWidth() {
    return rWidth;
  }
  public float getHeight() {
    return rHeight;
  }
  public PVector getTopLeft() {
    return topLeft;
  }
  public PVector getBottomRight() {
    return bottomRight;
  }

  //setters
  public void setX(float x) {
    this.topLeft.x = x;
    this.bottomRight.x = x+rWidth;
  }
  public void setY(float y) {
    this.topLeft.y = y;
    this.bottomRight.y = y+rHeight;
  }
  public void setWidth(float rWidth) {
    this.rWidth = rWidth;
    this.bottomRight.x = this.topLeft.x+rWidth;
  }
  public void setHeight(float rHeight) {
    this.rHeight = rHeight;
    this.bottomRight.y = this.topLeft.y+rHeight;
  }
  public void setPosition(PVector newPosition) {
    setX(newPosition.x);
    setY(newPosition.y);
  }
}
