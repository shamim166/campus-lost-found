package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import jakarta.persistence.Entity;

/** Concrete factory product for Other items */
@Entity
public class OtherItem extends Item {
    public OtherItem() {
        setCategory(Category.OTHER);
        setDescription("Other item — please provide a detailed description.");
    }
}
