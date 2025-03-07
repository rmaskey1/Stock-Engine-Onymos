# Real-Time Stock Trading Engine (Python)

This project implements a simplified real-time stock trading engine in Python, capable of adding buy/sell orders, matching compatible orders in real-time, and handling concurrent access through lock-free data structures.

## 📄 Project Files

- `main.py` — Entry point, contains logic to add orders and trigger matches.
- `order.py` — Defines the `Order` class structure.
- `orderbook.py` — Manages the buy/sell orders using a custom linked-list structure and matching logic.
- `globals.py` — Contains global state (`satisfied_history`) for tracking fulfilled orders.
- `test.py` — Unit tests for validating the trading engine functionality and handling race conditions.
- `demo.py` — Streamlit app for interactive live trading simulation and visualization.

## 🚀 Requirements

- Python 3.8 or higher
- Streamlit

Install Streamlit if you haven't already:
```bash
pip install streamlit
