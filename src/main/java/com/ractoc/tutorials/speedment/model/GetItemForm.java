package com.ractoc.tutorials.speedment.model;

import com.ractoc.tutorials.speedment.validators.CheckExactlyOneNotNull;
import com.ractoc.tutorials.speedment.validators.UuidPattern;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "Search Form Model", description = "Contains the search field used when searching for items")
@CheckExactlyOneNotNull(fieldNames = {"id", "name"}, message = "Either id or name should have a value, never none, never both.")
public class GetItemForm {
    @ApiModelProperty(value = "The id of the item.")
    @UuidPattern
    private String id;

    @ApiModelProperty(value = "The name of the item.")
    private String name;
}
