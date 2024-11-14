package com.devteria.identity_service.service;

import java.util.List;

import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.response.RoleRespone;

public interface RoleService {
    RoleRespone create(RoleRequest roleRequest);

    List<RoleRespone> getAll();

    void delete(String role);
}
