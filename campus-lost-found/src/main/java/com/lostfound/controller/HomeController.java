package com.lostfound.controller;

import com.lostfound.model.Item;
import com.lostfound.model.ItemStatus;
import com.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final ItemRepository itemRepository;

    public HomeController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       @RequestParam(required = false) String logout,
                       @RequestParam(required = false) String registered) {

        // Statistics for hero section
        long totalLost    = itemRepository.countByType(com.lostfound.model.ItemType.LOST);
        long totalFound   = itemRepository.countByType(com.lostfound.model.ItemType.FOUND);
        long totalClaimed = itemRepository.countByStatus(ItemStatus.CLAIMED);
        long totalActive  = itemRepository.countByStatus(ItemStatus.ACTIVE);

        model.addAttribute("totalLost",    totalLost);
        model.addAttribute("totalFound",   totalFound);
        model.addAttribute("totalClaimed", totalClaimed);
        model.addAttribute("totalActive",  totalActive);

        // Recent 8 active items for the homepage feed
        List<Item> recentItems = itemRepository.findTop8ByStatusOrderByCreatedAtDesc(ItemStatus.ACTIVE);
        model.addAttribute("recentItems", recentItems);

        if (logout != null)     model.addAttribute("logoutMsg",    true);
        if (registered != null) model.addAttribute("registeredMsg", true);

        return "home";
    }

}
