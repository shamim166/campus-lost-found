package com.lostfound.strategy;

import com.lostfound.model.Item;
import java.util.List;

/**
 * ============================================================
 * DESIGN PATTERN #4 — STRATEGY
 * ============================================================
 * FilterStrategy defines the contract for all filtering algorithms.
 * At runtime, the user's search form picks a different strategy.
 *
 * Flow:
 *   User selects filter type → BrowseController → FilterStrategy
 *   → FilterByCategory / FilterByLocation / FilterByDate
 * ============================================================
 */
public interface FilterStrategy {
    /**
     * Apply this strategy to filter a list of items.
     * @param items     the full list to filter
     * @param value     the search/filter value (keyword, location, date string)
     * @return filtered list
     */
    List<Item> filter(List<Item> items, String value);

    /** Human-readable strategy name (for UI display) */
    String getStrategyName();
}
