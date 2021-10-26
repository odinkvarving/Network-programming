import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

public class ClientThread extends Thread {
    public Socket socket;
    public String clientName;


    public ClientThread(Socket socket, String clientName) {
        this.socket = socket;
        this.clientName = clientName;
    }

    public void run() {
        webServer(this.socket);
    }

    public void privateServer(Socket socket) {

        InputStreamReader input = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            input = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(input);
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("Hello, you have contact with the server!");

            while (true) {
                while(true) {

                    writer.println("Enter an equation with in this format: X+Y OR X-Y.");

                    String equation = reader.readLine();
                    if(equation.contains("+")) {
                        String[] parts = equation.split(Pattern.quote("+"));
                        int answer = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
                        writer.println("Your answer is: " + answer + ". ");
                        System.out.println("The client entered: " + parts[0] + "+" + parts[1] + ". The answer was: " + answer);
                    }

                    else if(equation.contains("-")) {
                        String[] parts = equation.split(Pattern.quote("-"));
                        int answer = Integer.parseInt(parts[0]) - Integer.parseInt(parts[1]);
                        writer.println("Your answer is: " + answer);
                    }else{
                        writer.println("ERROR: You can only write in this format 'X+Y' or 'X-Y'");
                    }

                    writer.println("Do you want to enter another equation? (yes/no)");
                    String restart = reader.readLine();
                    if(restart.contains("yes")) {
                        System.out.println(this.clientName + " has chosen to enter another equation.");
                        writer.println("You have chosen to enter another equation. Continuing...");
                    }else if(restart.contains("no")) {
                        System.out.println(this.clientName + " has chosen to not enter another equation. \n" + clientName + " has disconnected.");
                        writer.println("You have chosen to not enter another equation. Exiting...");
                        break;
                    }else {
                        writer.println("You can only write 'yes' or 'no'.");
                    }
                }

                try {
                    reader.close();
                    writer.close();
                    socket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void webServer(Socket socket) {

        InputStreamReader input = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            input = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(input);
            writer = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e) {
            e.printStackTrace();
        }

        String line;
        String clientHeader = "";
        try {
            while(!((line = reader.readLine()).equals(""))){
                System.out.println(line);
                clientHeader += "<LI>" + line + "</LI>";
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writer.println("HTTP/1.0 200 OK\n" +
                    "Content-Type: text/html; charset=utf-8\n\n" +
                    "<HTML><BODY>" +
                    "<H1>Hello! You have connected to my web server</H1>" +
                    "Client header:" +
                    "<UL>" +
                    clientHeader +
                    "</UL>" +
                    "</BODY></HTML>");
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }finally {
            try {
                System.out.println("Connection closed.");
                reader.close();
                writer.close();
                socket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}