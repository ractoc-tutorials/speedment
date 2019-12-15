package com.ractoc.tutorials.speedment.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ractoc.tutorials.speedment.model.ItemModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@ApiModel(
        value = "ItemResponse Model",
        description = "Contains the item response with the requested item or no item if no item was found." +
                " In case no item was found, the responseCode should be 404.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ItemResponse extends BaseResponse {

    @ApiModelProperty(value = "The item being returned to the front end.")
    private final ItemModel item;

    public ItemResponse(HttpStatus responseCode, ItemModel item) {
        super(responseCode.value());
        this.item = item;
    }
}
