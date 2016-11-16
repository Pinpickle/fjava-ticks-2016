package uk.ac.cam.cas217.fjava.tick5.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/**
 * Exposes a server socket as a reactive stream of sockets
 */
public class ReactiveServerSocket implements Closeable {
    private ServerSocket socket;

    public ReactiveServerSocket(ServerSocket socket) {
        this.socket = socket;
    }

    public CompletableFuture<Void> observe(ReactiveInput.IOConsumer<Socket> onData) {
        return CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    onData.accept(socket.accept());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
