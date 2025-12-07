package com.sunilkumarl.springai.model;

/*{
        "order_id": 1001,
        "order_datetime": "2025-11-01T10:15:00",
        "customer_id": 1,
        "order_total": 125.99,
        "order_lines": [
        {
        "order_id": 1001,
        "order_line_id": 1,
        "order_line_seq_id": 1,
        "product_id": 1,
        "product_price": 25.99,
        "product_qty": 2
        },
        {
        "order_id": 1001,
        "order_line_id": 2,
        "order_line_seq_id": 2,
        "product_id": 2,
        "product_price": 75.5,
        "product_qty": 1
        }
        ],
        "id": "a726"
        }
        */
public record OrderLine(Integer order_id, Integer order_line_id, Integer order_line_seq_id, Integer product_id, Double product_price, Integer product_qty) {
}
