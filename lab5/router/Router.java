/* Pseudo code from Wikipedia:
   function BellmanFord(list vertices, list edges, vertex source)
   ::distance[],predecessor[]

        for each vertex v in vertices:
            if v is source then distance[v] := 0
            else distance[v] := inf
            predecessor[v] := null

        for i from 1 to size(vertices)-1:
            for each edge (u, v) with weight w in edges:
                if distance[u] + w < distance[v]:
                    distance[v] := distance[u] + w
                    predecessor[v] := u

        for each edge (u, v) with weight w in edges:
            if distance[u] + w < distance[v]:
                error "Graph contains a negative-weight cycle"
        
        return distance[], predecessor[]
 */
package router;

import java.io.*;
import java.util.*;

public class Router {
	private final static String CONF_FILE_NAME = "configuration.txt";
    private static double[] distance = new double[3];
    private static int[] predecessor = new int[3];
    private static int[] vertices = new int[3];
    private static int[][] edges = new int[3][3];
    private static int[] dv = new int[3];

	public Router() {

	}
    /* source is either 0, 1, or 2 representing X, Y, Z respectively.
       edges[][] is represented as the following matrix:
                                0 1 2 
                                0 2 7
                                1 2 1 
       where first and second columns are nodes, third column is the weights */
    private void bellmanFord(int source) {
        double inf = Double.POSITIVE_INFINITY;
        // Step 1: initialize graph
        for (int i = 0; i < 3; i ++) {
            if (i == source) {
                distance[i] = 0;
            } else {
                distance[i] = inf;
            }
            predecessor[i] = -1;
        }
        // Step 2: relax edges repeatedly
        for (int i = 0; i < vertices.length; i++) {
            for (int j = 0; j < edges.length; i++) {
                if ((distance[edges[j][0]] + edges[j][2]) < distance[edges[j][1]]) {
                    distance[edges[j][1]] = distance[edges[j][0]] + edges[j][2];
                    predecessor[edges[j][1]] = edges[j][0];
                }
            }
        }
        // Step 3: check for negative-weight cycles
        for (int i = 0; i < edges.length; i++) {
            if ((distance[edges[i][0]] + edges[i][2]) < distance[edges[i][1]]) {
                System.out.println("ERROR: Graph contains a negative weight"+
                                                                    " cycle");
            }
        }
    }

	public static void main(String args[]) throws IOException {
		int[] port_nums;
		String[] portStr;
		char routerId;
		List<String> lines = new ArrayList<String>();
		File file = new File(CONF_FILE_NAME);
		if (file.isFile() && file.canRead()) {
			Scanner in_file = new Scanner(file);
			while (in_file.hasNextLine()) {
				lines.add(in_file.nextLine());
			}
			in_file.close();
			portStr = lines.get(0).split("\\s+");
			port_nums = new int[portStr.length];

			for (int i = 0; i < portStr.length; i++) {
				port_nums[i] = Integer.parseInt(portStr[i]);
			}
		} else {
			System.out.println("Conf file " + CONF_FILE_NAME + " not found.");
			return;
		}

		routerId = getRouterId();
		if (routerId - 'X' < 0 || routerId - 'X' >= port_nums.length) {
			System.out.println("Invalid router id. Please enter router id"+
              " between 'X' and '"+ (char) ('X' + port_nums.length - 1) + "'");
			return;
		}

		System.out.println("Router " + routerId + " is running on port "
				+ port_nums[routerId - 'X']);
        getdv(lines, routerId);
	}

    private static void getdv(List<String> lines, char routerID) {
        String[] dvStr;
        
        if (routerID == 'X') {
            dvStr = lines.get(1).split("\\s+");
            dv = new int[dvStr.length];
            for (int i = 0; i < dvStr.length; i++) {
                dv[i] = Integer.parseInt(dvStr[i]);
            }
            System.out.println("Distance vector on router X is:");
            System.out.print("<");
            for (int i = 0; i < dv.length; i++){
                if (i == 0 || i == 1)
                    System.out.print(dv[i] + ",");
                else
                    System.out.print(dv[i]);
            }
            System.out.println(">");
        } else if (routerID == 'Y') {
            dvStr = lines.get(2).split("\\s+");
            dv = new int[dvStr.length];
            for (int i = 0; i < dvStr.length; i++) {
                dv[i] = Integer.parseInt(dvStr[i]);
            }
            System.out.println("Distance vector on router Y is:");
            System.out.print("<");
            for (int i = 0; i < dv.length; i++){
                if (i == 0 || i == 1)
                    System.out.print(dv[i] + ",");
                else
                    System.out.print(dv[i]);
            }
            System.out.println(">");
        } else if (routerID == 'Z') {
            dvStr = lines.get(3).split("\\s+");
            dv = new int[dvStr.length];
            for (int i = 0; i < dvStr.length; i++) {
                dv[i] = Integer.parseInt(dvStr[i]);
            }
            System.out.println("Distance vector on router Z is:");
            System.out.print("<");
            for (int i = 0; i < dv.length; i++){
                if (i == 0 || i == 1)
                    System.out.print(dv[i] + ", ");
                else
                    System.out.print(dv[i]);
            }
            System.out.println(">");

        } else {
            System.out.println("Invalid Router ID - please enter X, Y or Z.");
        }            
    }

	private static char getRouterId() throws IOException {
		while (true) {
			BufferedReader inFromUser = new BufferedReader(
					new InputStreamReader(System.in));
			System.out.print("Please enter the router's ID: ");
			String input = inFromUser.readLine();
			if (input.length() > 0)
				return input.charAt(0);
		}
	}
}
