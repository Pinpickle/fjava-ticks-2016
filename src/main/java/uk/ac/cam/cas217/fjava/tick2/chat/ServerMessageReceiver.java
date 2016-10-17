package uk.ac.cam.cas217.fjava.tick2.chat;

import uk.ac.cam.cl.fjava.messages.Execute;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.NewMessageType;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Handles messages sent to us by the server
 */
class ServerMessageReceiver {
    private final List<MessageAction> actions;

    ServerMessageReceiver(Client.ClientActions clientActions) {
        actions = Arrays.asList(
            new MessageAction<>(RelayMessage.class, (message) ->
                clientActions.writeOutput(MessageRenderer.renderMessage(message.getCreationTime(), message.getFrom(), message.getMessage()))
            ),
            new MessageAction<>(StatusMessage.class, message ->
                clientActions.writeOutput(MessageRenderer.renderMessage(message.getCreationTime(), "Server", message.getMessage()))
            ),
            new MessageAction<>(NewMessageType.class, message ->
                clientActions.addClass(message.getName(), message.getClassData())
            ),
            new MessageAction<>(Message.class, message -> {
                clientActions.writeOutput(MessageRenderer.renderMessageFromClient(message.getCreationTime(), MessageRenderer.getUnknownMessageInfo(message)));
                executeMessageIfPossible(message);
            }),
            new MessageAction<>(Object.class, message ->
                clientActions.writeOutput(MessageRenderer.renderMessageFromClientNow("Unknown message"))
            )
        );
    }

    @SuppressWarnings("unchecked")
    void acceptMessage(Object message) throws IOException {
        actions.stream()
            .filter(messageAction -> messageAction.classToActOn.isInstance(message))
            .findFirst()
            .ifPresent(messageAction -> messageAction.actionToPerform.accept(messageAction.classToActOn.cast(message)));
    }

    private void executeMessageIfPossible(Message message) {
        Arrays.stream(message.getClass().getDeclaredMethods())
            .filter(method -> method.getParameterCount() == 0)
            .filter(method -> method.isAnnotationPresent(Execute.class))
            .forEach(method -> {
                try {
                    method.invoke(message);
                } catch (InvocationTargetException | IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            });
    }

    private static class MessageAction<T> {
        private Class<T> classToActOn;
        private Consumer<T> actionToPerform;

        private MessageAction(Class<T> classToActOn, Consumer<T> actionToPerform) {
            this.classToActOn = classToActOn;
            this.actionToPerform = actionToPerform;
        }
    }
}
