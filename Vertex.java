
import java.util.ArrayList;

public class Vertex {
	public ArrayList<Edge> edges;
	public String actor;
	
	public Vertex(String val) {
		actor = val;
		edges = new ArrayList<Edge>();
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
}
