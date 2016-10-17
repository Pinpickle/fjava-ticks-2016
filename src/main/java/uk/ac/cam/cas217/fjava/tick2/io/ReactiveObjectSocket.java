package uk.ac.cam.cas217.fjava.tick2.io;

import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * Exposes a socket as a reactive stream of objects
 */
public class ReactiveObjectSocket implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final DynamicObjectInputStream inputStream;
    private final String host;
    private final int port;

    public ReactiveObjectSocket(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new DynamicObjectInputStream(socket.getInputStream());
    }

    public CompletableFuture<Void> observe(ReactiveInput.IOConsumer<Object> onData) throws IOException {
        return ReactiveInput.observeObjectInputStream(inputStream, onData);
    }

    public ReactiveObjectSocket write(Object object) throws IOException {
        outputStream.writeObject(object);

        return this;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void addClass(String name, byte[] classData) {
        inputStream.addClass(name, classData);
    }

    @Override
    public void close() throws IOException {
        socket.shutdownInput();
        socket.shutdownOutput();
    }
}
