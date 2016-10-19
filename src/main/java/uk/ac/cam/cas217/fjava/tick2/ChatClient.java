package uk.ac.cam.cas217.fjava.tick2;

import uk.ac.cam.cas217.fjava.tick2.chat.Client;
import uk.ac.cam.cas217.fjava.tick2.io.ReactiveInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

/**
 * Simple chat client
 */
@FurtherJavaPreamble(
    author = "Christian A. Silver",
    date = "16 October 2016",
    crsid = "cas217",
    summary = "Have a great time chatting with students",
    ticker = FurtherJavaPreamble.Ticker.A)
public class ChatClient {
    public static void main(String[] args) {
        ApplicationConfig config;
        try {
            config = ApplicationConfig.createFromApplicationArguments(args);
        } catch (ApplicationConfig.ServerConfigException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        CompletableFuture<Void> outputFuture;
        CompletableFuture<Void> readingFuture;

        try {
            Client client = new Client(config.host, config.port, System.out::print);

            readingFuture = ReactiveInput.observeBufferedReader(
                new BufferedReader(new InputStreamReader(System.in)),
                line -> client.inputLine(line)
            );

            outputFuture = client.start();
        } catch (IOException exception) {
            System.out.println(String.format("Cannot connect to %s on port %d", config.host, config.port));
            return;
        }

        CompletableFuture.anyOf(
            outputFuture,
            readingFuture.exceptionally(throwable -> { throw new RuntimeException(throwable); })
        )
            .thenRun(() -> readingFuture.cancel(true))
            .join();
    }
}
