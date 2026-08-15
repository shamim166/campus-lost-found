package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import jakarta.persistence.Entity;

/** Concrete factory product for Electronics items */
@Entity
public class ElectronicsItem extends Item {
    public ElectronicsItem() {
        setCategory(Category.ELECTRONICS);
        setDescription("Electronic device — please provide model/brand details.");
    }
}
