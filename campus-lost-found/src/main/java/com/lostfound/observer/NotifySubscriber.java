package com.lostfound.observer;

/**
 * ============================================================
 * DESIGN PATTERN #5 — OBSERVER
 * ============================================================
 * NotifySubscriber is the Observer interface.
 * Any class that wants to receive match notifications
 * must implement this interface.
 *
 * Flow:
 *   New Found Item → MatchingService → LostFoundSubject.notifyObservers()
 *   → ReportingUser.onMatchFound() → NotificationService → DB
 * ============================================================
 */
public interface NotifySubscriber {
    /**
     * Called when a potential match is detected.
     * @param lostItemId   ID of the lost item
     * @param foundItemId  ID of the found item that may match
     * @param score        match confidence score (0–100)
     */
    void onMatchFound(Long lostItemId, Long foundItemId, int score);
}
