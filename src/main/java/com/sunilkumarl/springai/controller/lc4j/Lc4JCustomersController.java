package com.sunilkumarl.springai.controller.lc4j;

import com.sunilkumarl.springai.service.lc4j.CustomersAgent;
import com.sunilkumarl.springai.tool.lc4j.LC4JCustomerTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.langchain4j.agentic.AgenticServices;

@RestController
@RequestMapping("customers/api/v1/lc4j")
public class Lc4JCustomersController {

    private final CustomersAgent customerAgent;

    public Lc4JCustomersController(LC4JCustomerTools lC4JCustomerTools,
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



        customerAgent = AgenticServices.agentBuilder(CustomersAgent.class)
                .chatModel(chatModel)
               // .chatMemory(chatMemory)
                .tools(lC4JCustomerTools)
                .outputKey("CustomersAgent")
                .build();

    }

    @GetMapping("/llm")
    public String getLLMResponse(@RequestParam(value = "question" , defaultValue = "customer_name is Alice Johnson") String question) {
        System.out.println("Lc4JCustomersController - getLLMResponse() : Received question : \n" + question);
        String response = customerAgent.ask(question);
        System.out.println("Lc4JCustomersController - getLLMResponse() : LLM Response : \n" + response);
        return response;
    }

}
