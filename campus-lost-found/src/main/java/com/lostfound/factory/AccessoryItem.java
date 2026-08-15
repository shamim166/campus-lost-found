package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import jakarta.persistence.Entity;

/** Concrete factory product for Accessory items */
@Entity
public class AccessoryItem extends Item {
    public AccessoryItem() {
        setCategory(Category.ACCESSORY);
        setDescription("Accessory item — please describe material, color, and any markings.");
    }
}
