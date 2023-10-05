import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 4
10/10/22
 */

public class Board {
    // An IMMUTABLE class that represents the state of the
    // board for an n x m sliding tile puzzle.  There are
    // two ways to indicate the position of a tile on the
    // board: first it can be a (row, column) pair; second
    // it can be an index into a n x m vector.  We will
    // use a one dimensional vector for state of the board
    // but it will be convenient to have functions that
    // go back and forth between the two representations
    // for the position of a tile.

    // Assertions
    //
    //     I strongly urge you to use assertions to validate
    //     the parameters passed to the methods in this class
    //     and have indicated how to do so for the first few
    //     methods.  You should enable assertions when running
    //     your code during development (the -ea option to java):
    //
    //         java -ea Puzzle -rows 4 -columns 4 ...
    //
    //     and AssertionError with be thrown whenever an assert
    //     statement fails.  When running your program as a
    //     finished product, omit the -ea option and assertions
    //     will not be checked so your program will run faster.


    private int[] board;
    private final int rows;
    private final int columns;


    public Board(int rows, int columns) {
        // Consruct a board in the solved configuration
        this.board = new int[rows * columns];
        for (int i = 0; i < this.board.length - 1; i++) {
            this.board[i] = i + 1;
        }
        this.board[this.board.length - 1] = 0;
        this.columns = columns;
        this.rows = rows;
    }

    public Board(int rows, int columns, int[] board) {
        // Construct a board with a given configuration
        this.columns = columns;
        this.rows = rows;
        this.board = new int[rows * columns];
        for (int i = 0; i < board.length; i++) {
            this.board[i] = board[i];
        }
        validate(rows, columns, board);

    }

    public Board(Board other, Direction direction) {
        // Construct a board by moving the empty square
        // in a specified position from a given board
        // configuration

        this.rows = other.rows;
        this.columns = other.columns;
        validate(this.rows(), this.columns(), other.move(direction));
        this.board = other.move(direction);
    }

    public int rows() {
        // Number of rows for this board
        return this.rows;
    }

    public int columns() {
        // Number of columns for this board
        return this.columns;
    }

    public int[] getBoard() {
        return this.board;
    }

