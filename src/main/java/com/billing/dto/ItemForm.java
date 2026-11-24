package com.billing.dto;

import com.billing.model.Item;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** The fields of the Add Item / Update Item forms, with their validation rules. */
public class ItemForm {

    @NotBlank(message = "Item ID / code is required.")
    @Size(max = 30, message = "Item ID / code can be at most 30 characters.")
    private String itemCode;

    @NotBlank(message = "Item name is required.")
    @Size(max = 120, message = "Item name can be at most 120 characters.")
    private String name;

    @Size(max = 60, message = "Category can be at most 60 characters.")
    private String category;

    @NotNull(message = "Quantity is required.")
    @Min(value = 0, message = "Quantity cannot be negative.")
    private Integer quantity;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.00", message = "Price cannot be negative.")
    @Digits(integer = 8, fraction = 2, message = "Price can have at most 2 decimal places.")
    private BigDecimal unitPrice;

    @Size(max = 255, message = "Description can be at most 255 characters.")
    private String description;

    public static ItemForm fromItem(Item item) {
        ItemForm f = new ItemForm();
        f.itemCode = item.getItemCode();
        f.name = item.getName();
        f.category = item.getCategory();
        f.quantity = item.getQuantity();
        f.unitPrice = item.getUnitPrice();
        f.description = item.getDescription();
        return f;
    }

    /** Copies the (already validated) form values into an item. */
    public void applyTo(Item item) {
        item.setItemCode(itemCode.trim());
        item.setName(name.trim());
        item.setCategory(category == null || category.isBlank() ? null : category.trim());
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice.setScale(2, RoundingMode.HALF_UP));
        item.setDescription(description == null || description.isBlank() ? null : description.trim());
    }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
