package uk.ac.cam.cas217.fjava.tick3;

/**
 * A message queue that is intentionally not thread safe
 */
public class UnsafeMessageQueue<T> implements MessageQueue<T> {
    private Link<T> first = null;
    private Link<T> last = null;

    @Override
    public void put(T msg) {
        Link<T> linkToAdd = new Link<>(msg);

        if (first == null) {
            first = linkToAdd;
        } else {
            last.next = linkToAdd;
        }

        last = linkToAdd;
    }

    @Override
    public T take() {
        while (first == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exception) {
                // Swallowing, because we're unsafe like that
            }
        }

        T value = first.value;

        first = first.next;
        if (first == null) {
            last = null;
        }

        return value;
    }

    private static class Link<T> {
        final T value;
        Link<T> next = null;

        Link(T value) {
            this.value = value;
        }
    }
}
