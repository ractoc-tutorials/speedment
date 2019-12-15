package com.ractoc.tutorials.speedment.controller;

import com.ractoc.tutorials.speedment.handler.ItemHandler;
import com.ractoc.tutorials.speedment.model.ItemListModel;
import com.ractoc.tutorials.speedment.model.ItemModel;
import com.ractoc.tutorials.speedment.model.GetItemForm;
import com.ractoc.tutorials.speedment.response.BaseResponse;
import com.ractoc.tutorials.speedment.response.ErrorResponse;
import com.ractoc.tutorials.speedment.response.ItemListResponse;
import com.ractoc.tutorials.speedment.response.ItemResponse;
import com.ractoc.tutorials.speedment.service.DuplicateEntryException;
import com.ractoc.tutorials.speedment.service.NoSuchEntryException;
import com.ractoc.tutorials.speedment.service.ServiceException;
import info.solidsoft.mockito.java8.api.WithBDDMockito;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@DisplayName("Test the ItemController")
@ExtendWith(MockitoExtension.class)
class ItemControllerTest implements WithAssertions, WithBDDMockito {
    private static final String TEST_ID = "87924b6e-408d-4d7d-b551-ce181017488e";
    private static final String TEST_NAME = "test name";
    private static final String TEST_LEAD = "test description";
    private static final String TEST_EXCEPTION = "test exception";

    @Mock
    private ItemHandler mockedItemHandler;

    @InjectMocks
    private ItemController controller;

    private ItemModel baseItem = ItemModel.builder().name(TEST_NAME).lead(TEST_LEAD).build();
    private ItemModel updateItem = ItemModel.builder().name(TEST_NAME).lead(TEST_LEAD).id(TEST_ID).build();
    private ItemListModel baseListItem = ItemListModel.builder().id(TEST_ID).name(TEST_NAME).build();

