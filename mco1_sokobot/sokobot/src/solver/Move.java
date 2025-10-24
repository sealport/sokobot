package solver;

import java.awt.Point;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum representing the possible moves in Sokoban which are up, down, left, and right.
 */
public enum Move {
	UP(-1, 0),				// Moves the bot up
	DOWN(1, 0),	// Moves the bot down
	LEFT(0, -1),				// Moves the bot left
	RIGHT(0, 1);	// Moves the bot right

	private final int rowDelta;			// Change in row when this move is applied.
	private final int columnDelta;		// Change in column when this move is applied.

	/**
     * Constructor to initialize the row and column delta for the move.
     *
     * @param rowDelta      Change in row
     * @param columnDelta   Change in column
     */
	Move(int rowDelta, int columnDelta) {
    	this.rowDelta = rowDelta;
    	this.columnDelta = columnDelta;
  	}

	/**
     * Returns the character command corresponding to this move.
     *
     * @return 'u' for up, 'd' for down, 'l' for left, 'r' for right
     */
	public char toCommand() {
		switch (this) {
			case UP -> { return 'u'; }
			case DOWN -> { return 'd'; }
			case LEFT -> { return 'l'; }
			case RIGHT -> { return 'r'; }
			default -> throw new IllegalStateException("Unhandled move: " + this);
		}
  	}

	/**
     * This attempts to apply a move to the given state. This would check walls, 
     * crate collisions, and moves crates if possible.
     *
     * @param state     Current state of the game
     * @return          Optional containing the new state if the move is valid, empty otherwise
     */
	public Optional<State> tryApply(State state) {
		int targetRow = state.getPlayerRow() + rowDelta;
		int targetColumn = state.getPlayerColumn() + columnDelta;
		int crateRow = targetRow + rowDelta;
		int crateColumn = targetColumn + columnDelta;

		// If target cell is wall or out of bounds, return empty
		if (!state.isWithinBounds(targetRow, targetColumn) || 
			 state.isWall(targetRow, targetColumn))
			return Optional.empty();

		// If no crate at target cell, move player there
		if (!state.hasCrateAt(targetRow, targetColumn))
			return Optional.of(state.moveTo(targetRow, targetColumn, state.getCrates()));

		crateRow = targetRow + rowDelta;
		crateColumn = targetColumn + columnDelta;

		// If crate cannot be moved, return empty
		if (!state.isWithinBounds(crateRow, crateColumn)
			|| state.isWall(crateRow, crateColumn)
			|| state.hasCrateAt(crateRow, crateColumn))
			return Optional.empty();

		// Move crate
		Set<Point> updatedCrates = copyCrates(state);
		updatedCrates.remove(point(targetColumn, targetRow));
		updatedCrates.add(point(crateColumn, crateRow));

		return Optional.of(state.moveTo(targetRow, targetColumn, updatedCrates));
	}
	
	/**
	 * Creates a defensive copy of the set of crates.
	 * 
	 * @param state Current state of the game
	 * @return		Copy of the set of crates in the given state
	 */
	public static Set<Point> copyCrates(State state) {
		return state.getCrates().stream()
			.map(Point::new)
			.collect(Collectors.toCollection(HashSet::new));
	}

	/**
     * Creates a Point object from column and row coordinates.
     *
     * @param column 	Column index
     * @param row 		Row index
     * @return 			Point representing the column and row
     */
	public static Point point(int column, int row) {
		return new Point(column, row);
	}
	
	/**
     * Returns the row delta of this move.
     *
     * @return Change in row
     */
	public int getRowDelta() {
		return rowDelta;
	}

	/**
     * Returns the column delta of this move.
     *
     * @return Change in column
     */
	public int getColumnDelta() {
		return columnDelta;
	}
}