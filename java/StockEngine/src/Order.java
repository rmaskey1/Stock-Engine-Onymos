import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Order {
    public enum OrderType { BUY, SELL }

    public final OrderType orderType;
    public final int ticker;
    public final double price;
    public final AtomicInteger quantity;
    public final AtomicReference<Order> next;

    public Order(OrderType orderType, int ticker, double price, int quantity) {
        this.orderType = orderType;
        this.ticker = ticker;
        this.price = price;
        this.quantity = new AtomicInteger(quantity);
        this.next = new AtomicReference<>(null);
    }

    @Override
    public String toString() {
        return "[Ticker " + ticker + "] " + orderType + " " + quantity.get() + " @ $" + price;
    }
}
