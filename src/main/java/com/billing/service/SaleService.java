package com.billing.service;

import com.billing.model.AppUser;
import com.billing.model.Item;
import com.billing.model.Sale;
import com.billing.repository.ItemRepository;
import com.billing.repository.SaleRepository;
import com.billing.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

    private final ItemRepository items;
    private final SaleRepository sales;
    private final UserRepository users;

    public SaleService(ItemRepository items, SaleRepository sales, UserRepository users) {
        this.items = items;
        this.sales = sales;
        this.users = users;
    }

    /**
     * Sells {@code quantity} units: locks the item row, checks stock, reduces it and records the sale
     * in ONE transaction. If anything fails (BusinessException is a RuntimeException) everything rolls back.
     */
    @Transactional
    public Sale recordSale(Long itemId, int quantity, String customer, String userEmail) {
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be at least 1.");
        }
        Item item = items.findByIdForUpdate(itemId)
                .orElseThrow(() -> new BusinessException("That item no longer exists."));
        if (quantity > item.getQuantity()) {
            throw new BusinessException("Not enough stock for " + item.getName()
                    + ". Available: " + item.getQuantity() + ".");
        }

        item.setQuantity(item.getQuantity() - quantity);
        items.save(item);

        Sale sale = new Sale();
        sale.setItemId(item.getId());
        sale.setItemCode(item.getItemCode());
        sale.setItemName(item.getName());
        sale.setCategory(item.getCategoryLabel());
        sale.setQuantity(quantity);
        sale.setUnitPrice(item.getUnitPrice());
        sale.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        sale.setCustomerName(customer == null || customer.isBlank() ? null : customer.trim());
        sale.setSoldBy(users.findByEmail(userEmail).map(AppUser::getId).orElse(null));
        return sales.save(sale);
    }

    public List<Sale> recentSales() {
        return sales.findTop10ByOrderBySoldAtDescIdDesc();
    }
}
