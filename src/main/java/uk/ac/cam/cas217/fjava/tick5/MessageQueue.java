package uk.ac.cam.cas217.fjava.tick5;

/**
 * A queue with concurrency in mind
 */
public interface MessageQueue<T> {
    /**
     * Adds an item to the tail of the queue
     */
    void put(T msg);

    /**
     * Blocks until an item is available on the head of the queue, removes it, and returns it
     */
    T take();
}
