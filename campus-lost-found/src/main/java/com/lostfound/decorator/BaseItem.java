package com.lostfound.decorator;

import com.lostfound.model.Item;

/**
 * Decorator Pattern: BaseItem wraps a real Item entity
 * and implements ItemComponent as the concrete base.
 */
public class BaseItem implements ItemComponent {

    private final Item item;

    public BaseItem(Item item) {
        this.item = item;
    }

    @Override
    public String getDisplayTitle() {
        return item.getTitle();
    }

    @Override
    public String getTags() {
        return "";
    }

    @Override
    public boolean isUrgent()   { return false; }

    @Override
    public boolean hasReward()  { return false; }

    @Override
    public boolean isVerified() { return false; }

}
