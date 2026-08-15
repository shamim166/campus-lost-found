package com.lostfound.service;

import com.lostfound.model.Notification;
import com.lostfound.model.NotificationType;
import com.lostfound.model.Item;
import com.lostfound.model.User;
import com.lostfound.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User user, String message, NotificationType type, Item relatedItem) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedItem(relatedItem);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public long countUnread(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(User user) {
        notificationRepository.markAllAsReadForUser(user);
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

}
