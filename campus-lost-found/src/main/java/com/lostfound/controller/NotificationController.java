package com.lostfound.controller;

import com.lostfound.model.User;
import com.lostfound.service.NotificationService;
import com.lostfound.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService         userService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService         = userService;
    }

    @GetMapping
    public String notifications(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByEmail(principal.getUsername());
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
        model.addAttribute("unreadCount",   notificationService.countUnread(user));
        return "notifications";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllRead(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.findByEmail(principal.getUsername());
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.deleteNotification(id);
        ra.addFlashAttribute("successMsg", "Notification deleted.");
        return "redirect:/notifications";
    }

    // API endpoint for unread count badge (used by JS polling)
    @GetMapping("/api/unread-count")
    @ResponseBody
    public String unreadCount(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return "{\"count\": 0}";
        User user = userService.findByEmail(principal.getUsername());
        return "{\"count\": " + notificationService.countUnread(user) + "}";
    }

}
