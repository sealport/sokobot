package solver;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents the state of the Sokoban game at any point in time.
 */
public final class State {
    private final int playerRow;      // Row position of the player
    private final int playerColumn;   // Column position of the player
    private final Set<Point> crates;  // Set of positions of the crates
    private final Set<Point> goals;   // Set of positions of the goals
    private final char[][] map;       // 2D array representing the map layout
    public static Map<Point, int[][]> heuristicsMap;  // 2D Heuristic maps for each goal

    /** 
     * Constructs a new state of the board.
     * 
     * @param playerRow     Row position of the player
     * @param playerColumn  Column position of the player
     * @param crates        Set of positions of the crates
     * @param goals         Set of positions of the goals 
     * @param map           2D array representing the map
     */
    public State(int playerRow, int playerColumn, Set<Point> crates, Set<Point> goals, char[][] map) {
      this.playerRow = playerRow;
      this.playerColumn = playerColumn;
      this.crates = Collections.unmodifiableSet(defensiveCopy(crates));
      this.goals = Collections.unmodifiableSet(defensiveCopy(goals));
      this.map = map;
    }

    /**
     * Creates a state object from map and item data.
     * 
     * @param mapData     2D char array representing the map
     * @param itemsData   2D char array representing the player and crates
     * @param width       Map width
     * @param height      Map height
     * @return            State object representing the initial state
     */
    public static State fromLevel(char[][] mapData, char[][] itemsData, int width, int height) {
        Set<Point> crates = new HashSet<>();
        Set<Point> goals = new HashSet<>();
        int playerRow = -1;
        int playerColumn = -1;

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (mapData[row][column] == '.') 
                    goals.add(point(column, row));

                if (itemsData[row][column] == '@') {
                    playerRow = row;
                    playerColumn = column;
                } else if (itemsData[row][column] == '$')
                    crates.add(point(column, row));
            }
        }

        if (playerRow == -1 || playerColumn == -1) {
            throw new IllegalArgumentException("Map does not contain a player '@'");
        }

        heuristicsMap = getHeuristicsMap(goals, mapData, width, height);

        return new State(playerRow, playerColumn, crates, goals, mapData);
    }

    /**
     * Checks if the current state is a goal state.
     * 
     * @return True if state is goal state, false otherwise
     */
    public boolean isGoalState() {
        return goals.containsAll(crates);
    }

    /**
     * Checks if there is a crate at the specified position.
     * 
     * @param row     Row of the position
     * @param column  Column of the position
     * @return        True if there is a crate at the position, false otherwise
     */
    public boolean hasCrateAt(int row, int column) {
        return crates.contains(point(column, row));
    }

    /**
     * Checks if the specified position is a wall.
     * 
     * @param row     Row of the position
     * @param column  Column of the position
     * @return        True if the position is a wall, false otherwise
     */
    public boolean isWall(int row, int column) {
        return map[row][column] == '#';
    }

    /**
     * Checks if the specified point is a wall.
     * 
     * @param point   Point to check
     * @param mapData 2D char array representing the map
     * @return        True if the point is a wall, false otherwise
     */
    public static boolean isWall(Point point, char[][] mapData) {
        return mapData[point.y][point.x] == '#';
    }

    /**
     * Checks if the specified position is within the bounds of the map.
     * 
     * @param row     Row of the position
     * @param column  Column of the position
     * @return        True if the position is within bounds, false otherwise
     */
    public boolean isWithinBounds(int row, int column) {
        return row >= 0 && row < map.length &&
               column >= 0 && column < map[row].length;
    }

    /**
     * Checks if the specified point is within the bounds of the map.
     * 
     * @param point   Point to check
     * @param width   Width of the map
     * @param height  Height of the map
     * @return        True if the point is within bounds, false otherwise
     */
    public static boolean isWithinBounds(Point point, int width, int height) {
        return point.x >= 0 &&
               point.x < width &&
               point.y >= 0 &&
               point.y < height;
    }

    /**
     * Creates a new state with updated player position and crate positions.
     * 
     * @param newPlayerRow      New player row position
     * @param newPlayerColumn   New player column position
     * @param newCrates         New set of crate positions
     * @return                  New state     
     */
    public State moveTo(int newPlayerRow, int newPlayerColumn, Set<Point> newCrates) {
        return new State(newPlayerRow, newPlayerColumn, newCrates, goals, map);
    }

    /**
     * Checks if this state is equal to another state.
     * 
     * @param obj   State to compare
     * @return      True if states are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof State))
            return false;
        
        State other = (State) obj;
        return playerRow == other.playerRow
            && playerColumn == other.playerColumn
            && crates.equals(other.crates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerRow, playerColumn, crates);
    }

    @Override
    public String toString() {
      return "State[row=" + playerRow + ", col=" + playerColumn
          + ", crates=" + crates + "]";
    }

    /** 
     * Creates a defensive copy of the set of points.
     * 
     * @param points    Set of points to copy
     * @return          Copy of the set of points
    */
    public static Set<Point> defensiveCopy(Set<Point> points) {
        return points;
    }

    /**
     * Creates a new Point object from column and row points.
     * 
     * @param column  Column index
     * @param row     Row index
     * @return        Point representing the column and row
     */
    public static Point point(int column, int row) {
        return new Point(column, row);
    }

    /**
     * Returns the player's row position.
     * 
     * @return Player's row
     */
    public int getPlayerRow() {
        return playerRow;
    }

    /**
     * Returns the player's column position.
     * 
     * @return Player's column
     */
    public int getPlayerColumn() {
        return playerColumn;
    }

    /**
     * Returns the set of crate positions.
     * 
     * @return Set of crate positions
     */
    public Set<Point> getCrates() {
        return crates;
    }

    /**
     * Returns the set of goal positions.
     * 
     * @return Set of goal positions
     */
    public Set<Point> getGoals() {
      return goals;
    }

    /**
     * Returns the map layout.
     * 
     * @return 2D char array map
     */
    public char[][] getMap() {
      return map;
    }

    /**
     * Computes a BFS-based heuristic map from the goal to all reachable positions.
     * 
     * @param goal    Goal position
     * @param mapData 2D char array map
     * @param width   Width of the map
     * @param height  Height of the map
     * @return        2D int array of heuristic distances
     */
    public static int[][] getGoalHeuristics(Point goal, char[][] mapData, int width, int height) {
        // Record that stores the coordinates and distance
        record Cell(int x, int y, int distance) {}
        // Stores distance from goal to each cell
        int[][] heuristics = new int[height][width];
        int x, y;

        // BFS Queue
        ArrayDeque<Cell> queue = new ArrayDeque<>();
        // Tracks visited cells
        boolean[][] visited = new boolean[height][width];

        // Start from the goal
        queue.add(new Cell(goal.x, goal.y, 0));
        visited[goal.y][goal.x] = true;
        
        while(!queue.isEmpty()) {
            Cell current = queue.poll();
            heuristics[current.y][current.x] = current.distance;    // Set distance for current cell

            // Explore all 4 moves
            for (Move move : Move.values()) {
                x = current.x + move.getColumnDelta();
                y = current.y + move.getRowDelta();
                
                // If it is within bounds, not a wall, and not yet visited, add cell to the queue.
                if(y >= 0 && y < height && x >= 0 && x < width &&
                  !isWall(new Point(x, y), mapData) && !visited[y][x]) {

                    visited[y][x] = true;
                    queue.add(new Cell(x, y, current.distance + 1));
                }
            }
        }

        return heuristics;
    }

    /**
     * Generates heuristic maps for the available goals.
     * 
     * @param goals   Set of goal positions
     * @param mapData 2D char array map
     * @param width   Width of the map
     * @param height  Height of the map
     * @return        Map of goal points to their heuristic distance maps
     */ 
    public static Map<Point, int[][]> getHeuristicsMap(Set<Point> goals, char[][] mapData, int width, int height) {
      Map<Point, int[][]> heuristicsMap = new HashMap<>();

      for (Point goal : goals)
        heuristicsMap.put(goal, getGoalHeuristics(goal, mapData, width, height));
      
      return heuristicsMap;
    }
}
