package com.lostfound.decorator;

/**
 * Decorator Pattern: RewardTag adds REWARD badge to an item.
 * Usage: new RewardTag(new BaseItem(item))
 */
public class RewardTag extends ItemDecorator {

    public RewardTag(ItemComponent wrapped) {
        super(wrapped);
    }

    @Override
    public String getDisplayTitle() {
        return wrapped.getDisplayTitle() + " [REWARD]";
    }

    @Override
    public String getTags() {
        return wrapped.getTags() + " REWARD";
    }

    @Override
    public boolean hasReward() { return true; }

}
