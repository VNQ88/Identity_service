package com.devteria.identity_service.repository;

import com.devteria.identity_service.dto.response.PageRespone;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devteria.identity_service.constant.AppConst.SORT_BY;

@Slf4j
@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private static final String LIKE_FORMAT = "%%%s%%";
    //pageNo = offset
    public PageRespone<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize, String sortBy, String search) {
        StringBuilder sqlQuery = new StringBuilder("select u from User u where 1=1 ");
        if(StringUtils.hasLength(search)){
            sqlQuery.append("and lower(u.firstName) like lower(:firstName) ");
            sqlQuery.append("or lower(u.lastName) like lower(:lastName) ");
            sqlQuery.append("or lower(u.email) like lower(:email) ");
        }

        if(StringUtils.hasLength(sortBy)){
            // firstName:asc|desc
            Pattern pattern = Pattern.compile(SORT_BY);
            Matcher matcher = pattern.matcher(sortBy);

            if (matcher.find()){
                sqlQuery.append(String.format("order by u.%s %s", matcher.group(1), matcher.group(3)));
            }
        }

        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (StringUtils.hasLength(search)){
            selectQuery.setParameter("firstName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("lastName", String.format(LIKE_FORMAT, search));
            selectQuery.setParameter("email", String.format(LIKE_FORMAT, search));
        }
        List<?> users = selectQuery.getResultList();

        //query Count users
        StringBuilder sqlCountQuery = new StringBuilder("select count(*) from User u where 1=1 ");
        if(StringUtils.hasLength(search)){
            sqlCountQuery.append("and lower(u.firstName) like lower(?1) ");
            sqlCountQuery.append("or lower(u.lastName) like lower(?2) ");
            sqlCountQuery.append("or lower(u.email) like lower(?3) ");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)){
            countQuery.setParameter(1, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(2, String.format(LIKE_FORMAT, search));
            countQuery.setParameter(3, String.format(LIKE_FORMAT, search));
        }

        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements: {}", totalElements);

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<?> page = new PageImpl<>(users, pageable, totalElements);
        return PageRespone.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .totalElements(totalElements)
                .items(users)
                .build();
    }


}
