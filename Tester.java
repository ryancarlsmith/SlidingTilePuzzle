import java.util.ArrayList;
/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 3
10/3/22
 */
public class Tester {

    private static int[] toArray(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        ArrayList<Direction> moves = new ArrayList<>();
        ArrayList<Integer> tiles = new ArrayList<>();
        String option = "";
        int columns = 4;
        int rows = 4;

        for (String arg : args) {
            switch (arg.toLowerCase()) {
                case "-size":
                case "-rows":
                case "-cols":
                case "-columns":
                    if (option.length() > 0) {
                        System.err.println("Missing value for option: " + option);
                    }
                    option = arg;
                    continue;

                case "up":
                case "down":
                case "left":
                case "right":
                    if (option.length() > 0) {
                        System.err.println("Missing value for option: " + option);
                    }
                    Direction move = Direction.valueOf(arg.toUpperCase());
                    moves.add(move);
                    continue;
            }

            try {
                switch (option) {
                    case "-size":
                        rows = Integer.parseInt(arg);
                        columns = rows;
                        break;

                    case "-rows":
                        rows = Integer.parseInt(arg);
                        break;

                    case "-cols":
                    case "-columns":
                        columns = Integer.parseInt(arg);
                        break;

                    default:
                        int tile = Integer.parseInt(arg);
                        tiles.add(tile);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid value for option " + option + ": " + arg);
            }
            option = "";
        }

        if (option.length() > 0) {
            System.err.println("Missing value for option: " + option);
        }

        Board board;
        if (tiles.size() > 0) {
            board = new Board(rows, columns, toArray(tiles));
        } else {
            board = new Board(rows, columns);
        }
        System.out.println();
        board.print();
        System.out.println();

        for (Direction direction : moves) {
            board = new Board(board, direction);
            System.out.println(direction);
            System.out.println();
            board.print();
            System.out.println();
        }





        System.out.println(board.isGoal() ? "Solved" : "Not Solved");
    }
}

