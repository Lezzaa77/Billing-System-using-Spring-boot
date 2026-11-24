package com.billing.controller;

import com.billing.dto.ItemForm;
import com.billing.model.Item;
import com.billing.service.ItemService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Add, update and delete item pages (each is a separate page). */
@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // trim spaces; an empty text box becomes null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /** Existing categories, offered as suggestions in the Category box. */
    @ModelAttribute("categories")
    public List<String> categories() {
        return itemService.categoryNames();
    }

    // ---------- Add ----------

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("form", new ItemForm());
        return "add-item";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("form") ItemForm form, BindingResult result, RedirectAttributes ra) {
        if (!result.hasFieldErrors("itemCode") && itemService.codeInUse(form.getItemCode(), null)) {
            result.rejectValue("itemCode", "duplicate", "An item with this ID already exists.");
        }
        if (result.hasErrors()) {
            return "add-item";
        }
        itemService.create(form);
        ra.addFlashAttribute("message", "Item added.");
        return "redirect:/home";
    }

    // ---------- Update ----------

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<Item> item = itemService.find(id);
        if (item.isEmpty()) {
            ra.addFlashAttribute("error", "That item no longer exists.");
            return "redirect:/home";
        }
        model.addAttribute("form", ItemForm.fromItem(item.get()));
        model.addAttribute("itemId", id);
        return "update-item";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") Long id, @Valid @ModelAttribute("form") ItemForm form,
                       BindingResult result, Model model, RedirectAttributes ra) {
        if (itemService.find(id).isEmpty()) {
            ra.addFlashAttribute("error", "That item no longer exists.");
            return "redirect:/home";
        }
        if (!result.hasFieldErrors("itemCode") && itemService.codeInUse(form.getItemCode(), id)) {
            result.rejectValue("itemCode", "duplicate", "Another item already uses this ID.");
        }
        if (result.hasErrors()) {
            model.addAttribute("itemId", id);
            return "update-item";
        }
        itemService.update(id, form);
        ra.addFlashAttribute("message", "Item updated.");
        return "redirect:/home";
    }

    // ---------- Delete (confirmation page, then POST) ----------

    @GetMapping("/{id}/delete")
    public String deleteConfirm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<Item> item = itemService.find(id);
        if (item.isEmpty()) {
            ra.addFlashAttribute("error", "That item no longer exists.");
            return "redirect:/home";
        }
        model.addAttribute("item", item.get());
        return "delete-item";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        itemService.delete(id);
        ra.addFlashAttribute("message", "Item deleted.");
        return "redirect:/home";
    }
}
