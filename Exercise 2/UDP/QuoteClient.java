import java.io.*;
import java.net.*;
import java.util.Scanner;

public class QuoteClient {
    public static void main(String[] args) throws IOException {

    if (args.length != 1) {
        System.out.println("Hostname required as argument for main method.");
        return;
    }

    System.out.println("Hello! You have connection with the server.");

    DatagramSocket socket = new DatagramSocket();
    Scanner sc = new Scanner(System.in);

    boolean loop = true;
    while(loop) {

        byte[] sendBuffer = new byte[256];
        System.out.println("Enter an equation with this format: X+Y OR X-Y.");

        InetAddress address = InetAddress.getByName(args[0]);
        String send = sc.nextLine();
        sendBuffer = send.getBytes();
        DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, 4445);
        socket.send(packet);

        sendBuffer = new byte[256];

        packet = new DatagramPacket(sendBuffer, sendBuffer.length);
        socket.receive(packet);
        String clientData = new String(packet.getData(), 0, packet.getLength());
        String[] str = clientData.split("/");
        System.out.println("You have entered: " + str[0] + ". Your answer was: " + str[1]);

        sendBuffer = new byte[256];

        System.out.println("Do you want to enter another equation? (yes/no)");
        String restart = sc.nextLine();
        sendBuffer = restart.getBytes();
        packet = new DatagramPacket(sendBuffer, sendBuffer.length, address, 4445);
        socket.send(packet);

        sendBuffer = new byte[256];

        packet = new DatagramPacket(sendBuffer, sendBuffer.length);
        socket.receive(packet);
        String restartData = new String(packet.getData(), 0, packet.getLength());

            if(restartData.equalsIgnoreCase("no")) {
                System.out.println("You have chosen to not enter another equation. Disconnecting... ");
                sc.close();
                socket.close();
                break;
            }else if(restartData.equalsIgnoreCase("yes")){
                System.out.println("You have chosen to enter another equation. Restarting... ");
            }else {
                System.out.println("ERROR: Wrong format.");
            }
        }
    }
}
