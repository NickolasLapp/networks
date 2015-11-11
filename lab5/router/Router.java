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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.*;

public class Router {
	private static DatagramSocket recvSock = null;
    private final static String CONF_FILE_NAME = "configuration.txt";
    private static int[] distance = new int[3];
    private static int[] predecessor = new int[3];
    private static int[] dv = new int[3]; // distance vector array
    private static int inf = 1000000; // infinity weight
    private static int intID = -1; // used to convert char routerID to int
    private static int count = 0;
	private static boolean printNew = false;
	private static boolean[] recvdFrom = { false, false, false };

    public Router() {

    }
    /* source is either 0, 1, or 2 representing X, Y, Z respectively.
       edges[][] is represented as the following matrix:
       0 1 2 
       0 2 7
       1 2 1 
       where first and second columns are nodes, third column is the weights */
	private static boolean bellmanFord(int source, int[][] edges, char ridC) {
        boolean didUpdate = false;
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
        }
        // Step 2: relax edges repeatedly
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ((distance[edges[j][0]] + edges[j][2]) < distance[edges[j][1]]) {
                    distance[edges[j][1]] = distance[edges[j][0]] + edges[j][2];
                    predecessor[edges[j][1]] = edges[j][0];
                    didUpdate = true;
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
		if (count > 0 && didUpdate && printNew) {
            System.out.println("Distance vector on router " + ridC + 
                    " is updated to:");
            System.out.print("<");
            for (int i = 0; i < 3; i++){
                if (i == 0 || i == 1)
                    System.out.print(distance[i] + ", ");
                else
                    System.out.print(distance[i]);
            }
            System.out.println(">");
        } else if (count > 0) {
            System.out.println("Distance vector on router " + ridC +
                    " is not updated");
		} else {
			count++;
			printNew = true;
        }
		return didUpdate;
    }

	public static void main(String args[]) throws Exception {
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
		setEdges = createEdges(dv, intID);
        bellmanFord(intID, setEdges, routerId); // init distance vector
		runRouter(intID, port_nums);
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

	private static int[][] createEdges(int[] distVec, int myID) {
        int[][] edgeList = new int[3][3];

		if (myID == 0) {
            edgeList[0] = new int[]{0, 1, distVec[1]};
            edgeList[1] = new int[]{0, 2, distVec[2]};
            edgeList[2] = new int[]{1, 2, (int)inf};
		} else if (myID == 1) {
            edgeList[0] = new int[]{1, 0, distVec[0]};
            edgeList[1] = new int[]{1, 2, distVec[2]};
            edgeList[2] = new int[]{0, 2, (int)inf};
        } else {
            edgeList[0] = new int[]{2, 0, distVec[0]};
            edgeList[1] = new int[]{2, 1, distVec[1]};
            edgeList[2] = new int[]{0, 1, (int)inf};
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

	// Main method; If first time, or bellmanford indicates dv update, send
	// packets
	// Always listen for packets to receive
	private static void runRouter(int routerId, int[] ports) throws Exception {
		char newRouterId = 'A';
		boolean first = true;


		while (true) {
			char tempRouterID = receivePackets(routerId, ports[routerId]);
			if (!first)
				newRouterId = tempRouterID;
			else
				first = false;

			Thread.sleep(10000);
			if (newRouterId != 'N') {
				int[] distanceWithID = new int[distance.length + 1];
				for (int i = 0; i < distance.length; i++)
					distanceWithID[i] = distance[i];
				distanceWithID[distanceWithID.length - 1] = routerId + 'X';

				if(newRouterId == 'A')
					sendPacket(routerId, ports, toByteArray(distanceWithID));
				else
					sendPacket(routerId, ports[(int) (newRouterId - 'X')],
							toByteArray(distanceWithID));
				newRouterId = 'N';
			}

		}
	}

	// this function listens for packets on the router's port. When received, it
	// converts them to an int array to put them into the edges[][] matrix and
	// then to run belmann ford.
	// It detects packets received with a 500mS timeout.
	private static char receivePackets(int routerId, int port)
			throws IOException {
		char toRet = 'N';
		while (true) {
			try {
				if (recvSock == null)
					recvSock = new DatagramSocket(port);
				byte[] data = new byte[1024];
				DatagramPacket recvPacket = new DatagramPacket(data,
						data.length);
				recvSock.setSoTimeout(500);
				recvSock.receive(recvPacket);

				if (recvPacket.getLength() != 4)
					System.out
							.println("You are reading more than 4 bytes. Bytes read = "
									+ recvPacket.getLength());

				int[] distanceRecvd = new int[recvPacket.getLength()-1];
				for (int i = 0; i < recvPacket.getLength()-1; i++)
					distanceRecvd[i] = (int) data[i];
				
				char routerChar = (char)data[recvPacket.getLength()-1];
				System.out.println("Receives distance vector from Router "
						+ routerChar + ": <" + distanceRecvd[0] + ", "
						+ distanceRecvd[1] + ", " + distanceRecvd[2] + ">");
				
				int[][] edges = createEdges(distanceRecvd,
						(int) routerChar - 'X');

				toRet = bellmanFord(intID, edges, (char) (intID + 'X')) ? 'A'
						: toRet;
				if (!recvdFrom[(int) routerChar - 'X']) {
					// System.out.println("First time receiving from router: "
					// + routerChar + " Force update send");
					recvdFrom[(int) routerChar - 'X'] = true;
					toRet = toRet == 'A' ? 'A' : routerChar;
				}

			} catch (SocketTimeoutException se) {
				// timeout expired
				return toRet;
			}
		}
	}

	// This function sends the router's Distance Vector to it's neighbors
	private static void sendPacket(int routerId, int[] ports, byte[] data)
			throws Exception {
		DatagramSocket sendSock = new DatagramSocket();
		// System.out.println("\n\nIn sendPacket, ports = " + ports[0] + ", "
		// + ports[1] + ", " + ports[2] + " and routerId = " + routerId);
		for (int i = 0; i < ports.length; i++) {
			if (i == routerId)
				continue;
			DatagramPacket sendPacket = new DatagramPacket(data, data.length,
					InetAddress.getByName("localhost"), ports[i]);
			sendSock.send(sendPacket);
		}
		sendSock.close();
	}
	
	private static void sendPacket(int routerId, int port, byte[] data)
			throws Exception {
		DatagramSocket sendSock = new DatagramSocket();
		// System.out.println("\n\nIn sendPacket, ports = " + ports[0] + ", "
		// + ports[1] + ", " + ports[2] + " and routerId = " + routerId);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length,
					InetAddress.getByName("localhost"), port);
			sendSock.send(sendPacket);
		sendSock.close();
	}

	private static byte[] toByteArray(int[] convertMe) {
		byte[] ret = new byte[convertMe.length];
		for (int i = 0; i < convertMe.length; i++)
			ret[i] = (byte) convertMe[i];
		return ret;
	}
}
