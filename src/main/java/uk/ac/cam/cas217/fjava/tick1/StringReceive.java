package uk.ac.cam.cas217.fjava.tick1;

import uk.ac.cam.cas217.fjava.tick1.io.ReactiveStringSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Prints messages from a server
 */
public class StringReceive {
    public static void main(String[] args) {
        ApplicationConfig config;
        try {
            config = ApplicationConfig.createFromApplicationArguments(args);
        } catch (ApplicationConfig.ServerConfigException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        CompletableFuture<Void> readingFuture;

        try {
            readingFuture = new ReactiveStringSocket(config.host, config.port)
                .observe(System.out::print);
        } catch (IOException exception) {
            System.out.println(String.format("Cannot connect to %s on port %d", config.host, config.port));
            return;
        }

        readingFuture.join();
    }
}
