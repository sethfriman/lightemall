
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

//import tester.*;
import javalib.impworld.*;
//import java.awt.Color;
import javalib.worldimages.*;

// represents a Light Em All game
class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;
  int sceneWidth;
  int sceneHeight;
  int pieceWidth;
  int pieceHeight;
  Random r;
  int numTurns;
  int currentHighScore;

  // constructor
  LightEmAll(int width, int height) {
    this.width = width;
    this.height = height;
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.sceneWidth = 700;
    this.sceneHeight = 700;
    this.pieceWidth = this.sceneWidth / this.width;
    this.pieceHeight = this.sceneHeight / this.height;
    this.currentHighScore = 0;
    this.r = new Random();
    this.initializeGame();
  }

  // constructor for testing only
  LightEmAll(int width, int height, int seed) {
    this.width = width;
    this.height = height;
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.sceneWidth = 700;
    this.sceneHeight = 700;
    this.pieceWidth = this.sceneWidth / this.width;
    this.pieceHeight = this.sceneHeight / this.height;
    this.currentHighScore = 0;
    this.r = new Random(seed);
    this.initializeGame();
  }

  void initializeGame() {
    this.numTurns = 0;
    this.buildBoard2();
    this.addNeighbors();
    this.addPowerCell();
    this.createEdges();
    this.rotateBoard();
    this.setRadii();
  }

  void createEdges() {
    this.mst = new ArrayList<Edge>();
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(0).size(); j ++) {
        GamePiece tempPiece = this.board.get(i).get(j);
        for (GamePiece gp : tempPiece.reachable) {
          Edge temp = new Edge(tempPiece, gp);
          if (!temp.existsIn(this.mst)) {
            this.mst.add(temp);
          }
        }
      }
    }
    this.createBoard();
  }

  void buildBoard2() {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    for (int i = 0; i < this.width; i++) {
      ArrayList<GamePiece> column = new ArrayList<GamePiece>();
      for (int j = 0; j < this.height; j++) {
        column.add(new GamePiece(j, i, false, false, false, false));
      }
      this.board.add(column);
    }
  }

  void createBoard() {
    if (this.mst.size() >= 2) {
      Edge min = this.mst.get(0);
      int minInd = 0;
      for (int i = 1; i < this.mst.size(); i++) {
        if (this.mst.get(i).weight < min.weight) {
          min = this.mst.get(i);
          minInd = i;
        }
      }
      if (min.canBeConnected()) {
        this.mst.get(minInd).connect();
      }
      this.mst.remove(minInd);
      this.createBoard();
    }
    else if (this.mst.size() == 1) {
      if (this.mst.get(0).canBeConnected()) {
        this.mst.get(0).connect();
      }
    }
  }

  void rotateBoard() {
    this.setRadii();
    int maxPower = this.furthestFromPower().distanceToFurthest(new ArrayList<Index>());
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(0).size(); j++) {
        int num = r.nextInt(4);
        this.board.get(i).get(j).setMaxPower(maxPower / 2 + 2);
        this.board.get(i).get(j).randomRotate(num);
      }
    }
  }

  // finds the GamePiece that is furthest from the power source at the start of the game
  GamePiece furthestFromPower() {
    int maxRadius = board.get(0).get(0).radius;
    GamePiece temp = board.get(0).get(0);
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(0).size(); j++) {
        if (board.get(i).get(j).radius > maxRadius) {
          maxRadius = board.get(i).get(j).radius;
          temp = board.get(i).get(j);
        }
      }
    }
    return temp;
  }

  // adds all the neighbors for each GamePiece
  void addNeighbors() {
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(0).size(); j++) {
        this.board.get(i).get(j).addAllNeighbors(i, j, board);
      }
    }
  }

  // adds the power cell in the top left corner of the board
  void addPowerCell() {
    this.board.get(0).get(0).givePowerCell();
  }

  // sets the radius for all GamePieces from the power cell
  void setRadii() {
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(0).size(); j++) {
        board.get(i).get(j).distanceToPower();
      }
    }
  }

  // returns the index on the grid of the location of the power station
  Index findPowerStation() {
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(0).size(); j++) {
        if (this.board.get(i).get(j).hasPowerCell()) {
          return new Index(i, j);
        }
      }
    }
    return new Index(-1, -1);
  }

  // builds the board as a fractal
  void buildBoard() {
    this.board = new ArrayList<ArrayList<GamePiece>>();
    for (int c = 0; c < this.width; c++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int r = 0; r < this.height; r++) {
        GamePiece temp = new GamePiece(r, c, false, false, false, false);
        if (r % 2 == 0) {
          if ((c % 4 == 0 || c % 4 == 3) && r != 0) {
            temp.top = true;
            temp.bottom = true;
          }
          else {
            temp.bottom = true;
          }
        }
        else {
          if ((c % 4 == 0 || c % 4 == 3) && r != this.height - 1) {
            if (c % 2 == 0) {
              temp.right = true;
              temp.top = true;
              temp.bottom = true;
            }
            else {
              temp.left = true;
              temp.top = true;
              temp.bottom = true;
            }
          }
          else {
            if (c % 2 == 0) {
              temp.right = true;
              temp.top = true;
            }
            else {
              temp.left = true;
              temp.top = true;
            }
          }
        }
        if (r % 4 == 3) {
          if (c % 4 == 1) {
            temp.right = true;
          }
          else if (c % 4 == 2) {
            temp.left = true;
          }
        }
        if (c % 8 == 3 || c % 8 == 4) {
          if (r % 4 == 3) {
            temp.bottom = false;
          }
          else if (r % 4 == 0) {
            temp.top = false;
          }
        }
        if (r == this.height - 1) {
          if (c == 0) {
            temp.right = true;
            temp.left = false;
          }
          else if (c == this.width - 1) {
            temp.left = true;
            temp.right = false;
          }
          else {
            temp.left = true;
            temp.right = true;
          }
          temp.bottom = false;
        }
        if (c == this.width - 1) {
          if (r == 0) {
            temp.bottom = true;
            temp.top = false;
          }
          else if (r == this.height - 1) {
            temp.top = true;
            temp.bottom = false;
          }
          else {
            temp.top = true;
            temp.bottom = true;
          }
          if (this.width % 8 == 4) {
            if (r % 4 == 3) {
              temp.bottom = false;
            }
            else if (r % 4 == 0) {
              temp.top = false;
            }
          }
          if (this.width % 4 == 3) {
            if (r % 4 == 0) {
              temp.top = false;
            }
            else if (r % 4 == 3) {
              temp.bottom = false;
            }
          }
          else if (this.width % 4 == 2) {
            if (r % 4 == 1) {
              temp.bottom = false;
            }
            else if (r % 4 == 2) {
              temp.top = false;
            }
            else if (r % 4 == 0) {
              temp.top = false;
            }
            else if (r % 4 == 3) {
              temp.bottom = false;
            }
          }
          if (this.width % 8 == 6) {
            if (r % 4 == 3 && r != this.height - 1) {
              temp.bottom = true;
            }
            else if (r % 4 == 0 && r != 0) {
              temp.top = true;
            }
          }
          else if (this.width % 8 == 7) {
            if (r % 4 == 3 && r != this.height - 1) {
              temp.bottom = true;
            }
            else if (r % 4 == 0 && r != 0) {
              temp.top = true;
            }
          }
          temp.right = false;
        }
        this.board.get(c).add(temp);
      }
    }
  }

  //draws the scene with the grid
  public WorldScene makeScene() {
    WorldScene result = new WorldScene(this.sceneWidth, this.sceneHeight);
    result.placeImageXY(this.drawBoard(), this.sceneWidth / 2, this.sceneHeight / 2);
    if (this.isGameOver()) {
      return this.makeWinScene();
    }
    else {
      return result;
    }
  }

  //makes the scene for winning the game
  WorldScene makeWinScene() {
    WorldScene result = new WorldScene(this.sceneWidth, this.sceneHeight);
    result.placeImageXY(this.drawBoard(), this.sceneWidth / 2, this.sceneHeight / 2);
    result.placeImageXY(new AboveImage(new TextImage("YOU WIN", 50, Color.GREEN), 
        new AboveImage(new TextImage("You won in: " + this.numTurns + " turns", 50, Color.BLUE), 
            new AboveImage(new TextImage("Your Current High Score: " + this.currentHighScore, 50,
                Color.ORANGE), 
                new AboveImage(new TextImage("Press Space", 35, Color.RED), 
                    new TextImage("to Restart", 35, Color.RED))))), 
        this.sceneWidth / 2, this.sceneHeight / 2);
    return result;
  }

  boolean isGameOver() {
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.height; j++) {
        if (this.board.get(i).get(j).pathColor().equals(new Color(0, 19, 76))) {
          return false;
        }
      }
    }
    if (this.currentHighScore == 0 || this.currentHighScore > this.numTurns) {
      this.currentHighScore = this.numTurns;
    }
    return true;
  }

  // draws the game board
  WorldImage drawBoard() {
    WorldImage rows = new EmptyImage();
    for (ArrayList<GamePiece> c : this.board) {
      WorldImage columns = new EmptyImage();
      for (GamePiece r : c) {
        columns = new AboveImage(columns, r.drawGamePiece(this.pieceWidth, this.pieceHeight));
      }
      rows = new BesideImage(rows, columns);
    }

    return rows;
  }

  // rotates game piece when clicked
  public void onMouseClicked(Posn p, String button) {
    if (!this.isGameOver()) {
      if (button.equals("LeftButton")) {
        this.board.get(p.x / this.pieceWidth).get(p.y / this.pieceHeight).rotate();
        this.numTurns++;
      }
      this.setRadii();
    }
  }

  // moves the power cell along the connected wires
  public void onKeyEvent(String key) {
    Index power = this.findPowerStation();
    GamePiece powerCell = this.board.get(power.column).get(power.row);
    if (!this.isGameOver()) {
      if (key.equals("up")) {
        if (power.row > 0 && 
            powerCell.connectedTo(this.board.get(power.column).get(power.row - 1))) {
          this.board.get(power.column).get(power.row - 1).givePowerCell();
          this.board.get(power.column).get(power.row).takePowerCell();
          this.numTurns++;
        }
      }
      else if (key.equals("down")) {
        if (power.row < this.board.get(0).size() - 1 && 
            powerCell.connectedTo(this.board.get(power.column).get(power.row + 1))) {
          this.board.get(power.column).get(power.row + 1).givePowerCell();
          this.board.get(power.column).get(power.row).takePowerCell();
          this.numTurns++;
        }
      }
      else if (key.equals("left")) {
        if (power.column > 0 && 
            powerCell.connectedTo(this.board.get(power.column - 1).get(power.row))) {
          this.board.get(power.column - 1).get(power.row).givePowerCell();
          this.board.get(power.column).get(power.row).takePowerCell();
          this.numTurns++;
        }
      }
      else if (key.equals("right")) {
        if (power.column < this.board.size() - 1 && 
            powerCell.connectedTo(this.board.get(power.column + 1).get(power.row))) {
          this.board.get(power.column + 1).get(power.row).givePowerCell();
          this.board.get(power.column).get(power.row).takePowerCell();
          this.numTurns++;
        }
      }
    }
    if (key.equals(" ")) {
      this.initializeGame();
    }
    this.setRadii();
  }
}
