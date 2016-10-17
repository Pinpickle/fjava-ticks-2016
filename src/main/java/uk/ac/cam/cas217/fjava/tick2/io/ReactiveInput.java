package uk.ac.cam.cas217.fjava.tick2.io;

import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Helper methods for observing inputs
 */
public abstract class ReactiveInput {

    public static CompletableFuture<Void> observeObjectInputStream(DynamicObjectInputStream sourceStream, IOConsumer<Object> onData) {
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

    public static CompletableFuture<Void> observeBufferedReader(BufferedReader sourceReader, IOConsumer<String> onData) {
        return CompletableFuture.runAsync(() -> {
            try {
                String lineRead;
                while ((lineRead = sourceReader.readLine()) != null) {
                    onData.accept(lineRead);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        });
    }

    public interface IOConsumer<T> {
        void accept(T value) throws IOException;
    }

    public static class UnknownClass { }
}
