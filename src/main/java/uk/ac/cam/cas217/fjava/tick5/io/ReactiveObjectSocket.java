package uk.ac.cam.cas217.fjava.tick5.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * Exposes a socket as a reactive stream of objects
 */
public class ReactiveObjectSocket implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ReactiveObjectSocket(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        inputStream = new ObjectInputStream(this.socket.getInputStream());
    }

    public CompletableFuture<Void> observe(ReactiveInput.IOConsumer<Object> onData) throws IOException {
        return ReactiveInput.observeObjectInputStream(inputStream, onData);
    }

    public ReactiveObjectSocket write(Object object) throws IOException {
        outputStream.writeObject(object);

        return this;
    }

    @Override
    public void close() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
    }
}
