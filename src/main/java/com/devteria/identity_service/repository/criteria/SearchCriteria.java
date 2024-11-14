package com.devteria.identity_service.repository.criteria;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchCriteria {
    String key;
    String operation;
    Object value;
}
