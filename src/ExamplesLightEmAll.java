import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

//import javalib.impworld.WorldScene;
import javalib.worldimages.AlignModeX;
import javalib.worldimages.AlignModeY;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.OverlayImage;
import javalib.worldimages.OverlayOffsetAlign;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import tester.Tester;

// examples and tests for Light Em All Game
class ExamplesLightEmAll {

  // examples of games
  LightEmAll g1;
  LightEmAll g2;

  // examples of GamePieces
  GamePiece p1;
  GamePiece p2;
  GamePiece p3;
  GamePiece p4;

  //examples of indexes
  Index i1;
  Index i2;
  Index i3;

  //examples of lists of indexes
  ArrayList<Index> ali1;
  ArrayList<Index> ali2;

  // examples of columns
  ArrayList<GamePiece> column1;
  ArrayList<GamePiece> column2;

  // examples of boards
  ArrayList<ArrayList<GamePiece>> board1;
  ArrayList<ArrayList<GamePiece>> boardEmpty;

  // initializes the data
  void initData() {
    g1 = new LightEmAll(6, 5, 3);
    g2 = new LightEmAll(3, 3, 3);

    p1 = new GamePiece(0, 0, false, false, false, true);
    p2 = new GamePiece(1, 0, false, true, true, false);
    p3 = new GamePiece(0, 1, false, false, false, true);
    p4 = new GamePiece(1, 1, true, false, true, false);

    i1 = new Index(0,0);
    i2 = new Index(2,2);
    i3 = new Index(2,2);

    ali1 = new ArrayList<Index>(Arrays.asList(i1, i2, i3));
    ali2 = new ArrayList<Index>(Arrays.asList(i2));

    column1 = new ArrayList<GamePiece>(Arrays.asList(p1, p2));
    column2 = new ArrayList<GamePiece>(Arrays.asList(p3, p4));

    board1 = new ArrayList<ArrayList<GamePiece>>(Arrays.asList(column1, column2));
    boardEmpty = new ArrayList<ArrayList<GamePiece>>();
  }

  // tests the drawBoard and makeScene function
  void testDrawBoardAndMakeScene(Tester t) {
    new LightEmAll(5, 5).bigBang(700, 700);
  }

  // tests the buildBoard function
  void testbuildBoard(Tester t) {
    initData();
    t.checkExpect(g1.board.size(), 6);
    t.checkExpect(g1.board.get(0).size(), 5);
  }

  // tests the makeScene method
  void testOnMouseClicked(Tester t) {
    initData();
    GamePiece piece1 = this.g1.board.get(0).get(0);
    piece1.left = true;
    piece1.bottom = false;
    g1.onMouseClicked(new Posn(0,0), "LeftButton");
    t.checkExpect(this.g1.board.get(0).get(0), piece1);
  }

  // tests the rotate method
  void testRotate(Tester t) {
    initData();
    p1.rotate();
    t.checkExpect(this.p1.left, true);
    t.checkExpect(this.p1.bottom, false);
  }

