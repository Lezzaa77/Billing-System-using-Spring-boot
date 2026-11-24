package com.billing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** The "Sell Item" form. */
public class SaleForm {

    @NotNull(message = "Choose an item.")
    private Long itemId;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity = 1;

    @Size(max = 100, message = "Customer name can be at most 100 characters.")
    private String customer;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
}
