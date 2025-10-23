
package solver;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class SokoBot {

  // public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
  //   /*
  //    * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
  //    */
  //   /*
  //    * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
  //    * sequence
  //    * that just moves left and right repeatedly.
  //    */

  //    char[][] map = getCompleteMap(width, height, mapData, itemsData);

     
  //   try {
  //     Thread.sleep(3000);
  //   } catch (Exception ex) {
  //     ex.printStackTrace();
  //   }
  //   return "lrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlrlr";
  // }

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    State initialState = State.fromLevel(mapData, itemsData);

    Queue<Node> queue = new ArrayDeque<>();
    Set<State> visited = new HashSet<>();

    queue.add(new Node(initialState, ""));
    visited.add(initialState);

    while (!queue.isEmpty()) {
      Node current = queue.poll();

      if (current.state.isGoalState()) {
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
}