    @Test
    void getItemList() {
        // Given
        List<ItemListModel> listItems = new ArrayList<>();
        listItems.add(baseListItem);
        when(mockedItemHandler.getItemList()).thenReturn(listItems);

        // When
        ResponseEntity<BaseResponse> response = controller.getItemList();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(OK.value());
        assertThat(response.getBody()).isInstanceOf(ItemListResponse.class);
        ItemListResponse body = (ItemListResponse) response.getBody();
        assertThat(body.getItemList()).isNotNull();
        assertThat(body.getItemList()).isNotEmpty();
        assertThat(body.getItemList()).hasSize(1);
        assertThat(body.getItemList().get(0).getId()).isEqualTo(TEST_ID);
        assertThat(body.getItemList().get(0).getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void getItemByName() {
        // Given
        when(mockedItemHandler.getItemByName(TEST_NAME)).thenReturn(updateItem);
        GetItemForm search = new GetItemForm();
        search.setName(TEST_NAME);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(OK.value());
        assertThat(response.getBody()).isInstanceOf(ItemResponse.class);
        ItemResponse body = (ItemResponse) response.getBody();
        assertThat(body.getItem()).isNotNull();
        assertThat(body.getItem().getName()).isNotNull();
        assertThat(body.getItem().getName()).isEqualTo(updateItem.getName());
        assertThat(body.getItem().getId()).isEqualTo(TEST_ID);
    }

    @Test
    void getItemByNameNoSuchEntryException() {
        // Given
        when(mockedItemHandler.getItemByName(TEST_NAME)).thenThrow(new NoSuchEntryException(TEST_EXCEPTION));
        GetItemForm search = new GetItemForm();
        search.setName(TEST_NAME);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody().getResponseCode()).isEqualTo(NOT_FOUND.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void getItemByNameServiceException() {
        // Given
        when(mockedItemHandler.getItemByName(TEST_NAME)).thenThrow(new ServiceException(TEST_EXCEPTION));
        GetItemForm search = new GetItemForm();
        search.setName(TEST_NAME);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isNotNull();
        assertThat(response.getBody().getResponseCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }
    @Test
    void getItemById() {
        // Given
        when(mockedItemHandler.getItemById(TEST_ID)).thenReturn(updateItem);
        GetItemForm search = new GetItemForm();
        search.setId(TEST_ID);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(OK.value());
        assertThat(response.getBody()).isInstanceOf(ItemResponse.class);
        ItemResponse body = (ItemResponse) response.getBody();
        assertThat(body.getItem()).isNotNull();
        assertThat(body.getItem().getName()).isNotNull();
        assertThat(body.getItem().getName()).isEqualTo(updateItem.getName());
        assertThat(body.getItem().getId()).isEqualTo(TEST_ID);
    }

    @Test
    void getItemByIdNoSuchEntryException() {
        // Given
        when(mockedItemHandler.getItemById(TEST_ID)).thenThrow(new NoSuchEntryException(TEST_EXCEPTION));
        GetItemForm search = new GetItemForm();
        search.setId(TEST_ID);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody().getResponseCode()).isEqualTo(NOT_FOUND.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void getItemByIDServiceException() {
        // Given
        when(mockedItemHandler.getItemById(TEST_ID)).thenThrow(new ServiceException(TEST_EXCEPTION));
        GetItemForm search = new GetItemForm();
        search.setId(TEST_ID);

        // When
        ResponseEntity<BaseResponse> response = controller.getItem(search);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isNotNull();
        assertThat(response.getBody().getResponseCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void createItem() {
        // Given
        when(mockedItemHandler.saveItem(baseItem)).thenReturn(baseItem.toBuilder().id(TEST_ID).build());

        // When
        ResponseEntity<BaseResponse> response = controller.createItem(baseItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(CREATED.value());
        assertThat(response.getBody()).isInstanceOf(ItemResponse.class);
        ItemResponse body = (ItemResponse) response.getBody();
        assertThat(body.getItem()).isNotNull();
        assertThat(body.getItem().getName()).isNotNull();
        assertThat(body.getItem().getName()).isEqualTo(baseItem.getName());
        assertThat(body.getItem().getId()).isEqualTo(TEST_ID);
    }

    @Test
    void createItemWithId() {
        // Given

        // When
        ResponseEntity<BaseResponse> response = controller.createItem(updateItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getResponseCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo("No ID allowed when creating new items.");
    }

    @Test
    void createItemDuplicateEntry() {
        // Given
        when(mockedItemHandler.saveItem(baseItem)).thenThrow(new DuplicateEntryException(TEST_EXCEPTION));

        // When
        ResponseEntity<BaseResponse> response = controller.createItem(baseItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getResponseCode()).isEqualTo(CONFLICT.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void createItemServiceException() {
        // Given
        when(mockedItemHandler.saveItem(baseItem)).thenThrow(new ServiceException(TEST_EXCEPTION));

        // When
        ResponseEntity<BaseResponse> response = controller.createItem(baseItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isNotNull();
        assertThat(response.getBody().getResponseCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void updateItem() {

        // Given
        when(mockedItemHandler.updateItem(updateItem)).thenReturn(updateItem.toBuilder().name("updated").build());

        // When
        ResponseEntity<BaseResponse> response = controller.updateItem(updateItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(MOVED_PERMANENTLY.value());
        assertThat(response.getBody()).isInstanceOf(ItemResponse.class);
        ItemResponse body = (ItemResponse) response.getBody();
        assertThat(body.getItem()).isNotNull();
        assertThat(body.getItem().getName()).isNotNull();
        assertThat(body.getItem().getName()).isEqualTo("updated");
        assertThat(body.getItem().getId()).isEqualTo(TEST_ID);
    }

    @Test
    void updateItemNoId() {
        // Given

        // When
        ResponseEntity<BaseResponse> response = controller.updateItem(baseItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getResponseCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo("ID mandatory when updating items.");
    }

    @Test
    void updateItemNoSuchEntryException() {

        // Given
        when(mockedItemHandler.updateItem(updateItem)).thenThrow(new NoSuchEntryException(TEST_EXCEPTION));

        // When
        ResponseEntity<BaseResponse> response = controller.updateItem(updateItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody().getResponseCode()).isEqualTo(NOT_FOUND.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void updateItemDuplicateEntryException() {

        // Given
        when(mockedItemHandler.updateItem(updateItem)).thenThrow(new DuplicateEntryException(TEST_EXCEPTION));

        // When
        ResponseEntity<BaseResponse> response = controller.updateItem(updateItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody().getResponseCode()).isEqualTo(CONFLICT.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void updateItemServiceException() {

        // Given
        when(mockedItemHandler.updateItem(updateItem)).thenThrow(new ServiceException(TEST_EXCEPTION));

        // When
        ResponseEntity<BaseResponse> response = controller.updateItem(updateItem);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getResponseCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }

    @Test
    void deleteItem() {
        // Given

        // When
        ResponseEntity<BaseResponse> response = controller.deleteItem(TEST_ID);

        // Then
        verify(mockedItemHandler).deleteItem(TEST_ID);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getResponseCode()).isEqualTo(GONE.value());
    }

    @Test
    void deleteItemServiceException() {
        // Given
        doThrow(new ServiceException(TEST_EXCEPTION)).when(mockedItemHandler).deleteItem(TEST_ID);

        // When
        ResponseEntity<BaseResponse> response = controller.deleteItem(TEST_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getResponseCode()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body.getMessage()).isNotNull();
        assertThat(body.getMessage()).isEqualTo(TEST_EXCEPTION);
    }
}
