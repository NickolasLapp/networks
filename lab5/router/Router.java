/* Pseudo code from Wikipedia:
   function BellmanFord(list vertices, list edges, vertex source)
   ::distance[],predecessor[]

        // Step 1: initialize graph
        for each vertex v in vertices:
            if v is source then distance[v] := 0
            else distance[v] := inf
            predecessor[v] := null

        // Step 2: relax edges repeatedly
        for i from 1 to size(vertices)-1:
            for each edge (u, v) with weight w in edges:
                if distance[u] + w < distance[v]:
                    distance[v] := distance[u] + w
                    predecessor[v] := u

        // Step 3: check for negative-weight cycles
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

	public Router() {

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
		if (routerId - 'x' < 0 || routerId - 'x' >= port_nums.length) {
			System.out
					.println("Invalid router id. Please enter router id between 'x' and '"
							+ (char) ('x' + port_nums.length - 1) + "'");
			return;
		}

		System.out.println("Router ID: " + routerId + " is running on "
				+ port_nums[routerId - 'x']);
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
