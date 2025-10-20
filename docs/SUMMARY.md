## Implementation flow for full compliance with the Sokoban AI project specifications

---

### **Phase 1 — Setup and Familiarization**

1. **Compile and Run Starter Code**

   * Verify `javac` and `java` commands work.
   * Test both modes:

     * `freeplay testlevel`
     * `sokobot testlevel`
   * Ensure the window and controls function properly.
2. **Inspect Project Structure**

   * Focus on the `solver` package and the `SokoBot` class.
   * Locate the `solveSokobanPuzzle()` method and understand its parameters (`width`, `height`, `mapData`, `itemsData`).

---

### **Phase 2 — Game State Modeling**

1. **Define State Representation**

   * Create a `State` class to encapsulate:

     * Player position `(x, y)`
     * Crate positions `(Set<Point> crates)`
     * Board layout references
   * Implement `equals()` and `hashCode()` for proper state comparison.
2. **Define Legal Moves**

   * Enumerate moves `UP, DOWN, LEFT, RIGHT`.
   * Implement validation:

     * Player cannot move into walls.
     * Crates can be pushed only if the next cell is empty and within bounds.

---

### **Phase 3 — Search Algorithm Design**

1. **Choose Base Algorithm**

   * Start with **Breadth-First Search (BFS)** for guaranteed optimality in shallow puzzles.
   * Optimize later with:

     * **A*** (A-star) search using heuristics.
     * **Iterative Deepening A*** (IDA*) for memory efficiency.
2. **Heuristic Function (for A*)**

   * Use Manhattan distance between crates and nearest targets.
   * Optionally penalize crates pushed into corners that are not targets.
3. **Search Flow**

   * Maintain:

     * `Queue<State>` for frontier.
     * `HashSet<State>` for visited states.
     * Map for predecessor tracking to reconstruct move sequence.

---

### **Phase 4 — Implementation in Java**

1. **Inside `solveSokobanPuzzle()`**

   * Initialize state from `itemsData`.
   * Run search algorithm to find goal state (all crates on targets).
   * Return a move string: `'u', 'd', 'l', 'r'`.
2. **Helper Classes**

   * `Node` class for storing `State`, `parent`, `move`, and `cost`.
   * `Move` enum for readability.
3. **Goal Test**

   * Check if every crate position overlaps a target in `mapData`.

---

### **Phase 5 — Performance Optimization**

1. **Pruning**

   * Detect deadlocks (e.g., crate in corner not on target).
   * Avoid revisiting mirrored or symmetric states.
2. **Time Constraint**

   * Enforce 15-second cutoff via `System.currentTimeMillis()` check.
   * Return partial or best-found path if timeout occurs.

---

### **Phase 6 — Evaluation and Testing**

1. **Test on Multiple Levels**

   * Start with simple 1–2 crate puzzles.
   * Scale up to 8 crates.
   * Measure:
     * Solvability rate.
     * Move efficiency.
     * Computation time.
     
2. **Edge Cases**

   * Crates already on targets.
   * Player starting surrounded by crates/walls.

---

### **Phase 7 — Documentation (Report)**

Structure the report as:

1. **Algorithm Explanation**

   * State model, move generation, search algorithm, and heuristics.
2. **Evaluation and Performance**

   * Metrics: number of solved levels, average moves, computation time.
3. **Challenges**

   * Discuss deadlock detection, search space explosion, and pruning.
4. **Contributions**

   * Table of member roles.

---

### **Phase 8 — Packaging and Submission**

* Ensure:

  * All `.java` files compile.
  * `maps/` directory is included.
  * Report is ≤4 pages (PDF).
* Create final ZIP with source code and report.

---