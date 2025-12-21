package com.sunilkumarl.springai.service.lc4j;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CustomersAgent {

    @Agent(name = "CustomersAgent", description = "This agent helps to find customer data")
    @SystemMessage(
            """
            Fetch customer data as per the user request.
            Use the right tool to fetch a customer data 
            otherwise Use Fetch all customers data
            IMPORTANT: Return your response as valid JSON only, new lines as \\n, without any markdown formatting or code blocks.
            """)
    @UserMessage(
           """
           {{UserMessage}}
           """)
    String ask(@V("UserMessage") String userMessage);
}
//if the customer attribute name and customer attribute value is given, fetch the customer data by attribute name and attribute value.
//if no attribute name and attribute value is given then fetch all customers data, now apply filtering/condition checks as per user request.