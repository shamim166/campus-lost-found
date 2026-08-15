package com.lostfound.controller;

import com.lostfound.model.*;
import com.lostfound.service.ItemService;
import com.lostfound.service.NotificationService;
import com.lostfound.service.UserService;
import com.lostfound.singleton.LostFoundRegistry;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    private final ItemService         itemService;
    private final UserService         userService;
    private final NotificationService notificationService;
    private final LostFoundRegistry   registry;

    public DashboardController(ItemService itemService, UserService userService,
                               NotificationService notificationService,
                               LostFoundRegistry registry) {
        this.itemService         = itemService;
        this.userService         = userService;
        this.notificationService = notificationService;
        this.registry            = registry;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByEmail(principal.getUsername());

        // Greeting time
        int hour = LocalDateTime.now().getHour();
        String greeting = hour < 12 ? "Good Morning" : hour < 18 ? "Good Afternoon" : "Good Evening";

        // Global stats
        long totalLost     = itemService.countByType(ItemType.LOST);
        long totalFound    = itemService.countByType(ItemType.FOUND);
        long totalRecovered= itemService.countByStatus(ItemStatus.CLAIMED);
        long unread     = notificationService.countUnread(user);

        // Recent Global Items for the feed
        List<Item> recentItems = itemService.getRecentItems();

        // Singleton registry stats
        int registryTotal = registry.getTotalCount();

        model.addAttribute("user",          user);
        model.addAttribute("greeting",      greeting);
        model.addAttribute("totalLost",     totalLost);
        model.addAttribute("totalFound",    totalFound);
        model.addAttribute("totalRecovered",totalRecovered);
        model.addAttribute("unreadCount",   unread);
        model.addAttribute("recentItems",   recentItems);
        model.addAttribute("registryCount", registryTotal);

        return "dashboard";
    }

}
