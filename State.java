import java.util.HashMap;

/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 4
10/10/22
 */
public class State implements Numbered {
    private static int id = -1;

    public static HashMap<Board, State> nodes = new HashMap<>();
    private final Board board;
    private State frontParent;
    private State backParent;
    private Direction action;
    private Direction backAction;
    private double cost;

    private boolean exploredFront;
    private boolean exploredBack;

    private boolean frontier;

    private State(Board board) {
        this.board = board;
        this.frontParent = null;
        this.backParent = null;
        this.action = null;
        this.cost = 0;
        this.exploredFront = false;
        this.exploredBack = false;
        this.frontier = false;
        ++id;
    }

    private State(Board board, Direction act) {
        this.board = board;
        this.frontParent = null;
        this.backParent = null;
        this.action = act;
        this.cost = 0;
        this.exploredFront = false;
        this.exploredBack = false;
        this.frontier = false;
        ++id;
    }

    private State(Board board, Direction act, boolean fromBack) {
        this.board = board;
        this.frontParent = null;
        this.backParent = null;
        if (fromBack) {
            this.backAction = act;
            this.action = null;
        } else {
            this.backAction = null;
            this.action = act;
        }
//        this.action = null;
        this.cost = 0;
        this.exploredFront = false;
        this.exploredBack = false;
        this.frontier = false;
        ++id;
    }

    public Board getGoal(State s) {
        Board goal = new Board(s.board.rows(), s.board.columns());
        return goal;
    }

    public boolean getFrontier() {
        return this.frontier;
    }

