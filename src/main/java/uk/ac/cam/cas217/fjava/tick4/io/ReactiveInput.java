package uk.ac.cam.cas217.fjava.tick4.io;

import uk.ac.cam.cas217.fjava.tick4.MessageQueue;
import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Helper methods for observing inputs
 */
public abstract class ReactiveInput {

    public static CompletableFuture<Void> observeObjectInputStream(ObjectInputStream sourceStream, IOConsumer<Object> onData) {
        return CompletableFuture.runAsync(() -> {
            try {
                try {
                    while (true) {
                        try {
                            onData.accept(sourceStream.readObject());
                        } catch (ClassNotFoundException exception) {
                            exception.printStackTrace();
                            onData.accept(new UnknownClass());
                        }
                    }
                } catch (EOFException exception) {
                    // Swallow as we just want the future to complete
               }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    public static <T> CompletableFuture<Void> observeMessageQueue(MessageQueue<T> queue, IOConsumer<T> onData) {
        return CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    onData.accept(queue.take());
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        });
    }

    public interface IOConsumer<T> {
        void accept(T value) throws IOException;
    }

    public static class UnknownClass { }
}
