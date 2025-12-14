package com.sunilkumarl.springai.service;

import com.sunilkumarl.springai.model.CustomerOrderedProducts;
import com.sunilkumarl.springai.model.Product;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


import java.util.List;

public interface LangChainAgent {


    public String getLLMResponse(String message);

//    @SystemMessage("""
//            You are a helpful AI assistant that helps customer to find their ordered product Ids from the given customer and customer's ordered data.
//            """
//            )
//            //You must answer in JSON format only. Remove any additional text or explanation. Remove ```json` tags if any.
//            //""")
    @SystemMessage("{{SystemMessage}}")
    @UserMessage("{{UserMessage}}")
   // public List<Product> getCustomerOrderedProductData(@V("SystemMessage") String systemMessage,
    //                                                   @V("UserMessage") String userMessage);
    public String getCustomerOrderedProductData(@V("SystemMessage") String systemMessage,
                                                @V("UserMessage") String userMessage);

//    @SystemMessage("""
//            You are a helpful AI assistant that helps customer to find their ordered products information from the given customer and customer's ordered data.
//            """
//                )
//            //You must answer in JSON format only. Remove any additional text or explanation. Remove ```json` tags if any.
//            //""")
    @SystemMessage("{{SystemMessage}}")
    @UserMessage("{{UserMessage}}")
    //public CustomerOrderedProducts getCustomerOrderedProductDetails(@V("SystemMessage") String systemMessage,
    //                                                                @V("UserMessage") String userMessage);
    public String getCustomerOrderedProductDetails(@V("SystemMessage") String systemMessage,
                                                   @V("UserMessage") String userMessage);

}
