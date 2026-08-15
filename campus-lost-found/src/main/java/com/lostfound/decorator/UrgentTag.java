package com.lostfound.decorator;

/**
 * Decorator Pattern: UrgentTag adds URGENT badge to an item.
 * Usage: new UrgentTag(new BaseItem(item))
 */
public class UrgentTag extends ItemDecorator {

    public UrgentTag(ItemComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDisplayTitle() {
        return "[URGENT] " + wrapped.getDisplayTitle();
    }

    @Override
    public String getTags() {
        return wrapped.getTags() + " URGENT";
    }

    @Override
    public boolean isUrgent() { return true; }

}
