public class Quadtree {

  private int MAX_OBJECTS = 10;
  private int MAX_LEVELS = 5; //I tried 2

  private int level;
  private List<Rectangle> objects;
  private Rectangle bounds;
  private Quadtree[] nodes;
  
  private ArrayList<Integer> indices = new ArrayList<Integer>();

  /*
  * Constructor
   */
  public Quadtree(int pLevel, Rectangle pBounds) {
    level = pLevel;
    objects = new ArrayList<Rectangle>();
    bounds = pBounds;
    nodes = new Quadtree[4];
  }

  /*
 * Clears the quadtree and redefines is boundary
   */
  public void clearReset(Rectangle newBounds) {
    clear();
    bounds = newBounds;
  }

  /*
 * Clears the quadtree
   */
  public void clear() {
    objects.clear();

    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i] != null) {
        nodes[i].clear();
        nodes[i] = null;
      }
    }
  }

  /*
 * Splits the node into 4 subnodes
   */
  private void split() {
    int subWidth = (int)(bounds.getWidth() / 2);
    int subHeight = (int)(bounds.getHeight() / 2);
    int x = (int)bounds.getX();
    int y = (int)bounds.getY();

    nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
    nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
    nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
    nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
  }

  /*
 * Determine which node the object belongs to. -1 means
   * object cannot completely fit within a child node and is part
   * of the parent node
   */
  private int getIndex(Rectangle pRect) {
    //offset blocks should stay in the root of the tree
    if(pRect.getX()%100 != 0 || pRect.getY()%100 != 0){
      return -1;
    }

    int index = -1;
    double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
    double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

    // Object can completely fit within the top quadrants
    boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
    // Object can completely fit within the bottom quadrants
    boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

    // Object can completely fit within the left quadrants
    if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
      if (topQuadrant) {
        index = 1;
      } else if (bottomQuadrant) {
        index = 2;
      }
    }
    // Object can completely fit within the right quadrants
    else if (pRect.getX() > verticalMidpoint) {
      if (topQuadrant) {
        index = 0;
      } else if (bottomQuadrant) {
        index = 3;
      }
    }

    return index;
  }


  /*
 * Insert the object into the quadtree. If the node
   * exceeds the capacity, it will split and add all
   * objects to their corresponding nodes.
   */
  public void insert(Rectangle pRect) {
    if (nodes[0] != null) {
      int index = getIndex(pRect);

      if (index != -1) {
        nodes[index].insert(pRect);

        return;
      }
    }

    objects.add(pRect);

    if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
      if (nodes[0] == null) { 
        split();
      }

      int i = 0;
      while (i < objects.size()) {
        int index = getIndex(objects.get(i));
        if (index != -1) {
          nodes[index].insert(objects.remove(i));
        } else {
          i++;
        }
      }
    }
  }

  private ArrayList<Integer> getIndices(Rectangle pRect) {
    indices.clear();
    //int index = -1;
    double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
    double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

    // Object can completely fit within the top quadrants
    boolean topQuadrant = (pRect.getY() < horizontalMidpoint);
    // Object can completely fit within the bottom quadrants
    boolean bottomQuadrant = (pRect.getY()+pRect.getHeight() > horizontalMidpoint);

    // Object can completely fit within the left quadrants
    if (pRect.getX() < verticalMidpoint) {
      if (topQuadrant) {
        indices.add(1);
      }
      if (bottomQuadrant) {
        indices.add(2);
      }
    }
    // Object can completely fit within the right quadrants
    if (pRect.getX()+pRect.getWidth() > verticalMidpoint) {
      if (topQuadrant) {
        indices.add(0);
      }
      if (bottomQuadrant) {
        indices.add(3);
      }
    }

    return indices;
  }

  /*
 * Return all objects that could collide with the given object
   */
  public ArrayList<Rectangle> retrieve(ArrayList<Rectangle> returnObjects, Rectangle pRect) {
    ArrayList<Integer> indices = getIndices(pRect);
    if (nodes[0] != null) {
      for(Integer i : indices){
         nodes[i].retrieve(returnObjects, pRect);
      }
    }

    returnObjects.addAll(objects);

    return returnObjects;
    //int index2 = getIndexRetrieve(pRect);
    //if (index2 != -1 && nodes[0] != null) {
    //  nodes[index2].retrieve(returnObjects, pRect);
    //}

    //returnObjects.addAll(objects);

    //return returnObjects;
  }

  //my custom version of retrieve that uses the center point of the rectangle being checked
  //private int getIndexRetrieve(Rectangle pRect) {
  //  int index = -1;
  //  double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
  //  double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);

  //  // Object midpoint is in the top quadrants
  //  boolean topQuadrant = (pRect.getY()+pRect.getHeight()/2 < horizontalMidpoint);
  //  // Object midpoint is in the bottom quadrants
  //  boolean bottomQuadrant = (pRect.getY()+pRect.getHeight()/2 > horizontalMidpoint);

  //  // Object midpoint is in the left quadrants
  //  if (pRect.getX()+pRect.getWidth()/2 < verticalMidpoint) {
  //    if (topQuadrant) {
  //      index = 1;
  //    } else if (bottomQuadrant) {
  //      index = 2;
  //    }
  //  }
  //  // Object midpoint is in the right quadrants
  //  else if (pRect.getX()+pRect.getWidth()/2 > verticalMidpoint) {
  //    if (topQuadrant) {
  //      index = 0;
  //    } else if (bottomQuadrant) {
  //      index = 3;
  //    }
  //  }

  //  return index;
  //}
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
