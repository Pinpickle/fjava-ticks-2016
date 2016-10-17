package uk.ac.cam.cas217.fjava.tick2.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class IOUtils {
    /**
     * Provides an appropriate input stream based on the input string referring to a location. For example, it will
     * determine if it points to a file or URL, and open the corresponding stream.
     */
    public static InputStream inputStreamFromString(String location) throws IOException {
        switch (getStringLocationType(location)) {
            case LOCATION_URL:
                return new URL(location).openStream();

            case LOCATION_FILE:
            default:
                return new FileInputStream(location);
        }
    }

    private static LocationType getStringLocationType(String location) {
        if (location.startsWith("http://") || location.startsWith("https://")) {
            return LocationType.LOCATION_URL;
        }

        return LocationType.LOCATION_FILE;
    }

    private enum LocationType {
        LOCATION_FILE,
        LOCATION_URL;
    }
}
