package com.ractoc.tutorials.speedment.handler;

import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.Item;
import com.ractoc.tutorials.speedment.db.speedment.my_collection.item.generated.GeneratedItem;
import com.ractoc.tutorials.speedment.model.ItemListModel;
import com.ractoc.tutorials.speedment.model.ItemModel;
import com.ractoc.tutorials.speedment.service.ItemBuilder;
import com.ractoc.tutorials.speedment.service.ItemService;
import info.solidsoft.mockito.java8.api.WithBDDMockito;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Test the ItemHandler")
@ExtendWith(MockitoExtension.class)
class ItemHandlerTest implements WithAssertions, WithBDDMockito {

    private static final String TEST_ID = "87924b6e-408d-4d7d-b551-ce181017488e";
    private static final String TEST_NAME = "test name";
    private static final String TEST_DESCRIPTION = "test description";

    @Mock
    private ItemService mockedItemService;

    @InjectMocks
    private ItemHandler handler;

    private ItemModel baseItemModel = new ItemModel().toBuilder().name(TEST_NAME).lead(TEST_DESCRIPTION).build();
    private ItemModel updateItemModel = new ItemModel().toBuilder().id(TEST_ID).name(TEST_NAME).lead(TEST_DESCRIPTION).build();
    private Item baseDbItem = ItemBuilder.builder().id(TEST_ID).name(TEST_NAME).lead(TEST_DESCRIPTION).build();

    @Test
    void getItemList() {
        // Given
        List<Item> items = new ArrayList<>();
        items.add(baseDbItem);
        when(mockedItemService.getItemList()).thenReturn(items.stream());

        // When
        List<ItemListModel> result = handler.getItemList();

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(0).getId()).isEqualTo(TEST_ID);
        assertThat(result.get(0).getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void getItemByName() {
        // Given
        when(mockedItemService.getItemByName(TEST_NAME)).thenReturn(baseDbItem);

        // When
        ItemModel result = handler.getItemByName(TEST_NAME);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void getItemById() {
        // Given
        when(mockedItemService.getItemById(TEST_ID)).thenReturn(baseDbItem);

        // When
        ItemModel result = handler.getItemById(TEST_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void saveItem() {
        // Given
        ArgumentCaptor<Item> dbItemCaptor = ArgumentCaptor.forClass(Item.class);
        when(mockedItemService.saveItem(any(Item.class))).thenReturn(baseDbItem);

        // When
        ItemModel result = handler.saveItem(baseItemModel);

        // Then
        verify(mockedItemService).saveItem(dbItemCaptor.capture());
        Item dbItemResult = dbItemCaptor.getValue();
        assertThat(dbItemResult).isNotNull();
        assertThat(dbItemResult.getId()).isNull();
        assertThat(dbItemResult.getName()).isEqualTo(TEST_NAME);
        assertThat(dbItemResult.getLead()).isNotNull();
        assertThat(dbItemResult.getLead()).isEqualTo(TEST_DESCRIPTION);
        assertThat(dbItemResult.getItemType()).isNotNull();
        assertThat(dbItemResult.getItemType()).isEqualByComparingTo(GeneratedItem.ItemType.BOARDGAMES);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getLead()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void updateItem() {
        // Given
        ArgumentCaptor<Item> dbItemCaptor = ArgumentCaptor.forClass(Item.class);
        when(mockedItemService.updateItem(any(Item.class))).thenReturn(baseDbItem);

        // When
        ItemModel result = handler.updateItem(updateItemModel);

        // Then
        verify(mockedItemService).updateItem(dbItemCaptor.capture());
        Item dbItemResult = dbItemCaptor.getValue();
        assertThat(dbItemResult).isNotNull();
        assertThat(dbItemResult.getId()).isNotNull();
        assertThat(dbItemResult.getId()).isEqualTo(TEST_ID);
        assertThat(dbItemResult.getName()).isEqualTo(TEST_NAME);
        assertThat(dbItemResult.getLead()).isNotNull();
        assertThat(dbItemResult.getLead()).isEqualTo(TEST_DESCRIPTION);
        assertThat(dbItemResult.getItemType()).isNotNull();
        assertThat(dbItemResult.getItemType()).isEqualByComparingTo(GeneratedItem.ItemType.BOARDGAMES);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TEST_ID);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getLead()).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void deleteItem() {
        // Given

        // when
        handler.deleteItem(TEST_ID);

        // Then
        verify(mockedItemService).deleteItem(TEST_ID);
    }
}
