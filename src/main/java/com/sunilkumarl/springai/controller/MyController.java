package com.sunilkumarl.springai.controller;


import com.sunilkumarl.springai.model.Customer;
import com.sunilkumarl.springai.model.CustomerOrderedProducts;
import com.sunilkumarl.springai.model.Product;
import com.sunilkumarl.springai.tool.CustomerOrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class MyController {

    private final ChatClient chatClient;
    private final CustomerOrderTools customerOrderTools;

    public MyController(ChatClient.Builder builder, CustomerOrderTools customerOrderTools) {

        // SET chatMemory as InMemoryChatMemoryRepository
        var chatMemory = MessageWindowChatMemory.builder()
                                                .build();

        this.chatClient = builder
                // add logging advisor, memory advisor
                .defaultAdvisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(chatMemory).build())
                // add tools
                .defaultTools(customerOrderTools)
                .build();

        this.customerOrderTools = customerOrderTools;
    }

    @GetMapping("/llm")
    public String getLLMResponse(@RequestParam(value = "message", defaultValue = "What is the capital of India?") String message) {
         ChatClient.CallResponseSpec responseSpec = chatClient.prompt()
                .user(message)
                .call();
         return responseSpec.content();

    }

    @GetMapping("/orders")
    public List<Product> getCustomerOrderedProductData(@RequestParam(value = "message", defaultValue = "customer name is `Bob Smith`") String message) {
        List<Product> productList = chatClient.prompt()
                .system(promptText)
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<Product>>() {});
        return productList;
    }

    @GetMapping("/detailed")
    public CustomerOrderedProducts getCustomerOrderedProductDetails
            (@RequestParam(value = "message", defaultValue = "customer name is `Bob Smith`") String message) {
        CustomerOrderedProducts customerOrderedProducts = chatClient.prompt()
                .system(litePromptText)
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<CustomerOrderedProducts>() {});
        return customerOrderedProducts;
    }

    private final String litePromptText =

            """
                You are a Customer Order Aggregation Assistant.
                        
                        TASK:
                        Given a customer name, return a JSON object.
                        
                        TOOLS:
                        You have access to external data tools. You MUST use them to retrieve real data. Do NOT fabricate or assume data.
                        
                        RULES:
                        - Always call the appropriate tools to fetch data.
                        - Use tool outputs as the single source of truth.
                        - Return ONLY valid, parsable JSON.
                        - Do NOT include explanations, comments, or extra text outside the JSON.
                        - If no data is found, return empty arrays for orders and products.
            """
            ;
    private final String litePromptTextWithSchemaName =

            """
                You are a Customer Order Aggregation Assistant.
                        
                        TASK:
                        Given a customer name, return a JSON object strictly matching the CustomerOrderedProducts schema.
                        
                        TOOLS:
                        You have access to external data tools. You MUST use them to retrieve real data. Do NOT fabricate or assume data.
                        
                        RULES:
                        - Always call the appropriate tools to fetch customer, order, and product data.
                        - Use tool outputs as the single source of truth.
                        - Return ONLY valid, parsable JSON.
                        - Do NOT include explanations, comments, or extra text outside the JSON.
                        - If no data is found, return empty arrays for orders and products.
            """
            ;

    private final String detailedPromptText =
            """
                You are a Customer Order Aggregation Assistant.
                        
                        Your task is to return a JSON object of type CustomerOrderedProducts when given a customer name.
                        
                        You have access to external tools to fetch data.
                        
                        You must follow this exact process:
                        
                        Use the tool to fetch Customer data using the provided customer_name.
                        
                        Extract the customer_id from the Customer response.
                        
                        Use the tool to fetch Order data using the customer_id.
                        
                        From each Order, extract all unique product_id values found in order_lines.
                        
                        Use the tool to fetch Product data for each unique product_id.
                        
                        Construct and return the final response strictly in this JSON shape:
                        
                        {
                          "customer": {
                            "customer_id": 0,
                            "customer_name": "",
                            "email": "",
                            "phone_number": ""
                          },
                          "orders": [
                            {
                              "order_id": 0,
                              "customer_id": 0,
                              "order_total": 0.0,
                              "order_lines": [
                                {
                                  "order_id": 0,
                                  "order_line_id": 0,
                                  "order_line_seq_id": 0,
                                  "product_id": 0,
                                  "product_price": 0.0,
                                  "product_qty": 0
                                }
                              ]
                            }
                          ],
                          "products": [
                            {
                              "product_id": 0,
                              "product_desc": "",
                              "product_image_url": "",
                              "product_category": ""
                            }
                          ]
                        }
                        
                        
                        Important rules:
                        
                        Always use tools to retrieve data (never fabricate data).
                        
                        Return only valid JSON.
                        
                        Do not include explanations or extra text.       
            """
            ;



    private final String promptText =
            """
                You are Customer Orders Data Assistant.
                       
                       Your task:
                       
                       Receive a customer_name from the user.
                       
                       Call the tool getCustomerData
                       
                       Use the parameter customer_name exactly as provided by the user.
                       
                       Extract customer_id from the tool’s response.
                       
                       Call the tool getOrderData
                       
                       Pass the extracted customer_id.
                       
                       Retrieve the list of orders.
                       
                       Extract list of distinct product_ids from the tool’s response.
                       
                       Call the tool getProductsByProductIdList
                       
                       Pass the extracted list of distinct product_ids.
                       
                       Return the final result to the user:
                       
                       Provide a list of distinct product data from the customer's orders.
                       
                       If no orders or no products exist, return an empty list.
                       
                       IMPORTANT RULES
                       
                       Always follow Tool → Extract → Next Tool flow strictly.
                       
                       Do not infer or guess customer_id — it must come from getCustomerData.
                       
                       If the user input is unclear or missing customer_name, ask for clarification.
                       
                       Output only the final distinct product_ids list unless the user asks for more.       
            """
            ;
}
