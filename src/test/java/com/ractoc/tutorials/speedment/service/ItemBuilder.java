package com.ractoc.tutorials.speedment.service;

import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.Item;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.ItemImpl;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@NoArgsConstructor
@Accessors(fluent = true)
public class ItemBuilder {
    private String id;
    private String name;
    private String lead;
    private GeneratedItem.ItemType itemType;

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }


    public Item build() {
        Item dbItem = new ItemImpl();
        dbItem.setId(id);
        dbItem.setLead(lead);
        dbItem.setName(name);
        dbItem.setItemType(itemType);
        return dbItem;
    }
}
