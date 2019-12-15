package com.ractoc.tutorials.speedment.handler;

import com.ractoc.tutorials.speedment.mapper.ItemMapper;
import com.ractoc.tutorials.speedment.model.ItemListModel;
import com.ractoc.tutorials.speedment.model.ItemModel;
import com.ractoc.tutorials.speedment.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class ItemHandler {

    private final ItemService itemService;

    @Autowired
    public ItemHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    public List<ItemListModel> getItemList() {
        return itemService.getItemList().map(ItemMapper.INSTANCE::dbToListModel).collect(Collectors.toList());
    }

    public ItemModel getItemById(String id) {
        return ItemMapper.INSTANCE.dbToModel(itemService.getItemById(id));
    }

    public ItemModel getItemByName(String name) {
        return ItemMapper.INSTANCE.dbToModel(itemService.getItemByName(name));
    }

    public ItemModel saveItem(ItemModel item) {
        return ItemMapper.INSTANCE.dbToModel(itemService.saveItem(ItemMapper.INSTANCE.modelToDb(item)));
    }

    public ItemModel updateItem(ItemModel item) {
        return ItemMapper.INSTANCE.dbToModel(itemService.updateItem(ItemMapper.INSTANCE.modelToDb(item)));
    }

    public void deleteItem(String id) {
        itemService.deleteItem(id);
    }
}
