package com.lostfound.decorator;

/**
 * Decorator Pattern: VerifiedTag adds VERIFIED badge to an item.
 * Usage: new VerifiedTag(new BaseItem(item))
 */
public class VerifiedTag extends ItemDecorator {

    public VerifiedTag(ItemComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDisplayTitle() {
        return wrapped.getDisplayTitle() + " ✓";
    }

    @Override
    public String getTags() {
        return wrapped.getTags() + " VERIFIED";
    }

    @Override
    public boolean isVerified() { return true; }

}
