package com.devteria.identity_service.repository.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria> {


    CriteriaBuilder criteriaBuilder;
    Predicate predicate;
    Root root;
    @Override
    public void accept(SearchCriteria param) {
        log.info("ok");
        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = criteriaBuilder
                    .and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        }
        else if (param.getOperation().equalsIgnoreCase("<")){
            predicate = criteriaBuilder
                    .and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));
        }
        else if (param.getOperation().equalsIgnoreCase(":")){
            if (root.get(param.getKey()).getJavaType() == String.class){
                predicate = criteriaBuilder
                        .and(predicate, criteriaBuilder.like(root.get(param.getKey()), String.format("%%%s%%", param.getValue())));
            }
            else {
                predicate = criteriaBuilder
                        .and(predicate, criteriaBuilder.equal(root.get(param.getKey()), param.getValue()));
            }
        }
    }
}
