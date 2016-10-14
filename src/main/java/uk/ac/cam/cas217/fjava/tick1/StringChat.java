package uk.ac.cam.cas217.fjava.tick1;

import uk.ac.cam.cas217.fjava.tick1.io.ReactiveInput;
import uk.ac.cam.cas217.fjava.tick1.io.ReactiveStringSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

/**
 * Allows for chat communication using an arbitrary protocol
 */
public class StringChat {
    public static void main(String[] args) throws IOException {
        ApplicationConfig config;
        try {
            config = ApplicationConfig.createFromApplicationArguments(args);
        } catch (ApplicationConfig.ServerConfigException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        CompletableFuture<Void> writingFuture;
        CompletableFuture<Void> readingFuture;

        try {
            ReactiveStringSocket socket = new ReactiveStringSocket(config.host, config.port);

            readingFuture = ReactiveInput.observeBufferedReader(
                new BufferedReader(new InputStreamReader(System.in)),
                line -> socket.write(line + "\n")
            );

            writingFuture = socket.observe(System.out::print);
        } catch (IOException exception) {
            System.out.println(String.format("Cannot connect to %s on port %d", config.host, config.port));
            return;
        }

        CompletableFuture.allOf(writingFuture, readingFuture).join();
    }
}
