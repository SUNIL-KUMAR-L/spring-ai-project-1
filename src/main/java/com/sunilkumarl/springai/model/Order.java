package com.sunilkumarl.springai.model;

/*/
        "order_id": 1001,
        "order_datetime": "2025-11-01T10:15:00",
        "customer_id": 1,
        "order_total": 125.99,
 */

import java.util.List;

public record Order(Integer order_id, Integer customer_id, Double order_total, List<OrderLine> order_lines) {
}
