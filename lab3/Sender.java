import java.io.*; 
import java.net.*; 
import java.util.*;
import java.time.*;

class Sender { 
    public static void main(String args[]) throws IOException 
    { 

        byte[] receiveData = new byte[1024]; 
        byte[] sendData  = new byte[1024]; 
        int window_sz    = 0;
        int max_seq_num  = 0;
        int packets_drop = 0;
        int count        = 0;
        int pack_num     = 0;
        int ack_num      = 0;
        String window    = "Need Window Size";
        String sequence  = "Need max sequence";
        List<String> win_status = new ArrayList<>();
        BufferedReader inFromUser; 
        String in_window = "";

        System.out.print("Enter the window’s size on the sender: ");
        inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        window_sz = Integer.parseInt(inFromUser.readLine());

        for (int i = 0; i < window_sz; i++) {
            win_status.add(Integer.toString(i));
        }

        System.out.print("Enter the maximum sequence number on the sender: ");
        max_seq_num = Integer.parseInt(inFromUser.readLine());

        System.out.print("Select the packet(s) that will be dropped: ");
        packets_drop = Integer.parseInt(inFromUser.readLine());

        DatagramSocket serverSocket = new DatagramSocket(11111); 
        try {
            while(true) 
            { 
                DatagramPacket receivePacket = 
                    new DatagramPacket(receiveData, receiveData.length); 
                serverSocket.receive(receivePacket); 
                String sentence = new String(receivePacket.getData());

                System.out.println("Receiver says: " + sentence);

                if (sentence.contains(window)) {
                    System.out.println("Sent window size");
                    sendData = (Integer.toString(window_sz)).getBytes();
                } else if (sentence.contains(sequence)) {
                    System.out.println("Sending max sequence");
                    sendData = (Integer.toString(max_seq_num)).getBytes();
                } else {
                    //System.out.println("Sending stuff" + sentence);
                    sendData = (Integer.toString(pack_num)).getBytes();
                }
                InetAddress IPAddress = receivePacket.getAddress(); 

                int port = receivePacket.getPort(); 

                //String capitalizedSentence = sentence.toUpperCase(); 

                //sendData = capitalizedSentence.getBytes(); 

                if (count == 2) { 
                    for (int i = 0; i < window_sz; i++) {
                        win_status.add(Integer.toString(i));
                    }
                    for (int i = 0; i < window_sz; i++) { 
                        sendData = (Integer.toString(i)).getBytes();
                        DatagramPacket sendPacket = 
                            new DatagramPacket(sendData, sendData.length, 
                                                            IPAddress, port); 
                        serverSocket.send(sendPacket);
                        in_window = i + "*";
                        win_status.set(i, in_window);
                        System.out.println("Packet " + i + " is sent, window " +
                                win_status);
                        pack_num++;
                    }
                }
                else {
                    sendData = sentence.getBytes();
                    DatagramPacket sendPacket = 
                       new DatagramPacket(sendData, sendData.length, IPAddress, 
                                    port); 
                    serverSocket.send(sendPacket);
                }
                if (!sentence.contains(window) && !sentence.contains(sequence) 
                        && !sentence.contains("Ack")) {
                    // Packet 0 is sent, window [0*, 1, 2, 3]
                    String add = pack_num + "*";
                    win_status.set(count, add);
                    System.out.println("Packet " + pack_num + 
                            " is sent, window " + win_status);
                    count++;
                    if (count == window_sz) {
                        count = 0;
                    }
                    pack_num++;
                } else if (sentence.contains("Ack")) {
                    ack_num = Integer.parseInt(sentence.replaceAll("[\\D]",
                                ""));
                    for (int i = 0; i < win_status.size(); i++) {
                        if (win_status.get(i).contains(Integer.
                                    toString(ack_num))) {
                            win_status.set(i, Integer.toString(pack_num));
                        }
                    }
                    System.out.println("Ack " + ack_num + " is received, " +
                            "window " + win_status);
                    pack_num++;
                }
            }
        } finally { serverSocket.close();} 
    } 
}  
