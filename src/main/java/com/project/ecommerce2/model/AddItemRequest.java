package com.project.ecommerce2.model;

import java.util.UUID;

public class AddItemRequest {
    private Long itemId;
    private String customerId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public UUID getCustomerId() {
        return UUID.fromString(customerId);
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}