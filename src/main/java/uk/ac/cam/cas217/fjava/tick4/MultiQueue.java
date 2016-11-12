package uk.ac.cam.cas217.fjava.tick4;

import java.util.HashSet;
import java.util.Set;

/**
 * Combines multiple {@link MessageQueue} instances so that put operations can be
 * done to all of them at once.
 */
public class MultiQueue<T> {
    private Set<MessageQueue<T>> outputs = new HashSet<>();

    public synchronized void register(MessageQueue<T> queue) {
        outputs.add(queue);
    }

    public synchronized void deregister(MessageQueue<T> queue) {
        outputs.remove(queue);
    }

    public synchronized void put(T message) {
        for (MessageQueue<T> queue : outputs) {
            queue.put(message);
        }
    }
}
