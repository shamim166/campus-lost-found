package com.lostfound.service;

import com.lostfound.decorator.*;
import com.lostfound.factory.ItemFactory;
import com.lostfound.model.*;
import com.lostfound.repository.ItemRepository;
import com.lostfound.singleton.LostFoundRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ItemService {

    private final ItemRepository     itemRepository;
    private final LostFoundRegistry  registry;

    private static final String UPLOAD_DIR = "uploads/items";

    public ItemService(ItemRepository itemRepository, LostFoundRegistry registry) {
        this.itemRepository = itemRepository;
        this.registry       = registry;
    }

    // =========================================================
    // On startup: load all active items into Singleton Registry
    // =========================================================
    @PostConstruct
    public void initRegistry() {
        List<Item> activeItems = itemRepository.findTop8ByStatusOrderByCreatedAtDesc(ItemStatus.ACTIVE);
        // Load ALL active items (not just 8) for registry
        List<Item> all = itemRepository.findByTypeAndStatusOrderByCreatedAtDesc(ItemType.LOST, ItemStatus.ACTIVE);
        all.addAll(itemRepository.findByTypeAndStatusOrderByCreatedAtDesc(ItemType.FOUND, ItemStatus.ACTIVE));
        registry.loadFromDatabase(all);
    }

    // =========================================================
    // Create a new item — uses FACTORY pattern to build item
    // then applies DECORATOR for Urgent/Reward/Verified tags
    // then registers into SINGLETON registry
    // =========================================================
    public Item createItem(
            Category category, String title, String description,
            String location, LocalDate itemDate, LocalTime itemTime,
            ItemType type, boolean urgent, BigDecimal rewardAmount,
            String contactPhone, String contactEmail,
            MultipartFile imageFile, User user) throws IOException {

        // === FACTORY METHOD: create correct item type ===
        Item item = ItemFactory.createItem(category, title, description);
        item.setLocation(location);
        item.setItemDate(itemDate);
        item.setItemTime(itemTime);
        item.setType(type);
        item.setStatus(ItemStatus.ACTIVE);
        item.setUrgent(urgent);
        item.setRewardAmount(rewardAmount);
        item.setContactPhone(contactPhone);
        item.setContactEmail(contactEmail);
        item.setUser(user);

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            item.setImageUrl(imageUrl);
        }

        Item saved = itemRepository.save(item);

        // === DECORATOR: apply tags based on properties ===
        ItemComponent decorated = new BaseItem(saved);
        if (saved.isUrgent())                            decorated = new UrgentTag(decorated);
        if (saved.getRewardAmount() != null)             decorated = new RewardTag(decorated);
        if (saved.isVerified())                          decorated = new VerifiedTag(decorated);
        // (decorated title is for display; entity already has flags stored)

        // === SINGLETON: register into central registry ===
        registry.registerItem(saved);

        return saved;
    }

    // =========================================================
    // Save image file to disk
    // =========================================================
    private String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            contentType = "image/jpeg";
        }
        String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
        return "data:" + contentType + ";base64," + base64;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }

    // =========================================================
    // Find / Query
    // =========================================================
    @Transactional(readOnly = true)
    public Item findById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
        if (item.getUser() != null) {
            item.getUser().getName(); // Initialize lazy proxy for view
        }
        return item;
    }

    @Transactional(readOnly = true)
    public List<Item> findByUser(User user) {
        return itemRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Item> getRecentItems() {
        return itemRepository.findTop8ByStatusOrderByCreatedAtDesc(ItemStatus.ACTIVE);
    }

    @Transactional(readOnly = true)
    public Page<Item> browseItems(String keyword, ItemType type, Category category,
                                   String location, LocalDate dateFrom, LocalDate dateTo, int page) {
        return itemRepository.browseItems(
            keyword, type, category, location, dateFrom, dateTo,
            PageRequest.of(page, 12)
        );
    }

    // =========================================================
    // Update item
    // =========================================================
    public Item updateItem(Long id, String title, String description, String location,
                           LocalDate itemDate, LocalTime itemTime, boolean urgent,
                           BigDecimal rewardAmount, String contactPhone, String contactEmail,
                           MultipartFile imageFile, User currentUser) throws IOException {
        Item item = findById(id);

        // Security: only owner can edit
        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not allowed to edit this item.");
        }

        item.setTitle(title);
        item.setDescription(description);
        item.setLocation(location);
        item.setItemDate(itemDate);
        item.setItemTime(itemTime);
        item.setUrgent(urgent);
        item.setRewardAmount(rewardAmount);
        item.setContactPhone(contactPhone);
        item.setContactEmail(contactEmail);

        if (imageFile != null && !imageFile.isEmpty()) {
            item.setImageUrl(saveImage(imageFile));
        }

        Item updated = itemRepository.save(item);
        registry.registerItem(updated); // refresh in registry
        return updated;
    }

    // =========================================================
    // Delete item
    // =========================================================
    public void deleteItem(Long id, User currentUser) {
        Item item = findById(id);
        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not allowed to delete this item.");
        }
        registry.deregisterItem(item);
        itemRepository.delete(item);
    }

    // =========================================================
    // Admin delete (no ownership check)
    // =========================================================
    public void adminDeleteItem(Long id) {
        Item item = findById(id);
        registry.deregisterItem(item);
        itemRepository.delete(item);
    }

    // =========================================================
    // Claim item
    // =========================================================
    public void claimItem(Long id) {
        Item item = findById(id);
        item.setStatus(ItemStatus.CLAIMED);
        registry.deregisterItem(item);
        itemRepository.save(item);
    }

    // =========================================================
    // Close item
    // =========================================================
    public void closeItem(Long id, User currentUser) {
        Item item = findById(id);
        if (!item.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Not authorized.");
        }
        item.setStatus(ItemStatus.CLOSED);
        registry.deregisterItem(item);
        itemRepository.save(item);
    }

    // =========================================================
    // Admin verify
    // =========================================================
    public void verifyItem(Long id) {
        Item item = findById(id);
        item.setVerified(true);
        itemRepository.save(item);
    }

    // =========================================================
    // Statistics
    // =========================================================
    public long countByType(ItemType type)       { return itemRepository.countByType(type); }
    public long countByStatus(ItemStatus status) { return itemRepository.countByStatus(status); }
    public long countByUserAndType(User u, ItemType t)       { return itemRepository.countByUserAndType(u, t); }
    public long countByUserAndStatus(User u, ItemStatus s)   { return itemRepository.countByUserAndStatus(u, s); }

    public List<Item> findAllItems() { return itemRepository.findAll(); }

    // Matching helpers
    public List<Item> findPotentialFoundMatches(Category category, String location) {
        String kw = location != null ? location.split(" ")[0] : "";
        return itemRepository.findPotentialFoundMatches(category, kw);
    }
    public List<Item> findPotentialLostMatches(Category category, String location) {
        String kw = location != null ? location.split(" ")[0] : "";
        return itemRepository.findPotentialLostMatches(category, kw);
    }

}
