package com.billing.dto;

import com.billing.model.Item;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** The items of one category plus their totals (used on the home page). */
public class CategoryGroup {

    private final String name;
    private final List<Item> items = new ArrayList<>();
    private int totalQuantity;
    private BigDecimal totalValue = BigDecimal.ZERO;

    public CategoryGroup(String name) {
        this.name = name;
    }

    public void add(Item item) {
        items.add(item);
        totalQuantity += item.getQuantity();
        totalValue = totalValue.add(item.getStockValue());
    }

    public String getName() { return name; }
    public List<Item> getItems() { return items; }
    public int getTotalQuantity() { return totalQuantity; }
    public BigDecimal getTotalValue() { return totalValue; }
}
