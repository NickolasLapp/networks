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
    private static int[] distance = new int[3];
    private static int[] predecessor = new int[3];
    private static int[] dv = new int[3]; // distance vector array
    private static int inf = 1000000; // infinity weight
    private static int intID = -1; // used to convert char routerID to int
    private static int count = 0;

    public Router() {

    }
    /* source is either 0, 1, or 2 representing X, Y, Z respectively.
       edges[][] is represented as the following matrix:
       0 1 2 
       0 2 7
       1 2 1 
       where first and second columns are nodes, third column is the weights */
    private static void bellmanFord(int source, int[][] edges) {
        // Step 1: initialize graph - only once
        if (count == 0) {
            for (int i = 0; i < 3; i ++) {
                if (i == source) {
                    distance[i] = 0; // weight to self is 0
                } else {
                    distance[i] = inf; // init weights to infinity
                }
                predecessor[i] = -1;
            }
            count++;
        }
        // Step 2: relax edges repeatedly
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((distance[edges[j][0]] + edges[j][2]) < distance[edges[j][1]]) {
                    distance[edges[j][1]] = distance[edges[j][0]] + edges[j][2];
                    predecessor[edges[j][1]] = edges[j][0];
                }
            }
        }
        // Step 3: check for negative-weight cycles
        for (int i = 0; i < 3; i++) {
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
        int [][] setEdges = new int[3][3];
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
        setEdges = createEdges(dv);
        bellmanFord(intID, setEdges); // init distance & predecessor vectors
        System.out.println("EDGES:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(setEdges[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("DISTANCE VECTOR:");
        for (int i = 0; i < 3; i++) {
            System.out.print(distance[i] + " ");
        }
        System.out.println();
        /* Need to make a call to connect and read in data here */
    }

    private static void getdv(List<String> lines, char routerID) {
        String[] dvStr;

        if (routerID == 'X') {
            intID = 0;
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
            intID = 1;
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
            intID = 2;
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

    private static int[][] createEdges(int[] distVec) {
        int[][] edgeList = new int[3][3];

        if (intID == 0) {
            edgeList[0] = new int[]{0, 1, distVec[1]};
            edgeList[1] = new int[]{0, 2, distVec[2]};
            edgeList[2] = new int[]{1, 2, (int)inf};
        } else if (intID == 1) {
            edgeList[0] = new int[]{0, 1, distVec[0]};
            edgeList[1] = new int[]{0, 2, (int)inf};
            edgeList[2] = new int[]{1, 2, distVec[2]};
        } else {
            edgeList[0] = new int[]{0, 1, (int)inf};
            edgeList[1] = new int[]{0, 2, distVec[0]};
            edgeList[2] = new int[]{1, 2, distVec[1]};
        }

        return edgeList;
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
