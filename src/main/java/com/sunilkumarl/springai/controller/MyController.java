package com.sunilkumarl.springai.controller;


import com.sunilkumarl.springai.model.Product;
import com.sunilkumarl.springai.tool.CustomerOrderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

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
        //ADD CHAT Memory as an additional advisor
        this.chatClient = builder
                // add logging advisor, memory advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
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
                .tools(customerOrderTools)
                .system(promptText)
                .user(message)
                .call()
                .entity(new ParameterizedTypeReference<List<Product>>() {});
        return productList;

    }

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
