package com.sunilkumarl.springai.controller;


import com.sunilkumarl.springai.model.CustomerOrderedProducts;
import com.sunilkumarl.springai.model.Product;
import com.sunilkumarl.springai.service.LangChainAgent;
import com.sunilkumarl.springai.tool.LangchainCustomerOrderTools;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/langchain/chat")
public class MyLangchainController {


    private final LangChainAgent agent;

    public MyLangchainController(LangchainCustomerOrderTools customerOrderTools,
                                 @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
                                 @Value("${spring.ai.ollama.chat.model:gpt-oss:20b}") String ollamaModelName) {

        ChatModel chatModel = OllamaChatModel.builder()
                                .baseUrl(ollamaBaseUrl)
                                .modelName(ollamaModelName)
                                .logRequests(true)
                                .logResponses(true)
                                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                                                        .maxMessages(10)
                                                        .build();


        agent = AiServices.builder(LangChainAgent.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .tools(customerOrderTools)
                .build();
    }

    @GetMapping("/llm")
    public String getLLMResponse(@RequestParam(value = "message", defaultValue = "What is the capital of India?") String message) {
        System.out.println("Invoking LLM endpoint... getLLMResponse");
        String response = agent.getLLMResponse(message);
        System.out.println("getLLMResponse Response from agent: " + response);
        return response;
    }

    @GetMapping("/orders")
    public String getCustomerOrderedProductData(@RequestParam(value = "message", defaultValue = "customer name is `Bob Smith`") String message) {
        //List<Product> productList = agent.getCustomerOrderedProductData(promptText, message);
        //return productList;
        System.out.println("Invoking detailed endpoint... getCustomerOrderedProductData");
        String response =  agent.getCustomerOrderedProductData(litePromptText, message);
        System.out.println("getCustomerOrderedProductData Response from agent: " + response);
        // Here, you would typically parse the JSON response into a List<Product> object.
        // For simplicity, we'll return an empty object.
        return response;

    }

    @GetMapping("/detailed")
    public String getCustomerOrderedProductDetails
            (@RequestParam(value = "message", defaultValue = "customer name is `Bob Smith`") String message) {
//        CustomerOrderedProducts customerOrderedProducts = agent.getCustomerOrderedProductDetails(litePromptText, message);
//        return customerOrderedProducts;
        System.out.println("Invoking detailed endpoint... getCustomerOrderedProductDetails");
        String response =  agent.getCustomerOrderedProductDetails(litePromptTextGetProductsInformation, message);
        System.out.println("getCustomerOrderedProductDetails Response from agent: " + response);
        // Here, you would typically parse the JSON response into a CustomerOrderedProducts object.
        // For simplicity, we'll return an empty object.
        return response;//new CustomerOrderedProducts(null, null, null);
    }

    private final String litePromptText =

            """
                You are a Customer Order Aggregation Assistant.
                        
                        TASK:
                        Given a customer name, respond back with 
                        Only List of Product details purchased by the same customer.
                        
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
    private final String litePromptTextGetProductsInformation =

            """
                You are a Customer Order Aggregation Assistant.
                        
                        TASK:
                        Given a customer name, respond back with  
                        Customer details,  
                        List of Order details, 
                        List of Product details purchased by the same customer.
                        
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
