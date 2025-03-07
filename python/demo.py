import streamlit as st
import time
import random
import io
import sys
from main import addOrder, orderbooks, MAX_TICKERS
from orderbook import OrderBook
from globals import satisfied_history  # Ensure satisfied_history is defined in globals.py

st.title("Live Stock Trading Simulation")

# Sidebar controls: user chooses number of transactions and ticker.
num_transactions = st.sidebar.number_input("Number of Transactions", min_value=1, max_value=1000, value=50, step=1)
ticker = st.sidebar.number_input("Ticker to simulate", min_value=0, max_value=MAX_TICKERS-1, value=3)
simulate_button = st.sidebar.button("Simulate")

# Create placeholders for live log updates.
trans_placeholder = st.empty()
match_placeholder = st.empty()
history_placeholder = st.empty()

# Initialize log strings.
transactions_log = ""
matches_log = ""
satisfied_log = ""

if simulate_button:
    # Reset the OrderBook for the chosen ticker and clear satisfied_history.
    orderbooks[ticker] = OrderBook()
    satisfied_history.clear()
    st.info(f"Starting simulation for ticker {ticker} with {num_transactions} transactions...")

    original_stdout = sys.stdout

    for i in range(num_transactions):
        buffer = io.StringIO()
        sys.stdout = buffer

        # Generate a random order.
        order_type = random.choice(["BUY", "SELL"])
        quantity = random.randint(1, 100)
        base_price = 100.0
        if order_type == "BUY":
            price = base_price - random.randint(0, 5)
        else:
            price = base_price + random.randint(0, 5)
        price = round(price, 2)
        
        # Call addOrder, which also triggers matchOrder.
        addOrder(order_type, ticker, price, quantity)
        
        sys.stdout = original_stdout
        output = buffer.getvalue()
        
        # Append a transaction log entry.
        transactions_log += f"Order added: [Ticker {ticker}] {order_type} {quantity} @ ${price}\n"
        
        # Check for match messages in the captured output.
        for line in output.splitlines():
            if "Order matched:" in line:
                matches_log += line + "\n"
        
        # Update satisfied log from the global satisfied_history.
        satisfied_log = "\n".join(satisfied_history)
        
        # Update the placeholders using markdown with inline HTML.
        # The style below sets a fixed height, dark blue background, white text, and preserves newlines.
        trans_placeholder.markdown(
            f"**Transactions:**\n<div style='height:200px; overflow-y:scroll; background-color: #262730; color: white; padding: 10px; white-space: pre-wrap;'>{transactions_log}</div>",
            unsafe_allow_html=True,
        )
        match_placeholder.markdown(
            f"**Matches:**\n<div style='height:200px; overflow-y:scroll; background-color: #262730; color: white; padding: 10px; white-space: pre-wrap;'>{matches_log}</div>",
            unsafe_allow_html=True,
        )
        history_placeholder.markdown(
            f"**History of Satisfied Orders:**\n<div style='height:200px; overflow-y:scroll; background-color: #262730; color: white; padding: 10px; white-space: pre-wrap;'>{satisfied_log}</div>",
            unsafe_allow_html=True,
        )
        
        time.sleep(0.1)
    
    st.success("Simulation complete!")
