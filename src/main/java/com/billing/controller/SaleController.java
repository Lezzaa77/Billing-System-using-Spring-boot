package com.billing.controller;

import com.billing.dto.SaleForm;
import com.billing.service.BusinessException;
import com.billing.service.ItemService;
import com.billing.service.SaleService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Records a sale. The sales report is built from these records. */
@Controller
public class SaleController {

    private final ItemService itemService;
    private final SaleService saleService;

    public SaleController(ItemService itemService, SaleService saleService) {
        this.itemService = itemService;
        this.saleService = saleService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/sell")
    public String sellForm(Model model) {
        model.addAttribute("form", new SaleForm());
        loadPageData(model);
        return "sell";
    }

    @PostMapping("/sell")
    public String sell(@Valid @ModelAttribute("form") SaleForm form, BindingResult result,
                       Principal principal, Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                saleService.recordSale(form.getItemId(), form.getQuantity(), form.getCustomer(), principal.getName());
                ra.addFlashAttribute("message", "Sale recorded and stock updated.");
                return "redirect:/sell";
            } catch (BusinessException e) {
                result.reject("sale.failed", e.getMessage());
            }
        }
        loadPageData(model);
        return "sell";
    }

    private void loadPageData(Model model) {
        model.addAttribute("items", itemService.inStock());
        model.addAttribute("recentSales", saleService.recentSales());
    }
}
