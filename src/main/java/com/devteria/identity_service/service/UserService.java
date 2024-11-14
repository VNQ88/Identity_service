package com.devteria.identity_service.service;

import java.util.List;

import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.dto.response.PageRespone;
import com.devteria.identity_service.dto.response.UserRespone;

public interface UserService {
    UserRespone createUser(UserCreationRequest request);
    //    @PreAuthorize("hasRole('ADMIN')")
    List<UserRespone> getUser();

    PageRespone<?> getAllUserWithSortBy(int pageNo, int pageSize, String sortBy);

    PageRespone<?> getAllUserWithSortByMultipleColumn(int pageNo, int pageSize, String... orders);

    UserRespone getMyInfo();

    UserRespone getUserById(String id);

    UserRespone updateUser(String id, UserUpdateRequest request);

    void deleteUser(String id);

    PageRespone<?> getAllUserWithSortByColumnAndSearch(int pageNo, int pageSize, String sortBy, String search);
}
