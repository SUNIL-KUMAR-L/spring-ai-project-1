package com.sunilkumarl.springai.tool;

import com.sunilkumarl.springai.model.Customer;
import com.sunilkumarl.springai.model.Order;
import com.sunilkumarl.springai.model.Product;
import com.sunilkumarl.springai.service.CustomerService;
import com.sunilkumarl.springai.service.OrderService;
import com.sunilkumarl.springai.service.ProductService;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LangchainCustomerOrderTools {

    private CustomerService customerService;
    private OrderService orderService;
    private ProductService productService;

    public LangchainCustomerOrderTools(CustomerService customerService, OrderService orderService, ProductService productService) {
        this.customerService = customerService;
        this.orderService = orderService;
        this.productService = productService;
    }

    //@Tool(name = "getCustomerData", description = "Fetch customer data given the customer_name")
    @Tool("Fetch customer data given the customer name")
    public Customer getCustomerData(String customer_name) {
        List<Customer> customers = customerService.findByCustomerName(customer_name);
        if(customers != null && customers.size() > 0) {
            if (customers.size() == 1) {
                System.out.println("Tool (getCustomerData) : ARGUMENTS customer look by name :" + customer_name);
                System.out.println("customer data : \n" + customers.get(0));
                return customers.get(0);
            } else {
                System.out.println("customer look by customer_name :" + customer_name);
                System.out.println("many customer data : \n" + customers);
                return customers.get(0);
            }
        } else {
            System.err.println("NO customers matching by look by name :" + customer_name );
            return null;
        }
    }

    //@Tool(name = "getOrderData", description = "Fetch Orders data given the customer_id")
    @Tool("Fetch Orders data given the customer_id")
    public List<Order> getOrderData(Integer customer_id) {
        List<Order> orders = orderService.findOrdersByCustomerId(customer_id);
        System.out.println("Tool (getOrderData) : ARGUMENTS Order look by customer_id : "+ customer_id);
        System.out.println("Order data : \n" + orders);
        return orders;
    }




    //@Tool(name = "getProductsByProductIdList", description = "Fetch products data given the list of `product_id``")
    @Tool("Fetch products data given the list of product_id")
    public List<Product> getProductsByProductIdList(List<Integer> product_ids) {

        System.out.println("Tool (getProductsByProductIdList) : ARGUMENTS Product List look up by : " + product_ids);

        List<Product> foundProduct = new ArrayList<>();

        List<Product> products = productService.findAll();

        Map<Integer, Product> allProducstMap = new HashMap<>();
        for (int i = 0; i< products.size(); i++) {
            allProducstMap.put(products.get(i).product_id(), products.get(i));
        }

//
//        List<Integer> inputProductIds = new ArrayList<>();
//        for (int i = 0; i< product_ids.size(); i++) {
//            inputProductIds.add(product_ids.get(i).product_id());
//        }

       Set<Integer> allProducstIds =  allProducstMap.keySet();


        for (int i = 0; i< product_ids.size(); i++) {
            if(allProducstIds.contains(product_ids.get(i))){
                foundProduct.add(allProducstMap.get(product_ids.get(i)));
            }
        }

        return foundProduct;

    }


    //@Tool(name = "getAllProducts", description = "Fetch all Products")
   // @Tool("Fetch all Products")
    public List<Product> getAllProducts() {
        System.out.println("Tool (getAllProducts) NO ARGUMENTS ");
        List<Product> products = productService.findAll();
        System.out.println("List of all Products : "+ products);
        return products;
    }

    //@Tool(name = "getProductById", description = "Fetch product data given the product_id")
   // @Tool("Fetch product data given the product_id")
    public Product getProductById(Integer product_id) {
        System.out.println("Tool (getProductById) : ARGUMENTS by id : " + product_id);
        List<Product> products = productService.findProductById(product_id);
        if(products != null && products.size() > 0) {
            if (products.size() == 1) {
                System.out.println("Product look by id :" + products.get(0));
                return products.get(0);
            } else {
                System.out.println("Product look by product_id :" + products);
                System.out.println("many Products data : \n" + products);
                return products.get(0);
            }
        } else {
            System.err.println("NO Products matching by look by product_id :" + product_id );
            return null;
        }
    }
}
