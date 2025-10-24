/**
 * Last Names: Follero, Garcia, Guinto, Mendoza, Prose
 * Section: S18
 * @version October 24, 2025
 */

package solver;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import reader.MapData;

/**
 * This solves the Sokoban puzzles.
 * It finds the shortest sequence of moves to push the crates using a priority
 * queue with a heuristic.
 */
public class SokoBot {
    /**
     * Solves the Sokoban puzzle given the map and initial crate positions.
     * 
     * @param width     Width of the map
     * @param height    Height of the map
     * @param mapData   2D char array representing the map
     * @param itemsData 2D char array representing the player and crates
     * @return          A string representing the sequence of moves to solve the puzzle
     */
    public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
        State initialState = State.fromLevel(mapData, itemsData, width, height);

        // Precompute deadlock spaces
        Set<Point> deadlockSpace = findDeadlockSpace(mapData, itemsData, width, height);

        PriorityQueue<Node> queue = makeQueue();    // Priority queue for A* search
        Set<State> visited = new HashSet<>();       // Set of visited states

        queue.add(new Node(initialState, ""));  // Add the initial state
        visited.add(initialState);                   // Mark initial state as visited

        while (!queue.isEmpty()) {
            Node current = queue.poll();        // Get node with the lowest cost

            if (current.state.isGoalState())    // Check if all crates are on goals
                return current.path; 

            // Try all possible moves
            for (Move move : Move.values()) {
                move.tryApply(current.state).ifPresent(nextState -> {
                    if (!visited.contains(nextState) 
                        && !isInDeadlock(deadlockSpace, nextState)) {

                        // Mark as visited and add to queue
                        visited.add(nextState);
                        queue.add(new Node(nextState, current.path + move.toCommand()));
                    }
                });
            }
        }

        return "";
    }

    /**
     * Creates a priority queue for Node objects sorted by their cost.
     * 
     * @return  A priority queue
     */
    public static PriorityQueue<Node> makeQueue() {
        return new PriorityQueue<>(1,
          Comparator.comparingDouble(n -> n.getCost()));
    }

    /**
     * Checks whether a state is in a deadlock.
     * 
     * @param deadlockSpace   Set of points representing corner deadlocks
     * @param nextState       The state to check
     * @return                true if state is deadlocked, false otherwise
     */
    public static boolean isInDeadlock(Set<Point> deadlockSpace, State nextState) {
        Set<Point> goals = nextState.getGoals();
        boolean[][] crateMap = new boolean[nextState.getMap().length][nextState.getMap()[0].length];
        boolean right, down, diag;

        // Build crate map for quick lookup of crates
        for (Point crate : nextState.getCrates()) 
          crateMap[crate.y][crate.x] = true;

        for (Point crate : nextState.getCrates()) {
            if (deadlockSpace.contains(crate))    // Corner deadlocks
                return true;

            if (goals.contains(crate))            // Skip crates on goals
                continue;  
        }

        return false;
    }

    /**
     * Finds deadlock positions on the map.
     * 
     * @param mapData     Map layout
     * @param itemsData   Positions of player and crates
     * @param width       Width of map
     * @param height      Height of map
     * @return            Set of points representing deadlock positions
     */
    public Set<Point> findDeadlockSpace(char[][] mapData, char[][] itemsData, int width, int height) {
        int i, j;
        char tile;
        boolean up, down, left, right;

        Set<Point> deadlockSpaces = new HashSet<>();

        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                tile = mapData[i][j];

                // Skips goals
                if (tile == '#' || tile == '.')
                    continue; 

                // Checks surrounding walls
                up    = i > 0 && mapData[i - 1][j] == '#';
                down  = i < height - 1 && mapData[i + 1][j] == '#';
                left  = j > 0 && mapData[i][j - 1] == '#';
                right = j < width - 1 && mapData[i][j + 1] == '#';

                // Corner deadlocks
                if ((up && left) || (up && right) || (down && left) || (down && right)) {
                    deadlockSpaces.add(new Point(j, i));
                }
            }
        }
        return deadlockSpaces;
    }
}