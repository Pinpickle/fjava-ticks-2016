package uk.ac.cam.cas217.fjava.tick2;

import uk.ac.cam.cas217.fjava.tick2.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Implements serialising and deserialising of {@link TestMessage} to demonstrate how Java serialisation works.
 */
public class TestMessageReadWrite {
    static boolean writeMessage(String messageText, String fileLocation) {
        try (ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(fileLocation))) {
            TestMessage message = new TestMessage();
            message.setMessage(messageText);

            objectStream.writeObject(message);
            return true;
        } catch (Throwable exception) {
            exception.printStackTrace();
            return false;
        }
    }

    static String readMessage(String location) throws IOException {
        try (ObjectInputStream objectStream = new ObjectInputStream(IOUtils.inputStreamFromString(location))) {
            Object objectRead = objectStream.readObject();

            if (objectRead instanceof TestMessage) {
                return ((TestMessage) objectRead).getMessage();
            } else {
                return null;
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) throws IOException { }
}
