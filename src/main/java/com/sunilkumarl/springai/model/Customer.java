package com.sunilkumarl.springai.model;


/*
"customer_id": 1,
"customer_name": "Alice Johnson",
"email": "alice.johnson@example.com",
"phone_number": "9876543210"
 */
public record Customer(Integer customer_id, String customer_name, String email, String phone_number) {
}
