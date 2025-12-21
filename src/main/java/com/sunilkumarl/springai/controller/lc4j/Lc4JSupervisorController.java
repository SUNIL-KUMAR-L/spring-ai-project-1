package com.sunilkumarl.springai.controller.lc4j;

import com.sunilkumarl.springai.service.lc4j.CustomersAgent;
import com.sunilkumarl.springai.service.lc4j.OrdersAgent;
import com.sunilkumarl.springai.tool.lc4j.LC4JCustomerTools;
import com.sunilkumarl.springai.tool.lc4j.LC4JOrderTools;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
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
@RequestMapping("superagent/api/v1/lc4j")
public class Lc4JSupervisorController {

    private final CustomersAgent customerAgent;
    private final OrdersAgent ordersAgent;
    private final SupervisorAgent supervisorAgent;

    public Lc4JSupervisorController(LC4JCustomerTools lC4JCustomerTools,
                                    LC4JOrderTools lC4JorderTools,
                                    @Value("${spring.ai.ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
                                    @Value("${spring.ai.ollama.chat.model:gpt-oss:20b}") String ollamaModelName) {

        ChatMemory customersAgentChatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();

        ChatMemory ordersAgentChatMemory = MessageWindowChatMemory.builder()
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
                //.chatMemory(customersAgentChatMemory)
                .tools(lC4JCustomerTools)
                .outputKey("CustomersAgent")
                .build();

        ordersAgent = AgenticServices.agentBuilder(OrdersAgent.class)
                .chatModel(chatModel)
               // .chatMemory(ordersAgentChatMemory)
                .tools(lC4JorderTools)
                .outputKey("OrdersAgent")
                .build();

        // 2. Build the Supervisor agent
        supervisorAgent = AgenticServices.supervisorBuilder()
                .chatModel(chatModel)
                .subAgents(customerAgent,ordersAgent)
                .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
                //.responseStrategy(SupervisorResponseStrategy.SUMMARY) // we want a summary of what happened, rather than retrieving a response
                .supervisorContext("Always use available subAgents configured. if any Specific Customer's Order data to be fetched from `OrdersAgent` then first get the customer_id from Customer's data using `CustomersAgent`, Always answer in English. When invoking agent, use pure JSON (no backticks, and new lines as backslash+n).")
                .build();

    }

    @GetMapping("/llm")
    public String getLLMResponse(@RequestParam(value = "question" , defaultValue = "`customer_name` is `Alice Johnson`, get the Orders purchased by this customer") String question) {
        System.out.println("Lc4JSupervisorController - getLLMResponse() : Received question : \n" + question);
        String response = supervisorAgent.invoke(question);
        System.out.println("Lc4JSupervisorController - getLLMResponse() : LLM Response : \n" + response);
        return response;
    }

}
