package com.ractoc.tutorials.speedment.controller;

import com.ractoc.tutorials.speedment.handler.ItemHandler;
import com.ractoc.tutorials.speedment.model.ItemModel;
import com.ractoc.tutorials.speedment.model.GetItemForm;
import com.ractoc.tutorials.speedment.response.BaseResponse;
import com.ractoc.tutorials.speedment.response.ErrorResponse;
import com.ractoc.tutorials.speedment.response.ItemListResponse;
import com.ractoc.tutorials.speedment.response.ItemResponse;
import com.ractoc.tutorials.speedment.service.DuplicateEntryException;
import com.ractoc.tutorials.speedment.service.NoSuchEntryException;
import com.ractoc.tutorials.speedment.service.ServiceException;
import com.ractoc.tutorials.speedment.validators.UuidPattern;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(tags = {"Item Resource"}, value = "/item", produces = "application/json")
@SwaggerDefinition(tags = {
        @Tag(name = "Item Resource", description = "Main entry point for the Item API. " +
                "Handles all related actions on the items. Aside from the HTTP return codes on the requests, " +
                "the response body also contains a HTTP status code which gives additional information.")
})
@RequestMapping("/item")
@Validated
public class ItemController {

    private final ItemHandler itemHandler;

    @Autowired
    public ItemController(ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @ApiOperation(value = "Get list of items", response = ItemListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed. This does not always mean tasks were found.", response = ItemListResponse.class)
    })
    @GetMapping(value = "/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItemList() {
        return new ResponseEntity<>(
                new ItemListResponse(OK, itemHandler.getItemList()), OK);
    }

    @ApiOperation(value = "Get the item by id", response = ItemListResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retrieval successfully processed.", response = ItemListResponse.class),
            @ApiResponse(code = 404, message = "The item does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping(value = "", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> getItem(@Valid GetItemForm form) {
        try {
            if (StringUtils.isNotEmpty(form.getId())) {
                return new ResponseEntity<>(
                        new ItemResponse(OK, itemHandler.getItemById(form.getId())), OK);
            } else {
                return new ResponseEntity<>(
                        new ItemResponse(OK, itemHandler.getItemByName(form.getName())), OK);
            }
        } catch (NoSuchEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Create a new item", response = ItemResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The item was successfully created", response = ItemResponse.class),
            @ApiResponse(code = 400, message = "Unable to create item, see body for more information", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> createItem(@Valid @RequestBody ItemModel item) {
        try {
            if (StringUtils.isNotBlank(item.getId())) {
                return new ResponseEntity<>(new ErrorResponse(UNPROCESSABLE_ENTITY, "No ID allowed when creating new items."), BAD_REQUEST);
            }
            return new ResponseEntity<>(new ItemResponse(CREATED, itemHandler.saveItem(item)), OK);
        } catch (DuplicateEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(CONFLICT, e.getMessage()), BAD_REQUEST);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Update an existing item", response = ItemResponse.class, consumes = "application/json", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The item was updated", response = ItemResponse.class),
            @ApiResponse(code = 404, message = "The item does not exist", response = ErrorResponse.class),
            @ApiResponse(code = 400, message = "Unable to update item, see body for more information", response = ErrorResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PutMapping(value = "", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> updateItem(@Valid @RequestBody ItemModel item) {
        try {
            if (StringUtils.isBlank(item.getId())) {
                return new ResponseEntity<>(new ErrorResponse(UNPROCESSABLE_ENTITY, "ID mandatory when updating items."), BAD_REQUEST);
            }
            return new ResponseEntity<>(new ItemResponse(MOVED_PERMANENTLY, itemHandler.updateItem(item)), OK);
        } catch (NoSuchEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(NOT_FOUND, e.getMessage()), NOT_FOUND);
        } catch (DuplicateEntryException e) {
            return new ResponseEntity<>(new ErrorResponse(CONFLICT, e.getMessage()), BAD_REQUEST);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "removes an item", response = BaseResponse.class, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deletion successfully processed.", response = BaseResponse.class)
    })
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BaseResponse> deleteItem(@PathVariable @UuidPattern String id) {
        try {
            itemHandler.deleteItem(id);
            return new ResponseEntity<>(new BaseResponse(GONE.value()), OK);
        } catch (ServiceException e) {
            return new ResponseEntity<>(new ErrorResponse(INTERNAL_SERVER_ERROR, e.getMessage()), INTERNAL_SERVER_ERROR);
        }
    }

}
