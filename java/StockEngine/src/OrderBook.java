import java.util.concurrent.atomic.AtomicReference;

public class OrderBook {

    private final AtomicReference<Order> buyHead;
    private final AtomicReference<Order> sellHead;

    public OrderBook() {
        this.buyHead = new AtomicReference<>(null);     // Linked list of BUY orders in descending order
        this.sellHead = new AtomicReference<>(null);    // Linked list of SELL orders in descending order
    }

    public AtomicReference<Order> getBuyHead() {
        return this.buyHead;
    }

    public AtomicReference<Order> getSellHead() {
        return this.sellHead;
    }

    public void insertOrder(Order order) {
        if (order.orderType == Order.OrderType.BUY) {
            insertBuyOrder(order);
        }
        else if (order.orderType == Order.OrderType.SELL) {
            insertSellOrder(order);
        }
    }

    private void insertBuyOrder(Order order) {
        while (true) {
            Order head = buyHead.get();
            // If first order in list
            if (head == null || order.price > head.price) {
                order.next.set(head);
                if (buyHead.compareAndSet(head, order)) {
                    return;
                }
            }
            // Normal insert in descending order
            else {
                Order pred = head;
                Order curr = pred.next.get();
                while (curr != null && curr.price >= order.price) {
                    pred = curr;
                    curr = curr.next.get();
                }
                order.next.set(curr);
                if (pred.next.compareAndSet(curr, order)) {
                    return;
                }
            }
        }
    }

    private void insertSellOrder(Order order) {
        while (true) {
            Order head = sellHead.get();
            // If first order in list
            if (head == null || order.price < head.price) {
                order.next.set(head);
                if (sellHead.compareAndSet(head, order)) {
                    return;
                }
            }
            // Normal insert in ascending order
            else {
                Order pred = head;
                Order curr = pred.next.get();
                while (curr != null && curr.price <= order.price) {
                    pred = curr;
                    curr = curr.next.get();
                }
                order.next.set(curr);
                if (pred.next.compareAndSet(curr, order)) {
                    return;
                }
            }
        }
    }

    public void matchOrder() {

        while (true) {

            Order buy = buyHead.get();
            Order sell = sellHead.get();
            if (buy == null || sell == null || buy.price < sell.price) {
                break;
            }

            int buyQty, sellQty, matchQty;
            while (true) {
                buyQty = buy.quantity.get();
                sellQty = sell.quantity.get();
                if (buyQty <= 0 || sellQty <= 0) {
                    break;
                }
                matchQty = Math.min(buyQty, sellQty);     // Price at which order is matched
                if (buy.quantity.compareAndSet(buyQty, buyQty - matchQty)) {    // Atomic function compareAndSet used to avoid interruption from other threads
                    if (sell.quantity.compareAndSet(sellQty, sellQty - matchQty)) {
                        System.out.println("[Ticker " + buy.ticker + "] Order matched: " + matchQty + " shares at $" + sell.price);
                        break;
                    } else {
                        continue;
                    }
                }
            }

            // If all BUY order shares are satisfied
            if (buy.quantity.get() == 0) {
                buyHead.compareAndSet(buy, (buy.next.get()));
            }
            // If all SELL order shares are satisfied
            if (sell.quantity.get() == 0) {
                sellHead.compareAndSet(sell, (sell.next.get()));
            }
        }
    }

    
}
