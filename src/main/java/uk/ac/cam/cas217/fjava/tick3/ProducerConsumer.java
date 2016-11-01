package uk.ac.cam.cas217.fjava.tick3;

/**
 * Showing off that our unsafe message queue kind of works
 */
public class ProducerConsumer {
    private MessageQueue<Character> queue = new UnsafeMessageQueue<>();

    void execute() {
        new Thread(() -> {
            char[] cl = "Computer Laboratory".toCharArray();

            for (int i =0; i < cl.length; i ++) {
                queue.put(cl[i]);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                System.out.print(queue.take());
                System.out.flush();
            }
        }).start();
    }

    public static void main(String[] args) {
        new ProducerConsumer().execute();
    }
}
