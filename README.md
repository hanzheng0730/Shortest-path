# Shortest-path
This is an implementation of a flight scheduling system using Dijkstra’s Algorithm.
## Motivation
A major concern for passengers travelling by air is to look for the lowest cost or shortest travelling distance from the origin to the destination. To cope with the increasing complexity of the airline network, the air carriers around the world are constantly upgrading their routing system to provide the appropriate choices of routes to their valued customers in an efficient manner. A good system should allow adding airports and routes easily, and more importantly calculating the route fast. Our project has been motivated by the demand of an efficient passenger service system. We take the United States domestic airline network as a reprehensive and use graph theory to model the real airports and real airways. In detail, graphs provide a discrete structure consisting of vertices and these vertices are connected together by edges. In this project, the airway network is modeled by a directed graph where a vertex represents an airport and an edge represents the route between two airports. The weight of an edge could be the price or the distance. Therefore, the problem of looking for the shortest route between two airports is equivalent to looking for the shortest path between two vertices on the graph.

There are a number of algorithms for solving the shortest path problem. For this project, we chose to firstly implement the Dijkstra’s algorithm which has been identified as one of the significant algorithms that are able to find the shortest path between two vertices with a low computational cost. In order to verify the results of the Dijkstra’s algorithm and also compare the efficiencies of different shortest path algorithms, we later implemented the Floyd–Warshall Algorithm. We will discuss about the performances of the two algorithms in the following sections.
## Basic architecture and main data structure
### App.java:
●	ArrayList is used to store the origin airports, destination airports, price, and distance data read from the file.
### LinkedGraph.java:
●	LinkedList is used to store and manipulate the airports as nodes.
●	Iterator is used to loop through the LinkedList.
●	HashMap is used to store the solution map as a key value pair. The key is the airport code and the value is the shortest path value from this airport to the origin airport.
●	Array is used to store (1) the distance between the origin airport to all other airports (declared as int dist[]), (2) the Boolean value for each airport (true for visited, false for unvisited) (declared as Boolean sptSet[]), (3) each airport’s nearest neighbor in the shortest path (declared as int touch[]).
●	Stack is used to store the nearest neighbors of the destination airport in an order so that the result could be printed out following the sequence from the origin to the destination.
## Main components of the algorithm
“int dist []” is created to store the distance value from the origin airport to all airports. “Boolean sptSet[]” is created to mark each airport as visited or unvisited (true for visited, false for unvisited). The distance value for the origin airport is set to 0 and therefore the origin airport is picked first after iteratively running the method minDistance(dist, sptSet, N) which keeps looking for the index of the airport with the shortest distance in the collection of unvisited airports. "touch[]" is used to store each vertex’s previous vertex along the shortest path from the origin so that the path can be printed out. The time complexity of this Algorithm is O(n^2). Below is the detail:
 
public void dijkstra(V org, V dest) {
		long startTime = System.currentTimeMillis();
		int N = VElist.size();
		int dist[] = new int[N]; // dist[i] holds the shortest distance from origin to i.
		Boolean sptSet[] = new Boolean[N]; // sptSet[i] is true if vertex i is visited.

		for (Vertex ve : VElist) {
			int in = VElist.indexOf(ve);
			dist[in] = Integer.MAX_VALUE; // Initialize all distances. The distance is set to max Int if the vertex is not immediately adjacent to the origin.
			sptSet[in] = false; //Initialize all vertices as unvisited.
			if (ve.v.equals(org)) {
				dist[in] = 0; //Assign the distance value as 0 for the origin airport so that it is picked first.
				Iterator<Edge<V>> iter = getVE(org).edgeList.iterator();
				while (iter.hasNext()) {
					Edge<V> edge = iter.next();
					dist[VElist.indexOf(getVE(edge.getDestination()))] = edge.getWeight(); //For all the vertices which are immediately adjacent to the origin, assign its dist value as the weight of the edge to the origin.
				}
			}
		}

		int touch[] = new int[N]; //Store all vertices’ nearest neighbor in the shortest path.
		for (int i = 0; i < N; i++) {
			touch[i] = VElist.indexOf(getVE(org)); //Initialize all vertices’ nearest neighbor as the origin.
		}

		// Below code block is to find the shortest path for all vertices.
		for (int count = 0; count < N - 1; count++) {
			int u = minDistance(dist, sptSet, N); // Pick the minimum distance vertex from the set of vertices not yet processed. u is equal to the index of org in first iteration.		
			sptSet[u] = true; // Mark the picked vertex as visited.

			// Below code block is to update the dist value of the adjacent vertices of the picked vertex.
			for (int i = 0; i < N; i++) {
				if (get(u, i) != null) { //once there is an edge between u and i then enter. 
					if (!sptSet[i] && get(u, i).getWeight() != 0 &&
							dist[u] != Integer.MAX_VALUE &&
							dist[u] + get(u, i).getWeight() < dist[i]) {
						dist[i] = dist[u] + get(u, i).getWeight(); // Update dist[i] only if sptSet[i] is false, and there is an weight for edge, and dist[u] is not equal to max int, and the total weight of path from origin to i through u is smaller than the current value dist[i].
						touch[i] = u; //update the nearest neighbor of vertex i to u.
					}
				}
			}
		}
		HashMap<V, Integer> cityWeightIndex = solutionMap(dist, N); //Store the solution map.
		System.out.println(cityWeightIndex.get(dest));
		int t = touch[VElist.indexOf(getVE(dest))];
		System.out.print("The route for dijkstra is: ");
		Stack st = new Stack(); //Store the nearest neighbors of the destination airport in an order so that the result could be printed out following the sequence from the origin to the destination.
		while (t != VElist.indexOf(getVE(org))) {
			st.push(get(t));
			t = touch[t];
		}
		System.out.print(org);
		while (!st.empty()) {
			System.out.print("->" + st.pop());
		}
		System.out.println("->" + dest);
		long endTime = System.currentTimeMillis();
		System.out.println("dijkstra time is "  +  (endTime - startTime));
	}

private int minDistance(int dist[], Boolean sptSet[], int n) { 
		int min = Integer.MAX_VALUE, min_index = -1; //Initialize min value to max int and min_index value to -1

		for (int i = 0; i < n; i++)
			if (sptSet[i] == false && dist[i] <= min) {
				min = dist[i];
				min_index = i;
			}
		return min_index; //find the index of the vertex with minimum dist value in the collection of the unvisited vertices.
	}

public HashMap<V, Integer> solutionMap(int dist[], int n) {
		HashMap<V, Integer> cityIndex = new HashMap<V, Integer>();
		for (int i = 0; i < n; i++) {
			V c = get(i);
			int d = dist[i];
			cityIndex.put(c, d);
		}
		return cityIndex; //Return the solution map as a key value pair. The key is the airport code and the value is the shortest path value from this airport to the origin airport.
	}
  ## Conclusion
  A well-designed data structure is very important to improve the data usage performance. As for the shortest path algorithm, Dijkstra only calculates single source to single destination shortest path. Floyd algorithm will be implemented to get the shortest path between all pairs of nodes.
