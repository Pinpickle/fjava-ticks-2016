package uk.ac.cam.cas217.fjava.tick5;

/**
 * Represents the configuration for server CLI applications
 */
class ApplicationConfig {
    final int port;
    final String databasePath;

    private ApplicationConfig(int port, String databasePath) {
        this.port = port;
        this.databasePath = databasePath;
    }

    static ApplicationConfig createFromApplicationArguments(String[] args) throws ServerConfigException {
        if (args.length != 2) {
            throw new ServerConfigException("Usage: java ChatServer <port> <database name>");
        }

        try {
            return new ApplicationConfig(Integer.valueOf(args[0]), args[1]);
        } catch (NumberFormatException exception) {
            throw new ServerConfigException("Usage: java ChatServer <port> <database name>");
        }
    }

    static class ServerConfigException extends Exception {
        ServerConfigException(String message) {
            super(message);
        }
    }
}
