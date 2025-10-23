package solver;

public class Node {
    public State state;
    public String path;
    
    public Node(State state, String path) {
        this.state = state;
        this.path = path;
    }
}
