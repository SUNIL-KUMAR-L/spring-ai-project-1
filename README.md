# SpringAI

SpringAI is a minimal Spring Boot example that demonstrates using a Local LLM ChatClient plus custom "tools" to answer customer-order related questions. The application accepts a customer name (via an HTTP GET parameter) and returns the distinct list of products the customer has purchased. Internally the LLM is wired with custom tools for fetching customer, order and product data from an external mock API (json-server).

Contents
- Overview
- Requirements
- External mock API (json-server)
- Application endpoints (as implemented in `MyController.java`)
- Internal components and where to find them
- Examples (curl)
- Build & run
- Troubleshooting

Overview
--------
The application exposes a small HTTP API that forwards user prompts to an LLM-style `ChatClient`. The ChatClient is configured with:
- a logging advisor (`SimpleLoggerAdvisor`) and
- a domain-specific toolset (`CustomerOrderTools`) that knows how to call the external mock API to fetch customers, orders and products.

Given a user prompt containing a customer name, the system runs a strict Tool → Extract → Next Tool flow (see `promptText` in `MyController.java`):
1. Use the provided customer name string to call `getCustomerData` tool to resolve the customer_id.
2. Use the customer_id to call `getOrderData` to get orders.
3. Extract unique product ids from orders and call `getProductsByProductIdList` to fetch product details.
4. Return a list of distinct product records (or an empty list if none found).

Requirements
------------
- Java 17+ (or the version used in your project pom)
- Maven (the repo includes Maven wrappers `mvnw.cmd` for Windows)
- Node.js + npm (to run `json-server` for the mock external API)

External mock API (json-server)
------------------------------
A json-server instance provides the sample data used by `CustomerOrderTools`.
Files:
- `src/main/resources/ecom_mock_data/db.json`  (mock dataset)
- `src/main/resources/ecom_mock_data/api-start-json-server.txt` (instructions)

To run the mock API (from the `ecom_mock_data` folder):

```powershell
# open a terminal in src/main/resources/ecom_mock_data
npx json-server db.json
```

Default endpoints exposed by json-server (base URL: `http://localhost:3000`):
- http://localhost:3000/customers
- http://localhost:3000/products
- http://localhost:3000/prices
- http://localhost:3000/orders

Make sure `json-server` is running before calling the Spring app endpoints.

Application API Endpoints (as implemented in `MyController.java`)
----------------------------------------------------------------
Base path: `/api/chat`

1) GET /api/chat/llm
- Query parameter: `message` (optional)
- Description: Sends the text message directly to the configured `ChatClient` and returns the raw string response from the model.
- Example:
  - `/api/chat/llm?message=What+is+the+capital+of+India%3F`

2) GET /api/chat/orders
- Query parameter: `message` (optional, default: `customer name is `Bob Smith``)
- Description: The LLM is driven with a system prompt (see `promptText`) and `CustomerOrderTools`. Provide a message containing the customer name (for example: `customer name is Bob Smith`) and the API responds with a JSON array of distinct product records the customer purchased.
- Example:
  - `/api/chat/orders?message=customer+name+is+Bob+Smith`

Internal components
-------------------
- `ChatClient` (org.springframework.ai.chat.client.ChatClient)
  - Configured in `MyController` using `ChatClient.Builder` with `SimpleLoggerAdvisor` and `CustomerOrderTools`.
- `CustomerOrderTools` (src/main/java/com/sunilkumarl/springai/tool/CustomerOrderTools.java)
  - Custom tool implementation used by the ChatClient to fetch data from the external json-server endpoints.
- `SimpleLoggerAdvisor` (org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor)
  - A simple advisor that logs chat activity and helps debugging.

Files of interest
- Controller: `src/main/java/com/sunilkumarl/springai/controller/MyController.java`
- Models: `src/main/java/com/sunilkumarl/springai/model/` (Customer, Order, OrderLine, Product)
- Tools: `src/main/java/com/sunilkumarl/springai/tool/CustomerOrderTools.java`
- App test example: `src/main/resources/application_test.txt` (contains example curl + expected response)

Examples
--------
Copy the example from `src/main/resources/application_test.txt`.

Example request (returns a JSON array of products):

```powershell
curl --location 'http://localhost:8080/api/chat/orders' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json'
```

Example response (sample):

```json
[
    {
        "product_id": 7,
        "product_desc": "Bluetooth Speaker",
        "product_image_url": "http://example.com/speaker.jpg",
        "product_category": "Electronics"
    },
    {
        "product_id": 9,
        "product_desc": "Smartwatch",
        "product_image_url": "http://example.com/watch.jpg",
        "product_category": "Wearables"
    }
]
```

Build & Run
-----------
1. Start the mock external API (in its folder):

```powershell
cd src/main/resources/ecom_mock_data
npx json-server db.json
```

2. Run the Spring Boot application (from project root on Windows):

```powershell
# using the Maven wrapper
mvnw.cmd spring-boot:run

# or build and run the jar
mvnw.cmd -DskipTests package
java -jar target/*.jar
```

By default the Spring app runs on port 8080. You can override the port using `--server.port=XXXX`.

Troubleshooting
---------------
- If the `orders` endpoint returns an empty list, confirm the json-server mock API is running and contains matching data for the requested customer name.
- If you see CORS errors in a browser, either enable CORS in the app or call from server-side tools (curl/postman).
- Check logs: `SimpleLoggerAdvisor` prints chat exchanges to the application logs to help debugging.

Tests and Examples
------------------
- Example test curl is saved in `src/main/resources/application_test.txt`.

Requirements coverage
---------------------
- Documented the SpringAI behavior: DONE
- Documented external json-server and endpoints: DONE
- Included controller endpoints matching `MyController.java`: DONE
- Mentioned internal components (`ChatClient`, `CustomerOrderTools`, `SimpleLoggerAdvisor`): DONE
- Included usage example from `application_test.txt`: DONE

If you want I can also:
- Add a small example client (Node/Python) that calls `/api/chat/orders` and prints results.
- Add a README section for how to configure a real LLM provider and environment variables if you plan to swap the sample client for a real provider.


