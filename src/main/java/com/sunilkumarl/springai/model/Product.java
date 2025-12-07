package com.sunilkumarl.springai.model;
/*
"product_id": 1,
"product_desc": "Wireless Mouse",
"product_image_url": "http://example.com/mouse.jpg",
"product_category": "Electronics"
*/
public record Product(Integer product_id, String product_desc, String product_image_url, String product_category) {
}
