package com.lostfound.strategy;

import com.lostfound.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern: FilterByLocation
 * Filters items whose location contains the search keyword (case-insensitive).
 */
public class FilterByLocation implements FilterStrategy {

    @Override
    public List<Item> filter(List<Item> items, String value) {
        if (value == null || value.isBlank()) return items;
        String keyword = value.toLowerCase().trim();
        return items.stream()
                .filter(i -> i.getLocation() != null
                        && i.getLocation().toLowerCase().contains(keyword))
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() { return "Filter by Location"; }
}
