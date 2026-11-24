package com.billing.config;

import com.billing.model.Item;
import com.billing.model.Sale;
import com.billing.repository.ItemRepository;
import com.billing.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Adds a few sample items and sales the first time the app starts on an EMPTY items table.
 * Turn off with  billing.seed-sample-data=false  in application.properties.
 */
@Component
@ConditionalOnProperty(name = "billing.seed-sample-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final ItemRepository items;
    private final SaleRepository sales;

    public DataSeeder(ItemRepository items, SaleRepository sales) {
        this.items = items;
        this.sales = sales;
    }

    @Override
    public void run(String... args) {
        if (items.count() > 0) {
            return;
        }
        Item notebook = item("ITM001", "Notebook", "Stationery", 45, "2.50", "A4 ruled notebook");
        Item pen = item("ITM002", "Ball Pen", "Stationery", 180, "0.50", "Blue ink pen");
        item("ITM003", "USB Cable", "Electronics", 30, "4.99", "1m USB-C cable");
        Item phones = item("ITM004", "Headphones", "Electronics", 13, "19.99", "Wired headphones");

        sale(notebook, 5, LocalDateTime.now().minusDays(2));
        sale(pen, 20, LocalDateTime.now().minusDays(1));
        sale(phones, 2, LocalDateTime.now());
    }

    private Item item(String code, String name, String category, int qty, String price, String description) {
        Item it = new Item();
        it.setItemCode(code);
        it.setName(name);
        it.setCategory(category);
        it.setQuantity(qty);
        it.setUnitPrice(new BigDecimal(price));
        it.setDescription(description);
        return items.save(it);
    }

    /** The stock numbers above already have these sales taken off, so stock is not reduced again. */
    private void sale(Item item, int qty, LocalDateTime when) {
        Sale s = new Sale();
        s.setItemId(item.getId());
        s.setItemCode(item.getItemCode());
        s.setItemName(item.getName());
        s.setCategory(item.getCategoryLabel());
        s.setQuantity(qty);
        s.setUnitPrice(item.getUnitPrice());
        s.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(qty)));
        s.setCustomerName("Sample customer");
        s.setSoldAt(when);
        sales.save(s);
    }
}
