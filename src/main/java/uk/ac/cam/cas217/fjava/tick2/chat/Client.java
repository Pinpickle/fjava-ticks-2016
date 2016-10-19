package uk.ac.cam.cas217.fjava.tick2.chat;

import uk.ac.cam.cas217.fjava.tick2.io.ReactiveObjectSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Simple chat client that communicates with a server via serialised objects
 */
public class Client {
    private final Consumer<String> observer;
    private final ReactiveObjectSocket socket;
    private ServerMessageReceiver serverMessageReceiver;
    private UserInputReceiver userInputReceiver;

    public Client(String host, int port, Consumer<String> observer) throws IOException {
        socket = new ReactiveObjectSocket(host, port);
        this.observer = observer;
    }

    public CompletableFuture<Void> start() throws IOException {
        ClientActions clientActions = new ClientActions();
        serverMessageReceiver = new ServerMessageReceiver(clientActions);
        userInputReceiver = new UserInputReceiver(clientActions);
        clientActions.writeOutput(MessageRenderer.renderMessageFromClientNow(
            String.format("Connected to %s on port %s.", socket.getHost(), socket.getPort())
        ));

        return socket.observe(serverMessageReceiver::acceptMessage).thenRun(() ->
            clientActions.writeOutput(MessageRenderer.renderMessageFromClientNow("Connection terminated.")
        ));
    }

    public void inputLine(String line) throws IOException {
        if (userInputReceiver == null) {
            throw new ClientException("Client has not been started");
        }

        userInputReceiver.acceptInput(line);
    }

    class ClientActions {
        void closeSocket() throws IOException {
            socket.close();
        }

        void writeSocket(Object toWrite) throws IOException {
            socket.write(toWrite);
        }

        void addClass(String name, byte[] classData) {
            socket.addClass(name, classData);
            writeOutput(MessageRenderer.renderMessageFromClientNow(String.format("New class %s loaded.", name)));
        }

        void writeOutput(String output) {
            observer.accept(output);
        }
    }

    public static class ClientException extends RuntimeException {
        private ClientException(String message) {
            super(message);
        }
    }
}
