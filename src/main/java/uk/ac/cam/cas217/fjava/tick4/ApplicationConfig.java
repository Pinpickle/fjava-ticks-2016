package uk.ac.cam.cas217.fjava.tick4;

/**
 * Represents the configuration for server CLI applications
 */
class ApplicationConfig {
    final int port;

    private ApplicationConfig(int port) {
        this.port = port;
    }

    static ApplicationConfig createFromApplicationArguments(String[] args) throws ServerConfigException {
        if (args.length != 1) {
            throw new ServerConfigException("Usage: java ChatServer <port>");
        }

        try {
            return new ApplicationConfig(Integer.valueOf(args[0]));
        } catch (NumberFormatException exception) {
            throw new ServerConfigException("Usage: java ChatServer <port>");
        }
    }

    static class ServerConfigException extends Exception {
        ServerConfigException(String message) {
            super(message);
        }
    }
}
