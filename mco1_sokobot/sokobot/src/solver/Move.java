package solver;

import java.awt.Point;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum Move {
  UP(-1, 0),
  DOWN(1, 0),
  LEFT(0, -1),
  RIGHT(0, 1);

  private final int rowDelta;
  private final int columnDelta;

  Move(int rowDelta, int columnDelta) {
    this.rowDelta = rowDelta;
    this.columnDelta = columnDelta;
  }

  public char toCommand() {
    switch (this) {
      case UP -> {
          return 'u';
          }
      case DOWN -> {
          return 'd';
          }
      case LEFT -> {
          return 'l';
          }
      case RIGHT -> {
          return 'r';
          }
      default -> throw new IllegalStateException("Unhandled move: " + this);
    }
  }

  public boolean isLegal(State state) {
    return tryApply(state).isPresent();
  }

  public Optional<State> tryApply(State state) {
    int targetRow = state.getPlayerRow() + rowDelta;
    int targetColumn = state.getPlayerColumn() + columnDelta;

    if (!state.isWithinBounds(targetRow, targetColumn) || state.isWall(targetRow, targetColumn)) {
      return Optional.empty();
    }

    if (!state.hasCrateAt(targetRow, targetColumn)) {
      return Optional.of(state.moveTo(targetRow, targetColumn, state.getCrates()));
    }

    int crateRow = targetRow + rowDelta;
    int crateColumn = targetColumn + columnDelta;

    if (!state.isWithinBounds(crateRow, crateColumn)
        || state.isWall(crateRow, crateColumn)
        || state.hasCrateAt(crateRow, crateColumn)) {
      return Optional.empty();
    }

    Set<Point> updatedCrates = copyCrates(state);
    updatedCrates.remove(point(targetColumn, targetRow));
    updatedCrates.add(point(crateColumn, crateRow));

    return Optional.of(state.moveTo(targetRow, targetColumn, updatedCrates));
  }

  public static Set<Point> copyCrates(State state) {
    return state.getCrates().stream()
        .map(Point::new)
        .collect(Collectors.toCollection(HashSet::new));
  }

  public static Point point(int column, int row) {
    return new Point(column, row);
  }
  
  public int getRowDelta() {
    return rowDelta;
  }

  public int getColumnDelta() {
    return columnDelta;
  }
  
}
