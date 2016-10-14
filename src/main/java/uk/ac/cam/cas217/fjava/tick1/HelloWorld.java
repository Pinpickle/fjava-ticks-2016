package uk.ac.cam.cas217.fjava.tick1;

/**
 * Program that spits out a welcome to the universe
 */
public class HelloWorld {
    public static void main(String[] args) {
        String toGreet = args.length > 0 ?
            String.join(" ", args) : "world";

        System.out.println(String.format("Hello, %s", toGreet));
    }
}
