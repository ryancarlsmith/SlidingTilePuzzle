/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 4
10/10/22
 */
public class Runner {
    public static void main(String[] args) {
        int size = 3;
        double weight = 1;
        String alg = "";
        String heurstic = "";
        boolean verbose = false;
        boolean stats = false;
        int input[] = new int[9]; // could initialize to solved 3x3 board if input array can be unspecified

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.charAt(0) == '-') {
                arg = arg.substring(1);
                switch (arg) {
                    case "bd":
                        alg = "bd";
                        break;
                    case "gbfs":
                        alg = "gbfs";
                        break;
                    case "ucs":
                        alg = "ucs";
                        break;
                    case "astar":
                        alg = "astar";
                        break;
                    case "bfs":
                        alg = "bfs";
                        break;
                    case "dfs":
                        alg = "dfs";
                        break;
                    case "L0":
                        heurstic = "L0";
                        break;
                    case "L1":
                        heurstic = "L1";
                        break;
                    case "L2":
                        heurstic = "L2";
                        break;
                    case "weight":
                        weight = Double.parseDouble(args[i + 1]);
                        i++;
                        break;
                    case "size":
                        size = Integer.parseInt(args[i + 1]);
                        i += 2;
                        input = new int[size * size];
                        for (int j = 0; j < size * size; j++) {
                            input[j] = Integer.parseInt(args[i + j]);
                        }
                        i += (size * size) - 1;
                        break;
                    case "verbose":
                        verbose = true;
                        break;
                    case "stats":
                        stats = true;
                        break;
                }
            }
        }

        Board b = new Board(size, size, input);
        Solver.solve(b, alg, heurstic, verbose, stats, weight);
    }
}
