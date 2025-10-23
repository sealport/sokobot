package solver;

import java.awt.Point;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Node {
    public State state;
    public String path;
    public int cost;
    
    public Node(State state, String path) {
        this.state = state;
        this.path = path;
        this.cost = computeCost(state, path);
    }

    public State getState() {
        return this.state;
    }

    public String getPath() {
        return this.path;
    }

    public int getCost() {
        return this.cost;
    }

    // Adds depth + sum of all min distances from crates to goals
    public int computeCost(State state, String path) {
        Set<Point> goals = state.getGoals();
        Set<Point> crates = state.getCrates();

        int cost = path.length();

        for (Point crate : crates) {
            int minDistance = Integer.MAX_VALUE;

            for (Point goal : goals) {
                int[][] hMap = State.heuristicsMap.get(goal);
                
                if(minDistance > hMap[crate.y][crate.x]) {
                    minDistance = hMap[crate.y][crate.x];
                }
            }

            cost += minDistance;
        }

        return cost;
    }
}
