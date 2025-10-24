package solver;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a node in the Sokobot search tree, each having a state, path, and cost.
 */
public class Node {
    public State state;     // Current state of the board
    public String path;     // Path taken from initial state to this state
    public int cost;        // Total cost of this node
    
    /**
     * Constructs a new Node with the given state and path.
     * 
     * @param state Current state of the board 
     * @param path  String representation of the moves taken
     */
    public Node(State state, String path) {
        this.state = state;
        this.path = path;
        this.cost = computeCost(state, path);
    }

    /**
     * Returns the state of this node.
     * 
     * @return Current state
     */
    public State getState() {
        return this.state;
    }

    /**
     * Returns the path string that represents the moves taken to reach this node.
     * 
     * @return Path string
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Returns the total cost of this node.
     * 
     * @return  Total cost
     */
    public int getCost() {
        return this.cost;
    }

    /**
     * Computes the total cost of a node.
     * 
     * @param state Current state
     * @param path  Path taken to reach this state
     * @return      Total cost
     */
    public int computeCost(State state, String path) {
        Set<Point> goals = state.getGoals();
        Set<Point> crates = state.getCrates();
        Set<Point> availableGoals = new HashSet<>(goals);   // Each crate assigned to only one goal
        int[][] hMap;
        int gcost = path.length();
        int hcost = 0;
        int dist, minDistance;

        // Skip crates already on goals
        for (Point crate : crates) {
            if (goals.contains(crate)) {
                availableGoals.remove(crate);
                continue;
            }

            minDistance = Integer.MAX_VALUE;    // Start at a large value
            Point closestGoal = null;

            // Find closest goal for this crate
            for (Point goal : availableGoals) {
                hMap = State.heuristicsMap.get(goal);
                
                if (hMap == null) 
                    continue; 
                
                dist = hMap[crate.y][crate.x];

                if (dist < minDistance) {       // If new distance is smaller than previous distance,
                    minDistance = dist;         // assigns goal to be removed and new distance.
                    closestGoal = goal;
                }
            }

            if (minDistance == Integer.MAX_VALUE)   // Makes it least prioritize for queue
                hcost += 1_000_000; 
            else {
                hcost += minDistance;
                availableGoals.remove(closestGoal);
            }
        }

        return gcost + hcost;       // Return total cost
    }
}