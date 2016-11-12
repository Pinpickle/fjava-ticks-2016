package uk.ac.cam.cas217.fjava.tick4;

import uk.ac.cam.cas217.fjava.tick4.io.ReactiveInput;
import uk.ac.cam.cas217.fjava.tick4.io.ReactiveObjectSocket;
import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Handles an individual client for the chat server
 */
public class ClientHandler {
    /**
     * A list of potential actions to perform on messages. The first action that matches the type of the incoming
     * message will be performed. This means that order is significant.
     */
    private final List<MessageAction> actions;
    private String nickname = generateDefaultNickname();
    private Socket socket;
    private MultiQueue<Message> allMessagesQueue;

    public ClientHandler(Socket socket, MultiQueue<Message> allMessagesQueue) {
        this.socket = socket;
        this.allMessagesQueue = allMessagesQueue;

        actions = Arrays.asList(
            new MessageAction<>(ChangeNickMessage.class, changeNickMessage -> {
                allMessagesQueue.put(new StatusMessage(String.format("%s is now known as %s.", nickname, changeNickMessage.name)));
                nickname = changeNickMessage.name;
            }),
            new MessageAction<>(ChatMessage.class, chatMessage -> {
                allMessagesQueue.put(new RelayMessage(nickname, chatMessage));
            })
        );
    }

    public void startListening() throws IOException {
        MessageQueue<Message> messagesQueue = new SafeMessageQueue<>();
        ReactiveObjectSocket reactiveSocket = new ReactiveObjectSocket(socket);

        allMessagesQueue.register(messagesQueue);

        allMessagesQueue.put(new StatusMessage(String.format("%s connected from %s.", nickname, socket.getInetAddress().getCanonicalHostName())));

        CompletableFuture<Void> outgoing = ReactiveInput.observeMessageQueue(messagesQueue, reactiveSocket::write);
        reactiveSocket
            .observe(this::actOnMessage)
            .exceptionally(throwable -> {
                throwable.printStackTrace();
                return null;
            })
            .thenRun(() -> {
                outgoing.cancel(true);
                allMessagesQueue.deregister(messagesQueue);
                allMessagesQueue.put(new StatusMessage(String.format("%s has disconnected", nickname)));
            });
    }

    private static String generateDefaultNickname() {
        return String.format("Anonymous %d5", new Random().nextInt(10000));
    }

    @SuppressWarnings("unchecked")
    private void actOnMessage(Object message) {
        actions.stream()
            .filter(messageAction -> messageAction.classToActOn.isInstance(message))
            .findFirst()
            .ifPresent(messageAction -> messageAction.actionToPerform.accept(messageAction.classToActOn.cast(message)));
    }

    /**
     * Encapsulates an action to be performed on a message, and what type of object it is expected to perform this
     * action on.
     */
    private static class MessageAction<T> {
        private Class<T> classToActOn;
        private Consumer<T> actionToPerform;

        private MessageAction(Class<T> classToActOn, Consumer<T> actionToPerform) {
            this.classToActOn = classToActOn;
            this.actionToPerform = actionToPerform;
        }
    }
}