    public double getCost() {
        this.cost = Solver.cost(this);
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void explore(boolean b) {
        this.exploredFront = b;
    }

    public void exploreBack(boolean b) {
        this.exploredBack = b;
    }

    public boolean isExploredFront() {
        return this.exploredFront;
    }

    public boolean isExploredBack() {
        return this.exploredBack;
    }

    public Board getBoard() {
        return this.board;
    }

    public void setFrontParent(State p) {
        this.frontParent = p;
    }

    public void setBackParent(State p) {
        this.backParent = p;
    }

    public void setFrontAction(Direction act) {
        this.action = act;
    }

    public void setBackAction(Direction act) {
        this.backAction = act;
    }

    public void setFrontier(boolean b) {
        this.frontier = b;
    }

    public int costFromStart() {
        return this.path().length; //check this
    }

    public double costFromHeuristic(String h) {
        if (h.equals("L0")) {
            return HammingDistance();
        } else if (h.equals("L1")) {
            return ManhattanDistance();
        } else if (h.equals("L2")) {
            return EuclideanDistance();
        }
        return -1;
    }

    public double costFromStartAndHeurstic(String h) {
        return (costFromStart() + costFromHeuristic(h));
    }

    public double HammingDistance() { //# of nodes out of place
        int nodes = 0;
        for (int i = 0; i < this.board.getBoard().length; i++) { //cycles through int array of board instance
            if (this.board.getBoard()[i] != this.board.getGoal()[i]) { //check to see positions that are off
                nodes++;
            }
        }
        return Solver.getWeight() * nodes;
    }

    public double ManhattanDistance() {
        int r = 0;
        int c = 0;
        int r1 = 0;
        int c1 = 0;
        int error = 0;
        for (int i = 0; i < this.board.getBoard().length; i++) { //cycles through int array of board instance
            if (this.board.getBoard()[i] != this.board.getGoal()[i]) { //check to see positions that are off
                //in where nodes are off
                r = board.row(i);
                c = board.column(i);
                //find out where they need to go
                int incorrectNum = this.board.getBoard()[i];
                for (int j = 0; j < this.board.getGoal().length; j++) {
                    if (this.board.getGoal()[j] == incorrectNum) {
                        r1 = board.row(j);
                        c1 = board.column(j);
                        error += (Math.abs(r - r1) + Math.abs(c - c1));
                    }
                }
            }
        }
        return Solver.getWeight() * error;
    }

    public double EuclideanDistance() {
        // same logic as manhattan
        int r = 0;
        int c = 0;
        int r1 = 0;
        int c1 = 0;
        double error = 0.0;
        for (int i = 0; i < this.board.getBoard().length; i++) { //cycles through int array of board instance
            if (this.board.getBoard()[i] != this.board.getGoal()[i]) { //check to see positions that are off
                r = board.row(i);
                c = board.column(i);
                //find out where they need to go
                int incorrectNum = this.board.getBoard()[i];
                for (int j = 0; j < this.board.getGoal().length; j++) {
                    if (this.board.getGoal()[j] == incorrectNum) {
                        r1 = board.row(j);
                        c1 = board.column(j);
                        double r3 = Math.pow(r - r1, 2);
                        double c3 = Math.pow(c - c1, 2);

                        error += Math.sqrt((r3 + c3));
                    }
                }
            }
        }
        return Solver.getWeight() * error;
    }

    public int steps() {
        State rover = this;
        int steps = 0;

        while (rover.frontParent != null) {
            rover = rover.frontParent;
            steps++;
        }
        return steps;
    }

    public Direction[] actions() {
        State rover = this;
        int steps = this.steps();
        Direction[] actions = new Direction[steps];

        while (rover != null) {
            if (rover.frontParent != null) {
                actions[(steps--) - 1] = rover.action;
            }
            rover = rover.frontParent;
        }
        return actions;
    }

    public Direction getAction() {
        return this.action;
    }

    public Direction getBackAction() {
        return this.backAction;
    }

    public Board[] states() {
        State rover = this;
        int steps = this.steps();
        Board[] states = new Board[steps + 1];

        while (rover.frontParent != null) {
            states[steps--] = rover.board;
            rover = rover.frontParent;
        }
        return states;
    }

    public static State next(State s, Direction action) { //transition
        Board result = new Board(s.board, action);
        if (nodes.containsKey(result)) {
            return nodes.get(result); //return s?
        }
        return new State(result, action);
    }

    public static State next(State s, Direction action, boolean fromBack) { //transition
        Board result = new Board(s.board, action);
        if (nodes.containsKey(result)) {
            return nodes.get(result);
        }
        return new State(result, action, fromBack);
    }

    public Direction[] possibleActions() {
        return this.board.moves(this.board.empty());
    }

    //takes a board and gives the state
    public static State find(Board board) {
        State node = nodes.get(board);
        if (node == null) {
            node = new State(board);
            nodes.put(board, node);
        }
        return node;
    }

    public boolean isGoal(State current) {
        return current.board.isGoal();
    }

    //final path from start to goal
    public State[] mergedPath() {
        State[] path = this.path();
        State[] backPath = this.pathFromBack();
        return append(path, backPath);
    }

    //total actions array -> the solution
    public Direction[] mergedActions() {
        State[] path = this.path();
        State[] backPath = this.pathFromBack();
        Direction[] dirs = new Direction[path.length + backPath.length];
        for (int i = 1; i < path.length; i++) {
            dirs[i - 1] = path[i].action;
        }
        dirs[path.length - 1] = reverse(path[path.length - 1].backAction);
        for (int i = 0; i < backPath.length - 1; i++) { //length-1

            dirs[path.length + i] = reverse(backPath[i].backAction); //Actions taken starting from goal must be reversed
        }
        return dirs;
    }

    public Direction reverse(Direction d) {
        if (d == null) {
            return null;
        }
        switch (d) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                return null;
        }

    }

    //returns front path
    public State[] path() {
        State rover = this;
        int steps = this.steps();
        State[] parents = new State[steps + 1];
        while (rover != null) {
            parents[steps--] = rover;
            rover = rover.frontParent;
        }
        return parents;
    }

    public State[] pathFromBack() { // path to get from this node to goal using backParent pointers, does not include this node
        State rover = this.backParent;
//        int steps = this.steps();
        if (rover == null) {
            return new State[0];
        }
        int steps = 0;
        State[] parents = new State[this.steps()];
        while (rover != null && steps < parents.length) {
            parents[steps++] = rover;
            rover = rover.backParent;
        }
        return parents;
    }

    // Appends 2 state array's
    public static State[] append(State[] a1, State[] a2) {
        State[] res = new State[a1.length + a2.length];
        int i = 0;
        for (; i < a1.length; i++) {
            res[i] = a1[i];
        }
        for (int j = 0; j < a2.length; j++) {
            res[i + j] = a2[j];
        }
        return res;
    }

    @Override
    public int id() {
        return id;
    }
}