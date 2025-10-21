
package solver;

import java.util.EnumSet;
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
    Set<Move> legalMoves = computeLegalMoves(initialState);
    return legalMoves.stream()
        .map(move -> String.valueOf(move.toCommand()))
        .collect(Collectors.joining());
  }

  public Set<Move> computeLegalMoves(State state) {
    EnumSet<Move> legalMoves = EnumSet.noneOf(Move.class);
    for (Move move : Move.values()) {
      if (move.isLegal(state)) {
        legalMoves.add(move);
      }
    }
    return legalMoves;
  }
}