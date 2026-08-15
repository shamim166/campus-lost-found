package com.lostfound.service;

import com.lostfound.model.Category;
import com.lostfound.model.Item;
import com.lostfound.observer.LostFoundSubject;
import com.lostfound.observer.ReportingUser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MatchingService implements the scoring-based item matching algorithm.
 * Uses the Observer pattern to notify users when a match is found.
 *
 * Scoring:
 *   Category match  = +40
 *   Location match  = +30
 *   Keyword match   = +20
 *   Date proximity  = +10
 *   Threshold       = 40 (must at least match category)
 */
@Service
public class MatchingService {

    private final ItemService       itemService;
    private final LostFoundSubject  subject;
    private final ReportingUser     reportingUser;

    private static final int MATCH_THRESHOLD = 40;

    public MatchingService(ItemService itemService,
                           LostFoundSubject subject,
                           ReportingUser reportingUser) {
        this.itemService   = itemService;
        this.subject       = subject;
        this.reportingUser = reportingUser;
    }

    // Register the ReportingUser observer on startup
    @PostConstruct
    public void init() {
        subject.subscribe(reportingUser);
    }

    // =========================================================
    // Called when a new LOST item is reported
    // Finds matching FOUND items and notifies via Observer
    // =========================================================
    public void findMatchesForLostItem(Item lostItem) {
        List<Item> foundCandidates = itemService.findPotentialFoundMatches(
            lostItem.getCategory(), lostItem.getLocation()
        );

        for (Item foundItem : foundCandidates) {
            int score = computeScore(lostItem, foundItem);
            if (score >= MATCH_THRESHOLD) {
                // === OBSERVER: notify all subscribers ===
                subject.notifyObservers(lostItem.getId(), foundItem.getId(), score);
            }
        }
    }

    // =========================================================
    // Called when a new FOUND item is reported
    // Finds matching LOST items and notifies via Observer
    // =========================================================
    public void findMatchesForFoundItem(Item foundItem) {
        List<Item> lostCandidates = itemService.findPotentialLostMatches(
            foundItem.getCategory(), foundItem.getLocation()
        );

        for (Item lostItem : lostCandidates) {
            int score = computeScore(lostItem, foundItem);
            if (score >= MATCH_THRESHOLD) {
                // === OBSERVER: notify all subscribers ===
                subject.notifyObservers(lostItem.getId(), foundItem.getId(), score);
            }
        }
    }

    // =========================================================
    // Matching score algorithm (max 100)
    // =========================================================
    public int computeScore(Item lostItem, Item foundItem) {
        int score = 0;

        // +40: Same category
        if (lostItem.getCategory() == foundItem.getCategory()) {
            score += 40;
        }

        // +30: Location overlap
        if (locationsMatch(lostItem.getLocation(), foundItem.getLocation())) {
            score += 30;
        }

        // +20: Keyword match in title
        if (titlesMatch(lostItem.getTitle(), foundItem.getTitle())) {
            score += 20;
        }

        // +10: Date proximity (within 7 days)
        if (datesProximate(lostItem, foundItem)) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    private boolean locationsMatch(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) return false;
        String l1 = loc1.toLowerCase().trim();
        String l2 = loc2.toLowerCase().trim();
        // Check if any word in l1 appears in l2
        for (String word : l1.split("\\s+")) {
            if (word.length() > 2 && l2.contains(word)) return true;
        }
        return false;
    }

    private boolean titlesMatch(String t1, String t2) {
        if (t1 == null || t2 == null) return false;
        String low1 = t1.toLowerCase();
        String low2 = t2.toLowerCase();
        for (String word : low1.split("\\s+")) {
            if (word.length() > 3 && low2.contains(word)) return true;
        }
        return false;
    }

    private boolean datesProximate(Item lost, Item found) {
        if (lost.getItemDate() == null || found.getItemDate() == null) return false;
        long diff = Math.abs(lost.getItemDate().toEpochDay() - found.getItemDate().toEpochDay());
        return diff <= 7;
    }

}
