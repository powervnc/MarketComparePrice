# Market Price Comparator


## Overview

This is a Spring Boot application built with **Java 21**, using **PostgreSQL**, **Spring Data JPA**, and **Spring Web**. It tracks product prices and discounts across multiple stores and allows users to manage baskets and receive price alerts.

### Project Structure

- **src/main/resources/**  
  Contains CSV data folders used for initialization:  
  - `setup/` — static data (stores, brands, categories, products)  
  - `prices/` — product price CSV files  
  - `discounts/` — discount CSV files

- **market.price_comparator** — main package  
  - `controller/` — REST API endpoints  
  - `service/` — business logic and CSV import  
  - `model/` — JPA entities  
  - `repository/` — Spring Data repositories  
  - `dto/` — Data Transfer Objects

- **StartupInitializer.java**  
  On app startup, imports CSV data from the folders listed above. Tracks imported files in the database to avoid duplicate imports.

---

## Prerequisites

- Java 21
- Gradle
- PostgreSQL database running and accessible  
- Configure your DB connection in `src/main/resources/application.properties` or `.yml`  
- Recommended IDE: **IntelliJ IDEA** (for Gradle & Spring Boot support)

---

## Build and Run
- Clone the repo directly
- Add files as need (discounts, prices files)
- Before running the first time, add all the necessary brands, stores, categories in the corresponding files


## Assumptions

- If a certain product at a specific store has overlapping discount periods (e.g., 10.10–12.10 and 11.10–13.10), the first discount period is shortened to avoid overlap.
- If two discounts start on the same day for the same product and store, only the last discount entered into the system is applied.
- All prices are in RON; currency exchange is not currently considered.
- Stores, brands, and categories remain static throughout the application lifecycle and are loaded from CSV files.
- Product prices and discounts already entered into the system cannot be modified during runtime.
- Discounts cannot be applied to products that do not exist in the database.
- CSV files are assumed to be correctly formatted.
- CSV files do not contain multiple variants (e.g., due to misspelling or case sensitivity) of the same brand, category, or store name.
- A product is considered unique if it differs in name, brand, or quantity (for example, two cheeses from the same brand with 250g and 500g packaging are treated as different products).

### Feature-Specific Decisions

- Notifications are requested by the frontend via API calls, allowing clients to set intervals for periodically checking the current price of an item (e.g., in a React application).
- Notifications are store-specific to allow better filtering.
- A user can have multiple baskets (shopping lists) for better organization, such as separate lists for necessities or family.
- Data points are provided only for price changes, along with the time intervals during which the prices are valid.
- Data points extend as far back as the earliest price recorded in the database.

## Future Improvements

- Migrate the entire system to a database-centric architecture.
- Enable admin users to perform CRUD operations on all entities.
- Implement user authentication (currently users and related entities can only be added manually).
- Improve security by adding token-based authentication and HTTPS support.
- Provide users more control, such as removing notifications for specific products and managing baskets and products within baskets.
- Introduce more Data Transfer Objects (DTOs) to improve communication between backend and frontend.

## Request examples
See the provided JSON exports from Postman
