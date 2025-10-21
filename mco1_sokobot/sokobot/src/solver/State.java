package solver;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class State {
  private final int playerRow;
  private final int playerColumn;
  private final Set<Point> crates;
  private final Set<Point> goals;
  private final char[][] map;

  public State(int playerRow, int playerColumn, Set<Point> crates, Set<Point> goals, char[][] map) {
    this.playerRow = playerRow;
    this.playerColumn = playerColumn;
    this.crates = Collections.unmodifiableSet(defensiveCopy(crates));
    this.goals = Collections.unmodifiableSet(defensiveCopy(goals));
    this.map = map;
  }

  public static State fromLevel(char[][] mapData, char[][] itemsData) {
    Set<Point> crates = new HashSet<>();
    Set<Point> goals = new HashSet<>();
    int playerRow = -1;
    int playerColumn = -1;

    for (int row = 0; row < mapData.length; row++) {
      for (int column = 0; column < mapData[row].length; column++) {
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

  public boolean isWithinBounds(int row, int column) {
    return row >= 0 && row < map.length
        && column >= 0 && column < map[row].length;
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
}
