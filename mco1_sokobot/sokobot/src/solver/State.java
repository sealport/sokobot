package solver;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public final class State {
  private final int playerRow;
  private final int playerColumn;
  private final Set<Point> crates;
  private final Set<Point> goals;
  private final char[][] map;

  public static Map<Point, int[][]> heuristicsMap;

  public State(int playerRow, int playerColumn, Set<Point> crates, Set<Point> goals, char[][] map) {
    this.playerRow = playerRow;
    this.playerColumn = playerColumn;
    this.crates = Collections.unmodifiableSet(defensiveCopy(crates));
    this.goals = Collections.unmodifiableSet(defensiveCopy(goals));
    this.map = map;
  }

  public static State fromLevel(char[][] mapData, char[][] itemsData, int width, int height) {
    Set<Point> crates = new HashSet<>();
    Set<Point> goals = new HashSet<>();
    int playerRow = -1;
    int playerColumn = -1;

    for (int row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        if (mapData[row][column] == '.') {
          goals.add(point(column, row));
        }
        if (itemsData[row][column] == '@') {
          playerRow = row;
          playerColumn = column;
        } else if (itemsData[row][column] == '$') {
          crates.add(point(column, row));
        }
      }
    }

    if (playerRow == -1 || playerColumn == -1) {
      throw new IllegalArgumentException("Map does not contain a player '@'");
    }

    heuristicsMap = getHeuristicsMap(goals, mapData, width, height);

    return new State(playerRow, playerColumn, crates, goals, mapData);
  }

  public boolean isGoalState() {
    return goals.containsAll(crates);
  }

  public boolean hasCrateAt(int row, int column) {
    return crates.contains(point(column, row));
  }

  public boolean isWall(int row, int column) {
    return map[row][column] == '#';
  }

  public static boolean isWall(Point point, char[][] mapData) {
    return mapData[point.y][point.x] == '#';
  }

  public boolean isWithinBounds(int row, int column) {
    return row >= 0 && row < map.length
        && column >= 0 && column < map[row].length;
  }

  public static boolean isWithinBounds(Point point, int width, int height) {
    return point.x >= 0
           && point.x < width
           && point.y >= 0
           && point.y < height;
  }

  public State moveTo(int newPlayerRow, int newPlayerColumn, Set<Point> newCrates) {
    return new State(newPlayerRow, newPlayerColumn, newCrates, goals, map);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof State)) {
      return false;
    }
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

  public static Set<Point> defensiveCopy(Set<Point> points) {
    return points.stream()
        .map(point -> new Point(point))
        .collect(Collectors.toCollection(HashSet::new));
  }

  public static Point point(int column, int row) {
    return new Point(column, row);
  }

  public int getPlayerRow() {
    return playerRow;
  }

  public int getPlayerColumn() {
    return playerColumn;
  }

  public Set<Point> getCrates() {
    return crates;
  }

  public Set<Point> getGoals() {
    return goals;
  }

  public char[][] getMap() {
    return map;
  }

  // BFS algorithm to compute for heuristics and store in an int[][] array
  public static int[][] getGoalHeuristics(Point goal, char[][] mapData, int width, int height) {
    record Cell(Point point, int distance) {}
    
    int[][] heuristics = new int[height][width];

    Queue<Cell> queue = new LinkedList<>();
    Set<Point> visited = new HashSet<>();

    queue.add(new Cell(goal, 0));
    visited.add(goal);
    
    while(!queue.isEmpty()) {
      Cell current = queue.poll();
      Point p = current.point();
      int d = current.distance();

      heuristics[p.y][p.x] = d;

      for (Move move : Move.values()) {
        Point next = new Point(p.x + move.getColumnDelta(), 
                               p.y + move.getRowDelta()
        );

        if(isWithinBounds(next, width, height)) {
          if(!isWall(next, mapData)
          && !visited.contains(next)) {

            visited.add(next);
            queue.add(new Cell(next, d+1));
          }
        }
      }
    }
    return heuristics;
  }

  public static Map<Point, int[][]> getHeuristicsMap(Set<Point> goals, char[][] mapData, int width, int height) {
    Map<Point, int[][]> heuristicsMap = new HashMap<>();

    for (Point goal : goals) {
      int[][] matrix = getGoalHeuristics(goal, mapData, width, height);

      heuristicsMap.put(goal, matrix);
    }
    
    return heuristicsMap;
  }
}
