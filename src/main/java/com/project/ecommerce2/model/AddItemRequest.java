package com.project.ecommerce2.model;

import java.util.List;
import java.util.UUID;

public class AddItemRequest {
    private List<Long> itemIds;
    private String customerId;

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public UUID getCustomerId() {
        return customerId != null ? UUID.fromString(customerId) : null;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}