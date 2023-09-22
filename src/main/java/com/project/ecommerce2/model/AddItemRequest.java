package com.project.ecommerce2.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddItemRequest {
    private Map<Long, Integer> itemQuantities = new HashMap<>();
    private String customerId;

    public Map<Long, Integer> getItemQuantities() {
        return itemQuantities;
    }

    public void setItemQuantities(Map<Long, Integer> itemQuantities) {
        this.itemQuantities = itemQuantities;
    }

    public UUID getCustomerId() {
        return customerId != null ? UUID.fromString(customerId) : null;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<Long> getItemIds() {
        return null;
    }
}