  // tests the drawGamePiece method
  void testDrawGamePiece(Tester t) {
    initData();
    t.checkExpect(p1.drawGamePiece(20, 20), new OverlayOffsetAlign(
        AlignModeX.CENTER, 
        AlignModeY.BOTTOM, 
        new RectangleImage(1, 10, OutlineMode.SOLID, Color.yellow), 
        0, 0, new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.DARK_GRAY))));
  }

  //tests same index method
  void testSameIndex(Tester t) {
    initData();
    t.checkExpect(i1.sameIndex(i2), false);
    t.checkExpect(i1.sameIndex(i1), true);
    t.checkExpect(i3.sameIndex(i2), true);
  }

  // tests exists in method
  void testExistsIn(Tester t) {
    initData();
    t.checkExpect(i1.existsIn(ali1), true);
    t.checkExpect(i1.existsIn(ali2), false);
    t.checkExpect(i3.existsIn(ali1), true);
  }

  // tests the buildBoard method
  void testBuildBoard(Tester t) {
    initData();
    this.g1.board = boardEmpty;
    this.g1.buildBoard();
    t.checkExpect(g1.board.size(), 6);
    t.checkExpect(g1.board.get(0).size(), 5);
  }

  // tests adding the neighbors
  void testAddNeighbors(Tester t) {
    initData();
    this.g1.board = boardEmpty;
    this.g1.buildBoard();
    this.g1.addNeighbors();
    t.checkExpect(this.g1.board.get(0).get(0).reachable.size(), 2);
  }

  // tests adding a power cell to the board
  void testAddPowerCell(Tester t) {
    initData();
    this.g1.board = boardEmpty;
    this.g1.buildBoard();
    this.g1.addNeighbors();
    this.g1.addPowerCell();
    t.checkExpect(g1.board.get(0).get(0).powerStation, true);
  }

  // tests setting the radii for each GamePiece
  void testSetRadii(Tester t) {
    initData();
    this.g1.board = boardEmpty;
    this.g1.buildBoard();
    this.g1.addNeighbors();
    this.g1.addPowerCell();
    this.g1.setRadii();
    t.checkExpect(g1.board.get(0).get(0).radius, 0);
    t.checkExpect(g1.board.get(0).get(1).radius, 1);
    t.checkExpect(g1.board.get(1).get(0).radius, 3);
  }

  // tests finding the index of the power station
  void testFindPowerStation(Tester t) {
    initData();
    t.checkExpect(g1.findPowerStation(), new Index(0, 0));
    this.g1.board = boardEmpty;
    this.g1.buildBoard();
    this.g1.addNeighbors();
    t.checkExpect(g1.findPowerStation(), new Index(-1, -1));
  }

  // tests using the keys to move the power cell
  void testOnKeyEvent(Tester t) {
    initData();
    g1.onKeyEvent("down");
    t.checkExpect(g1.board.get(0).get(0).powerStation, false);
    t.checkExpect(g1.board.get(0).get(1).powerStation, true);
    g1.onKeyEvent("left");
    t.checkExpect(g1.board.get(0).get(1).powerStation, true);
    g1.onKeyEvent("down");
    g1.onKeyEvent("right");
    t.checkExpect(g1.board.get(1).get(2).powerStation, false);
  }

  // tests the pathColor method
  void testPathColor(Tester t) {
    initData();
    t.checkExpect(g1.board.get(0).get(0).pathColor(), Color.yellow);
  }

  // tests the setMaxPower method
  void testSetMaxPower(Tester t) {
    initData();
    p1.setMaxPower(50);
    t.checkExpect(p1.maxPowerRadius, 50);
  }

  // tests the hasPowerCell method
  void testHasPowerCell(Tester t) {
    initData();
    t.checkExpect(g1.board.get(0).get(0).hasPowerCell(), true);
    t.checkExpect(g1.board.get(1).get(0).hasPowerCell(), false);
  }

  // tests the connectedTo method
  void testConnectedTO(Tester t) {
    initData();
    t.checkExpect(p1.connectedTo(p2), true);
    t.checkExpect(p1.connectedTo(p3), false);
  }

  // tests the distance to the power cell method
  void testDistance(Tester t) {
    initData();
    t.checkExpect(g2.board.get(0).get(0).radius, 0);
    t.checkExpect(g2.board.get(1).get(1).radius, 2);
  }

  // tests the findMinRadius method
  void testFindMinRadius(Tester t) {
    initData();
    t.checkExpect(g2.board.get(0).get(0).findMinRadius(new ArrayList<Index>()), 0);
    t.checkExpect(g2.board.get(1).get(1).findMinRadius(new ArrayList<Index>()), 2);
  }

  // tests the findMinRadius helper method
  void testFindMinRadiusHelp(Tester t) {
    initData();
    t.checkExpect(g2.board.get(0).get(0).findMinRadiusHelp(new ArrayList<Index>(), 
        5, g2.board.get(0).get(1)), 5);
  }

  // tests the givePowerCell  and takePowerCell functions
  void testGiveandTakePowerCell(Tester t) {
    initData();
    g1.board.get(1).get(0).givePowerCell();
    g1.board.get(0).get(0).takePowerCell();
    t.checkExpect(g1.board.get(1).get(0).powerStation, true);
    t.checkExpect(g1.board.get(0).get(0).powerStation, false);
  }

}
