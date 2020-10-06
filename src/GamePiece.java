import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import javalib.worldimages.AlignModeX;
import javalib.worldimages.AlignModeY;
import javalib.worldimages.FromFileImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.OverlayOffsetAlign;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.StarImage;
import javalib.worldimages.WorldImage;

class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  // list of reachable pieces from this piece
  ArrayList<GamePiece> reachable;
  // radius from power station
  int radius;
  // maximum distance power station can reach
  int maxPowerRadius;
  ArrayList<GamePiece> connected;


  // constructor
  GamePiece(int row, int col, boolean left, boolean right, boolean top, boolean bottom) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.reachable = new ArrayList<GamePiece>();
    this.radius = 0;
    this.connected = new ArrayList<GamePiece>();
    this.initConnected();
  }

  // rotates game piece 90 degrees right
  void rotate() {

    boolean ogLeft = this.left;
    boolean ogTop = this.top;
    boolean ogRight = this.right;
    boolean ogBottom = this.bottom;

    this.top = ogLeft;
    this.right = ogTop;
    this.bottom = ogRight;
    this.left = ogBottom;

  }

  boolean samePiece(GamePiece gp) {
    return this.row == gp.row && this.col == gp.col;
  }

  boolean existsIn(ArrayList<GamePiece> list) {
    for (GamePiece gp : list) {
      if (this.samePiece(gp)) {
        return true;
      }
    }
    return false;
  }

  boolean alreadyContains(GamePiece i) {
    return i.existsIn(this.connected);
  }

  void initConnected() {
    this.connected.add(this);
  }

  void updateConnected(GamePiece gp) {
    for (GamePiece i : gp.connected) {
      if (!i.existsIn(this.connected)) {
        this.connected.add(i);
      }
    }
    for (GamePiece g : this.connected) {
      if (!gp.existsIn(g.connected)) {
        g.updateConnected(gp);
      }
    }
  }

  void randomRotate(int num) {
    int cur = 0;
    while (cur < num) {
      this.rotate();
      cur++;
    }
  }

  Index asIndex() {
    return new Index(this.col, this.row);
  }

  // determines the path color based on the radius
  Color pathColor() {
    if (this.radius <= this.maxPowerRadius * .2) {
      return Color.yellow;
    }
    else if (this.radius <= this.maxPowerRadius * .3) {
      return new Color(204, 204, 0);
    }
    else if (this.radius <= this.maxPowerRadius * .7) {
      return new Color(153, 153, 0);
    }
    else if (this.radius <= this.maxPowerRadius) {
      return new Color(102, 102, 0);
    }
    else {
      return new Color(0, 19, 76);
    }
  }

  // sets the maximum distance reachable from the powerCell
  void setMaxPower(int rad) {
    this.maxPowerRadius = rad;
  }

  // draws a game piece
  WorldImage drawGamePiece(int pieceWidth, int pieceHeight) {
    WorldImage piece = new OverlayImage(new RectangleImage(pieceWidth, pieceHeight, 
        OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(pieceWidth, pieceHeight, OutlineMode.SOLID, Color.DARK_GRAY));
    Color pathColor = this.pathColor();
    if (this.top) {
      piece = new OverlayOffsetAlign(
          AlignModeX.CENTER, 
          AlignModeY.TOP, 
          new RectangleImage(pieceWidth / 20, pieceHeight / 2, OutlineMode.SOLID, pathColor), 
          0, 0, piece);
    }
    if (this.right) {
      piece = new OverlayOffsetAlign(
          AlignModeX.RIGHT, 
          AlignModeY.MIDDLE, 
          new RectangleImage(pieceWidth / 2, pieceWidth / 20, OutlineMode.SOLID, pathColor), 
          0, 0, piece);
    }
    if (this.bottom) {
      piece = new OverlayOffsetAlign(
          AlignModeX.CENTER, 
          AlignModeY.BOTTOM, 
          new RectangleImage(pieceWidth / 20, pieceHeight / 2, OutlineMode.SOLID, pathColor), 
          0, 0, piece);
    }
    if (this.left) {
      piece = new OverlayOffsetAlign(
          AlignModeX.LEFT, 
          AlignModeY.MIDDLE, 
          new RectangleImage(pieceWidth / 2, pieceWidth / 20, OutlineMode.SOLID, pathColor), 
          0, 0, piece);
    }
    if (this.powerStation) {
      if (new File("aoun.png").isFile()) {
        WorldImage aoun = new FromFileImage("aoun.png");
        piece = new OverlayImage(aoun, piece);
      }
      else {
        WorldImage power = new StarImage(15, OutlineMode.SOLID, Color.GREEN);
        piece = new OverlayImage(power, piece);
      }
    }
    return piece;
  }

  //adds the given cell as a neighbor of this cell
  void addNeighbor(GamePiece c) {
    this.reachable.add(c);
  }

  // returns true if this GamePiece has a powerStation
  boolean hasPowerCell() {
    return powerStation;
  }

  // returns true if two GamePieces are next to each other and their connections touch
  boolean connectedTo(GamePiece gp) {
    if (this.col - gp.col == 1) {
      if (this.row == gp.row) {
        return this.left && gp.right;
      }
    }
    else if (gp.col - this.col == 1) {
      if (this.row == gp.row) {
        return this.right && gp.left;
      }
    }
    else if (this.col == gp.col) {
      if (this.row - gp.row == 1) {
        return this.top && gp.bottom;
      }
      else if (gp.row - this.row == 1) {
        return this.bottom && gp.top;
      }
    }
    return false;
  }

  //returns true if two GamePieces are next to each other and their connections touch
  void join(GamePiece gp) {
    if (this.col - gp.col == 1) {
      if (this.row == gp.row) {
        this.left = true;
      }
    }
    else if (gp.col - this.col == 1) {
      if (this.row == gp.row) {
        this.right = true;
      }
    }
    else if (this.col == gp.col) {
      if (this.row - gp.row == 1) {
        this.top = true;
      }
      else if (gp.row - this.row == 1) {
        this.bottom = true;
      }
    }
  }

  int distanceToFurthest(ArrayList<Index> acc) {
    GamePiece first = this.reachable.get(0);
    GamePiece second = this.reachable.get(1);
    GamePiece third;
    GamePiece fourth;
    if (this.reachable.size() == 2) {
      third = new GamePiece(-1, -1, false, false, false, false);
      fourth = new GamePiece(-1, -1, false, false, false, false);
    }
    else if (this.reachable.size() == 3) {
      third = this.reachable.get(2);
      fourth = new GamePiece(-1, -1, false, false, false, false);
    }
    else {
      third = this.reachable.get(2);
      fourth = this.reachable.get(3);
    }
    Index temp = this.asIndex();
    acc.add(temp);
    return Math.max(first.distanceToFurthestHelp(acc, 0, this), 
        Math.max(second.distanceToFurthestHelp(acc, 0, this), 
            Math.max(third.distanceToFurthestHelp(acc, 0, this), 
                fourth.distanceToFurthestHelp(acc, 0, this))));
  }

  int distanceToFurthestHelp(ArrayList<Index> acc, int total, GamePiece last) {
    Index curInd = this.asIndex();
    if (this.connectedTo(last)) {
      if (curInd.existsIn(acc)) {
        return total;
      }
      else {
        GamePiece first = this.reachable.get(0);
        GamePiece second = this.reachable.get(1);
        GamePiece third;
        GamePiece fourth;
        if (this.reachable.size() == 2) {
          third = new GamePiece(-1, -1, false, false, false, false);
          fourth = new GamePiece(-1, -1, false, false, false, false);
        }
        else if (this.reachable.size() == 3) {
          third = this.reachable.get(2);
          fourth = new GamePiece(-1, -1, false, false, false, false);
        }
        else {
          third = this.reachable.get(2);
          fourth = this.reachable.get(3);
        }
        acc.add(curInd);
        return Math.max(first.distanceToFurthestHelp(acc, total + 1, this), 
            Math.max(second.distanceToFurthestHelp(acc, total + 1, this),
                Math.max(third.distanceToFurthestHelp(acc, total + 1, this), 
                    fourth.distanceToFurthestHelp(acc, total + 1, this))));
      }
    }
    else {
      return total;
    }
  }

  // sets the radius int to the minimum distance between this cell and the powerCell
  void distanceToPower() {
    this.radius = this.findMinRadius(new ArrayList<Index>());
  }

  // finds the minimum distance from this cell to the powerCell
  int findMinRadius(ArrayList<Index> acc) {
    if (this.powerStation) {
      return 0;
    }
    else {
      GamePiece first = this.reachable.get(0);
      GamePiece second = this.reachable.get(1);
      GamePiece third;
      GamePiece fourth;
      if (this.reachable.size() == 2) {
        third = new GamePiece(-1, -1, false, false, false, false);
        fourth = new GamePiece(-1, -1, false, false, false, false);
      }
      else if (this.reachable.size() == 3) {
        third = this.reachable.get(2);
        fourth = new GamePiece(-1, -1, false, false, false, false);
      }
      else {
        third = this.reachable.get(2);
        fourth = this.reachable.get(3);
      }
      Index temp = new Index(this.col, this.row);
      acc.add(temp);
      return Math.min(first.findMinRadiusHelp(acc, 1, this), 
          Math.min(second.findMinRadiusHelp(acc, 1, this),
              Math.min(third.findMinRadiusHelp(acc, 1, this), 
                  fourth.findMinRadiusHelp(acc, 1, this))));
    }
  }

  // helper function for the findMinRadius method
  int findMinRadiusHelp(ArrayList<Index> acc, int total, GamePiece last) {
    Index curInd = new Index(this.col, this.row);
    if (this.connectedTo(last)) {
      if (this.powerStation) {
        return total;
      }
      else if (curInd.existsIn(acc)) {
        return 8000;
      }
      else {
        GamePiece first = this.reachable.get(0);
        GamePiece second = this.reachable.get(1);
        GamePiece third;
        GamePiece fourth;
        if (this.reachable.size() == 2) {
          third = new GamePiece(-1, -1, false, false, false, false);
          fourth = new GamePiece(-1, -1, false, false, false, false);
        }
        else if (this.reachable.size() == 3) {
          third = this.reachable.get(2);
          fourth = new GamePiece(-1, -1, false, false, false, false);
        }
        else {
          third = this.reachable.get(2);
          fourth = this.reachable.get(3);
        }
        acc.add(curInd);
        return Math.min(first.findMinRadiusHelp(acc, total + 1, this), 
            Math.min(second.findMinRadiusHelp(acc, total + 1, this),
                Math.min(third.findMinRadiusHelp(acc, total + 1, this), 
                    fourth.findMinRadiusHelp(acc, total + 1, this))));
      }
    }
    else {
      return 8000;
    }
  }

  // sets the powerStation boolean to true
  void givePowerCell() {
    this.powerStation = true;
  }

  // sets the powerStation boolean to false
  void takePowerCell() {
    this.powerStation = false;
  }

  // adds all neighbors of a given cell with the grid
  void addAllNeighbors(int i, int j, ArrayList<ArrayList<GamePiece>> grid) {
    if (i - 1 >= 0) {
      this.addNeighbor(grid.get(i - 1).get(j));
    }
    if (i + 1 < grid.size()) {
      this.addNeighbor(grid.get(i + 1).get(j));
    }
    if (j + 1 < grid.get(0).size()) {
      this.addNeighbor(grid.get(i).get(j + 1));
    }
    if (j - 1 >= 0) {
      this.addNeighbor(grid.get(i).get(j - 1));
    }
  }
}
