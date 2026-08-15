package com.lostfound.factory;

import com.lostfound.model.Category;
import com.lostfound.model.Item;

import jakarta.persistence.Entity;

/** Concrete factory product for Document items */
@Entity
public class DocumentItem extends Item {
    public DocumentItem() {
        setCategory(Category.DOCUMENT);
        setDescription("Document or ID — please provide details about the document type.");
    }
}
