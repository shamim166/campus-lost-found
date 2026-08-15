package com.lostfound.decorator;

/**
 * Decorator Pattern: Abstract ItemDecorator wraps any ItemComponent
 * and delegates calls to it. Concrete decorators extend this.
 */
public abstract class ItemDecorator implements ItemComponent {

    protected final ItemComponent wrapped;

    public ItemDecorator(ItemComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDisplayTitle() { return wrapped.getDisplayTitle(); }

    @Override
    public String getTags() { return wrapped.getTags(); }

    @Override
    public boolean isUrgent()   { return wrapped.isUrgent();   }

    @Override
    public boolean hasReward()  { return wrapped.hasReward();  }

    @Override
    public boolean isVerified() { return wrapped.isVerified(); }

}
