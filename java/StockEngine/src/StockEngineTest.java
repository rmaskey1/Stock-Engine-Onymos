import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class StockEngineTest {

    @Before
    public void setUp() {
        // Reset all order books before each test.
        for (int i = 0; i < Main.MAX_TICKERS; i++) {
            Main.orderBooks[i] = new OrderBook();
        }
    }
    
    @Test
    public void testValidTickersAddOrder() {
        int ticker1 = 2;
        int ticker2 = Main.MAX_TICKERS - 3;
       
        Main.addOrder("BUY", ticker1, 10, 100);
        Main.addOrder("SELL", ticker2, 10, 100);

        assertNotNull("BUY order book for ticker " + ticker1 + " should not be null", Main.orderBooks[ticker1].getBuyHead().get());
        assertNotNull("SELL order book for ticker " + ticker2 + " should not be null", Main.orderBooks[ticker2].getSellHead().get());
    }
    
    @Test
    public void testInvalidTickersAddOrder() {
        int ticker1 = -1;
        int ticker2 = Main.MAX_TICKERS;

        Main.addOrder("BUY", ticker1, 100, 10);
        Main.addOrder("SELL", ticker2, 100, 10);
        

        assertNull("Order book for ticker 0 should be empty if no orders added", Main.orderBooks[0].getBuyHead().get());
        assertNull("Order book for ticker 0 should be empty if no orders added", Main.orderBooks[0].getSellHead().get());
    }
    
    @Test
    public void testFullMatch() {
        int ticker = 1;

        Main.addOrder("BUY", ticker, 103, 50);
        Main.addOrder("SELL", ticker, 100, 50);
        
        // After full match, both order lists should be empty
        assertNull("BUY head should be null after full match", Main.orderBooks[ticker].getBuyHead().get());
        assertNull("SELL head should be null after full match", Main.orderBooks[ticker].getSellHead().get());
    }
    
    @Test
    public void testPartialMatchBuyFullyMatched() {
        int ticker = 1;

        Main.addOrder("BUY", ticker, 103, 20);
        Main.addOrder("SELL", ticker, 100, 50);
        
        // BUY order should be fully matched and removed
        assertNull("BUY head should be null after partial match", Main.orderBooks[ticker].getBuyHead().get());
       
        // SELL order should remain with leftover shares
        Order remainingSell = Main.orderBooks[ticker].getSellHead().get();
        assertNotNull("SELL head should not be null after partial match", remainingSell);
        // Expecting 50 - 20 = 30 shares remaining
        assertEquals("Remaining SELL quantity should be 30", 30, remainingSell.quantity.get());
    }

    @Test
    public void testPartialMatchSellFullyMatched() {
        int ticker = 1;
        // Place a BUY order for 20 shares at 103 and a SELL order for 50 shares at 100.
        Main.addOrder("BUY", ticker, 103, 50);
        Main.addOrder("SELL", ticker, 100, 20);
        
        // After matching:
        // The BUY order should be fully matched and removed.
        assertNull("SELL head should be null after partial match", Main.orderBooks[ticker].getSellHead().get());
        // The SELL order should remain with leftover shares.
        Order remainingBuy = Main.orderBooks[ticker].getBuyHead().get();
        assertNotNull("SELL head should not be null after partial match", remainingBuy);
        // Expecting 50 - 20 = 30 shares remaining.
        assertEquals("Remaining SELL quantity should be 30", 30, remainingBuy.quantity.get());
    }
    
    @Test
    public void testRaceConditionConcurrentInsertion() throws InterruptedException {
        int ticker = 3;

        Main.orderBooks[ticker] = new OrderBook();
        int numThreads = 50;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numThreads; i++) {
            executor.execute(() -> {
                int quantity = 10;
                double price = ThreadLocalRandom.current().nextDouble(90, 110);
                price = Math.round(price * 100.0) / 100.0;
                Main.addOrder("BUY", ticker, price, quantity);
            });
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Verify that the BUY orders for ticker 3 are sorted in descending order.
        AtomicReference<Order> headRef = Main.orderBooks[ticker].getBuyHead();
        Order current = headRef.get();
        int count = 0;
        double prevPrice = Double.POSITIVE_INFINITY;
        boolean sortedDescending = true;
        while (current != null) {
            if (current.price > prevPrice) {
                sortedDescending = false;
            }
            prevPrice = current.price;
            count++;
            current = current.next.get();
        }
        
        System.out.println("Final BUY order list for ticker " + ticker + ":");
        current = headRef.get();
        while (current != null) {
            System.out.println(current);
            current = current.next.get();
        }
        System.out.println("Total orders in list: " + count);
        
        // Assert that exactly 50 orders are present.
        assertEquals("Expected " + numThreads + " orders", numThreads, count);
        // Assert that the orders are sorted in descending order.
        assertTrue("Orders are not sorted in descending order", sortedDescending);
    }
}

