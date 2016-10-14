package uk.ac.cam.cas217.fjava.tick1.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * Exposes a socket as a reactive stream of strings
 */
public class ReactiveStringSocket implements Closeable {
    private final Socket socket;

    public ReactiveStringSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public CompletableFuture<Void> observe(ReactiveInput.IOConsumer<String> onData) throws IOException {
        return ReactiveInput.observeInputStream(socket.getInputStream(), onData);
    }

    public ReactiveStringSocket write(String data) throws IOException {
        socket.getOutputStream().write(data.getBytes());

        return this;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
