package uk.ac.cam.cas217.fjava.tick3;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simulates a bank with money flying every which way
 */
public class BankSimulator {
    private static Random random = new Random();

    private BankAccount[] accounts;
    private int tellerCount;

    public BankSimulator(int initialCapital, int accountCount, int tellerCount) {
        this.accounts = new BankAccount[accountCount];
        this.tellerCount = tellerCount;

        for(int i = 0; i< this.accounts.length; i++) {
            this.accounts[i] = new BankAccount(initialCapital / this.accounts.length);
        }
    }

    private Thread createRoboTellerThread() {
        return new Thread(() -> {
            for(int i = 9 * 60 * 60; i < 17 * 60 * 60; i ++) {
                int a = random.nextInt(accounts.length);
                int b = random.nextInt(accounts.length);
                accounts[a].transferTo(accounts[b], random.nextInt(100));
            }
        });
    }

    public int getCapital() {
        return Arrays.stream(accounts)
            .mapToInt(account -> account.balance)
            .reduce(0, (result, balance) -> result + balance);
    }

    public void runDay() {
        List<Thread> tellerThreads = IntStream
            .range(0, tellerCount)
            .mapToObj((index) -> createRoboTellerThread())
            .peek(Thread::start)
            .collect(Collectors.toList());

        // Somehow, joining the threads in the stream automagically synchronises everything
        // So we keep the joining in this thread manually with a loop to exhibit that our implementation is in fact
        // thread safe
        for (Thread thread : tellerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BankSimulator javaBank = new BankSimulator(10000,10,100);
        javaBank.runDay();
        System.out.println("Capital at close: £"+javaBank.getCapital());
    }

    private class BankAccount {
        private int balance;

        BankAccount(int deposit) {
            balance = deposit;
        }

        public void transferTo(BankAccount b, int amount) {
            synchronized (this) {
                balance -= amount;
            }

            synchronized (b) {
                b.balance += amount;
            }
        }
    }
}