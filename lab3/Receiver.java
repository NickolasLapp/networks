import java.io.*; 
import java.net.*; 
import java.util.*;
import java.time.*;

class Receiver { 

    public static void main(String args[]) throws Exception 
    { 
        int window_sz;
        int max_seq = 1000;
        int pack_num = 0;
        int ack_num = 0;
        int count = 0;
        String sentence = "";
        List<String> win_status = new ArrayList<>();
        int packN;

        BufferedReader inFromUser = 
            new BufferedReader(new InputStreamReader(System.in)); 

        DatagramSocket clientSocket = new DatagramSocket(); 

        InetAddress IPAddress = InetAddress.getByName(args[0]); 
        byte[] sendData = new byte[1024]; 
        byte[] receiveData = new byte[1024]; 

        while (true) {
            //String sentence = inFromUser.readLine();
            if (count == 0) {
                sentence = "Need Window Size";
            } else if (count == 1) {
                sentence = "Need max sequence";
            } else if (count > 1 && count < 3) {
                sentence = "Need packet";
                count++;
            }
            System.out.println("Sending: " + sentence);
            sendData = sentence.getBytes();          

            DatagramPacket sendPacket = 
                new DatagramPacket(sendData, sendData.length, IPAddress, 11111); 

            clientSocket.send(sendPacket); 

            DatagramPacket receivePacket = 
                new DatagramPacket(receiveData, receiveData.length); 

            clientSocket.receive(receivePacket); 

            String modifiedSentence = 
                new String(receivePacket.getData());

            if (count >= 2) {
                pack_num = Integer.parseInt(modifiedSentence.replaceAll("[\\D]",
                            ""));
                System.out.println("Packet " + pack_num + 
                                   " is received, send Ack " + pack_num + 
                                   ", window " + win_status);
                packN = pack_num;
                sentence = "Ack " + packN;
            }
            
            System.out.println("Received: " + modifiedSentence);
            if (pack_num == max_seq)
                break;
            if (count == 0) {
                window_sz = Integer.parseInt(modifiedSentence.replaceAll(
                            "[\\D]", ""));
                count++;
            } else if (count == 1) {
                max_seq = Integer.parseInt(modifiedSentence.replaceAll(
                            "[\\D]", ""));
                count++;
            } 
        }
        clientSocket.close(); 
    } 
}