    public int empty() {
        // Position of the empty square
        int index = -1;
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int get(int index) {
        // Contents of a particual position on the board
        assert isValidIndex(index) : "Not valid index";
        return this.board[index];
    }

    public int get(int row, int column) {
        // Contents of a particual position on the board
        assert isValidRow(row) : "Bad row";
        assert isValidColumn(column) : "Bad column";
        return this.board[index(row, column)];
    }

    public int index(int row, int column) {
        // Conversion from row/column to position
        assert isValidRow(row) : "Bad row";
        assert isValidColumn(column) : "Bad column";
        return (row * this.columns) + column;
    }

    public int row(int index) {
        // Conversion from index to row
        assert isValidIndex(index) : "Not valid index";
        return index / this.rows;
    }

    public int column(int index) {
        // Conversion from index to column
        assert isValidIndex(index) : "Not valid index";
        return index % this.columns;
    }

    public int position(int position, Direction direction) {
        // Position of the adjacent square in a given direction
        switch (direction) {
            case UP:
                assert isValidIndex(position - this.columns);
                return this.board[position - this.columns];
            case DOWN:
                assert isValidIndex(position + this.columns);
                return this.board[position + this.columns];
            case LEFT:
                assert isValidIndex(position - 1);
                return this.board[position - 1];
            case RIGHT:
                assert isValidIndex(position + 1);
                return this.board[position + 1];
        }
        return -1;
    }


    public Direction[] moves(int position) { //TO DO
        // The list of possible moves from a given board position
        //if min row -> cant move up, max row -> cant move down
        //if min column -> cant move left, if max column -> cant move right
        int row = row(position);
        int column = column(position);
        List<Direction> vals = new ArrayList<>();
        //evaluate columns (LEFT or RIGHT)

        //evaluate rows (UP or DOWN)
        if (isValidRow(row + 1)) {
            vals.add(Direction.DOWN);
        }
        if (isValidColumn(column + 1)) {
            vals.add(Direction.RIGHT);
        }

        if (isValidRow(row - 1)) {
            vals.add(Direction.UP);
        }
        if (isValidColumn(column - 1)) {
            vals.add(Direction.LEFT);
        }


        Direction[] result = vals.toArray(new Direction[vals.size()]);
        return result;
    }


    public int[] move(Direction direction) {
        // Creates a new board array with the empty
        // square moved in the indicated direction
        int[] answer = new int[this.rows * this.columns];
        for (int i = 0; i < this.board.length; i++) { //copying array to answer
            answer[i] = this.board[i];
        }
        int blankIndex = empty();
        //check if direction is valid?
        //move function
        switch (direction) {
            case UP:
                int switchIndex = blankIndex - this.columns;
                answer[blankIndex] = answer[switchIndex];
                answer[switchIndex] = 0;
                break;
            case DOWN:
                int switchIndex1 = blankIndex + this.columns;
                answer[blankIndex] = answer[switchIndex1];
                answer[switchIndex1] = 0;
                break;
            case LEFT:
                int switchIndex2 = blankIndex - 1;
                answer[blankIndex] = answer[switchIndex2];
                answer[switchIndex2] = 0;
                break;
            case RIGHT:
                int switchIndex3 = blankIndex + 1;
                answer[blankIndex] = answer[switchIndex3];
                answer[switchIndex3] = 0;
                break;
        }
        return answer;
    }


    public boolean equals(Board other) {
        return Arrays.equals(this.board, other.board);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Board && this.equals((Board) other);
    }

    @Override
    public int hashCode() { //hash to correspond to order of the tiles bc thats differnce between states
        // You WILL want a better hashCode function
        // hashCode and equals MUST be compatible
        int radix = this.columns * this.rows;
        int hash = this.board[0];
        for (int i = 1; i < this.board.length; i++) {
            hash *= radix;
            hash += this.board[i];
        }
        return hash;
    }

    @Override
    public String toString() {
        String result = "";
        String separator = "";
        for (int i = 0; i < this.board.length; i++) {
            result += separator;
            result += String.format("%2d", this.board[i]);
            separator = " ";
        }
        return result;
    }

    public void print() {
        for (int row = 0; row < this.rows(); row++) {
            String line = "";
            if (row > 0) printRowSeparator();
            String separator = "";
            for (int col = 0; col < this.columns(); col++) {
                int tile = this.get(row, col);
                line += separator;
                if (tile > 0) {
                    line += String.format(" %2d ", this.get(row, col));
                } else {
                    line += "    ";
                }
                separator = "|";
            }
            System.out.println(line);
        }
    }

    private void printRowSeparator() {
        String line = "";
        String separator = "";
        for (int col = 0; col < this.columns(); col++) {
            line += separator;
            line += "----";
            separator = "+";
        }
        System.out.println(line);
    }

    private static int[] goal(int rows, int columns) {
        // All tiles are in ascending order
        // Empty square is in lower right corner
        int[] goal = new int[rows * columns];
        for (int i = 0; i < goal.length - 1; i++) {
            goal[i] = i + 1;
        }
        goal[goal.length - 1] = 0;
        return goal;
    }

    public int[] getGoal() {
        return goal(this.rows, this.columns);
    }

    public boolean isGoal() {
        boolean result = true;
        int[] goal = goal(this.rows, this.columns);
        for (int i = 0; i < this.board.length; i++) {
            if (this.board[i] != goal[i]) {
                result = false;
            }
            continue;
        }
        return result;
    }


    private boolean isValidRow(int row) {
        return row >= 0 && row < this.rows;
    }

    public boolean isValidColumn(int column) {
        return column >= 0 && column < this.columns;
    }

    public boolean isValidIndex(int index) {
        int size = this.rows * this.columns;
        return index >= 0 && index < size;
    }

    private void validate(int rows, int columns, int[] board) {
        // Check that size = rows * columns;
        // Check that each tile is valid:
        //    Tile values are in range 0 .. size-1
        //    There are no duplicate tiles
        if (board.length == rows * columns) { //check size
            List<Integer> boardIntegers = new ArrayList<>();
            for (int i : board) {
                if (isValidIndex(i) == false) { //check if tile is in range 0.. size-1
                    throw new IllegalArgumentException("Invalid board: tile is out of range");
                }
                if (boardIntegers.contains(i) == false) { //check duplicates
                    boardIntegers.add(i);
                } else {
                    throw new IllegalArgumentException("Invalid board: duplicate tile");
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid board: bad size");
        }
    }
}