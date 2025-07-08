import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server starting...");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
        }
    }

    public static void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        System.out.println(client.getUsername() + " disconnected");
        broadcast(client.getUsername() + " left the chat", null);
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public String getUsername() {
            return username;
        }

        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                username = reader.readLine();
                broadcast(username + " joined the chat", this);

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println(username + ": " + clientMessage);
                    broadcast(username + ": " + clientMessage, this);
                }
            } catch (IOException e) {
                System.out.println("Error in ClientHandler: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Socket close error: " + e.getMessage());
                }
                ChatServer.removeClient(this);
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
        }
    }
}
