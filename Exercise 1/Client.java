import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {
    public static void main(String[] args) throws IOException {
        final int PRIVATEPORT = 1250;

        Scanner readingFromCMD = new Scanner(System.in);
        String serverMachine = "localhost";

        Socket connection = new Socket(serverMachine, PRIVATEPORT);
        System.out.println("The connection has been established.");

        InputStreamReader readingConnection = new InputStreamReader(connection.getInputStream());
        BufferedReader reader = new BufferedReader(readingConnection);
        PrintWriter writer = new PrintWriter(connection.getOutputStream(), true);

        String intro1 = reader.readLine();
        System.out.println(intro1);

        String line = "";
        while (!line.equals("QUIT")) {
            String firstResponse = reader.readLine();
            if(firstResponse == null) break;
            System.out.println(firstResponse);
            String equation = readingFromCMD.nextLine();
            writer.println(equation);
            System.out.println(reader.readLine());
        }

        System.out.println("Client connection closed.");
        reader.close();
        writer.close();
        connection.close();
    }
}
