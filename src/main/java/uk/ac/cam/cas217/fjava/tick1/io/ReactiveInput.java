package uk.ac.cam.cas217.fjava.tick1.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Helper methods for observing inputs
 */
public abstract class ReactiveInput {
    public static CompletableFuture<Void> observeInputStream(InputStream sourceStream, IOConsumer<String> onData) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] byteBuffer = new byte[1024];
                int numBytesRead;

                while ((numBytesRead = sourceStream.read(byteBuffer)) != -1) {
                    onData.accept(new String(byteBuffer, 0, numBytesRead));
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }).exceptionally(printException);
    }

    public static CompletableFuture<Void> observeBufferedReader(BufferedReader sourceReader, IOConsumer<String> onData) {
        return CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    onData.accept(sourceReader.readLine());
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }).exceptionally(printException);
    }

    private static final Function<Throwable, Void> printException = throwable -> {
        throwable.printStackTrace();
        return null;
    };

    public interface IOConsumer<T> {
        void accept(T value) throws IOException;
    }
}
