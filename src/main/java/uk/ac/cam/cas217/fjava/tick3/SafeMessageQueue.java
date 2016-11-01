package uk.ac.cam.cas217.fjava.tick3;

/**
 * A message queue that is thread safe
 */
public class SafeMessageQueue<T> implements MessageQueue<T> {
    private Link<T> first = null;
    private Link<T> last = null;

    @Override
    public synchronized void put(T msg) {
        Link<T> linkToAdd = new Link<>(msg);

        if (first == null) {
            first = linkToAdd;
        } else {
            last.next = linkToAdd;
        }

        last = linkToAdd;

        this.notify();
    }

    @Override
    public synchronized T take() {
        while (first == null) {
            try {
                this.wait();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
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

