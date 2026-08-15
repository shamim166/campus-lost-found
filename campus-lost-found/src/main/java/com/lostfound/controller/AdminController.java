package com.lostfound.controller;

import com.lostfound.model.ItemStatus;
import com.lostfound.model.ItemType;
import com.lostfound.service.ItemService;
import com.lostfound.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ItemService itemService;
    private final UserService userService;

    public AdminController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers",   userService.countUsers());
        model.addAttribute("totalLost",    itemService.countByType(ItemType.LOST));
        model.addAttribute("totalFound",   itemService.countByType(ItemType.FOUND));
        model.addAttribute("totalClaimed", itemService.countByStatus(ItemStatus.CLAIMED));
        model.addAttribute("totalActive",  itemService.countByStatus(ItemStatus.ACTIVE));
        model.addAttribute("recentItems",  itemService.findAllItems().stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(10).toList());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id);
        ra.addFlashAttribute("successMsg", "User deleted.");
        return "redirect:/admin/users";
    }

    @GetMapping("/items")
    public String manageItems(Model model) {
        model.addAttribute("items", itemService.findAllItems());
        return "admin/items";
    }

    @PostMapping("/items/{id}/delete")
    public String adminDeleteItem(@PathVariable Long id, RedirectAttributes ra) {
        itemService.adminDeleteItem(id);
        ra.addFlashAttribute("successMsg", "Item deleted.");
        return "redirect:/admin/items";
    }

    @PostMapping("/items/{id}/verify")
    public String verifyItem(@PathVariable Long id, RedirectAttributes ra) {
        itemService.verifyItem(id);
        ra.addFlashAttribute("successMsg", "Item verified.");
        return "redirect:/admin/items";
    }

}
