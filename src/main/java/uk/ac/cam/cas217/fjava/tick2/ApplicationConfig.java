package uk.ac.cam.cas217.fjava.tick2;

/**
 * Represents the configuration for server CLI applications
 */
class ApplicationConfig {
    final String host;
    final int port;

    private ApplicationConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static ApplicationConfig createFromApplicationArguments(String[] args) throws ServerConfigException {
        if (args.length != 2) {
            throw new ServerConfigException("This application requires two arguments: <machine> <port>");
        }

        try {
            return new ApplicationConfig(args[0], Integer.valueOf(args[1]));
        } catch (NumberFormatException exception) {
            throw new ServerConfigException("This application requires two arguments: <machine> <port>");
        }
    }

    static class ServerConfigException extends Exception {
        ServerConfigException(String message) {
            super(message);
        }
    }
}
