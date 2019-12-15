package com.ractoc.tutorials.speedment.mapper;

import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.Item;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.ItemImpl;
import com.ractoc.tutorials.speedment.model.ItemListModel;
import com.ractoc.tutorials.speedment.model.ItemModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemImpl modelToDb(ItemModel model);

    ItemModel dbToModel(Item item);

    ItemListModel dbToListModel(Item item);
}
