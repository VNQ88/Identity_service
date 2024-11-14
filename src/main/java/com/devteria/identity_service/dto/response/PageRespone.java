package com.devteria.identity_service.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageRespone<T> implements Serializable {
    int pageNo;
    int pageSize;
    long totalPage;
    long totalElements;
    T items;
}
