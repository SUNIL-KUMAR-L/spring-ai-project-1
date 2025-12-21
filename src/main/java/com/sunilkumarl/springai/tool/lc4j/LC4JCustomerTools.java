package com.sunilkumarl.springai.tool.lc4j;

import com.sunilkumarl.springai.model.Customer;
import com.sunilkumarl.springai.service.CustomerService;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LC4JCustomerTools {

    private CustomerService customerService;

    private static final List<String> LOOK_UP_ALLOWED_CUSTOMER_ATTRIBUTES_ = List.of("customer_id", "customer_name", "email", "phone_number");

    public LC4JCustomerTools(CustomerService customerService) {
        this.customerService = customerService;
    }

   // @Tool("Fetch Customers data given the customer attribute name and customer attribute value")
    public Customer findCustomersByAttributeNameAndValue(String customerAttributeName, String customerAttributeValue) {
        System.out.println("Tool (findCustomersByKeyAndValue) : ARGUMENTS customer lookup by key : " + customerAttributeName + " and value : " + customerAttributeValue);
        if(!LOOK_UP_ALLOWED_CUSTOMER_ATTRIBUTES_.contains(customerAttributeName)) {
            System.err.println("findCustomersByKeyAndValue :: Customer attribute name : " + customerAttributeName + " is not allowed for lookup. Allowed attributes are : " + LOOK_UP_ALLOWED_CUSTOMER_ATTRIBUTES_);
            return null;
        }

        List<Customer> customers = customerService.findCustomersByAttributeNameAndValue(customerAttributeName, customerAttributeValue);
        if(customers != null && customers.size() > 0) {
            if (customers.size() == 1) {
                System.out.println("findCustomersByKeyAndValue :: customer data [0] : \n" + customers.get(0));
                return customers.get(0);
            } else {
                System.out.println("findCustomersByKeyAndValue :: Many customer data : \n" + customers);
                return customers.get(0);
            }
        } else {
            System.err.println("findCustomersByKeyAndValue :: NO customers matching lookup by key : " + customerAttributeName + " and value : " + customerAttributeValue);
            return null;
        }
    }

    @Tool("Fetch All Customers data")
    public List<Customer> findAllCustomers() {
        List<Customer> customers = customerService.findAll();
        System.out.println("Tool (findAllCustomers)");
        if(customers != null && customers.size() > 0) {
            System.out.println("findAllCustomers:: All customer data : \n" + customers);
            return customers;
        }
        System.err.println("findAllCustomers :: NO customers data found");
        return List.of();
    }

    @Tool("Fetch Customer data By Customer's Name")
    public Customer findByCustomerName(String customer_name) {

        System.out.println("Tool (findByCustomerName)");

        List<Customer> customers = customerService.findByCustomerName(customer_name);
        if(customers != null && customers.size() > 0) {
            if (customers.size() == 1) {
                System.out.println("findByCustomerName :: customer data [0] : \n" + customers.get(0));
                return customers.get(0);
            } else {
                System.out.println("findByCustomerName :: Many customer data : \n" + customers);
                return customers.get(0);
            }
        } else {
            System.err.println("findByCustomerName :: NO customers matching lookup by customer_name : " + customer_name);
            return null;
        }
    }

}
