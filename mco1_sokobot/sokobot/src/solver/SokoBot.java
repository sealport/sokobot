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

public class SokoBot {
  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    State initialState = State.fromLevel(mapData, itemsData, width, height);

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
          if (!visited.contains(nextState)) {
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
}