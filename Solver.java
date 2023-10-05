import java.util.*;

/*
Ryan Carlsmith
Artificial Intelligence
Checkpoint 4
10/10/22
 */

public class Solver implements Puzzle.Solver {
    private static String heursitic;
    private static String cost;
    private static double weight = 1;

    @Override
    public State search(Puzzle.Board initial) {
        return null;
    }

    public static Direction[] solve(Board initial, String alg, String h, boolean verbose, boolean stats, double w) {
        State goal = null;
        heursitic = h;
        State[] path;
        weight = w;
        Direction[] actions;
        if (alg.equalsIgnoreCase("dfs")) {
            goal = dfs(initial);
            path = goal.path();
            actions = goal.actions();
        } else if (alg.equalsIgnoreCase("bfs")) {
            goal = bfs(initial);
            path = goal.path();
            actions = goal.actions();
        } else if (alg.equalsIgnoreCase("bd")) {
            cost ="heuristic";
            goal = bidirectional(initial);
            path = goal.mergedPath();
            actions = goal.mergedActions();
        } else if (alg.equalsIgnoreCase("gbfs")) {
            cost = "heuristic";
            goal = solve2(initial);
            path = goal.path();
            actions = goal.actions();
        } else if (alg.equalsIgnoreCase("astar")) {
            cost = "startAndHeuristic";
            goal = solve2(initial);
            path = goal.path();
            actions = goal.actions();
        } else if (alg.equalsIgnoreCase("ucs")) {
            cost = "start";
            goal = solve2(initial);
            path = goal.path();
            actions = goal.actions();
        } else {
            goal = bfs(initial);
            path = goal.path();
            actions = goal.actions();
        }

        for (int i = 0; i < path.length; i++) {
            if (verbose) {
                System.out.println();
                path[i].getBoard().print();
                System.out.println();
            }
            if (i < path.length - 1) {
                System.out.println(actions[i]);
            }
        }
        System.out.println();
        if (stats) {
            int depth = path.length - 1;
            int explored = 0;
            for (Board b : State.nodes.keySet()) {
                State st = State.nodes.get(b);
                if (st.isExploredFront()) {
                    explored++;
                }
            }
            int expanded = State.nodes.size();
            System.out.println("Solution Depth: " + depth);
            System.out.println("Explored Nodes: " + explored);
            System.out.println("Expanded Nodes: " + expanded);
            System.out.println("Branching Factor: " + Math.pow(expanded, (double) 1 / depth));

        }

        if (goal != null) {
            return actions;
        } else {
            return new Direction[0];
        }
    }

    public static double getWeight(){
        return weight;
    }

    private static State dfs(Board initial) {
        Stack<State> stack = new Stack<>();
        State start = State.find(initial);
        stack.push(start);
        while (!stack.isEmpty()) {
            State current = stack.pop();
            if (current.isGoal(current)) {
                return current; //if its the goal
            }
            if (!current.isExploredFront()) { //if its not explored
                current.explore(true);
            }
            for (Direction action : current.possibleActions()) { //start expanding
                State next = State.next(current, action);
                if (!State.nodes.containsKey(next.getBoard())) { //change for priority queue -explored flag
                    next.setFrontParent(current);
                    stack.push(next);
                    State.nodes.put(next.getBoard(), next);
                }
            }
        }
        return null;
    }

