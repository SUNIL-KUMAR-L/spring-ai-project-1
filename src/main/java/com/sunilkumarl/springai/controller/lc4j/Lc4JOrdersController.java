package com.sunilkumarl.springai.controller.lc4j;

import com.sunilkumarl.springai.service.lc4j.OrdersAgent;
import com.sunilkumarl.springai.tool.lc4j.LC4JCustomerTools;
import com.sunilkumarl.springai.tool.lc4j.LC4JOrderTools;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders/api/v1/lc4j")
public class Lc4JOrdersController {

    private final OrdersAgent ordersAgent;

    public Lc4JOrdersController(LC4JOrderTools lC4JorderTools,
                                @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
                                @Value("${spring.ai.ollama.chat.model:gpt-oss:20b}") String ollamaModelName) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .logRequests(true)
                .logResponses(true)
                .build();



        ordersAgent = AgenticServices.agentBuilder(OrdersAgent.class)
                .chatModel(chatModel)
               // .chatMemory(chatMemory)
                .tools(lC4JorderTools)
                .outputKey("OrdersAgent")
                .build();

    }

    @GetMapping("/llm")
    public String getLLMResponse(@RequestParam(value = "question" , defaultValue = "customer_id is 1") String question) {
        System.out.println("Lc4JOrdersController - getLLMResponse() : Received question : \n" + question);
        String response = ordersAgent.ask(question);
        System.out.println("Lc4JOrdersController - getLLMResponse() : LLM Response : \n" + response);
        return response;
    }

}
