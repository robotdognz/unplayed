public class Quadtree2 {
  private QuadNode root;
  //private ArrayList<Rectangle> backup;    //probably a shitty way to do this

  public Quadtree2(Rectangle bounds) {
    root = new QuadNode(bounds, null, this); //top level node has null for parent
    //backup = new ArrayList<Rectangle>();
  }

  public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, Rectangle player) {
    root.retrieve(returnObjects, player);
    return returnObjects;
  }

  public void insert(Rectangle current) {
    root.insert(current);
    //backup.add(current);
  }

  public void remove(Rectangle current) {
    root.remove(current);
    //backup.remove(current);
  }

  public void setRoot(QuadNode newRoot) {
    this.root = newRoot;
  }

  //public void rebuild(){
  //  for(Rectangle r : backup){
  //    insertRebuild(r);
  //  }
  //}

  //private void insertRebuild(Rectangle current){
  //  root.insert(current);
  //}

  public void draw() {
    root.draw();
  }
}

public class QuadNode {
  private int MAX_OBJECTS = 20;

  QuadNode parent;
  Quadtree2 tree;

  Rectangle bounds;
  ArrayList<Rectangle> objects;

  QuadNode topLeft = null; //null so we can check if this node has been split
  QuadNode topRight;
  QuadNode bottomLeft;
  QuadNode bottomRight;

  public QuadNode(Rectangle bounds, QuadNode parent, Quadtree2 tree) { //top down constructor
    this.parent = parent;
    this.bounds = bounds;
    this.objects = new ArrayList<Rectangle>();
    this.tree = tree;
  }

  public QuadNode(Rectangle bounds, QuadNode parent, Quadtree2 tree, ArrayList<Rectangle> add) { //top down constructor and add rectangles
    this.parent = parent;
    this.bounds = bounds;
    this.objects = new ArrayList<Rectangle>();
    this.tree = tree;
    for (Rectangle r : add) {
      insert(r);
    }
  }

  public void addNodes(QuadNode topLeft, QuadNode topRight, QuadNode bottomLeft, QuadNode bottomRight) { //bottom up constructor
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
    //check what direction to grow in and make the three new leaf nodes passing them the rectangles that overlap
    //construct the new root node with null for the parent and pass it this and the new leaves
    //set this.parent to the new root node
    //insert current into the parent - this is the recursive step


    float bWidth = bounds.getWidth();
    float bHeight = bounds.getHeight();

    Rectangle newBounds;
    QuadNode newTopLeft;
    QuadNode newTopRight;
    QuadNode newBottomLeft;
    QuadNode newBottomRight;

    // If object is left of this node
    if (current.getX() < bounds.getX()) {
      // If object is to the top of this node
      if (current.getY() < bounds.getY()) {
        // Grow towards top left
        newBounds = new Rectangle(bounds.getX()-bWidth, bounds.getY()-bHeight, bWidth*2, bHeight*2);
        QuadNode newRoot = new QuadNode(newBounds, null, tree);
        this.parent = newRoot;
        tree.setRoot(newRoot);
        
        Rectangle topLeft = new Rectangle(bounds.getX()-bWidth, bounds.getY()-bHeight, bWidth, bHeight);
        Rectangle topRight = new Rectangle(bounds.getX(), bounds.getY()-bHeight, bWidth, bHeight);
        Rectangle bottomLeft = new Rectangle(bounds.getX()-bWidth, bounds.getY(), bWidth, bHeight);
        
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
        
        newTopLeft.insert(current);
      } else {
        // Grow towards bottom left
        
      }
      // If object is right of this node
    } else if (current.getX() > (bounds.getX() + bounds.getWidth())) {
      // If object is to the top of this node
      if (current.getY() < bounds.getY()) {
        // Grow towards top right
        
      } else {
        // Grow towards bottom right
        
      }


      // If object is within x-axis but top of node
    } else if (current.getY() < bounds.getY()) {
      // Grow towards top right (top left is just as valid though)

      // If object is within x-axis but bottom of node
    } else if (current.getY() + current.getHeight() > bounds.getY() + bounds.getHeight()) {
      // Grow towards bottom right (bottom left is just as valid though)
    }

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
