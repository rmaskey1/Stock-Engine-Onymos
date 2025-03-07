# Real-Time Stock Trading Engine

## Overview

This project implements a simplified real-time stock trading engine that supports matching buy and sell orders across 1,024 tickers. The engine simulates live trading by accepting orders, matching them according to price/time priority, and handling concurrent access using lock-free data structures.

Two separate implementations have been provided:

- **Python Implementation:**  
  Utilizes custom linked-list structures with simulated lock-free behavior (given Python’s GIL limitations) and includes an interactive Streamlit demo for live visualization. Unit tests are also provided.

- **Java Implementation:**  
  Leverages Java's atomic operations (`AtomicReference`, `compareAndSet`, etc.) to build a truly lock-free, concurrent order book. A set of JUnit tests is provided to validate the concurrency and matching logic.

## Project Architecture

### Core Components
- **Order:**  
  Represents a trading order. Both implementations define an Order class that contains order type (BUY/SELL), ticker, price, and quantity. In the Java version, order quantities and pointers are wrapped in atomic classes for thread safety.

- **OrderBook:**  
  Maintains two sorted lists of orders:
  - **Buy Orders:** Stored in descending order (highest price first).
  - **Sell Orders:** Stored in ascending order (lowest price first).  
  Matching is performed by comparing the highest buy and lowest sell orders, executing trades when conditions are met.
  The OrderBooks are stored in a list where the index of the OrderBook represents the ticker of which those orders belong to.

- **Adding Function:**
  A new order is added to its respective OrderBook based on the ticker number and whether it's a BUY or SELL order. 

- **Matching Function:**  
  When a new order is added, the engine checks if it can be matched with an opposing order. A match occurs if the buy order’s price is greater than or equal to the sell order’s price. Partial matches are allowed. Fully satisfied orders are removed from the order book and logged.

### Concurrency & Lock-Free Design
- **Python:**  
  Although Python’s Global Interpreter Lock (GIL) limits true parallelism, the design simulates a lock-free data structure using custom linked lists and atomic-like updates. Concurrency is simulated via threading and unit tests to stress race conditions.
  
- **Java:**  
  The Java implementation employs true atomic operations. By using `AtomicReference` for pointer manipulation and `AtomicInteger` for quantities, the design ensures that concurrent modifications (such as insertions and matching) are handled without explicit locks. The use of `compareAndSet` ensures that the linked list is updated atomically, maintaining consistency under heavy concurrency.

## Design Decisions

### Order Matching
- **Price-Time Priority:**  
  Orders are prioritized by price and then by order of arrival (FIFO). The engine matches orders when the highest buy order price meets or exceeds the lowest sell order price.
  
- **Partial Matching:**  
  If an order is partially filled, its quantity is reduced and it remains in the order book until completely satisfied.

- **Immediate Matching:**
  The matching logic is invoked immediately each time a new order is added. This behavior replicates the instantaneous nature of real-world stock exchanges.

### Data Structure
- **Custom Linked Lists:**  
  Instead of using built-in dictionary or map structures, the project uses custom linked lists to store orders for each ticker. This data structure avoids thread locking.
  In Java, the use of atomic classes and CAS loops allows for truly lock-free data structures. In Python, while the lock-free guarantee is not as strict due to the GIL, the custom linked-list data structure design mimics a lock-free structure and is validated through concurrency tests.

### Simulation & Visualization
- **Python Streamlit Demo:**  
  A live simulation demo is implemented using Streamlit. The demo allows users to choose the number of transactions and ticker. Live logs display:
  - Transactions (orders added)
  - Matches (order matching events)
  - History of satisfied orders (fully executed orders)  
  This interactive interface helps demonstrate the engine’s behavior in real time.
  
- **Unit Testing:**  
  Both implementations include unit tests:
  - **Python tests** verify core functionality and simulate race conditions.
  - **Java JUnit tests** ensure the correctness of order insertion, matching, and concurrent behavior.

## Running the Project

### Python
- **Dependencies:**  
  Python 3.8+, Streamlit, and standard libraries
- **Demo:**  
  Run the demo with:
  ```bash
  streamlit run demo.py
  ```
  (Ensure that your files are organized as outlined in the project structure.)
- **Tests:**  
  Run unit tests with:
  ```bash
  python test.py
  ```

### Java
- **Dependencies:**  
  JDK 8+, JUnit 4
- **Compile & Run:**  
  Compile:
  ```bash
  javac Main.java Order.java OrderBook.java
  ```
  Run:
  ```bash
  java Main
  ```
- **Unit Tests:**  
  Run JUnit tests (ensure your classpath includes the JUnit JAR and Hamcrest) by:
  ```bash
  javac -cp .:junit-4.13.2.jar Order.java OrderBook.java Main.java StockEngineTest.java
  java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore StockEngineTest
  ```

## Acknowledgements

Onymos - Project Provider

## Contact
GitHub: rmaskey1
Email: maskey.reshaj@gmail.com
