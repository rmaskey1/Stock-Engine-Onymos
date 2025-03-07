import unittest
import random
import time
from concurrent.futures import ThreadPoolExecutor

from order import Order
from orderbook import OrderBook
from main import addOrder, matchOrder, orderbooks

MAX_TICKERS = 1024

class TestStockEngine(unittest.TestCase):

    def setUp(self) -> None:
        for i in range(MAX_TICKERS):
            orderbooks[i] = OrderBook()

    def test_valid_tickers_addOrder(self):

        ticker1 = 2
        ticker2 = MAX_TICKERS-3

        addOrder("BUY", ticker1, 100, 10)
        addOrder("SELL", ticker2, 100, 10)

        self.assertIsNotNone(orderbooks[ticker1].buy_head)      # Head for buy orders should not be None
        self.assertIsNotNone(orderbooks[ticker2].sell_head)     # Head for sell orders should not be None

    def test_invalid_tickers_addOrder(self):

        ticker1 = -1
        ticker2 = MAX_TICKERS

        addOrder("BUY", ticker1, 100, 10)
        addOrder("SELL", ticker2, 100, 10)

        self.assertIsNone(orderbooks[ticker1].buy_head)     # Head for buy orders should be None
        self.assertRaises(IndexError, lambda: orderbooks[ticker2].sell_head)    # Head for buy orders should be None
    
    def test_full_match_matchOrder(self):

        ticker = 1

        addOrder("BUY", ticker, 103, 50)
        addOrder("SELL", ticker, 100, 50)

        print("Buy head:", orderbooks[ticker].buy_head)
        print("Sell head:", orderbooks[ticker].sell_head)

        # For full match, both orders should be fulfilled and removed from their respective lists
        self.assertIsNone(orderbooks[ticker].buy_head)
        self.assertIsNone(orderbooks[ticker].sell_head)

    def test_partial_buy_match_matchOrder(self):

        ticker = 1

        addOrder("BUY", ticker, 103, 20)
        addOrder("SELL", ticker, 100, 50)

        print("Buy head:", orderbooks[ticker].buy_head)
        print("Sell head:", orderbooks[ticker].sell_head)

        # For partial match where the buy order is fully matched but the sell order is partially matched:
        #   1. The buy order should be removed from its list
        self.assertIsNone(orderbooks[ticker].buy_head)
        #   2. The sell order should remain with the leftover shares, which is 30 in this case
        self.assertIsNotNone(orderbooks[ticker].sell_head)
        self.assertEqual(orderbooks[ticker].sell_head.quantity, 30)
    
    def test_partial_sell_match_matchOrder(self):

        ticker = 1

        addOrder("BUY", ticker, 103, 50)
        addOrder("SELL", ticker, 100, 20)

        print("Buy head:", orderbooks[ticker].buy_head)
        print("Sell head:", orderbooks[ticker].sell_head)

        # For partial match where the buy order is fully matched but the sell order is partially matched:
        #   1. The sell order should be removed from its list
        self.assertIsNone(orderbooks[ticker].sell_head)
        #   2. The buy order should remain with the leftover shares, which is 30 in this case
        self.assertIsNotNone(orderbooks[ticker].buy_head)
        self.assertEqual(orderbooks[ticker].buy_head.quantity, 30)
    
    def test_no_match_matchOrder(self):

        ticker = 1

        addOrder("BUY", ticker, 100, 50)
        addOrder("SELL", ticker, 103, 50)

        print("Buy head:", orderbooks[ticker].buy_head)
        print("Sell head:", orderbooks[ticker].sell_head)

        # For full match, both orders should remain their respective lists
        self.assertIsNotNone(orderbooks[ticker].buy_head)
        self.assertIsNotNone(orderbooks[ticker].sell_head)

    def test_race_condition_buy_order_sorting(self):

        ticker = 3

        orderbooks[ticker] = OrderBook()
        num_threads = 50

        # Each worker will add a BUY order with a random price between 90 and 110.
        def worker():
            quantity = 10
            price = random.uniform(90, 110)
            price = round(price, 2)
            addOrder("BUY", ticker, price, quantity)

        with ThreadPoolExecutor(max_workers=num_threads) as executor:
            futures = [executor.submit(worker) for i in range(num_threads)]
            for future in futures:
                future.result()
        
        time.sleep(0.1)

        current = orderbooks[ticker].buy_head
        count = 0
        prev_price = float('inf')
        sorted_descending = True
        while current is not None:
            if current.price > prev_price:
                sorted_descending = False
            prev_price = current.price
            count += 1
            current = current.next
        print("Final BUY order list for ticker", ticker)
        
        current = orderbooks[ticker].buy_head
        while current is not None:
            print(current)
            current = current.next
        print("Total orders in list:", count)

        self.assertEqual(count, num_threads, f"Expected {num_threads} orders, but got {count}")
        self.assertTrue(sorted_descending, "Orders are not sorted in descending order")

if __name__ == '__main__':
    unittest.main()