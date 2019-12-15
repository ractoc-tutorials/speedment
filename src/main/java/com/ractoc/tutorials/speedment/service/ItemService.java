package com.ractoc.tutorials.speedment.service;

import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.Item;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.ItemManager;
import com.speedment.runtime.core.exception.SpeedmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem.ID;
import static com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem.NAME;


@Service
public class ItemService {

    private static final String ITEM_NOT_FOUND = "Item with %s %s not found";
    private final ItemManager itemManager;

    @Autowired
    public ItemService(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public Stream<Item> getItemList() {
        return itemManager.stream();
    }

    public Item getItemByName(String name) {
        Optional<Item> item = itemManager.stream()
                .filter(NAME.equalIgnoreCase(name)).findAny();
        return item.orElseThrow(() -> new NoSuchEntryException(String.format(ITEM_NOT_FOUND, "name", name)));
    }

    public Item getItemById(String id) {
        Optional<Item> item = itemManager.stream()
                .filter(ID.equalIgnoreCase(id)).findAny();
        return item.orElseThrow(() -> new NoSuchEntryException(String.format(ITEM_NOT_FOUND, "id", id)));
    }

    public Item saveItem(Item item) {
        try {
            createIdForItem(item);
            Optional<Item> existingItem = itemManager.stream()
                    .filter(NAME.equalIgnoreCase(item.getName())).findAny();
            if (existingItem.isPresent()) {
                throw new DuplicateEntryException("Item with name " + item.getName() + " already exists.");
            }
            return itemManager.persist(item);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to save item " + item.getName(), e);
        }
    }

    public Item updateItem(Item item) {
        try {
            // first check validity of the ID
            Item currentItem = itemManager.stream()
                    .filter(ID.equal(item.getId()))
                    .findAny()
                    .orElseThrow(() -> new NoSuchEntryException(String.format(ITEM_NOT_FOUND, "id", item.getId())));
            if (!currentItem.getName().equals(item.getName())) {
                // If the name has changed check it.
                Optional<Item> existingItem = itemManager.stream()
                        .filter(NAME.equalIgnoreCase(item.getName()))
                        .findAny();
                if (existingItem.isPresent()) {
                    throw new DuplicateEntryException("Item with name " + item.getName() + " already exists.");
                }
            }
            // then update the item
            return itemManager.update(item);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to update item " + item.getName(), e);
        }
    }

    public void deleteItem(String id) {
        try {
            Optional<Item> existingItem = itemManager.stream().filter(ID.equal(id)).findAny();
            // if no item is present with the requested ID, just report it as successfully removed.
            existingItem.ifPresent(itemManager::remove);
        } catch (SpeedmentException e) {
            throw new ServiceException("Unable to delete item " + id, e);
        }
    }

    private void createIdForItem(Item item) {
        boolean idExists;
        String id;
        do {
            id = UUID.randomUUID().toString();
            Optional<Item> existingItem = itemManager.stream().filter(ID.equal(id)).findAny();
            idExists = existingItem.isPresent();
        } while (idExists);
        item.setId(id);
    }
}
