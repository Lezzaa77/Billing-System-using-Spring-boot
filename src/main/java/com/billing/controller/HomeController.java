package com.billing.controller;

import com.billing.dto.CategoryGroup;
import com.billing.service.ItemService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Home page: totals plus every item listed under its category. */
@Controller
public class HomeController {

    private final ItemService itemService;

    public HomeController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<CategoryGroup> groups = itemService.groupedByCategory();
        int totalItems = 0;
        int totalQty = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        for (CategoryGroup g : groups) {
            totalItems += g.getItems().size();
            totalQty += g.getTotalQuantity();
            totalValue = totalValue.add(g.getTotalValue());
        }
        model.addAttribute("groups", groups);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("totalQty", totalQty);
        model.addAttribute("totalValue", totalValue);
        return "home";
    }
}
