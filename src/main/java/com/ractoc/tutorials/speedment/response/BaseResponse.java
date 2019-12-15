package com.ractoc.tutorials.speedment.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ApiModel(value = "BaseResponse Model", description = "Contains the basic response. All other response inherit this response.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BaseResponse {

    @ApiModelProperty(value = "The internal response code.")
    @NonNull
    private Integer responseCode;

}
