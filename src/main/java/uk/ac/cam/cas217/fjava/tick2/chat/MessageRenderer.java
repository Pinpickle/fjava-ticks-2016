package uk.ac.cam.cas217.fjava.tick2.chat;

import uk.ac.cam.cl.fjava.messages.Message;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Renders messages from structured data as strings
 */
class MessageRenderer {
    static String renderMessage(Date date, String name, String messageBody) {
        return String.format("%s [%s] %s\n", new SimpleDateFormat("HH:mm:ss").format(date), name, messageBody);
    }

    static String renderMessageFromClient(Date date, String messageBody) {
        return renderMessage(date, "Client", messageBody);
    }

    static String renderMessageFromClientNow(String messageBody) {
        return renderMessageFromClient(new Date(), messageBody);
    }

    static String getUnknownMessageInfo(Message message) {
        return String.format(
            "%s: %s",
            message.getClass().getSimpleName(),
            String.join(
                ", ",
                Arrays.stream(message.getClass().getDeclaredFields()).<String>map(field -> {
                    try {
                        field.setAccessible(true);
                        return String.format("%s(%s)", field.getName(), field.get(message).toString());
                    } catch (IllegalAccessException exception) {
                        // This shouldn't happen as we are setting the field to be accessible
                        return String.format("%s", field.getName());
                    }
                }).collect(Collectors.toList())
            )
        );
    }
}
