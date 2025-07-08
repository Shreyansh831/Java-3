import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 12345;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Send username to server
            writer.println(username);
            
            // Start a thread to read messages from server
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = reader.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server");
                }
            }).start();
            
            // Read messages from console and send to server
            String message;
            while (true) {
                message = scanner.nextLine();
                writer.println(message);
                
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
            
            socket.close();
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
        scanner.close();
    }
}
