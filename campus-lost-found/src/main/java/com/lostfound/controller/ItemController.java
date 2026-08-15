package com.lostfound.controller;

import com.lostfound.model.*;
import com.lostfound.service.ItemService;
import com.lostfound.service.MatchingService;
import com.lostfound.service.UserService;
import com.lostfound.strategy.*;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class ItemController {

    private final ItemService     itemService;
    private final UserService     userService;
    private final MatchingService matchingService;

    public ItemController(ItemService itemService, UserService userService,
                          MatchingService matchingService) {
        this.itemService     = itemService;
        this.userService     = userService;
        this.matchingService = matchingService;
    }

    // ── Browse ────────────────────────────────────────────────
    @GetMapping("/browse")
    public String browse(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        ItemType   typeEnum     = parseEnum(ItemType.class,   type);
        Category   categoryEnum = parseEnum(Category.class,   category);
        LocalDate  from         = parseDate(dateFrom);
        LocalDate  to           = parseDate(dateTo);

        Page<Item> items = itemService.browseItems(keyword, typeEnum, categoryEnum, location, from, to, page);

        model.addAttribute("items",      items);
        model.addAttribute("keyword",    keyword);
        model.addAttribute("type",       type);
        model.addAttribute("category",   category);
        model.addAttribute("location",   location);
        model.addAttribute("dateFrom",   dateFrom);
        model.addAttribute("dateTo",     dateTo);
        model.addAttribute("categories", Category.values());
        model.addAttribute("types",      ItemType.values());

        // Show active strategy name for display
        String strategyName = resolveStrategyName(category, location, dateFrom);
        model.addAttribute("strategyName", strategyName);

        return "browse";
    }

    // ── Item Detail ───────────────────────────────────────────
    @GetMapping("/items/{id}")
    public String itemDetail(@PathVariable Long id, Model model,
                             @AuthenticationPrincipal UserDetails principal) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isOwner", item.getUser().getId().equals(currentUser.getId()));
        }
        return "item-details";
    }

    // ── Report Lost GET ───────────────────────────────────────
    @GetMapping("/report-lost")
    public String reportLostPage(Model model) {
        model.addAttribute("categories", Category.values());
        return "report-lost";
    }

    // ── Report Lost POST ──────────────────────────────────────
    @PostMapping("/report-lost")
    public String reportLost(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String itemDate,
            @RequestParam(required = false) String itemTime,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(defaultValue = "false") boolean urgent,
            @RequestParam(required = false) BigDecimal rewardAmount,
            @RequestParam(required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            User user  = userService.findByEmail(principal.getUsername());
            Category cat = Category.valueOf(category.toUpperCase());

            Item saved = itemService.createItem(
                cat, title, description, location,
                parseDate(itemDate), parseTime(itemTime),
                ItemType.LOST, urgent, rewardAmount,
                contactPhone, contactEmail, imageFile, user
            );

            // === MATCHING: check for possible found matches ===
            matchingService.findMatchesForLostItem(saved);

            redirectAttributes.addFlashAttribute("successMsg",
                "Lost item reported successfully! We'll notify you if a match is found.");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
            return "redirect:/report-lost";
        }
    }

    // ── Report Found GET ──────────────────────────────────────
    @GetMapping("/report-found")
    public String reportFoundPage(Model model) {
        model.addAttribute("categories", Category.values());
        return "report-found";
    }

    // ── Report Found POST ─────────────────────────────────────
    @PostMapping("/report-found")
    public String reportFound(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String itemDate,
            @RequestParam(required = false) String itemTime,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(principal.getUsername());
            Category cat = Category.valueOf(category.toUpperCase());

            Item saved = itemService.createItem(
                cat, title, description, location,
                parseDate(itemDate), parseTime(itemTime),
                ItemType.FOUND, false, null,
                contactPhone, contactEmail, imageFile, user
            );

            // === MATCHING: check if any LOST items match this found item ===
            matchingService.findMatchesForFoundItem(saved);

            redirectAttributes.addFlashAttribute("successMsg",
                "Found item reported! We've notified relevant users about a potential match.");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Error: " + e.getMessage());
            return "redirect:/report-found";
        }
    }

    // ── Edit Item GET ─────────────────────────────────────────
    @GetMapping("/items/{id}/edit")
    public String editPage(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal UserDetails principal) {
        Item item = itemService.findById(id);
        User user = userService.findByEmail(principal.getUsername());
        if (!item.getUser().getId().equals(user.getId())) return "redirect:/my-reports";
        model.addAttribute("item",       item);
        model.addAttribute("categories", Category.values());
        return "edit-item";
    }

    // ── Edit Item POST ────────────────────────────────────────
    @PostMapping("/items/{id}/edit")
    public String editItem(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String itemDate,
            @RequestParam(required = false) String itemTime,
            @RequestParam(defaultValue = "false") boolean urgent,
            @RequestParam(required = false) BigDecimal rewardAmount,
            @RequestParam(required = false) String contactPhone,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) MultipartFile imageFile,
            @AuthenticationPrincipal UserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(principal.getUsername());
            itemService.updateItem(id, title, description, location,
                parseDate(itemDate), parseTime(itemTime),
                urgent, rewardAmount, contactPhone, contactEmail, imageFile, user);
            redirectAttributes.addFlashAttribute("successMsg", "Item updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/items/" + id;
    }

    // ── Delete ────────────────────────────────────────────────
    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails principal,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getUsername());
            itemService.deleteItem(id, user);
            redirectAttributes.addFlashAttribute("successMsg", "Item deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/my-reports";
    }

    // ── Claim ─────────────────────────────────────────────────
    @PostMapping("/items/{id}/claim")
    public String claimItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        itemService.claimItem(id);
        redirectAttributes.addFlashAttribute("successMsg", "Item marked as claimed!");
        return "redirect:/items/" + id;
    }

    // ── Close ─────────────────────────────────────────────────
    @PostMapping("/items/{id}/close")
    public String closeItem(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails principal,
                            RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getUsername());
        itemService.closeItem(id, user);
        redirectAttributes.addFlashAttribute("successMsg", "Report closed.");
        return "redirect:/my-reports";
    }

    // ── My Reports ────────────────────────────────────────────
    @GetMapping("/my-reports")
    public String myReports(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByEmail(principal.getUsername());
        List<Item> items = itemService.findByUser(user);
        model.addAttribute("items",    items);
        model.addAttribute("statuses", ItemStatus.values());
        return "my-reports";
    }

    // ── Helpers ───────────────────────────────────────────────
    private <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null || value.isBlank()) return null;
        try { return Enum.valueOf(type, value.toUpperCase()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); } catch (Exception e) { return null; }
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalTime.parse(s); } catch (Exception e) { return null; }
    }

    private String resolveStrategyName(String category, String location, String dateFrom) {
        if (category != null && !category.isBlank()) return new FilterByCategory().getStrategyName();
        if (location  != null && !location.isBlank())  return new FilterByLocation().getStrategyName();
        if (dateFrom  != null && !dateFrom.isBlank())   return new FilterByDate().getStrategyName();
        return "Browse All";
    }

}
