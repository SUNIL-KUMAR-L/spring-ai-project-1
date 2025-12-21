package com.sunilkumarl.springai.tool.lc4j;

import com.sunilkumarl.springai.model.Order;
import com.sunilkumarl.springai.service.OrderService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LC4JOrderTools {

    private OrderService orderService;

    private static final List<String> LOOK_UP_ALLOWED_ORDER_ATTRIBUTES = List.of("customer_id", "order_id");

    public LC4JOrderTools(OrderService orderService) {
        this.orderService = orderService;
    }

    @Tool("Fetch Orders data given the Order attribute name and Orders attribute value")
    public List<Order> findOrdersByAttributeNameAndValue(String orderAttributeName, String orderAttributeValue) {
        System.out.println("Tool (findOrdersByAttributeNameAndValue) : ARGUMENTS order lookup by key : " + orderAttributeName + " and value : " + orderAttributeValue);
        if(!LOOK_UP_ALLOWED_ORDER_ATTRIBUTES.contains(orderAttributeName)) {
            System.err.println("findOrdersByAttributeNameAndValue :: Orders attribute name : " + orderAttributeName + " is not allowed for lookup. Allowed attributes are : " + LOOK_UP_ALLOWED_ORDER_ATTRIBUTES);
            return null;
        }
        List<Order> orders = orderService.findOrdersByAttributeNameAndValue(orderAttributeName, orderAttributeValue);
        if(orders != null && orders.size() > 0) {
                System.out.println("findOrdersByAttributeNameAndValue :: Orders data : \n" + orders);
                return orders;
        } else {
            System.err.println("findOrdersByAttributeNameAndValue :: NO orders matching lookup by key : " + orderAttributeName + " and value : " + orderAttributeValue);
            return List.of();
        }
    }

    @Tool("Fetch All Orders data")
    public List<Order> findAllOrders() {
        System.out.println("Tool (findAllOrders)");
        List<Order> orders = orderService.findAll();
        if(orders != null && orders.size() > 0) {
            System.out.println("findAllOrders ::  order data : \n" + orders);
            return orders;
        }
        System.err.println("findAllOrders :: NO orders data found");
        return List.of();
    }

    @Tool("Fetch Orders data by customer_id")
    public List<Order> findOrdersByCustomerId(Integer customer_id) {
        System.out.println("Tool (findOrdersByCustomerId) by customer_id : " + customer_id);
        List<Order> orders = orderService.findOrdersByCustomerId(customer_id);
        if(orders != null && orders.size() > 0) {
            System.out.println("findOrdersByCustomerId :: order data : \n" + orders);
            return orders;
        }
        System.err.println("findOrdersByCustomerId :: NO orders data found");
        return List.of();
    }

    @Tool("Fetch Orders data by order_id")
    public List<Order> findOrdersByOrderId(Integer order_id) {
        System.out.println("Tool (findOrdersByOrderId) by order_id : " + order_id);
        List<Order> orders = orderService.findOrdersByOrderId(order_id);
        if(orders != null && orders.size() > 0) {
            System.out.println("findOrdersByOrderId :: order data : \n" + orders);
            return orders;
        }
        System.err.println("findOrdersByOrderId :: NO orders data found");
        return List.of();
    }

}
