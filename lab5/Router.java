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

import java.io.*;
import java.util.*;

public class Router {

    public Router() {

    }
    
    public static void main (String args[]) throws IOException {
        int[] port_nums;
        String[] portStr;
        List<String> lines = new ArrayList<String>();
        File file = new File("configuration.txt");
        Scanner in_file = new Scanner(file);

        while (in_file.hasNextLine()) {
            lines.add(in_file.nextLine());    
        }
        portStr = lines.get(0).split("\\s+");    
        port_nums = new int[portStr.length];

        for(int i = 0; i < portStr.length; i++) {
            port_nums[i] = Integer.parseInt(portStr[i]);
        }
    }
}





