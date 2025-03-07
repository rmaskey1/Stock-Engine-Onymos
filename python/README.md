# Real-Time Stock Trading Engine (Python)

This project implements a simplified real-time stock trading engine in Python, capable of adding buy/sell orders, matching compatible orders in real-time, and handling concurrent access through lock-free data structures.

## Project Files

- `main.py` — Entry point, contains logic to add orders and trigger matches.
- `order.py` — Defines the `Order` class structure.
- `orderbook.py` — Manages the buy/sell orders using a custom linked-list structure and matching logic.
- `globals.py` — Contains global state (`satisfied_history`) for tracking fulfilled orders (used for demo)
- `test.py` — Unit tests for validating the trading engine functionality and handling race conditions.
- `demo.py` — Streamlit app for interactive live trading simulation and visualization.

## Demo Requirements

- Python 3.8 or higher
- Streamlit

Install Streamlit if you haven't already:
```bash
pip install streamlit
```

## Running the Demo

To run the demo, simply use the command below in the terminal
```bash
streamlit run demo.py
```  

## Interface

- Choose how many order transactions to simulate and for which ticker
- Once submitted, a real-time simulation of the orders, matches, and satisfied orders will begin
- **Why only for one ticker**
  - Purely for demonstration purposes
  - Simulating all 1024 available tickers gives the program a very small chance to match orders
  - Simulating one ticker will allow more frequent order matches

![image](https://github.com/user-attachments/assets/57133829-cf74-4da2-879b-a5b11b4deaf2)
