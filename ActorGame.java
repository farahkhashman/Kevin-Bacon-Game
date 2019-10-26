import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ActorGame {
	Graph g = new Graph();
	HashMap<String, String> actorCodes = new HashMap<String, String>();
	HashMap<String, String> movieCodes = new HashMap<String, String>();
	HashMap<String, ArrayList<String>> movieActors = new HashMap<String, ArrayList<String>>();
	HashMap<Vertex, Edge> leadto = new HashMap<Vertex, Edge>();
	Scanner scan = new Scanner(System.in);
	Vertex first;
	String choice = "";
	
	public void start() {
		readMovie();
		System.out.println("a. Actor-To-Actor Connectivity");
		System.out.println("b. Actor Connectivity");
		System.out.println("c. Average Connectivity of Actors in Total");
		choice = scan.nextLine();
		if(choice.equals("a")) {
			System.out.println("Enter a base actor.");
			String base = scan.nextLine();
			if(!actorCodes.containsValue(base))
				System.out.println("Actor not found.");
			else {
				System.out.println("Enter target actor.");
				String tofind = scan.nextLine();
				if(!actorCodes.containsValue(tofind))
					System.out.println("Actor not found.");
				else {
					for(String k : g.vertices.keySet()) {
						if(g.vertices.get(k).actor.equals(base)) {
							BFS(g.vertices.get(k), tofind);
							break;
						}
					}
				}
			}
		}
		else if(choice.equals("b")) {
			System.out.println("Enter a base actor.");
			String base = scan.nextLine();
			if(!actorCodes.containsValue(base))
				System.out.println("Actor not found.");
			else {
				for(String k : g.vertices.keySet()) {
					if(g.vertices.get(k).actor.equals(base)) {
						average(g.vertices.get(base));
						break;
					}
				}
			}
		}
	}
	
	private void readMovie() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("movies.txt"));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if(line.length()>1) {
					String[] strings = line.split("\\|");
					movieCodes.put(strings[0], strings[1]);
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found :( make sure file is in the project (not source code) and "
					+ "has the correct name");
		} 
		catch (IOException e) {}
		readActor();
	}
	
	private void readActor() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("actors.txt"));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if(line.length()>1) {
					String[] strings = line.split("\\|");
					actorCodes.put(strings[0], strings[1]);
					first = g.addVertex(strings[1]);
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found :( make sure file is in the project (not source code) and "
					+ "has the correct name");
		} 
		catch (IOException e) {}
		
		readMovieActor();
		
		addEdges();
		//bft(first);
		//BFS(first, "Tom Cruise");
	}
		
	private void readMovieActor() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("movie-actors.txt"));
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				if(line.length()>1) {
					String[] strings = line.split("\\|");
					String moviename = movieCodes.get(strings[0]);
					String actorname = actorCodes.get(strings[1]);
					if(!movieActors.containsKey(moviename)) {
						ArrayList<String> cast = new ArrayList<String>();
						cast.add(actorname);
						movieActors.put(moviename, cast);
					}
					else if(movieActors.containsKey(moviename)) {
						ArrayList<String> casting = movieActors.get(moviename);
						casting.add(actorname);
						movieActors.put(moviename, casting);
					}
				}
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found :( make sure file is in the project (not source code) and "
					+ "has the correct name");
		} 
		catch (IOException e) {}
	}
	
	public void addEdges() {
		for(String keys : movieActors.keySet()) {
			ArrayList<String> cast = movieActors.get(keys);
			for(int i = 0; i<cast.size(); i++) {
				for(int j = i+1; j<cast.size(); j++) {
					g.addEdge(cast.get(i), cast.get(j), keys);
				}
			}
		}
	}
	
	public static void bft(Vertex first) {
		ArrayList<Vertex> toVisit = new ArrayList<Vertex>();
		ArrayList<Vertex> visited = new ArrayList<Vertex>();
		
		toVisit.add(first);
		
		while(!toVisit.isEmpty()) {
			first = toVisit.get(0);
			for(Edge e: first.edges) {
				Vertex neighbour = e.get(first);
				if(!toVisit.contains(neighbour) && !visited.contains(neighbour))
					toVisit.add(neighbour);
			}
			System.out.println(first.actor);
			visited.add(toVisit.remove(0));
		}
	}
	
	public void BFS(Vertex v, String target) {
			ArrayList<Vertex> toVisit = new ArrayList<Vertex>();
			ArrayList<Vertex> visited = new ArrayList<Vertex>();
			boolean found = false;
			Vertex initial = v;
			Vertex person = null;
			
			toVisit.add(v);
			
			if(v.actor.equals(target)) 
				System.out.println("base actor is same as target actor");
			
			else {
				while(!toVisit.isEmpty()) {
					v = toVisit.get(0);
					for(Edge e: v.edges) {
						Vertex neighbour = e.get(v);
						if(!toVisit.contains(neighbour) && !visited.contains(neighbour) && !target.equals(neighbour.actor)) {
							toVisit.add(neighbour);
							leadto.put(neighbour, e);
						}	
						else if(target.equals(neighbour.actor)) {
							leadto.put(neighbour, e);
							person = neighbour;
							found = true;
							break;
						}
					}
					if(found)
						break;
					visited.add(toVisit.remove(0));
				}
				if(!found) 
					System.out.println("No Connection.");
			}
	}
	
	public void backtrace(Vertex initial, Vertex finalactor) {
		Vertex s = finalactor;
		Vertex nextactor = null;
		int count = 0;
		while(!s.equals(initial)) {
			for(Edge e: leadto.values()) {
				if(e.equals(leadto.get(s))) {
					nextactor = e.get(s);
					break;
				}
			}
			count++;
			System.out.println(s.actor +" is connected to "+ nextactor.actor+ " through " +leadto.get(s).movie);
			s = nextactor;
		}
		System.out.print("The connectivity is "+ count);
	}
	
	public int Search(Vertex v, String target) {
		ArrayList<Vertex> toVisit = new ArrayList<Vertex>();
		ArrayList<Vertex> visited = new ArrayList<Vertex>();
		boolean found = false;
		Vertex initial = v;
		Vertex person = null;
		
		toVisit.add(v);
		
		if(v.actor.equals(target)) 
			System.out.println("base actor is same as target actor");
		
		else {
			while(!toVisit.isEmpty()) {
				v = toVisit.get(0);
				for(Edge e: v.edges) {
					Vertex neighbour = e.get(v);
					if(!toVisit.contains(neighbour) && !visited.contains(neighbour) && !target.equals(neighbour.actor)) {
						toVisit.add(neighbour);
						leadto.put(neighbour, e);
					}	
					else if(target.equals(neighbour.actor)) {
						leadto.put(neighbour, e);
						person = neighbour;
						found = true;
					}
				}
				if(found)
					break;
				visited.add(toVisit.remove(0));
			}
		}
		if(!found) {
			System.out.print("No Connection");
			return connection(0);
		}
		else {
			return connection(initial, person);
		}
}
	
	public int connection(int x) {
		return 0;
	}
	
	public int connection(Vertex initial, Vertex finalactor) {
		Vertex s = finalactor;
		Vertex nextactor = null;
		int count = 0;
		while(!s.equals(initial)) {
			for(Edge e: leadto.values()) {
				if(e.equals(leadto.get(s))) {
					nextactor = e.get(s);
					break;
				}
			}
			count++;
			s = nextactor;
		}
		return count;
	}
	
	
	public void average(Vertex actor) {
		int average = 0;
		int count = 0;
		for(String keys : actorCodes.keySet()) {
			if(!actorCodes.get(keys).equals(actor.actor)) {
				average += Search(actor, actorCodes.get(keys));
				System.out.println(average);
				count++;
			}
		}
		System.out.println("The average connectivity for "+ actor.actor+ " is " +(average/count));
	}
		
	public static void main(String[] args) {
	ActorGame test = new ActorGame();
	test.start();
	}
	
}