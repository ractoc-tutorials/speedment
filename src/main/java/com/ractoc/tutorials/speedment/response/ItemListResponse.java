package com.ractoc.tutorials.speedment.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.tutorials.speedment.model.ItemListModel;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@ApiModel(
        value = "ListResponse Model",
        description = "Contains a list of items or an empty list if no item was found.")
@JsonInclude(Include.NON_EMPTY)
public class ItemListResponse extends BaseResponse {

    private List<ItemListModel> itemList;

    public ItemListResponse(HttpStatus responseCode, List<ItemListModel> itemList) {
        super(responseCode.value());
        this.itemList = itemList;
    }
}
