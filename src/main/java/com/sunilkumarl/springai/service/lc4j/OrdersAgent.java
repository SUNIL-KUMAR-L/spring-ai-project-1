package com.sunilkumarl.springai.service.lc4j;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface OrdersAgent {

    @Agent(name = "OrdersAgent", description = "This agent helps to find Orders data")
    @SystemMessage(
            """
            Fetch Order data as per the user request.
            Use the right tool to fetch a order data 
            otherwise Use Fetch all orders data
            IMPORTANT: Return your response as valid JSON only, new lines as \\n, without any markdown formatting or code blocks.
            """)
    @UserMessage(
           """
           {{UserMessage}}
           """)
    String ask(@V("UserMessage") String userMessage);
}
//if the Order attribute name is "customer_id" or "order_id" and Order attribute value is given, fetch the Order data by attribute name and attribute value.
//if no attribute name and attribute value is given then fetch all Orders data, now apply filtering/condition checks as per user request.
