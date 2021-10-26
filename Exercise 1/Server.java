import java.io.*;
import java.net.*;

class Server {

    static final int WEBPORT = 80;
    static final int PRIVATEPORT = 1250;
    static int counter = 0;

    public static void main(String[] args) {

        Socket socket = null;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(WEBPORT);
            System.out.println("Log for the server. Waiting for connections...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientThread clientThread = null;

        while (true) {
            try {
                socket = serverSocket.accept();
                counter++;
                String clientName = "Client " + counter;
                clientThread = new ClientThread(socket, clientName);
                System.out.println(clientName + " has connected.");
            }catch (IOException e) {
                e.printStackTrace();
            }try {
                clientThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
