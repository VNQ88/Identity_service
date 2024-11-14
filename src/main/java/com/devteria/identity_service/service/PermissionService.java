package com.devteria.identity_service.service;

import java.util.List;

import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.response.PermissionRespone;

public interface PermissionService {
    PermissionRespone create(PermissionRequest permissionRequest);

    List<PermissionRespone> getAll();

    void delete(String permission);
}
