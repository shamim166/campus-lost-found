package com.lostfound.singleton;

import com.lostfound.model.Item;
import com.lostfound.model.ItemStatus;
import com.lostfound.model.ItemType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================
 * DESIGN PATTERN #1 — SINGLETON
 * ============================================================
 * LostFoundRegistry maintains a central in-memory registry of
 * recently active Lost and Found items. The Spring @Component
 * annotation ensures there is exactly ONE instance in the
 * application context (Spring's default scope is singleton).
 *
 * Flow:
 *   Controller → Service → LostFoundRegistry → Active Items
 * ============================================================
 */
@Component
public class LostFoundRegistry {

    // Central in-memory store: itemId → Item
    private final Map<Long, Item> activeLostItems  = new ConcurrentHashMap<>();
    private final Map<Long, Item> activeFoundItems = new ConcurrentHashMap<>();

    // --------------------------------------------------------
    // Register a new item into the registry
    // --------------------------------------------------------
    public void registerItem(Item item) {
        if (item == null || item.getId() == null) return;
        if (item.getStatus() != ItemStatus.ACTIVE) return;

        if (item.getType() == ItemType.LOST) {
            activeLostItems.put(item.getId(), item);
        } else if (item.getType() == ItemType.FOUND) {
            activeFoundItems.put(item.getId(), item);
        }
    }

    // --------------------------------------------------------
    // Remove item from registry (closed / claimed)
    // --------------------------------------------------------
    public void deregisterItem(Item item) {
        if (item == null || item.getId() == null) return;
        activeLostItems.remove(item.getId());
        activeFoundItems.remove(item.getId());
    }

    // --------------------------------------------------------
    // Retrieve active LOST items from registry
    // --------------------------------------------------------
    public List<Item> getActiveLostItems() {
        return new ArrayList<>(activeLostItems.values());
    }

    // --------------------------------------------------------
    // Retrieve active FOUND items from registry
    // --------------------------------------------------------
    public List<Item> getActiveFoundItems() {
        return new ArrayList<>(activeFoundItems.values());
    }

    // --------------------------------------------------------
    // Registry statistics
    // --------------------------------------------------------
    public int getLostCount()  { return activeLostItems.size();  }
    public int getFoundCount() { return activeFoundItems.size(); }
    public int getTotalCount() { return getLostCount() + getFoundCount(); }

    // --------------------------------------------------------
    // Reload registry from DB (called on startup or refresh)
    // --------------------------------------------------------
    public void loadFromDatabase(List<Item> items) {
        activeLostItems.clear();
        activeFoundItems.clear();
        items.forEach(this::registerItem);
    }

}
