package com.lostfound.strategy;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern: FilterByCategory
 * Filters items whose category matches the given value.
 */
public class FilterByCategory implements FilterStrategy {

    @Override
    public List<Item> filter(List<Item> items, String value) {
        if (value == null || value.isBlank()) return items;
        try {
            Category target = Category.valueOf(value.toUpperCase());
            return items.stream()
                    .filter(i -> i.getCategory() == target)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return items; // Unknown category — return all
        }
    }

    @Override
    public String getStrategyName() { return "Filter by Category"; }
}
