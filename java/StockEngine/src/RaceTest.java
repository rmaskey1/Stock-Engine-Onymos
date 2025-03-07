import java.util.concurrent.ThreadLocalRandom;

public class RaceTest {
    public static void main(String[] args) throws InterruptedException {
        int ticker = 3;

        Main.orderBooks[ticker] = new OrderBook();

        int numThreads = 50;
        Thread[] threads = new Thread[numThreads];

        // Each thread will add one BUY order concurrently with a random price
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                int quantity = 10; // e.g., 10 shares per order
                double price = ThreadLocalRandom.current().nextDouble(90, 110);
                price = Math.round(price * 100.0) / 100.0;
                Main.addOrder("BUY", ticker, price, quantity);
            });
        }

        // Start all threads
        for (Thread t : threads) {
            t.start();
        }
        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        // Print the final BUY order list for ticker 3
        System.out.println("Final BUY order list for ticker " + ticker + ":");
        Order current = Main.orderBooks[ticker].getBuyHead().get();
        int count = 0;
        double prevPrice = Double.POSITIVE_INFINITY;
        boolean sortedDescending = true;
        while (current != null) {
            System.out.println(current);
            if (current.price > prevPrice) {
                sortedDescending = false;
            }
            prevPrice = current.price;
            count++;
            current = current.next.get();
        }
        System.out.println("Total orders in list: " + count);

        // Visual and programmatic check:
        if (count != numThreads) {
            System.out.println("Test FAILED: Expected " + numThreads + " orders, but found " + count);
        } else if (!sortedDescending) {
            System.out.println("Test FAILED: Orders are not sorted in descending order.");
        } else {
            System.out.println("Test PASSED: Found exactly " + numThreads + " orders in descending order.");
        }
    }
}
