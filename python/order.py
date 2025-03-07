class Order:

    def __init__(self, order_type, ticker, price, quantity):
        self.order_type = order_type.upper()    # "BUY" or "SELL"
        self.ticker = ticker                    # Ticker symbol index (0 to 1023)
        self.quantity = quantity                # Number of shares
        self.price = price                      # Price per share
        self.next = None                        # Reference for linked list node  

    def __repr__(self):
        return f"[Ticker {self.ticker}] {self.order_type} {self.quantity} @ ${self.price}"