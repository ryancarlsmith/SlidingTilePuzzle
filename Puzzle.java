/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 4
10/10/22
 */
public class Puzzle {
    public static interface Action {
        default public int cost() {
            return 1;
        }
    }

    public static interface Board extends Iterable<Action> {
        public State next(Action action);

        public boolean isGoal();
    }

    public static interface Solver {
        public State search(Board initial);

        default Direction[] solution(Board initial) {
            return search(initial).actions();
        }

        default int steps(Board initial) {
            return search(initial).steps();
        }
    }
}

