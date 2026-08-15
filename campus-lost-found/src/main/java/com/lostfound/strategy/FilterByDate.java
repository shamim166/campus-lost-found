package com.lostfound.strategy;

import com.lostfound.model.Item;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy Pattern: FilterByDate
 * Filters items whose itemDate equals the given date string (yyyy-MM-dd).
 */
public class FilterByDate implements FilterStrategy {

    @Override
    public List<Item> filter(List<Item> items, String value) {
        if (value == null || value.isBlank()) return items;
        try {
            LocalDate target = LocalDate.parse(value);
            return items.stream()
                    .filter(i -> i.getItemDate() != null
                            && i.getItemDate().equals(target))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return items; // Invalid date format — return all
        }
    }

    @Override
    public String getStrategyName() { return "Filter by Date"; }
}
