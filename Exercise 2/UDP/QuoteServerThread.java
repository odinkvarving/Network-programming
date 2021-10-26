import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

public class QuoteServerThread extends Thread {

    protected  int counter = 0;
    protected DatagramSocket socket = null;

    public QuoteServerThread() {
        this("QuoteServerThread");
    }

    public QuoteServerThread(String name) {
        super(name);
        try {
            socket = new DatagramSocket(4445);
            counter++;
            System.out.println("Server is up and running. Awaiting connection...");
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        boolean run = true;

        while (run) {
            try {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("Client " + counter + " has connected to the server.");

                String test = new String(packet.getData(), 0, packet.getLength());
                if(test.contains("+")) {
                    String[] parts = test.split(Pattern.quote("+"));
                    int answer = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
                    System.out.println("Client " + counter + " has entered: " + test + ". The answer was: " + answer);
                    test += "/" + answer;

                }else if(test.contains("-")) {
                    String[] parts = test.split(Pattern.quote("-"));
                    int answer = Integer.parseInt(parts[0]) - Integer.parseInt(parts[1]);
                    System.out.println("Client " + counter + " has entered: " + test + ". The answer was: " + answer);
                    test += "/" + answer;
                }else {
                    System.out.println("Client " + counter + " has entered wrong format.");
                }
                buf = test.getBytes();
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

                packet = new DatagramPacket(buf, buf.length);

                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                if(response.equalsIgnoreCase("yes")) {
                    System.out.println("Client " + counter + " has chosen to enter another equation. Restarting... ");
                }else if(response.equalsIgnoreCase("no")) {
                    System.out.println("Client " + counter + " has chosen to not enter another equation. Disconnecting... ");
                }else {
                    System.out.println("ERROR: Wrong format.");
                }
                buf = response.getBytes();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                run = false;
            }
        }
        socket.close();
    }
}
