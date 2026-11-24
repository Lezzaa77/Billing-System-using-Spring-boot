package com.billing.service;

import com.billing.dto.CategoryGroup;
import com.billing.dto.ItemForm;
import com.billing.model.Item;
import com.billing.repository.ItemRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository items;

    public ItemService(ItemRepository items) {
        this.items = items;
    }

    /** Every item, grouped by category (categories and items sorted A-Z). Used by the home page. */
    public List<CategoryGroup> groupedByCategory() {
        List<Item> all = new ArrayList<>(items.findAll(Sort.by("name")));
        all.sort(Comparator.comparing((Item i) -> i.getCategoryLabel().toLowerCase())
                .thenComparing(i -> i.getName().toLowerCase()));
        Map<String, CategoryGroup> groups = new LinkedHashMap<>();
        for (Item it : all) {
            groups.computeIfAbsent(it.getCategoryLabel(), CategoryGroup::new).add(it);
        }
        return List.copyOf(groups.values());
    }

    public List<Item> inStock() {
        return items.findByQuantityGreaterThanOrderByNameAsc(0);
    }

    public List<String> categoryNames() {
        return items.findCategoryNames();
    }

    public Optional<Item> find(Long id) {
        return items.findById(id);
    }

    /** True if another item (not {@code excludeId}) already uses this code. Pass null when adding. */
    public boolean codeInUse(String code, Long excludeId) {
        String c = code == null ? "" : code.trim();
        return excludeId == null ? items.existsByItemCode(c) : items.existsByItemCodeAndIdNot(c, excludeId);
    }

    @Transactional
    public Item create(ItemForm form) {
        Item item = new Item();
        form.applyTo(item);
        return items.save(item);
    }

    @Transactional
    public void update(Long id, ItemForm form) {
        Item item = items.findById(id).orElseThrow(() -> new BusinessException("That item no longer exists."));
        form.applyTo(item);
        items.save(item);
    }

    @Transactional
    public void delete(Long id) {
        if (items.existsById(id)) {
            items.deleteById(id);
        }
    }
}
