import java.util.Random;


public class Main {
    public static final int MAX_TICKERS = 1024;
    public static final OrderBook[] orderBooks = new OrderBook[MAX_TICKERS];
    private static final Random random = new Random();

    static {
        for (int i = 0; i < MAX_TICKERS; i++) {
            orderBooks[i] = new OrderBook();
        }
    }

    public static void addOrder(String orderTypeStr, int ticker, double price, int quantity) {
        // Checking for invalid tickers
        if (ticker < 0 || ticker >= MAX_TICKERS) {
            System.out.println("Invalid ticker");
            return;
        }
        Order.OrderType orderType = Order.OrderType.valueOf(orderTypeStr.toUpperCase());
        Order order = new Order(orderType, ticker, price, quantity);
        orderBooks[ticker].insertOrder(order); // See OrderBook.java for insertOrder implementation
        System.out.println("Order successfully added: " + order);
        matchOrder(ticker);
    }

    public static void matchOrder(int ticker) {
        orderBooks[ticker].matchOrder(); // See OrderBook.java for matchOrder implementation
    }

    public static void simulate(int orders) {
        for (int i = 0; i < orders; i++) {
            // Randomizing order details
            String orderType = random.nextBoolean() ? "BUY" : "SELL";
            int ticker = random.nextInt(MAX_TICKERS);
            int quantity = random.nextInt(100) + 1;
            double basePrice = 100.0;
            double price;
            if ("BUY".equals(orderType)) {
                price = basePrice - random.nextInt(6);
            } else {
                price = basePrice + random.nextInt(6);
            }
            price = Math.round(price * 100.0) / 100.0;
            addOrder(orderType, ticker, price, quantity);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void simulationThread() {
        simulate(50);
    }

    public static void main(String[] args) {
        int numThreads = 20;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(Main::simulationThread);
            threads[i].start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
