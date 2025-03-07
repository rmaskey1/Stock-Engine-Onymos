from globals import satisfied_history

class OrderBook:

    def __init__(self):
        self.buy_head = None    # Linked list of buy orders in descending order
        self.sell_head = None   # Linked list of sell orders in ascending order

    def insert_order(self, order):
        if order.order_type == "BUY":
            # Insert into buy list in descending order
            if self.buy_head is None or order.price > self.buy_head.price:
                order.next = self.buy_head
                self.buy_head = order
            else:
                current = self.buy_head
                while current.next is not None and current.next.price >= order.price:
                    current = current.next
                order.next = current.next
                current.next = order
        elif order.order_type == "SELL":
            # Insert into sell list in ascending order
            if self.sell_head is None or order.price < self.sell_head.price:
                order.next = self.sell_head
                self.sell_head = order
            else:
                current = self.sell_head
                while current.next is not None and current.next.price <= order.price:
                    current = current.next
                order.next = current.next
                current.next = order
    
    def match_order(self):
        while (self.buy_head is not None and self.sell_head is not None and self.buy_head.price >= self.sell_head.price):

            match_price = self.sell_head.price    # Price at which order is matched

            match_quantity = min(self.buy_head.quantity, self.sell_head.quantity)   # Quantity of shares matched
            
            print(f"[Ticker {self.buy_head.ticker}] Order matched: {match_quantity} shares at ${match_price}")

            self.buy_head.quantity -= match_quantity
            self.sell_head.quantity -= match_quantity

            if self.buy_head.quantity <= 0:
                satisfied_history.append(f"BUY order satisfied: [Ticker {self.buy_head.ticker}] {self.buy_head.order_type} {match_quantity} @ ${self.buy_head.price}")
                self.buy_head = self.buy_head.next
            if self.sell_head is not None and self.sell_head.quantity <= 0:
                satisfied_history.append(f"SELL order satisfied: [Ticker {self.sell_head.ticker}] {self.sell_head.order_type} {match_quantity} @ ${self.sell_head.price}")
                self.sell_head = self.sell_head.next