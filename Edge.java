public class Edge {
	public Vertex v1, v2;
	String movie;
	
	public Edge(Vertex v1, Vertex v2, String movie) {
		this.v1 = v1;
		this.v2 = v2;
		this.movie = movie;
	}
	
	public Vertex get(Vertex v) {
		if (v.equals(v1))
			return v2;
		return v1;
	}
}
