package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import jakarta.persistence.Entity;

/** Concrete factory product for Bag items */
@Entity
public class BagItem extends Item {
    public BagItem() {
        setCategory(Category.BAG);
        setDescription("Bag or backpack — please describe color, brand, and contents if known.");
    }
}
