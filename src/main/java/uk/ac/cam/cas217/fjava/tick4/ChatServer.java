package uk.ac.cam.cas217.fjava.tick4;

import uk.ac.cam.cas217.fjava.tick4.io.ReactiveServerSocket;
import uk.ac.cam.cl.fjava.messages.Message;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * A simple chat server following the interface that tick2 uses
 */
public class ChatServer {
    public static void main(String[] args) {
        ApplicationConfig config;
        try {
            config = ApplicationConfig.createFromApplicationArguments(args);
        } catch (ApplicationConfig.ServerConfigException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        ReactiveServerSocket socket;

        try {
            socket = new ReactiveServerSocket(new ServerSocket(config.port));
        } catch (IOException exception) {
            System.out.println(String.format("Cannot use port number %d", config.port));
            return;
        }

        System.out.println(String.format("Chat server started on port %d", config.port));

        MultiQueue<Message> messageQueue = new MultiQueue<>();

        socket.observe(clientSocket -> {
            new ClientHandler(clientSocket, messageQueue).startListening();
        }).join();
    }
}
