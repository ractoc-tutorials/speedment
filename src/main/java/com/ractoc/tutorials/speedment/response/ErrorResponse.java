package com.ractoc.tutorials.speedment.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@ApiModel(value = "ErrorResponse Model", description = "Contains the error response indicating something went wrong.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse extends BaseResponse {

    @ApiModelProperty(value = "The error message.")
    private final String message;

    public ErrorResponse(HttpStatus responseCode, String message) {
        super(responseCode.value());
        this.message = message;
    }
}
