package solver;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import reader.MapData;

public class SokoBot {
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    State initialState = State.fromLevel(mapData, itemsData, width, height);

    Set<Point> deadlockSpace = findDeadlockSpace(mapData, itemsData, width, height); // new HashSet<>()

    PriorityQueue<Node> queue = makeQueue();
    Set<State> visited = new HashSet<>();

    queue.add(new Node(initialState, ""));
    visited.add(initialState);

    while (!queue.isEmpty()) {
      Node current = queue.poll();

      if (current.state.isGoalState()) {
        System.out.println(current.path); // NOTE: this is used for checking, not sure
        return current.path; 
      }

      for (Move move : Move.values()) {
        move.tryApply(current.state).ifPresent(nextState -> {
          if (!visited.contains(nextState) 
              && !isInDeadlock(deadlockSpace, nextState)) {

            visited.add(nextState);
            queue.add(new Node(nextState, current.path + move.toCommand()));
          }
        });
      }
    }

    return "";
  }

  public static PriorityQueue<Node> makeQueue() {
    return new PriorityQueue<>(1,
      Comparator.comparingDouble(n -> n.getCost())
    );
  }

  public static boolean isInDeadlock(Set<Point> deadlockSpace, State nextState) {
    Set<Point> crates = nextState.getCrates();

    for (Point crate : crates) {
      if(deadlockSpace.contains(crate))
        return true;
    }

    return false;
  }

  public Set<Point> findDeadlockSpace(char[][] mapData, char[][] itemsData, int width, int height)
  {
      int i, j;
      char item, tile;
      boolean checkUp, checkDown, checkLeft, checkRight;

      Set<Point> deadlockSpaces = new HashSet<>();

      for (i = 0 ; i < height ; i++) {
          for (j = 0 ; j < width ; j++) {
              item = itemsData[i][j];
              tile = mapData[i][j];

              if (tile == '#' || tile == '.') {
                  continue;
              }

              if (i > 0 && mapData[i - 1][j] == '#') {
                  checkUp = true;
              }
              else {
                  checkUp = false;
              }

              if (i < height - 1 && mapData[i + 1][j] == '#') {
                  checkDown = true;
              }
              else {
                  checkDown = false;
              }

              if (j > 0 && mapData[i][j - 1] == '#') {
                  checkLeft = true;
              }
              else {
                  checkLeft = false;
              }

              if (j < width - 1 && mapData[i][j + 1] == '#') {
                  checkRight = true;
              }
              else {
                  checkRight = false;
              }

              if ((checkUp && checkLeft) || (checkUp && checkRight) || (checkDown && checkLeft) || (checkDown && checkRight))
              {
                deadlockSpaces.add(new Point(j, i));
              }

          }
      }

      return deadlockSpaces;
  }
  
}
