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
  
  public String toString(){
    return this.getName() + " " + (int)getX() + " " + (int)getY() + " " + (int)getWidth() + " " + (int)getHeight();
  }
  
  public String getName(){
    return "Rectangle";
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

public class Quadtree {
  private Rectangle bounds;
  private QuadNode root;
  private int insertCount = 0;

  public Quadtree(Rectangle bounds) {
    this.bounds = bounds;
    root = new QuadNode(bounds, null, this); //top level node has null for parent
  }

  public HashSet<Rectangle> retrieve(HashSet<Rectangle> returnObjects, Rectangle player) {
    root.retrieve(returnObjects, player);
    return returnObjects;
  }

  public void insert(Rectangle current) {
    insertCount++;
    root.nodeInsert(current);
  }

  public void remove(Rectangle current) {
    insertCount--;
    root.remove(current);
  }

  public void setRoot(QuadNode newRoot) {
    this.root = newRoot;
  }

  public void draw() {
    root.draw();
  }

  public void clear() {
    insertCount = 0;
    root = new QuadNode(bounds, null, this);
  }

  public int size() {
    HashSet<Rectangle> count = new HashSet<Rectangle>();
    getAll(count);
    return count.size();
  }

  public int insertCount() {
    return insertCount;
  }

  public HashSet<Rectangle> getAll(HashSet<Rectangle> returnSet) {
    root.getAll(returnSet);
    return returnSet;
  }
}

public class QuadNode {
  private int MAX_OBJECTS = 20;

  QuadNode parent;
  Quadtree tree;

  Rectangle bounds;
  HashSet<Rectangle> objects;

  QuadNode topLeft = null; //null so we can check if this node has been split
  QuadNode topRight;
  QuadNode bottomLeft;
  QuadNode bottomRight;

  public QuadNode(Rectangle bounds, QuadNode parent, Quadtree tree) { 
    this.parent = parent;
    this.bounds = bounds;
    this.objects = new HashSet<Rectangle>();
    this.tree = tree;
  }

  public void addNodes(QuadNode topLeft, QuadNode topRight, QuadNode bottomLeft, QuadNode bottomRight) {
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
  }

  public HashSet<Rectangle> retrieve(HashSet<Rectangle> returnObjects, Rectangle player) {
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

  public HashSet<Rectangle> getAll(HashSet<Rectangle> returnSet) {
    if (topLeft != null) {
      topLeft.getAll(returnSet);
      topRight.getAll(returnSet);
      bottomLeft.getAll(returnSet);
      bottomRight.getAll(returnSet);
    } else {
      returnSet.addAll(objects);
    }
    return returnSet; //return the set with all the rectangles in the tree
  }

  private boolean insideBounds(Rectangle current) {
    if (current.getTopLeft().x > bounds.getBottomRight().x) {
      return false;
    }
    if (current.getBottomRight().x < bounds.getTopLeft().x) {
      return false;
    }
    if (current.getTopLeft().y > bounds.getBottomRight().y) {
      return false;
    }
    if (current.getBottomRight().y < bounds.getTopLeft().y) {
      return false;
    }
    return true;
  }

  public void nodeInsert(Rectangle current) {
    if (insideBounds(current)) { //if it is inside the current node bounds
      if (topLeft != null) {
        topLeft.nodeInsert(current);
        topRight.nodeInsert(current);
        bottomLeft.nodeInsert(current);
        bottomRight.nodeInsert(current);
      } else {
        objects.add(current); //add the new rectangle
        if (objects.size() < MAX_OBJECTS) { //if this node can take it without splitting
          return;
        } else { //else split
          split();
        }
      }
    } else { //if it is outside the current node bounds
      //check if this is the root
      if (parent == null) { //if it is the root, grow
        grow(current);
      }
    }
  }

  public void remove(Rectangle current) {
    if (insideBounds(current)) { //if it is inside the current node bounds
      if (topLeft != null) { //if this node has children
        topLeft.remove(current);
        topRight.remove(current);
        bottomLeft.remove(current);
        bottomRight.remove(current);

        //This only removes inner nodes, it doesn't remove outer nodes, but that's probably fine
        HashSet<Rectangle> allBelow = new HashSet<Rectangle>();
        getAll(allBelow);
        if (allBelow.size() < MAX_OBJECTS) {
          objects.clear();
          objects = allBelow;
          topLeft = null;
          topRight = null;
          bottomLeft = null;
          bottomRight = null;
        }
      } else { //if this node doesn't have children
        ArrayList<Rectangle> matches = new ArrayList<Rectangle>();
        for (Rectangle r : objects) { //find all matching rectangles
          if (current.equals(r)) { // || r.getX() == current.getX() && r.getY() == current.getY()) { //TODO: this needs to be cleaned up, probably doesn't need to make a list and go throught it
            matches.add(r);
          }
        }
        for (Rectangle r : matches) { //remove them
          objects.remove(r);
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
      topLeft.nodeInsert(r);
      topRight.nodeInsert(r);
      bottomLeft.nodeInsert(r);
      bottomRight.nodeInsert(r);
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
    HashSet<Rectangle> toAdd = new HashSet<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    tree.root.nodeInsert(current);

    //add overlaping rectangles
    for (Rectangle r : toAdd) {
      tree.root.nodeInsert(r);
    }
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
    HashSet<Rectangle> toAdd = new HashSet<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomRight);

    //insert the new rectangle
    tree.root.nodeInsert(current);

    //add overlaping rectangles
    for (Rectangle r : toAdd) {
      tree.root.nodeInsert(r);
    }
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
    HashSet<Rectangle> toAdd = new HashSet<Rectangle>();
    retrieve(toAdd, topLeft);
    retrieve(toAdd, bottomLeft);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    tree.root.nodeInsert(current);

    //add overlaping rectangles
    for (Rectangle r : toAdd) {
      tree.root.nodeInsert(r);
    }
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
    HashSet<Rectangle> toAdd = new HashSet<Rectangle>();
    retrieve(toAdd, topRight);
    retrieve(toAdd, bottomLeft);
    retrieve(toAdd, bottomLeft);

    //insert the new rectangle
    tree.root.nodeInsert(current);

    //add overlaping rectangles
    for (Rectangle r : toAdd) {
      tree.root.nodeInsert(r);
    }
  }

  public void draw() {
    //draw the bounds of all the nodes in the tree
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
