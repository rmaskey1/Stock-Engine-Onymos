

# **Java Implementation README** (`README.md`)

# Real-Time Stock Trading Engine (Java)

This Java project provides a concurrent, real-time stock trading engine capable of adding buy/sell orders and matching orders in real-time. The engine handles concurrency effectively by leveraging Java's atomic operations and lock-free structures.

## Project Files

- `Main.java` — Application entry point. Contains logic for order creation and matching.
- `Order.java` — Defines the `Order` class with essential order properties.
- `OrderBook.java` — Manages buy/sell orders using atomic operations for concurrent environments.
- `StockEngineTest.java` — JUnit tests for validating functionality and concurrency handling.

## Requirements

- Java Development Kit (JDK) 8 or higher
- JUnit 4 or 5 (Ensure your classpath includes JUnit libraries)

## How to Run

### **Compile and Run the Application**

Navigate to the Java project's root directory in your terminal and compile:

```bash
javac Main.java Order.java OrderBook.java
```
