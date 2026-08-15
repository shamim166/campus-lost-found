package com.lostfound.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: LostFoundSubject (the Subject/Publisher)
 * Maintains a list of observers and notifies them when a match is found.
 *
 * Spring @Component ensures one instance in the application context.
 */
@Component
public class LostFoundSubject {

    private final List<NotifySubscriber> observers = new ArrayList<>();

    // --------------------------------------------------------
    // Subscribe a new observer
    // --------------------------------------------------------
    public void subscribe(NotifySubscriber observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // --------------------------------------------------------
    // Unsubscribe an observer
    // --------------------------------------------------------
    public void unsubscribe(NotifySubscriber observer) {
        observers.remove(observer);
    }

    // --------------------------------------------------------
    // Notify all subscribed observers of a potential match
    // --------------------------------------------------------
    public void notifyObservers(Long lostItemId, Long foundItemId, int score) {
        for (NotifySubscriber observer : observers) {
            observer.onMatchFound(lostItemId, foundItemId, score);
        }
    }

    public int getObserverCount() {
        return observers.size();
    }

}
