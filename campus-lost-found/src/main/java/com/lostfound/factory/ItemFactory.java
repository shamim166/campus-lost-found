package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

/**
 * ============================================================
 * DESIGN PATTERN #2 — FACTORY METHOD
 * ============================================================
 * ItemFactory creates the correct concrete Item subtype based
 * on the category selected by the user. Controllers do NOT
 * instantiate items directly — they always go through the factory.
 *
 * Flow:
 *   User selects Category → Controller → ItemFactory → ConcreteItem
 * ============================================================
 */
public class ItemFactory {

    /**
     * Factory method: creates a pre-configured Item based on category.
     * Each concrete type sets sensible defaults for that category.
     */
    public static Item createItem(Category category) {
        return switch (category) {
            case ELECTRONICS -> new ElectronicsItem();
            case DOCUMENT    -> new DocumentItem();
            case BAG         -> new BagItem();
            case ACCESSORY   -> new AccessoryItem();
            default          -> new OtherItem();
        };
    }

    /**
     * Overload: also sets the basic fields at creation time.
     */
    public static Item createItem(Category category, String title, String description) {
        Item item = createItem(category);
        item.setTitle(title);
        item.setDescription(description);
        return item;
    }

}
