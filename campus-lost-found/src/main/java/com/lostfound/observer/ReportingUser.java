package com.lostfound.observer;

import com.lostfound.model.Item;
import com.lostfound.model.NotificationType;
import com.lostfound.repository.ItemRepository;
import com.lostfound.service.NotificationService;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern: ReportingUser (the Concrete Observer)
 * When a match is found, this observer:
 * 1. Looks up the lost item and its owner from the DB
 * 2. Creates a notification for the owner via NotificationService
 * 3. Stores the notification in PostgreSQL
 *
 * This is registered as a Spring @Component so it can be
 * auto-wired into the application startup configuration.
 */
@Component
public class ReportingUser implements NotifySubscriber {

    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    public ReportingUser(ItemRepository itemRepository,
                         NotificationService notificationService) {
        this.itemRepository       = itemRepository;
        this.notificationService  = notificationService;
    }

    @Override
    public void onMatchFound(Long lostItemId, Long foundItemId, int score) {
        // Fetch both items from DB
        itemRepository.findById(lostItemId).ifPresent(lostItem -> {
            itemRepository.findById(foundItemId).ifPresent(foundItem -> {

                // Build notification message
                String message = String.format(
                    "🔔 Possible Match Found! Your lost item \"%s\" may match a found report: \"%s\" (Score: %d/100). " +
                    "Location: %s. Check it out and claim if it's yours!",
                    lostItem.getTitle(),
                    foundItem.getTitle(),
                    score,
                    foundItem.getLocation() != null ? foundItem.getLocation() : "Unknown"
                );

                // Notify the LOST item's owner
                notificationService.createNotification(
                    lostItem.getUser(),
                    message,
                    NotificationType.MATCH,
                    foundItem
                );
            });
        });
    }

}
