import java.util.ArrayList;

// represents the position of a GamePiece on a board
class Index {
  
  int column; // column number
  int row; // row number

  // constructor
  Index(int column, int row) {
    this.column = column;
    this.row = row;
  }

  // returns true if two Indices have the same values
  boolean sameIndex(Index in) {
    return this.column == in.column && this.row == in.row;
  }

  // returns true if an Index exists in a list of indices
  boolean existsIn(ArrayList<Index> in) {
    for (int i = 0; i < in.size(); i++) {
      if (in.get(i).sameIndex(this)) {
        return true;
      }
    }
    return false;
  }

}