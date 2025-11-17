package com.ticonsys.online_meet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response<T> {

    private T data;
    private PageInfo pageInfo;
    private boolean success = true;
    private String message = "Success";
    private int code = HttpStatus.OK.value();
    private String status = HttpStatus.OK.getReasonPhrase();

    public Response(T data) {
        this.data = data;
        validationCheck(data);
    }

    public Response(T data, String message) {
        this.data = data;
        this.message = message;
        validationCheck(data);
    }

    public Response(T data, PageInfo pageInfo) {
        this.data = data;
        this.pageInfo = pageInfo;
        if (pageInfo.getTotalElements() == 0)
            setExtraParameterForHttpNoContent();
    }

    private void validationCheck(T data) {
        if (Objects.isNull(data)) setExtraParameterForHttpNoContent();
        if (data instanceof List<?> && ((List<?>) data).isEmpty()) setExtraParameterForHttpNoContent();
    }

    private void setExtraParameterForHttpNoContent() {
        this.code = HttpStatus.NO_CONTENT.value();
        this.status = HttpStatus.NO_CONTENT.getReasonPhrase();
        this.message = "no content found with the request";
    }
}
