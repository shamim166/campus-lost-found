package com.lostfound.decorator;

/**
 * ============================================================
 * DESIGN PATTERN #3 — DECORATOR
 * ============================================================
 * ItemComponent is the base interface for the Decorator pattern.
 * It defines the operations that can be dynamically enhanced:
 *   - getDisplayTitle()   → title with tags
 *   - getTags()           → list of active tags
 *   - isUrgent()
 *   - hasReward()
 *   - isVerified()
 * ============================================================
 */
public interface ItemComponent {
    String getDisplayTitle();
    String getTags();
    boolean isUrgent();
    boolean hasReward();
    boolean isVerified();
}
