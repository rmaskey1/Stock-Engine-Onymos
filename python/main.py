import random
import threading
import time

from order import Order
from orderbook import OrderBook
from globals import satisfied_history

MAX_TICKERS = 1024

# List of OrderBooks for each possible ticker
orderbooks = []


for i in range(MAX_TICKERS):
    orderbooks.append(OrderBook())

def addOrder(order_type, ticker, price, quantity):

    if ticker < 0 or ticker >= MAX_TICKERS:
        print("invalid ticker")
        return
    
    order = Order(order_type, ticker, price, quantity)
    orderbooks[ticker].insert_order(order)
    print(f"Order successfully added: {order}")

    matchOrder(ticker)

def matchOrder(ticker):
    orderbooks[ticker].match_order()

def simulate(orders):
    for i in range(orders):
        order_type = random.choice(["BUY", "SELL"])
        ticker = random.randint(0, MAX_TICKERS - 1)
        quantity = random.randint(1, 100)
        
        base_price = 100

        if order_type == "BUY":
            # price = base_price * (1 - random.uniform(0, 0.05))  # 0 to 5% positive deviation in buy orders
            price = base_price - random.randint(0, 5)  # 0 to 5% positive deviation in buy orders
        else:
            # price = base_price * (1 + random.uniform(0, 0.05))  # 0 to 5% negative deviation in sell orders
            price = base_price + random.randint(0, 5)  # 0 to 5% positive deviation in buy orders
        price = round(price, 2)
        
        addOrder(order_type, ticker, price, quantity)
        
        time.sleep(0.1)

def simulation_thread():
    simulate(50)

if __name__ == "__main__":
    threads = []
    num_threads = 5  # For example, 5 concurrent traders
    for i in range(num_threads):
        t = threading.Thread(target=simulation_thread)
        threads.append(t)
        t.start()
    for t in threads:
        t.join()