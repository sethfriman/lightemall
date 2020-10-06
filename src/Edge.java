import java.util.ArrayList;

// represents an edge
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  double weight;

  Edge(GamePiece from, GamePiece to) {
    this.fromNode = from;
    this.toNode = to;
    this.weight = Math.random();
  }

  boolean existsIn(ArrayList<Edge> list) {
    for (Edge e : list) {
      if ((this.fromNode.samePiece(e.fromNode) && this.toNode.samePiece(e.toNode)) ||
          (this.fromNode.samePiece(e.toNode) && this.toNode.samePiece(e.fromNode))) {
        return true;
      }
    }
    return false;
  }

  void connect() {
    this.fromNode.join(this.toNode);
    this.toNode.join(this.fromNode);
    this.fromNode.updateConnected(this.toNode);
    this.toNode.updateConnected(this.fromNode);
  }

  boolean canBeConnected() {

    return !(this.toNode.alreadyContains(this.fromNode) || 
        this.fromNode.alreadyContains(this.toNode));
  }
}
