package uk.ac.cam.cas217.fjava.tick2.chat;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles user input for chatting
 */
class UserInputReceiver {
    private final Client.ClientActions clientActions;

    UserInputReceiver(Client.ClientActions clientActions) {
        this.clientActions = clientActions;
    }

    void acceptInput(String inputLine) throws IOException {
        String commandString = inputLine.startsWith("\\") ? inputLine : "\\say " + inputLine;
        Matcher commandMatcher = Pattern.compile("\\\\(?<command>[^\\s]*)\\s?(?<argument>.*)").matcher(commandString);

        if (commandMatcher.find()) {
            String command = commandMatcher.group("command");
            String argument = commandMatcher.group("argument");

            handleCommand(command, argument);
        }
    }

    private void handleCommand(String command, String argument) throws IOException {
        switch (command) {
            case "say":
                clientActions.writeSocket(new ChatMessage(argument));
                break;

            case "nick":
                clientActions.writeSocket(new ChangeNickMessage(argument));
                break;

            case "quit":
                clientActions.closeSocket();
                break;

            default:
                clientActions.writeOutput(
                    MessageRenderer.renderMessageFromClientNow(String.format("Unknown command \"%s\"", command))
                );
                break;
        }
    }
}