    private static State bfs(Board initial) {
        Queue<State> queue = new LinkedList<>(); //the frontier
        State start = State.find(initial);
        queue.offer(start);
        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (current.isGoal(current)) {
                return current; //if its the goal
            }
            if (!current.isExploredFront()) { //if its not explored
                current.explore(true);
            }

            for (Direction action : current.possibleActions()) { //start expanding
                State next = State.next(current, action);
                if (!State.nodes.containsKey(next.getBoard())) {
                    next.setFrontParent(current);
                    queue.offer(next);
                    State.nodes.put(next.getBoard(), next);
                }
            }
        }
        return null;
    }

    private static class stateComparator implements Comparator<State> {
        @Override
        public int compare(State x, State y) {
            double xCost = cost(x);
            double yCost = cost(y);
            if (xCost < yCost) {
                return -1;
            } else if (xCost > yCost) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static State intersection() {
        for (Board b : State.nodes.keySet()) {
            State s = State.nodes.get(b);
            if (s.isExploredFront() && s.isExploredBack()) {
                return s;
            }
        }
        return null;
    }

    public static State bidirectional(Board initial) {
        Queue<State> startFrontier = new LinkedList<>();
        Queue<State> backFrontier = new LinkedList<>();
        Board goalStart = State.find(initial).getGoal(State.find(initial));

        return BFS_BiDi(initial, goalStart, startFrontier, backFrontier);
    }

    public static State BFS_BiDi(Board initial, Board goal,
                                 Queue<State> startFrontier, Queue<State> backFrontier) {
        //set up
        startFrontier = new LinkedList<>(); //the frontier
        backFrontier = new LinkedList<>();
        State start = State.find(initial);
        State end = State.find(goal);
        startFrontier.offer(start);
        backFrontier.offer(end);
        //start bidi BFS
        while (!startFrontier.isEmpty() && !backFrontier.isEmpty()) {
            State res = intersection();
            if (res != null) {
                return res;
            }

            State front = startFrontier.poll();
            State back = backFrontier.poll();
            //check visitation
            if (!front.isExploredFront()) { //if its not explored
                front.explore(true);
            }
            if (!back.isExploredBack()) { //if its not explored
                back.exploreBack(true);
            }
            //start expanding both
            for (Direction action : front.possibleActions()) {
                State next = State.next(front, action); //can add false as 3rd param
                //if (!State.nodes.containsKey(next.getBoard())) {
                if (!next.isExploredFront()) {
                    if (State.nodes.containsKey(next.getBoard())) {
                        State.nodes.get(next.getBoard()).setFrontParent(front);
                        State.nodes.get(next.getBoard()).setFrontAction(action);
//                        State.nodes.get(next.getBoard()).exploreBack(true);
                    } else {
                        State.nodes.put(next.getBoard(), next);
                        next.setFrontParent(front); //should these 2 go above if
                        startFrontier.offer(next); //should these 2 go above if (state.nodes.containskey...)
                    }
                }
            }
            for (Direction action : back.possibleActions()) {
                State next = State.next(back, action, true);
                //if (!State.nodes.containsKey(next.getBoard())) {
                if (!next.isExploredBack()) {
                    next.setBackParent(back);
                    backFrontier.offer(next);
                    // if board is in the hashmap simply change its backAction and backParent
                    if (State.nodes.containsKey(next.getBoard())) {
                        State.nodes.get(next.getBoard()).setBackParent(back);
                        State.nodes.get(next.getBoard()).setBackAction(action);
                        State.nodes.get(next.getBoard()).exploreBack(true);
                    } else {
                        State.nodes.put(next.getBoard(), next);
                    }
                }
            }
        }
        return null;
    }

    protected static double cost(State s) {
        if (cost.equalsIgnoreCase("start")) {
            return s.costFromStart();
        } else if (cost.equals("startAndHeuristic")) {
            return s.costFromStartAndHeurstic(heursitic);
        } else if (cost.equalsIgnoreCase("heuristic")) {
            return s.costFromHeuristic(heursitic);
        }
        return s.costFromStart();
    }

    private static State solve2(Board start) {
        PriorityQueue<State> queue = new PriorityQueue<>(new stateComparator());
        queue.offer(State.find(start));

        while (!queue.isEmpty()) {
            State current = queue.poll();
            current.setFrontier(false);
            current.explore(true);

            if (current.isGoal(current)) {
                return current; //if its the goal
            }

            for (Direction action : current.possibleActions()) { //start expanding
                State next = State.next(current, action);

                if (!next.isExploredFront()) {
                    next.setFrontParent(current);
                    queue.offer(next);
                    next.setFrontier(true);
                    State.nodes.put(next.getBoard(), next);
                }

                if (queue.contains(next)) {
                    if (next.getCost() < State.nodes.get(next.getBoard()).getCost()) {
                        queue.remove(next);
                        State.nodes.put(next.getBoard(), next);
                        next.setCost(next.getCost());
                        queue.offer(next);
                    }
                }
            }
        }
        return null;
    }
}